
CREATE TABLE IF NOT EXISTS login (
    id       uuid PRIMARY KEY USING INDEX TABLESPACE payment_tablespace,
    username varchar(20) NOT NULL UNIQUE USING INDEX TABLESPACE payment_tablespace,
    password varchar(180) NOT NULL,
    roles   varchar(32)
    ) TABLESPACE payment_tablespace;

CREATE TABLE payment
(
    id           UUID NOT NULL,
    total_amount       DECIMAL,
    currency     VARCHAR(255),
    payment_date TIMESTAMP WITHOUT TIME ZONE,
    order_number text,
    customer_id  UUID,
    CONSTRAINT pk_payment PRIMARY KEY (id)
);