### Backup

```shell
docker compose exec --user postgres database \
  pg_dump --column-inserts --on-conflict-do-nothing --section data --table meals --table meals_by_time --username seblm-meals | grep INSERT > backup/insert.sql
```

### Restore

```shell
cat backup/truncate.sql backup/insert.sql | docker compose exec --no-TTY --user postgres database \
  psql --username seblm-meals
```
