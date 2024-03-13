
psql -U payment_db_user payment_db

CREATE SCHEMA IF NOT EXISTS payment_schema AUTHORIZATION payment_db_user;

ALTER ROLE payment_db_user SET search_path = 'payment_schema';

