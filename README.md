# seblm-meals

## how to setup local database

Given user `johndoe`:

```shell script
$ createdb seblm-meals
$ psql
# alter user johndoe with encrypted password 'password';
# grant all privileges on database johndoe to johndoe;
# \q
```

## how to start application

```shell
export POSTGRESQL_ADDON_USER=johndoe
export POSTGRESQL_ADDON_PASSWORD=password
export POSTGRESQL_ADDON_HOST=localhost
export POSTGRESQL_ADDON_DB=johndoe
sbt run
```
