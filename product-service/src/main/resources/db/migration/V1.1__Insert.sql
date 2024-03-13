
INSERT INTO login (id, username, password, roles)
VALUES
    ('30000000-0000-0000-0000-000000000000','admin','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g','ADMIN,CUSTOMER,ACTUATOR');


-- Beispiel 1
INSERT INTO product (id, version, name, brand, price, description, category, created, updated)
VALUES ('12000000-0000-0000-0000-000000000001', 0, 'Laptop', 'ExampleBrand', 999.99, 'High-performance laptop', 'ELEKTRONIK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('12000000-0000-0000-0000-000000000002', 0, 'Smartphone', 'AnotherBrand', 499.99, 'Latest smartphone response', 'ELEKTRONIK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('12000000-0000-0000-0000-000000000003', 0, 'Television', 'AwesomeBrand', 799.99, '4K Ultra HD Smart TV', 'ELEKTRONIK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('12000000-0000-0000-0000-000000000004', 0, 'Tablet', 'YetAnotherBrand', 299.99, 'Portable tablet device', 'ELEKTRONIK',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('12000000-0000-0000-0000-000000000005', 0, 'Smartwatch', 'CoolBrand', 199.99, 'Fitness tracker smartwatch', 'ELEKTRONIK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
