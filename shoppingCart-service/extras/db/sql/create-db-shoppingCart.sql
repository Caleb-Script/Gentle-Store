
CREATE ROLE shopping_cart_db_user LOGIN PASSWORD 'p';

CREATE DATABASE shopping_cart_db;

GRANT ALL ON DATABASE shopping_cart_db TO shopping_cart_db_user;

CREATE TABLESPACE shopping_cart_tablespace OWNER shopping_cart_db_user LOCATION '/Users/gentlebookpro/GentleStore/Tablespace/Shopping-Cart';
