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

 - do not suggest meals from 7 days before current meal suggestion
 - prefer suggest meals by lunch/dinner time
