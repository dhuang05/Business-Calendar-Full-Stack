FROM mysql:8.0

COPY ./setup_calendar_ddl.sql /docker-entrypoint-initdb.d/setup_calendar_ddl.sql
COPY ./setup_calendar_mdl.sql /docker-entrypoint-initdb.d/setup_calendar_mdl.sql

