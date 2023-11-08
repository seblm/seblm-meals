-- !Ups

ALTER TABLE meals ADD COLUMN url text;

-- !Downs

ALTER TABLE meals DROP COLUMN url;
