package com.marcuslull.aigmmcp.tools.diceroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class DiceRollerService {

    private final Random random;

    public DiceRollerService(Random random) {
        this.random = random;
    }


    @Tool(name = "rollDice", description = "Roll some dice by specifying count and die type")
    public DiceRollResult rollDice(DiceRoll diceRoll) {

        log.info("New dice roll: {}", diceRoll);

        // TODO logic - Jackson error message on conversion failure

        List<Integer> rolls = new ArrayList<>();
        int total = 0;

        if (diceRoll == null || diceRoll.diceType() == null || diceRoll.quantity() <= 0) {
            log.warn("invalid dice roll: {}", diceRoll);
            return new DiceRollResult(diceRoll, rolls, total, "You must provide a valid dice roll");
        }

        for (int i = 0; i < diceRoll.quantity(); i++) {
            int roll = random.nextInt(diceRoll.diceType().getSides()) + 1;
            rolls.add(roll);
            total += roll;
        }

        log.info("Dice roll result: {} = {}", rolls, total);
        return new DiceRollResult(diceRoll, rolls, total, null);
    }

}
