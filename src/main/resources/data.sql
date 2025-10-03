-- Campsites
insert into campsites (site_number, description, max_people, status)
values ('A-01', '숲 뷰, 전기가능', 4, 'AVAILABLE'),
       ('A-02', '강가, 그늘많음', 6, 'AVAILABLE');

-- Products
insert into products (name, stock_quantity, price, product_type)
values ('랜턴', 20, 30000.00, 'RENTAL'),
       ('장작팩', 50, 10000.00, 'SALE'),
       -- 추가 RENTAL 5개
       ('코펠 세트', 15, 20000.00, 'RENTAL'),
       ('의자', 25, 15000.00, 'RENTAL'),
       ('테이블', 10, 25000.00, 'RENTAL'),
       ('버너', 12, 18000.00, 'RENTAL'),
       ('취사도구 세트', 30, 12000.00, 'RENTAL'),
       -- 추가 SALE 5개
       ('생수(2L)', 100, 2000.00, 'SALE'),
       ('라면 세트', 80, 4000.00, 'SALE'),
       ('스낵팩', 60, 3000.00, 'SALE'),
       ('휴지', 70, 2500.00, 'SALE'),
       ('아이스팩', 90, 1500.00, 'SALE');

insert into reservations (customer_name, start_date, end_date, campsite_id, phone_number, status, reservation_date, confirmation_code, created_at)
values
  ('홍길동', DATEADD('DAY', 10, current_date), DATEADD('DAY', 12, current_date), 1, '010-1111-2222', 'CONFIRMED', current_date, 'ABC123', current_timestamp),
  ('김철수', DATEADD('DAY', 15, current_date), DATEADD('DAY', 17, current_date), 2, '010-3333-4444', 'CONFIRMED', current_date, 'XYZ789', current_timestamp),
  ('이영희', DATEADD('DAY', 20, current_date), DATEADD('DAY', 22, current_date), 1, '010-5555-6666', 'CONFIRMED', current_date, 'R00003', current_timestamp),
  ('박민수', DATEADD('DAY', 25, current_date), DATEADD('DAY', 27, current_date), 2, '010-7777-8888', 'CONFIRMED', current_date, 'R00004', current_timestamp),
  ('최수정', DATEADD('DAY', 30, current_date), DATEADD('DAY', 33, current_date), 1, '010-9999-0000', 'CONFIRMED', current_date, 'R00005', current_timestamp),
  ('정하늘', DATEADD('DAY', 35, current_date), DATEADD('DAY', 37, current_date), 2, '010-2222-3333', 'CONFIRMED', current_date, 'R00006', current_timestamp),
  ('오세훈', DATEADD('DAY', 40, current_date), DATEADD('DAY', 42, current_date), 1, '010-4444-5555', 'CONFIRMED', current_date, 'R00007', current_timestamp),
  ('유지민', DATEADD('DAY', 45, current_date), DATEADD('DAY', 47, current_date), 2, '010-6666-7777', 'CONFIRMED', current_date, 'R00008', current_timestamp),
  ('선우진', DATEADD('DAY', 50, current_date), DATEADD('DAY', 52, current_date), 1, '010-1212-3434', 'CONFIRMED', current_date, 'R00009', current_timestamp),
  ('배수아', DATEADD('DAY', 55, current_date), DATEADD('DAY', 57, current_date), 2, '010-5656-7878', 'CONFIRMED', current_date, 'R00010', current_timestamp);

-- Sales Records
insert into sales_records (product_id, quantity, total_price, created_at) values
  (2, 3, 30000.00, DATEADD('DAY', -1, current_timestamp)),
  (2, 1, 10000.00, current_timestamp),
  (8, 5, 10000.00, DATEADD('DAY', -7, current_timestamp)),
  (9, 2, 8000.00, DATEADD('DAY', -15, current_timestamp)),
  (10, 10, 30000.00, DATEADD('DAY', -25, current_timestamp));




