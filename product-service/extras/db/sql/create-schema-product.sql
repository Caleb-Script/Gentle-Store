
psql -U product_db_user product_db

CREATE SCHEMA IF NOT EXISTS product_schema AUTHORIZATION product_db_user;

ALTER ROLE product_db_user SET search_path = 'product_schema';


DROP TABLE customer_activity;
DROP TABLE phone_number_list;
DROP TABLE login;
DROP TABLE customer;
DROP TABLE address;