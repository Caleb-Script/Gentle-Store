
CREATE ROLE payment_db_user LOGIN PASSWORD 'p';

CREATE DATABASE payment_db;

GRANT ALL ON DATABASE payment_db TO payment_db_user;

CREATE TABLESPACE payment_tablespace OWNER payment_db_user LOCATION '/Users/gentlebookpro/GentleStore/Tablespace/Payment';
