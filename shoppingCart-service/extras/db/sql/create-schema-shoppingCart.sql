
psql -U shopping_cart_db_user shopping_cart_db

CREATE SCHEMA IF NOT EXISTS shopping_cart_schema AUTHORIZATION shopping_cart_db_user;

ALTER ROLE shopping_cart_db_user SET search_path = 'shopping_cart_schema';


DROP TABLE customer_activity;
DROP TABLE phone_number_list;
DROP TABLE login;
DROP TABLE customer;
DROP TABLE address;