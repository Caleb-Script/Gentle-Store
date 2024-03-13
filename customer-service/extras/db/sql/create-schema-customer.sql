
psql -U customer_db_user customer_db

CREATE SCHEMA IF NOT EXISTS customer_schema AUTHORIZATION customer_db_user;

ALTER ROLE customer_db_user SET search_path = 'customer_schema';


DROP TABLE customer_activity;
DROP TABLE phone_number_list;
DROP TABLE login;
DROP TABLE customer;
DROP TABLE address;