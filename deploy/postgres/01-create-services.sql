-- Demo-only credentials. Production requires managed secrets.
CREATE ROLE claims_app LOGIN PASSWORD 'local-claims-only';
CREATE ROLE insights_app LOGIN PASSWORD 'local-insights-only';
CREATE SCHEMA claims AUTHORIZATION claims_app;
CREATE SCHEMA insights AUTHORIZATION insights_app;
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
