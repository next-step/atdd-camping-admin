-- Campsites (1001~)
INSERT INTO campsites (id, site_number, description, max_people) VALUES
  (1001, 'A-01', '숲 뷰, 전기가능', 4),
  (1002, 'A-02', '강가, 그늘많음', 6);

-- Products (1001~)
INSERT INTO products (id, name, stock_quantity, price, product_type) VALUES
  (1001, '랜턴', 20, 30000.00, 'RENTAL'),
  (1002, '장작팩', 50, 10000.00, 'SALE'),
  (1003, '코펠 세트', 15, 20000.00, 'RENTAL'),
  (1004, '의자', 25, 15000.00, 'RENTAL'),
  (1005, '테이블', 10, 25000.00, 'RENTAL'),
  (1006, '버너', 12, 18000.00, 'RENTAL'),
  (1007, '취사도구 세트', 30, 12000.00, 'RENTAL'),
  (1008, '생수(2L)', 100, 2000.00, 'SALE'),
  (1009, '라면 세트', 80, 4000.00, 'SALE'),
  (1010, '스낵팩', 60, 3000.00, 'SALE'),
  (1011, '휴지', 70, 2500.00, 'SALE'),
  (1012, '아이스팩', 90, 1500.00, 'SALE'),
  (1013, '품절 텐트', 0, 50000.00, 'RENTAL'),
  (1014, '품절 침낭', 0, 35000.00, 'SALE'),
  (1015, '한정판 굿즈', 5, 8000.00, 'SALE');

-- Reservations (1001~, campsite_id는 1001~1002 참조)
-- 테스트용 고정 데이터: 다양한 상태의 예약
INSERT INTO reservations (id, customer_name, start_date, end_date, campsite_id, phone_number, status, reservation_date, confirmation_code, created_at) VALUES
  -- 취소 가능한 예약 (CONFIRMED) - 테스트 ID: 1001
  (1001, '홍길동', current_date, current_date + 1, 1001, '010-1111-2222', 'CONFIRMED', current_date, 'ABC123', current_timestamp),
  -- 취소 가능한 예약 (PENDING) - 테스트 ID: 1002
  (1002, '김철수', current_date + 1, current_date + 2, 1002, '010-3333-4444', 'PENDING', current_date, 'XYZ789', current_timestamp),
  -- 이미 취소된 예약 - 테스트 ID: 1003
  (1003, '이영희', DATEADD('DAY', -28, current_date), DATEADD('DAY', -27, current_date), 1001, '010-5555-6666', 'CANCELLED', DATEADD('DAY', -29, current_date), 'R00003', DATEADD('DAY', -29, current_timestamp)),
  -- 체크아웃된 예약 - 테스트 ID: 1004
  (1004, '박민수', DATEADD('DAY', -25, current_date), DATEADD('DAY', -24, current_date), 1002, '010-7777-8888', 'CHECKED_OUT', DATEADD('DAY', -26, current_date), 'R00004', DATEADD('DAY', -26, current_timestamp)),
  -- 일반 예약들
  (1005, '최수정', DATEADD('DAY', -21, current_date), DATEADD('DAY', -19, current_date), 1001, '010-9999-0000', 'CONFIRMED', DATEADD('DAY', -22, current_date), 'R00005', DATEADD('DAY', -22, current_timestamp)),
  (1006, '정하늘', DATEADD('DAY', -18, current_date), DATEADD('DAY', -17, current_date), 1002, '010-2222-3333', 'CONFIRMED', DATEADD('DAY', -19, current_date), 'R00006', DATEADD('DAY', -19, current_timestamp)),
  (1007, '오세훈', DATEADD('DAY', -15, current_date), DATEADD('DAY', -14, current_date), 1001, '010-4444-5555', 'CONFIRMED', DATEADD('DAY', -16, current_date), 'R00007', DATEADD('DAY', -16, current_timestamp)),
  (1008, '유지민', DATEADD('DAY', -12, current_date), DATEADD('DAY', -11, current_date), 1002, '010-6666-7777', 'CONFIRMED', DATEADD('DAY', -13, current_date), 'R00008', DATEADD('DAY', -13, current_timestamp)),
  (1009, '선우진', DATEADD('DAY', -9, current_date), DATEADD('DAY', -8, current_date), 1001, '010-1212-3434', 'CONFIRMED', DATEADD('DAY', -10, current_date), 'R00009', DATEADD('DAY', -10, current_timestamp)),
  (1010, '배수아', DATEADD('DAY', -6, current_date), DATEADD('DAY', -5, current_date), 1002, '010-5656-7878', 'CONFIRMED', DATEADD('DAY', -7, current_date), 'R00010', DATEADD('DAY', -7, current_timestamp)),
  (1011, '고다빈', DATEADD('DAY', -3, current_date), DATEADD('DAY', -2, current_date), 1001, '010-9090-1010', 'CONFIRMED', DATEADD('DAY', -4, current_date), 'R00011', DATEADD('DAY', -4, current_timestamp)),
  (1012, '한도윤', DATEADD('DAY', -1, current_date), current_date, 1002, '010-2323-4545', 'CONFIRMED', DATEADD('DAY', -2, current_date), 'R00012', DATEADD('DAY', -2, current_timestamp));

-- Sales Records (1001~, product_id는 1001~ 참조)
INSERT INTO sales_records (id, product_id, quantity, total_price, created_at) VALUES
  (1001, 1002, 3, 30000.00, DATEADD('DAY', -1, current_timestamp)),
  (1002, 1002, 1, 10000.00, current_timestamp),
  (1003, 1008, 5, 10000.00, DATEADD('DAY', -7, current_timestamp)),
  (1004, 1009, 2, 8000.00, DATEADD('DAY', -15, current_timestamp)),
  (1005, 1010, 10, 30000.00, DATEADD('DAY', -25, current_timestamp));

-- Rental Records (1001~, reservation_id는 1003~, product_id는 1001~ 참조)
INSERT INTO rental_records (id, reservation_id, product_id, quantity, is_returned, created_at) VALUES
  (1001, 1003, 1003, 2, false, DATEADD('DAY', -28, current_timestamp)),
  (1002, 1004, 1004, 1, true, DATEADD('DAY', -25, current_timestamp)),
  (1003, 1005, 1005, 3, false, DATEADD('DAY', -21, current_timestamp)),
  (1004, 1006, 1006, 1, true, DATEADD('DAY', -18, current_timestamp)),
  (1005, 1007, 1007, 4, false, DATEADD('DAY', -15, current_timestamp)),
  (1006, null, 1003, 1, false, DATEADD('DAY', -3, current_timestamp));