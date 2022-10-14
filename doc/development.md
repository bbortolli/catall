# Docker

## 1. Clojure

```
docker run -it --name catall -p 3000:3000  -v %cd%:/api -w /api clojure:latest bash
```

## 2. PostgreSQL

```
docker run -it --rm --name pgserver -e POSTGRES_PASSWORD=api -v pgdata:/var/lib/postgresql/data -p 5433:5432 postgres:14.5-alpine
docker exec -it pgserver psql -U postgres -W
```
