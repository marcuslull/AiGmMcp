package com.marcuslull.aigmmcp.tools.diceroller;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiceRollerService {

    @Tool(description = "Roll some dice based on common expression. Ex. 2d10, 4d6, etc...")
    public DiceRollResult rollDice(DiceRoll diceRoll) {

        // TODO logic - dont forget to catch from the enum and throw to error

        List<Integer> rolls = new ArrayList<>();
        int total = 0;
        String error = "";

        return new DiceRollResult(diceRoll, rolls, total, error);
    }

}
