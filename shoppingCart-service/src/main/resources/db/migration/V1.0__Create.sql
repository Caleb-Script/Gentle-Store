
CREATE TABLE IF NOT EXISTS login (
    id       uuid PRIMARY KEY USING INDEX TABLESPACE shopping_cart_tablespace,
    username varchar(20) NOT NULL UNIQUE USING INDEX TABLESPACE shopping_cart_tablespace,
    password varchar(180) NOT NULL,
    roles   varchar(32)
) TABLESPACE shopping_cart_tablespace;

CREATE TABLE shopping_cart
(
    id                UUID    PRIMARY KEY USING INDEX TABLESPACE shopping_cart_tablespace,
    version           INTEGER NOT NULL DEFAULT 0,
    total_amount      DECIMAL,
    customer_id       UUID    NOT NULL UNIQUE,
    customer_username text    NOT NULL UNIQUE,
    is_complete       BOOLEAN DEFAULT TRUE,
    created           TIMESTAMP,
    updated           TIMESTAMP
) TABLESPACE shopping_cart_tablespace;
CREATE INDEX IF NOT EXISTS shopping_cart_customer_id_idx ON shopping_cart(customer_id) TABLESPACE shopping_cart_tablespace;

CREATE TABLE item
(
    id                UUID    PRIMARY KEY USING INDEX TABLESPACE shopping_cart_tablespace,
    sku_code          VARCHAR(255),
    quantity          INTEGER NOT NULL,
    shopping_cart_id  UUID REFERENCES shopping_cart,
    idx               INTEGER DEFAULT 0
) TABLESPACE shopping_cart_tablespace;
CREATE INDEX IF NOT EXISTS item_shopping_shopping_cart_id_idx ON item(shopping_cart_id) TABLESPACE shopping_cart_tablespace;