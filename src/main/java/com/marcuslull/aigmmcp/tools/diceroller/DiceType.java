package com.marcuslull.aigmmcp.tools.diceroller;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public enum DiceType {
    D4(4),
    D6(6),
    D8(8),
    D10(10),
    D12(12),
    D20(20),
    D100(100);

    private final int sides;

    DiceType(int sides) {
        this.sides = sides;
    }

    public static DiceType fromString(String diceString) {
        return Arrays.stream(values())
                .filter(t -> t.name().equals(Objects.requireNonNull(diceString).toUpperCase()))
                .findFirst()
                .orElseThrow();
    }

    public static DiceType fromSides(int sides) {
        return Arrays.stream(values())
                .filter(t -> t.getSides() == sides)
                .findFirst()
                .orElseThrow();
    }
}
