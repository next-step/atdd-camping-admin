INSERT INTO reservations (customer_name, start_date, end_date, reservation_date, campsite_id, phone_number, status, confirmation_code, created_at)
VALUES ('홍길동', DATEADD('DAY', 7, CURRENT_DATE), DATEADD('DAY', 9, CURRENT_DATE), DATEADD('DAY', 7, CURRENT_DATE), 1, '010-1234-5678', 'CONFIRMED', 'ABC123', CURRENT_TIMESTAMP);
