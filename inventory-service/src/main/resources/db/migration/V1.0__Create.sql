
CREATE TABLE IF NOT EXISTS login (
    id       uuid PRIMARY KEY USING INDEX TABLESPACE inventory_tablespace,
    username varchar(20) NOT NULL UNIQUE USING INDEX TABLESPACE inventory_tablespace,
    password varchar(180) NOT NULL,
    roles   varchar(32)
    ) TABLESPACE inventory_tablespace;

CREATE TABLE inventory
(
    id         UUID    PRIMARY KEY USING INDEX TABLESPACE inventory_tablespace,
    version    INTEGER NOT NULL DEFAULT  0,
    sku_code   VARCHAR(255),
    quantity   INTEGER,
    unit_price DECIMAL,
    status     VARCHAR(255),
    created    TIMESTAMP,
    updated    TIMESTAMP,
    product_id UUID
) TABLESPACE inventory_tablespace;
CREATE INDEX IF NOT EXISTS inventory_id_idx ON inventory(id) TABLESPACE inventory_tablespace;

CREATE TABLE reserved_products
(
    id           UUID    PRIMARY KEY USING INDEX TABLESPACE inventory_tablespace,
    sku_code     VARCHAR(255),
    quantity     INTEGER NOT NULL,
    customer_id  UUID,
    inventory_id UUID REFERENCES inventory,
    idx          INTEGER DEFAULT 0
) TABLESPACE inventory_tablespace;
CREATE INDEX IF NOT EXISTS reserved_products_inventory_id_idx ON reserved_products(inventory_id) TABLESPACE inventory_tablespace;
