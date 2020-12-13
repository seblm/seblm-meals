# seblm-meals

## how to setup local database

Given user `johndoe`:

```shell script
$ createdb johndoe
$ psql
# alter user johndoe with encrypted password 'password';
# grant all privileges on database johndoe to johndoe;
# \q
```

## backlog

 - next week screen should be able to shuffle all empty meals
 - next week screen should be able to re-shuffle an already planed meal
