-- !Ups

CREATE TABLE meals
(
    id          uuid PRIMARY KEY,
    description text NOT NULL
);
CREATE TABLE meals_by_time
(
    time    timestamp PRIMARY KEY,
    meal_id uuid REFERENCES meals (id)
);

-- !Downs

DROP TABLE meals;
DROP TABLE meals_by_time;
