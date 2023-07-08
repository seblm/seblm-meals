### Backup

```shell
docker compose exec --interactive --tty --user postgres database \
  pg_dump --column-inserts --on-conflict-do-nothing --section data --table meals --table meals_by_time --username seblm-meals | grep INSERT > backup/insert.sql
```

### Restore

```shell
cat backup/truncate.sql backup/insert.sql | docker compose exec --interactive --user postgres database \
  psql --username seblm-meals
```
