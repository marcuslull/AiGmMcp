package com.marcuslull.aigmmcp.tools.diceroller;

import java.util.List;

public record DiceRollResult(
        DiceRoll diceRoll,
        List<Integer> rolls,
        int total,
        String error
) {
}
