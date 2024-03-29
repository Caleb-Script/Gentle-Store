
INSERT INTO login (id, username, password, roles)
VALUES
    ('30000000-0000-0000-0000-000000000000','admin','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g','ADMIN,CUSTOMER,ACTUATOR'),
    ('30000000-0000-0000-0000-000000000001','gentlecg99','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g', 'CUSTOMER'),
    ('30000000-0000-0000-0000-000000000002','rae_n_a','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g','CUSTOMER'),
    ('30000000-0000-0000-0000-000000000003','thomasM','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g','CUSTOMER'),
    ('30000000-0000-0000-0000-000000000004','VeroXx','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g','CUSTOMER'),
    ('30000000-0000-0000-0000-000000000005','asd','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g','CUSTOMER');
-- Beispiele für Address
INSERT INTO address (id, street, house_number, zip_code, state, city)
VALUES ('10000000-0000-0000-0000-000000000001', 'kp', '41', '70376', 'BADEN_WUERTTEMBERG', 'Stuttgart'),
       ('10000000-0000-0000-0000-000000000002', 'Bühlallee', '1/3', '71139', 'BADEN_WUERTTEMBERG', 'Ehningen'),
       ('10000000-0000-0000-0000-000000000003', 'Musterstraße', '789', '98765', 'BAYERN', 'München'),
       ('10000000-0000-0000-0000-000000000004', 'Hauptstraße', '123', '98765', 'HAMBURG', 'UFF'),
       ('10000000-0000-0000-0000-000000000005', 'Am Markt', '789', '98765', 'SCHLESWIG_HOLSTEIN', 'keine ahnung');

-- Beispiele für Customer
INSERT INTO customer (id, version, surname, forename, email, contact_options,customer_category, has_newsletter, birth_date, gender, status, marital_status,interests, address_id, username, created, updated)
VALUES ('00000000-0000-0000-0000-000000000001', 0, 'Gyamgi', 'Ceb', 'Calzb_G@outlk.de','EMAIL,MAIL,SMS', 1, FALSE, '1993-05-03', 'MALE', 'ACTIVE', 'MARRIED','INVESTMENTS,SAVING_AND_FINANCE,TECHNOLOGY_AND_INNOVATION', '10000000-0000-0000-0000-000000000001', 'gentlecg99', '2024-02-25 00:00:00','2024-02-25 00:00:00'),
       ('00000000-0000-0000-0000-000000000002', 0, 'Gyfi', 'Rael', 'rachooh@icloud.com', 'MAIL,EMAIL', 1, FALSE, '1998-12-21', 'FEMALE', 'ACTIVE', 'MARRIED', 'TRAVEL,SAVING_AND_FINANCE,REAL_ESTATE', '10000000-0000-0000-0000-000000000002', 'rae_n_a', '2024-02-25 00:00:00', '2024-02-25 00:00:00'),
       ('00000000-0000-0000-0000-000000000003', 0, 'Mustermann', 'Thomas', 'thomas.mustermann@example.com', 'SMS', 4, TRUE, '1985-12-12', 'MALE', 'ACTIVE', 'SINGLE', 'REAL_ESTATE,INSURANCE,INVESTMENTS,CREDIT_AND_DEBT', '10000000-0000-0000-0000-000000000003', 'thomasM', '2024-02-25 00:00:00', '2024-02-25 00:00:00'),
       ('00000000-0000-0000-0000-000000000004', 0, 'Muratori', 'Veronica', 'Muratori.Veronica@gmail.com', 'SMS', 1, FALSE, '1999-06-12', 'FEMALE', 'BLOCKED', 'DIVORCED', 'REAL_ESTATE,INSURANCE,INVESTMENTS,CREDIT_AND_DEBT', '10000000-0000-0000-0000-000000000004', 'VeroXx', '2024-02-25 00:00:00', '2024-02-25 00:00:00'),
       ('00000000-0000-0000-0000-000000000005', 0, 'Polly', 'Rolly', 'rollypolly@ok.de', 'MAIL', 1, FALSE, '1980-02-22', 'MALE', 'ACTIVE', 'MARRIED', 'TRAVEL', '10000000-0000-0000-0000-000000000005', 'asd', '2024-02-25 00:00:00', '2024-02-25 00:00:00');
-- Beispiele für CustomerActivity
INSERT INTO customer_activity (id, activity_type, timestamp, content, customer_id, idx)
VALUES ('20000000-0000-0000-0000-000000000001', 'SIGN_UP', '2024-02-25 10:00:00','Registrierung abgeschlossen.','00000000-0000-0000-0000-000000000001', 0),
       ('20000000-0000-0000-0000-000000000002', 'SIGN_UP', '2024-02-25 11:00:00','Registrierung abgeschlossen.','00000000-0000-0000-0000-000000000002', 0),
       ('20000000-0000-0000-0000-000000000003', 'SIGN_UP', '2024-02-25 12:00:00','Registrierung abgeschlossen.','00000000-0000-0000-0000-000000000003', 0),
       ('20000000-0000-0000-0000-000000000004', 'SIGN_UP', '2024-02-25 10:00:00','Registrierung abgeschlossen.','00000000-0000-0000-0000-000000000004', 0),
       ('20000000-0000-0000-0000-000000000005', 'SIGN_UP', '2024-02-25 11:00:00','Registrierung abgeschlossen.','00000000-0000-0000-0000-000000000005', 0),
       ('20000000-0000-0000-0000-000000000007', 'INQUIRY', '2024-02-25 10:00:00','Kundenanfrage bezüglich eines Produkts.','00000000-0000-0000-0000-000000000002', 1),
       ('20000000-0000-0000-0000-000000000008', 'COMPLAINT', '2024-02-25 11:00:00','Kundenbeschwerde über gesperten Konto.','00000000-0000-0000-0000-000000000004', 0),
       ('20000000-0000-0000-0000-000000000009', 'CONSULTATION', '2024-02-25 12:00:00','Beratung über Finanzierungsoptionen.','00000000-0000-0000-0000-000000000003', 0);

INSERT INTO phone_number_list (id, dialing_code, number, is_default_phone_number, customer_id, idx)
VALUES ('40000000-0000-0000-0000-000000000001', '+49151', '11951223', TRUE, '00000000-0000-0000-0000-000000000001', 0),
       ('40000000-0000-0000-0000-000000000002', '0152', '87654321', TRUE, '00000000-0000-0000-0000-000000000002', 0),
       ('40000000-0000-0000-0000-000000000003', '+44', '555123456', TRUE, '00000000-0000-0000-0000-000000000003', 0),
       ('40000000-0000-0000-0000-000000000004', '0711', '123456789', FALSE, '00000000-0000-0000-0000-000000000001', 1),
       ('40000000-0000-0000-0000-000000000005', '+233', '987654321', TRUE, '00000000-0000-0000-0000-000000000005', 0),
       ('40000000-0000-0000-0000-000000000006', '+44', '555123456', TRUE, '00000000-0000-0000-0000-000000000004', 0);
