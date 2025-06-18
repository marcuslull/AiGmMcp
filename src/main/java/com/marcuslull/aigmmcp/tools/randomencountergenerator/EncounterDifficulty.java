package com.marcuslull.aigmmcp.tools.randomencountergenerator;

import java.util.Arrays;
import java.util.Objects;

public enum EncounterDifficulty {
    L,
    M,
    H;

    public static EncounterDifficulty fromString(String difficultyString) {
        return Arrays.stream(values())
                .filter(d -> d.name().equals(Objects.requireNonNull(difficultyString).toUpperCase()))
                .findFirst()
                .orElseThrow();
    }
}
