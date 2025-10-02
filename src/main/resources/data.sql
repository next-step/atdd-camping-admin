-- Campsites
insert into campsites (site_number, description, max_people, status) values
  ('A-01', '숲 뷰, 전기가능', 4, 'AVAILABLE'),
  ('A-02', '강가, 그늘많음', 6, 'AVAILABLE');

-- Products
insert into products (name, stock_quantity, price, product_type) values
  ('랜턴', 20, 30000.00, 'RENTAL'),
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

-- Reservations, Sales Records, Rental Records는 테스트를 위해 제거


