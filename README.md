# seblm-meals

## How to build and run

First generate a `Dockerfile` with sbt through Docker:

```shell
docker run --rm --tty \
  --volume .:/home/sbtuser/seblm-meals \
  --volume ~/Library/Caches/Coursier:/home/sbtuser/.cache/coursier \
  --user sbtuser --workdir /home/sbtuser/seblm-meals \
  sbtscala/scala-sbt:eclipse-temurin-jammy-17.0.8.1_1_1.9.6_3.3.1 sbt "Docker / stage"
```

Then run compose:

```shell
APPLICATION_UPDATE_DB=true docker compose up
```

Go to http://localhost:9000 and you are good to go. Please note that `APPLICATION_UPDATE_DB=true` is only required for
the first time you start compose. Once schema is created, this configuration can be omitted.

## How to stop container:

```shell
docker compose stop
```

## How to backup database:

Please have a look to [specific documentation](backup/README.md).

## How to start application in dev mode

Expose database by adding exposed ports into `compose.yaml`:

```yaml
services:
  database:
    ports:
      - 5432:5432
```

```shell
docker compose up database --detach
export POSTGRESQL_ADDON_USER=seblm-meals
export POSTGRESQL_ADDON_PASSWORD=seblm-database-password
export POSTGRESQL_ADDON_HOST=localhost
export POSTGRESQL_ADDON_DB=seblm-meals
sbt run
```
