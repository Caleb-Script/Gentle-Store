
CREATE ROLE order_db_user LOGIN PASSWORD 'p';

CREATE DATABASE order_db;

GRANT ALL ON DATABASE order_db TO order_db_user;

CREATE TABLESPACE orederspace OWNER order LOCATION '/var/lib/postgresql/tablespace/order';
CREATE TABLESPACE order_tablespace OWNER order_db_user LOCATION '/Users/gentlebookpro/GentleStore/Tablespace/Order';
