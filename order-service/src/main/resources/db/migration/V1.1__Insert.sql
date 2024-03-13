
INSERT INTO login (id, username, password, roles)
VALUES
    ('30000000-0000-0000-0000-000000000000','admin','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g','ADMIN,CUSTOMER,ACTUATOR');


-- Erstelle eine Bestellung
INSERT INTO orders (id, version, order_number, is_complete, customer_id, total_amount)
VALUES ('80000000-0000-0000-0000-000000000001', 0, 'uhb', false, '00000000-0000-0000-0000-000000000001', 26.48);

-- Füge Artikel zur Bestellung hinzu
INSERT INTO ordered_item (id, sku_code, price, quantity, order_id, idx)
VALUES ('90000000-0000-0000-0000-000000000001', 'asd', 10.99, 20, '80000000-0000-0000-0000-000000000001', 0),
       ('90000000-0000-0000-0000-000000000002', 'yxc', 15.49, 10, '80000000-0000-0000-0000-000000000001', 1);
