
CREATE ROLE product_db_user LOGIN PASSWORD 'p';

CREATE DATABASE product_db;

GRANT ALL ON DATABASE product_db TO product_db_user;

CREATE TABLESPACE product_tablespace OWNER product_db_user LOCATION '/Users/gentlebookpro/GentleBank/Tablespace/Product';
