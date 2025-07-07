DROP TABLE IF EXISTS srd521monstercr;
DROP TABLE IF EXISTS npcs;
DROP TABLE IF EXISTS npc;
DROP TABLE IF EXISTS npc_relationships;

CREATE TABLE srd521monstercr (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    cr INT
);

CREATE TABLE npc (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    race VARCHAR(255) NOT NULL,
    sex CHAR NOT NULL,
    age INT NOT NULL,
    description TEXT NOT NULL,
    personality TEXT NOT NULL,
    background TEXT,
    npc_class VARCHAR(255),
    level INT DEFAULT 0,
    status VARCHAR(255) DEFAULT 'Alive',
    location TEXT,
    notes TEXT
);

CREATE TABLE npc_relationships (
    npc1_id BIGINT NOT NULL,
    npc2_id BIGINT NOT NULL,
    details TEXT NOT NULL,
    PRIMARY KEY (npc1_id, npc2_id),
    FOREIGN KEY (npc1_id) REFERENCES npcs(id) ON DELETE CASCADE,
    FOREIGN KEY (npc2_id) REFERENCES npcs(id) ON DELETE CASCADE
)