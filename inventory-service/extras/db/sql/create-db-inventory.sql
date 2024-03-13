
CREATE ROLE inventory_db_user LOGIN PASSWORD 'p';

CREATE DATABASE inventory_db;

GRANT ALL ON DATABASE inventory_db TO inventory_db_user;

CREATE TABLESPACE inventory_tablespace OWNER inventory_db_user LOCATION '/Users/gentlebookpro/GentleStore/Tablespace/Inventory';
