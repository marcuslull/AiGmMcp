DROP TABLE IF EXISTS srd521monstercr;

CREATE TABLE srd521monstercr (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    cr INT
);