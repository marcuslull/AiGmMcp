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


    /**
     * Rolls a specified number of dice of a particular type and returns the individual results and their sum.
     * This method is exposed as a tool that can be called by an AI model, as indicated by the {@code @Tool} annotation.
     * It expects a {@link DiceRoll} object containing the type of dice and the quantity to roll.
     *
     * If the provided {@code diceRoll} is null, or if its {@code diceType} is null, or if the {@code quantity} is not positive,
     * an error message will be included in the {@link DiceRollResult}, and no dice will be rolled.
     *
     * @param diceRoll An object encapsulating the dice rolling parameters: the type of dice (e.g., D6, D20)
     *                 and the number of dice to roll.
     * @return A {@link DiceRollResult} object containing the original {@code diceRoll} request, a list of individual
     *         roll results, the sum of all rolls, and an optional error message. If the input is invalid,
     *         the list of rolls will be empty, the total will be zero, and an error message will be present.
     */
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
