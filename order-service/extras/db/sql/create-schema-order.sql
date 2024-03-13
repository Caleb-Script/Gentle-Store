
psql -U order_db_user order_db

CREATE SCHEMA IF NOT EXISTS order_schema AUTHORIZATION order_db_user;

ALTER ROLE order_db_user SET search_path = 'order_schema';
