
CREATE TABLE IF NOT EXISTS login (
    id       uuid PRIMARY KEY USING INDEX TABLESPACE order_tablespace,
    username varchar(20) NOT NULL UNIQUE USING INDEX TABLESPACE order_tablespace,
    password varchar(180) NOT NULL,
    roles   varchar(32)
    ) TABLESPACE order_tablespace;

CREATE TABLE ordered_item
(
    id       UUID    PRIMARY KEY USING INDEX TABLESPACE order_tablespace,
    sku_code VARCHAR(255),
    price    DECIMAL,
    quantity INTEGER,
    order_id UUID,
    idx      INTEGER DEFAULT 0
) TABLESPACE order_tablespace;
CREATE INDEX IF NOT EXISTS ordered_item_order_id_idx ON ordered_item(order_id) TABLESPACE order_tablespace;

CREATE TABLE orders
(
    id           UUID    PRIMARY KEY USING INDEX TABLESPACE order_tablespace,
    version      INTEGER NOT NULL,
    order_number VARCHAR(255),
    is_complete  BOOLEAN NOT NULL,
    customer_id  UUID,
    total_amount DECIMAL
) TABLESPACE order_tablespace;
CREATE INDEX IF NOT EXISTS order_id_idx ON orders(id) TABLESPACE order_tablespace;