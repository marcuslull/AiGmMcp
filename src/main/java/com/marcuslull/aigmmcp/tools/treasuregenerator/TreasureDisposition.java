package com.marcuslull.aigmmcp.tools.treasuregenerator;

import java.util.Arrays;
import java.util.Objects;

public enum TreasureDisposition {
    INCIDENTAL,
    USING,
    PERSONAL,
    STASH,
    TROVE,
    FORTUNE,
    HOARD;


    public static TreasureDisposition fromString(String dispositionString) {
        return Arrays.stream(values())
                .filter(d -> d.name().equals(Objects.requireNonNull(dispositionString).toUpperCase()))
                .findFirst()
                .orElseThrow();
    }
}
