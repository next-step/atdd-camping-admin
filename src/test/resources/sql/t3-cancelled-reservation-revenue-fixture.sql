INSERT INTO campsites (id, site_number, description, max_people)
VALUES (3001, 'T-3-01', 'T-3 취소 예약 매출 테스트 사이트', 4);

INSERT INTO reservations (
    id,
    customer_name,
    start_date,
    end_date,
    campsite_id,
    phone_number,
    status,
    reservation_date,
    confirmation_code,
    created_at
) VALUES (
    3001,
    'T-3 예약 고객',
    DATE '2026-08-05',
    DATE '2026-08-06',
    3001,
    '010-3001-3001',
    'CONFIRMED',
    DATE '2026-08-04',
    'T30001',
    TIMESTAMP '2026-08-03 12:00:00'
);
