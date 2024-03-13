
CREATE TABLE IF NOT EXISTS login (
    id       uuid PRIMARY KEY USING INDEX TABLESPACE customer_tablespace,
    username varchar(20) NOT NULL UNIQUE USING INDEX TABLESPACE customer_tablespace,
    password varchar(180) NOT NULL,
    roles   varchar(32)
    ) TABLESPACE customer_tablespace;

CREATE TABLE address
(
    id           UUID         PRIMARY KEY USING INDEX TABLESPACE customer_tablespace,
    street       VARCHAR(100) NOT NULL,
    house_number VARCHAR(5)   NOT NULL,
    zip_code     VARCHAR(5)   NOT NULL,
    state        VARCHAR(20)  NOT NULL,
    city         VARCHAR(50)  NOT NULL
) TABLESPACE customer_tablespace;
CREATE INDEX IF NOT EXISTS address_zip_code_idx ON address(zip_code) TABLESPACE customer_tablespace;

CREATE TABLE customer
(
    id                UUID        PRIMARY KEY USING INDEX TABLESPACE customer_tablespace,
    version           INTEGER     NOT NULL DEFAULT 0,
    surname           VARCHAR(40) NOT NULL,
    forename          VARCHAR(40) NOT NULL,
    email             VARCHAR(40) NOT NULL UNIQUE USING INDEX TABLESPACE customer_tablespace,
    customer_category INTEGER     NOT NULL,
    birth_date        date        NOT NULL CHECK (birth_date < CURRENT_DATE),
    has_newsletter    BOOLEAN     NOT NULL,
    gender            VARCHAR(7)  NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'DIVERSE')),
    status            VARCHAR(12) NOT NULL,
    marital_status    VARCHAR(12) NOT NULL CHECK (marital_status IN ('SINGLE', 'MARRIED', 'DIVORCED', 'WIDOWED')),
    interests         VARCHAR(255),
    contact_options   VARCHAR(255),
    address_id        UUID        NOT NULL UNIQUE USING INDEX TABLESPACE customer_tablespace REFERENCES address,
    created           TIMESTAMP,
    updated           TIMESTAMP,
    username          varchar(20) NOT NULL UNIQUE USING INDEX TABLESPACE customer_tablespace REFERENCES login(username)
) TABLESPACE customer_tablespace;
CREATE INDEX IF NOT EXISTS customer_surname_idx ON customer(surname) TABLESPACE customer_tablespace;

CREATE TABLE customer_activity
(
    id            UUID         PRIMARY KEY USING INDEX TABLESPACE customer_tablespace,
    activity_type VARCHAR(255) NOT NULL,
    timestamp     TIMESTAMP,
    content       VARCHAR(1000),
    customer_id   UUID         REFERENCES customer,
    idx           INTEGER      DEFAULT 0
) TABLESPACE customer_tablespace;
CREATE INDEX IF NOT EXISTS customer_activity_customer_id_idx ON customer_activity(customer_id) TABLESPACE customer_tablespace;

CREATE TABLE phone_number_list
(
    id                      UUID         PRIMARY KEY USING INDEX TABLESPACE customer_tablespace,
    dialing_code            VARCHAR(6)   NOT NULL,
    number                  VARCHAR(255) NOT NULL,
    is_default_phone_number BOOLEAN      NOT NULL DEFAULT FALSE,
    customer_id             UUID         REFERENCES customer,
    idx                     INTEGER      DEFAULT 0
) TABLESPACE customer_tablespace;
CREATE INDEX IF NOT EXISTS phone_number_list_customer_id_idx ON phone_number_list(customer_id) TABLESPACE customer_tablespace;