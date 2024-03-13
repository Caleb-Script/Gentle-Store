
psql -U inventory_db_user inventory_db

CREATE SCHEMA IF NOT EXISTS inventory_schema AUTHORIZATION inventory_db_user;

ALTER ROLE inventory_db_user SET search_path = 'inventory_schema';
