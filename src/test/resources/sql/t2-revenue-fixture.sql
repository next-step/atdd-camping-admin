INSERT INTO campsites (id, site_number, description, max_people)
VALUES (2001, 'T-2-01', 'T-2 매출 테스트 사이트', 4);

INSERT INTO products (id, name, stock_quantity, price, product_type) VALUES
    (2001, 'T-2 판매 상품 A', 100, 15000.00, 'SALE'),
    (2002, 'T-2 대여 상품', 100, 20000.00, 'RENTAL'),
    (2003, 'T-2 판매 상품 B', 100, 10000.00, 'SALE');

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
) VALUES
    (
        2001,
        'T-2 예약 고객',
        DATE '2026-08-05',
        DATE '2026-08-06',
        2001,
        '010-2001-2001',
        'CONFIRMED',
        DATE '2026-08-04',
        'T20001',
        TIMESTAMP '2026-08-03 12:00:00'
    ),
    (
        2002,
        'T-2 기간 예약 고객 A',
        DATE '2026-09-10',
        DATE '2026-09-11',
        2001,
        '010-2002-2002',
        'CONFIRMED',
        DATE '2026-08-28',
        'T20002',
        TIMESTAMP '2026-08-26 12:00:00'
    ),
    (
        2003,
        'T-2 기간 예약 고객 B',
        DATE '2026-09-11',
        DATE '2026-09-12',
        2001,
        '010-2003-2003',
        'CONFIRMED',
        DATE '2026-08-29',
        'T20003',
        TIMESTAMP '2026-08-27 12:00:00'
    ),
    (
        2004,
        'T-2 기간 예약 고객 C',
        DATE '2026-09-12',
        DATE '2026-09-13',
        2001,
        '010-2004-2004',
        'CONFIRMED',
        DATE '2026-08-30',
        'T20004',
        TIMESTAMP '2026-08-28 12:00:00'
    ),
    (
        2005,
        'T-2 기간 예약 고객 D',
        DATE '2026-09-13',
        DATE '2026-09-14',
        2001,
        '010-2005-2005',
        'CONFIRMED',
        DATE '2026-08-31',
        'T20005',
        TIMESTAMP '2026-08-29 12:00:00'
    );

INSERT INTO sales_records (id, product_id, quantity, total_price, created_at) VALUES
    (2001, 2001, 2, 30000.00, TIMESTAMP '2026-08-29 10:00:00'),
    (2002, 2003, 1, 10000.00, TIMESTAMP '2026-09-01 10:00:00');

INSERT INTO rental_records (id, reservation_id, product_id, quantity, is_returned, created_at)
VALUES (2001, 2002, 2002, 1, FALSE, TIMESTAMP '2026-09-01 11:00:00');
