# seblm-meals

## how to set up local database

Given user `johndoe`:

```shell script
$ createdb seblm-meals
$ psql
# alter user johndoe with encrypted password 'password';
# grant all privileges on database johndoe to johndoe;
# \q
```

## how to build and run

First generate a `Dockerfile` with sbt:

```shell
docker run --rm --tty \
  --volume .:/home/sbtuser/seblm-meals \
  --volume ~/Library/Caches/Coursier:/home/sbtuser/.cache/coursier \
  --user sbtuser --workdir /home/sbtuser/seblm-meals \
  sbtscala/scala-sbt:eclipse-temurin-jammy-17.0.5_8_1.9.0_3.3.0 sbt "Docker / stage"
```

Then build and run image:

```shell
docker buildx build --tag seblm-meals target/docker/stage
docker run --detach \
  --env POSTGRESQL_ADDON_USER=johndoe --env POSTGRESQL_ADDON_PASSWORD=password \
  --env POSTGRESQL_ADDON_HOST=host.docker.internal --env POSTGRESQL_ADDON_DB=johndoe \
  --env APPLICATION_SECRET=ziN0JiGEzE6FvUBddNcEttAC/WdabutXXblfzusCQSs= \
  --publish 9000:9000 --name seblm-meals seblm-meals
```

Go to http://localhost:9000 and you are good to go.

## how to stop container:

```shell
docker stop seblm-meals
```

## how to start application in dev mode

```shell
export POSTGRESQL_ADDON_USER=johndoe
export POSTGRESQL_ADDON_PASSWORD=password
export POSTGRESQL_ADDON_HOST=localhost
export POSTGRESQL_ADDON_DB=johndoe
sbt run
```
