# Chat App for Akka HTTP and Skinny

DB: postgres 9.5.3

initialize
```
$ docker run -d -p 5432:5432 -e POSTGRESQL_USER=chatdb -e POSTGRESQL_PASS=chatpass -e POSTGRESQL_DB=chatdb orchardup/postgresql
$ psql -h localhost -U chatdb chatdb < initTables.ddl
```

access
```
$ psql -h localhost -U chatdb chatdb
```
