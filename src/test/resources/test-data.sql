-- Test Data for Cucumber Tests

-- Campsites
insert into campsites (id, site_number, description, max_people, status) values
  (1, 'A-01', '숲 뷰, 전기가능', 4, 'AVAILABLE'),
  (2, 'A-02', '강가, 그늘많음', 6, 'AVAILABLE'),
  (3, 'B-01', '호수 뷰, 조용함', 4, 'AVAILABLE'),
  (4, 'B-02', '산 뷰, 넓은 공간', 8, 'AVAILABLE');

-- Products
insert into products (id, name, stock_quantity, price, product_type) values
  (1, '랜턴', 20, 30000.00, 'RENTAL'),
  (2, '장작팩', 50, 10000.00, 'SALE'),
  (3, '텐트', 15, 100000.00, 'RENTAL'),
  (4, '침낭', 30, 50000.00, 'RENTAL'),
  (5, '코펠세트', 25, 8000.00, 'SALE');

-- Test Reservations with various states
insert into reservations (id, customer_name, start_date, end_date, campsite_id, phone_number, status, reservation_date, confirmation_code, created_at)
values
  -- CONFIRMED 상태 예약들
  (1, '홍길동', current_date, current_date + 1, 1, '010-1111-2222', 'CONFIRMED', current_date, 'ABC123', current_timestamp),
  (2, '김철수', current_date + 1, current_date + 2, 2, '010-3333-4444', 'CONFIRMED', current_date, 'XYZ789', current_timestamp),
  (3, '이영희', current_date + 3, current_date + 4, 1, '010-5555-6666', 'CONFIRMED', current_date, 'DEF456', current_timestamp),
  -- PENDING 상태 예약
  (4, '박민수', current_date + 5, current_date + 6, 2, '010-7777-8888', 'PENDING', current_date, 'GHI789', current_timestamp),
  -- CANCELLED 상태 예약
  (5, '최수정', current_date + 7, current_date + 8, 1, '010-9999-0000', 'CANCELLED', current_date, 'JKL012', current_timestamp),

  -- CHECKED_IN 상태 예약
  (6, '정하늘', current_date + 9, current_date + 10, 2, '010-2222-3333', 'CHECKED_IN', current_date, 'MNO345', current_timestamp),
  (7, '유지민', current_date + 11, current_date + 12, 1, '010-3333-4444', 'CHECKED_IN', current_date, 'STU901', current_timestamp),
  (8, '선우진', current_date + 13, current_date + 14, 2, '010-4444-5555', 'CHECKED_IN', current_date, 'VWX234', current_timestamp),
  (9, '배수아', current_date + 15, current_date + 16, 1, '010-5555-6666', 'CHECKED_IN', current_date, 'YZA567', current_timestamp),

  -- CHECKED_OUT 상태 예약
  (10, '오세훈', current_date + 17, current_date + 18, 2, '010-6666-7777', 'CHECKED_OUT', current_date, 'PQR678', current_timestamp),
  (11, '고다빈', current_date + 19, current_date + 20, 1, '010-7777-8888', 'CHECKED_OUT', current_date, 'BCD890', current_timestamp),
  (12, '한도윤', current_date + 21, current_date + 22, 2, '010-8888-9999', 'CHECKED_OUT', current_date, 'EFG123', current_timestamp),

  -- INVALID_STATUS 상태 예약
  (13, '장미영', current_date + 23, current_date + 24, 1, '010-9999-0000', 'INVALID_STATUS', current_date, 'HIJ456', current_timestamp),
  (14, '윤성호', current_date + 25, current_date + 26, 2, '010-0000-1111', 'INVALID_STATUS', current_date, 'KLM789', current_timestamp);
