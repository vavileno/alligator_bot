CREATE ROLE alli WITH LOGIN PASSWORD 'alli';
CREATE SCHEMA alli AUTHORIZATION alli;

ALTER ROLE alli WITH BYPASSRLS;