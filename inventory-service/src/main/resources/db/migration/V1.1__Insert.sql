
INSERT INTO login (id, username, password, roles)
VALUES
    ('30000000-0000-0000-0000-000000000000','admin','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g','ADMIN,CUSTOMER,ACTUATOR');

-- Beispiele für INSERT-Statements für die Tabelle "inventory"
INSERT INTO inventory (id, version, sku_code, quantity, unit_price, status, created, updated, product_id)
VALUES
    ('01000000-0000-0000-0000-000000000001', 0, 'asd', 100, 29.99, 'AVAILABLE', '2024-03-05 10:00:00', '2024-03-05 10:00:00', '12000000-0000-0000-0000-000000000001'),
    ('01000000-0000-0000-0000-000000000002', 0, 'asdd', 150, 39.99, 'AVAILABLE', '2024-03-06 10:00:00', '2024-03-06 10:00:00', '12000000-0000-0000-0000-000000000002'),
    ('01000000-0000-0000-0000-000000000003', 0, 'qwe', 200, 49.99, 'AVAILABLE', '2024-03-07 10:00:00', '2024-03-07 10:00:00', '12000000-0000-0000-0000-000000000003'),
    ('01000000-0000-0000-0000-000000000004', 0, 'qw', 120, 19.99, 'AVAILABLE', '2024-03-08 10:00:00', '2024-03-08 10:00:00', '12000000-0000-0000-0000-000000000004'),
    ('01000000-0000-0000-0000-000000000005', 0, 'yxc', 80, 59.99, 'AVAILABLE', '2024-03-09 10:00:00', '2024-03-09 10:00:00',  '12000000-0000-0000-0000-000000000005');

-- Beispiele für INSERT-Statements für die Tabelle "reserved_products"
INSERT INTO reserved_products (id,sku_code, quantity, customer_id, inventory_id, idx)
VALUES
    ('11000000-0000-0000-0000-000000000000', 'asd', 30, '00000000-0000-0000-0000-000000000001','01000000-0000-0000-0000-000000000001', 0),
    ('11000000-0000-0000-0000-000000000001', 'asd', 20, '00000000-0000-0000-0000-000000000002','01000000-0000-0000-0000-000000000001', 1);

