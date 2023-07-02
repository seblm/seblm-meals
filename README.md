# seblm-meals

## how to build and run

First generate a `Dockerfile` with sbt through Docker:

```shell
docker run --rm --tty \
  --volume .:/home/sbtuser/seblm-meals \
  --volume ~/Library/Caches/Coursier:/home/sbtuser/.cache/coursier \
  --user sbtuser --workdir /home/sbtuser/seblm-meals \
  sbtscala/scala-sbt:eclipse-temurin-jammy-17.0.5_8_1.9.1_3.3.0 sbt "Docker / stage"
```

Then run compose:

```shell
docker compose up
```

Go to http://localhost:9000 and you are good to go.

## how to stop container:

```shell
docker compose stop
```

## how to start application in dev mode

```shell
docker compose up database
export POSTGRESQL_ADDON_USER=seblm-meals
export POSTGRESQL_ADDON_PASSWORD=seblm-database-password
export POSTGRESQL_ADDON_HOST=localhost
export POSTGRESQL_ADDON_DB=seblm-meals
sbt run
```
