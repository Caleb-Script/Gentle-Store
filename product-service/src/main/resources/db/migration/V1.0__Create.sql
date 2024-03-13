
CREATE TABLE IF NOT EXISTS login (
    id       uuid PRIMARY KEY USING INDEX TABLESPACE product_tablespace,
    username varchar(20) NOT NULL UNIQUE USING INDEX TABLESPACE product_tablespace,
    password varchar(180) NOT NULL,
    roles   varchar(32)
    ) TABLESPACE product_tablespace;

CREATE TABLE product
(
    id          UUID    PRIMARY KEY USING INDEX TABLESPACE product_tablespace,
    version     INTEGER NOT NULL DEFAULT 0,
    name        VARCHAR(255),
    brand       VARCHAR(255),
    price       DECIMAL,
    description VARCHAR(255),
    category    VARCHAR(255),
    created     TIMESTAMP,
    updated     TIMESTAMP
) TABLESPACE product_tablespace;
CREATE INDEX IF NOT EXISTS product_id_idx ON product(id) TABLESPACE product_tablespace;