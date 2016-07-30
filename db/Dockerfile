FROM postgres:9.5.3

COPY ./workdir /workdir
WORKDIR /workdir

COPY ./workdir/initTables.ddl /docker-entrypoint-initdb.d/
