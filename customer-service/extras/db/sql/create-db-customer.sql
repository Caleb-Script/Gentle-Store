
CREATE ROLE customer_db_user LOGIN PASSWORD 'p';

CREATE DATABASE customer_db;

GRANT ALL ON DATABASE customer_db TO customer_db_user;

CREATE TABLESPACE kundespace OWNER kunde LOCATION '/var/lib/postgresql/tablespace/customer';
CREATE TABLESPACE customer_tablespace OWNER customer_db_user LOCATION '/Users/gentlebookpro/GentleBank/Tablespace/Customer';
