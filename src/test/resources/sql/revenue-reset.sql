delete from reservations where id = 13;
insert into reservations (id, customer_name, start_date, end_date, campsite_id, phone_number, status, reservation_date, confirmation_code, created_at)
values (13, '검증용예약', DATEADD('DAY', 5, current_date), DATEADD('DAY', 6, current_date), 1, '010-0000-0000', 'CONFIRMED', DATEADD('DAY', 5, current_date), 'TMPCHK', DATEADD('DAY', -5, current_timestamp));
