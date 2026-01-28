package com.camping.admin.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.RentalRecord;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.dto.ProcessSaleRequest;
import com.camping.admin.dto.SaleItemResponse;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.service.RentalService;
import com.camping.admin.service.SalesService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * 동시성 문제 검증을 위한 통합 테스트
 *
 * Top 3 동시성 이슈:
 * 1. 재고 감소 시 락 없음 → 재고 과다 판매 (음수 재고)
 * 2. 대여 반납 시 락 없음 → 동일 대여 2회 반납 → 재고 2배 증가
 * 3. 예약 중복 체크 락 없음 → 동일 캠핑장 동일 날짜 이중 예약
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ConcurrencyIntegrationTest {

    @Autowired
    private SalesService salesService;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RentalRecordRepository rentalRecordRepository;

    @Nested
    @DisplayName("Issue #1: 재고 동시 감소 - 과다 판매 문제")
    class StockDecrementConcurrencyTest {

        private Product saleProduct;

        @BeforeEach
        void setUp() {
            saleProduct = productRepository.save(
                new Product("테스트 판매 상품", 10, new BigDecimal("10000"), ProductType.SALE)
            );
        }

        @Test
        @DisplayName("동시에 10개 요청이 각각 1개씩 구매하면 재고가 0이 되어야 한다")
        void concurrent_sales_should_deplete_stock_correctly() throws InterruptedException {
            // given
            int threadCount = 10;
            int quantityPerThread = 1;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // when
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        ProcessSaleRequest request = new ProcessSaleRequest();
                        SaleItemResponse item = new SaleItemResponse();
                        item.setProductId(saleProduct.getId());
                        item.setQuantity(quantityPerThread);
                        request.setItems(List.of(item));
                        salesService.processSale(request);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            // then
            Product updatedProduct = productRepository.findById(saleProduct.getId()).orElseThrow();
            int finalStock = updatedProduct.getStockQuantity();

            System.out.println("=== 재고 동시 감소 테스트 결과 ===");
            System.out.println("초기 재고: 10");
            System.out.println("요청 수: " + threadCount + " (각 1개씩)");
            System.out.println("성공: " + successCount.get() + ", 실패: " + failCount.get());
            System.out.println("최종 재고: " + finalStock);
            System.out.println("예상 재고: 0");

            // 락이 없으면 race condition으로 인해 재고가 0보다 클 수 있음 (lost update)
            // 또는 재고 체크 후 감소 사이에 다른 스레드가 끼어들어 음수가 될 수 있음
            assertThat(finalStock)
                .as("동시성 제어가 되면 재고는 정확히 0이어야 함")
                .isEqualTo(0);
            assertThat(successCount.get()).isEqualTo(10);
            assertThat(failCount.get()).isEqualTo(0);
        }

        @Test
        @DisplayName("재고 10개에 동시에 20개 요청 시 10개만 성공해야 한다")
        void concurrent_sales_exceeding_stock_should_fail_appropriately() throws InterruptedException {
            // given
            int threadCount = 20;
            int quantityPerThread = 1;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            List<String> errors = Collections.synchronizedList(new ArrayList<>());

            // when
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        ProcessSaleRequest request = new ProcessSaleRequest();
                        SaleItemResponse item = new SaleItemResponse();
                        item.setProductId(saleProduct.getId());
                        item.setQuantity(quantityPerThread);
                        request.setItems(List.of(item));
                        salesService.processSale(request);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        errors.add(e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            // then
            Product updatedProduct = productRepository.findById(saleProduct.getId()).orElseThrow();
            int finalStock = updatedProduct.getStockQuantity();

            System.out.println("=== 재고 초과 동시 요청 테스트 결과 ===");
            System.out.println("초기 재고: 10");
            System.out.println("요청 수: " + threadCount + " (각 1개씩)");
            System.out.println("성공: " + successCount.get() + ", 실패: " + failCount.get());
            System.out.println("최종 재고: " + finalStock);
            System.out.println("예상: 성공 10, 실패 10, 최종 재고 0");

            // 동시성 제어가 제대로 되면:
            // - 정확히 10개 성공, 10개 실패
            // - 최종 재고 0
            // 동시성 제어가 안 되면:
            // - 10개 이상 성공 가능 (lost update)
            // - 음수 재고 가능
            assertThat(finalStock)
                .as("최종 재고는 0 이상이어야 함 (음수 재고 불가)")
                .isGreaterThanOrEqualTo(0);
            assertThat(successCount.get())
                .as("성공 횟수는 초기 재고(10)를 초과할 수 없음")
                .isLessThanOrEqualTo(10);
        }
    }

    @Nested
    @DisplayName("Issue #2: 대여 반납 동시 요청 - 이중 반납 문제")
    class RentalReturnConcurrencyTest {

        private Product rentalProduct;
        private RentalRecord rentalRecord;

        @BeforeEach
        void setUp() {
            rentalProduct = productRepository.save(
                new Product("테스트 대여 상품", 10, new BigDecimal("5000"), ProductType.RENTAL)
            );

            // 대여 생성 (재고 5개 사용)
            rentalRecord = rentalRecordRepository.save(
                new RentalRecord(null, rentalProduct, 5)
            );
        }

        @Test
        @DisplayName("동시에 같은 대여 건을 반납 요청하면 1번만 성공해야 한다")
        void concurrent_return_same_rental_should_succeed_once() throws InterruptedException {
            // given
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            List<String> errors = Collections.synchronizedList(new ArrayList<>());

            int stockBeforeReturn = productRepository.findById(rentalProduct.getId())
                .orElseThrow().getStockQuantity();

            // when
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        rentalService.markAsReturned(rentalRecord.getId());
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        errors.add(e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            // then
            Product updatedProduct = productRepository.findById(rentalProduct.getId()).orElseThrow();
            int stockAfterReturn = updatedProduct.getStockQuantity();

            System.out.println("=== 대여 이중 반납 테스트 결과 ===");
            System.out.println("대여 수량: 5");
            System.out.println("반납 전 재고: " + stockBeforeReturn);
            System.out.println("동시 반납 요청 수: " + threadCount);
            System.out.println("성공: " + successCount.get() + ", 실패: " + failCount.get());
            System.out.println("반납 후 재고: " + stockAfterReturn);
            System.out.println("예상 재고: " + (stockBeforeReturn + 5));

            // 동시성 제어가 제대로 되면:
            // - 정확히 1개 성공, 9개 실패
            // - 재고는 대여 수량(5)만큼만 증가
            // 동시성 제어가 안 되면:
            // - 여러 개 성공 가능
            // - 재고가 대여 수량 * 성공횟수 만큼 증가 (잘못된 재고 증가)
            assertThat(successCount.get())
                .as("반납은 정확히 1번만 성공해야 함")
                .isEqualTo(1);
            assertThat(stockAfterReturn)
                .as("재고는 대여 수량(5)만큼만 증가해야 함")
                .isEqualTo(stockBeforeReturn + 5);
        }
    }

    @Nested
    @DisplayName("Issue #3: 대여 동시 생성 - 재고 과다 대여 문제")
    class RentalCreateConcurrencyTest {

        private Product rentalProduct;

        @BeforeEach
        void setUp() {
            rentalProduct = productRepository.save(
                new Product("테스트 대여 상품", 5, new BigDecimal("5000"), ProductType.RENTAL)
            );
        }

        @Test
        @DisplayName("재고 5개에 동시에 10개 대여 요청 시 5개만 성공해야 한다")
        void concurrent_rentals_exceeding_stock_should_fail_appropriately() throws InterruptedException {
            // given
            int threadCount = 10;
            int quantityPerThread = 1;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            List<String> errors = Collections.synchronizedList(new ArrayList<>());

            // when
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        rentalService.createRental(rentalProduct.getId(), quantityPerThread, null);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        errors.add(e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            // then
            Product updatedProduct = productRepository.findById(rentalProduct.getId()).orElseThrow();
            int finalStock = updatedProduct.getStockQuantity();

            System.out.println("=== 대여 동시 생성 테스트 결과 ===");
            System.out.println("초기 재고: 5");
            System.out.println("동시 대여 요청 수: " + threadCount + " (각 1개씩)");
            System.out.println("성공: " + successCount.get() + ", 실패: " + failCount.get());
            System.out.println("최종 재고: " + finalStock);
            System.out.println("예상: 성공 5, 실패 5, 최종 재고 0");

            assertThat(finalStock)
                .as("최종 재고는 0 이상이어야 함")
                .isGreaterThanOrEqualTo(0);
            assertThat(successCount.get())
                .as("성공 횟수는 초기 재고(5)를 초과할 수 없음")
                .isLessThanOrEqualTo(5);
        }
    }
}