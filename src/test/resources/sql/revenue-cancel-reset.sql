delete from reservations where id = 14;
insert into reservations (id, customer_name, start_date, end_date, campsite_id, phone_number, status, reservation_date, confirmation_code, created_at)
values (14, '취소검증용예약', current_date, DATEADD('DAY', 1, current_date), 1, '010-0000-0001', 'CONFIRMED', current_date, 'CNCL14', current_timestamp);
