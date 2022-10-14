CREATE DATABASE db_api_catall;
CREATE USER api WITH PASSWORD 'api';

ALTER ROLE api SET client_encoding TO 'utf8';
ALTER ROLE api SET default_transaction_isolation TO 'read committed';
ALTER ROLE api SET TIMEZONE TO 'America/Sao_Paulo';
SET TIMEZONE TO 'America/Sao_Paulo';

GRANT ALL PRIVILEGES ON DATABASE db_api_catall TO api;

CREATE TABLE IF NOT EXISTS place (
  id text NOT NULL,
  name text NOT NULL,
  modifiedat text NOT NULL,
  PRIMARY KEY (id)
);
