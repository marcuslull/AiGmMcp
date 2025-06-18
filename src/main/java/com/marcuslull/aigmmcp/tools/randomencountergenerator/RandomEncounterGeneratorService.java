package com.marcuslull.aigmmcp.tools.randomencountergenerator;

import com.marcuslull.aigmmcp.data.csv.CsvParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class RandomEncounterGeneratorService {

    private final CsvParserService csvParserService;
    private final Random random;

    public RandomEncounterGeneratorService(CsvParserService csvParserService, Random random) {
        this.csvParserService = csvParserService;
        this.random = random;
    }


    @Tool(name = "randomEncounterGenerator", description = "Generate a random monster encounter based on PC levels and difficulty.")
    public EncounterGenerationResult generateEncounter(EncounterGenerationQuery encounterGenerationQuery) {

        log.info("Generating a random encounter: {}", encounterGenerationQuery);

        // receive args
        if (!argsAreGood(encounterGenerationQuery)) {
            log.warn("invalid random encounter args: {}", encounterGenerationQuery);
            return new EncounterGenerationResult(encounterGenerationQuery, null, null, null,
                    "Encounter query must be an array of PC levels 1-20 and a difficulty level - L (low), M (Moderate), H (Hard).");
        }

        // calculate encounter budget
        int budget = calculateEncounterBudget(encounterGenerationQuery);
        if (budget == -1) {
            log.error("Budget returned -1 which is indicative of a CSV parsing issue");
            return new EncounterGenerationResult(encounterGenerationQuery, budget, null, null,
                    "Internal error - Either generate an appropriate encounter yourself or try again later");
        }
        if (budget < 200) {
            log.warn("Random encounter budget was less than min threshold. A random encounter will not be generated.");
            return new EncounterGenerationResult(encounterGenerationQuery, budget, null, null,
                    "The total party level is too low to meet the minimum random encounter threshold. You should carefully plan encounter for this weak group of PCs.");
        }

        // calculate random CRs from budget
        List<Integer> crList = generateRandomCrListFromBudget(budget);
        if (crList == null || crList.isEmpty()) {
            log.error("CR list is empty when it should be populated: Budget - {}, CRList - {}", budget, crList);
            return new EncounterGenerationResult(encounterGenerationQuery, budget, crList,null,
                    "Internal error - Either generate an appropriate encounter yourself or try again later");
        }

        // fetch monsters based on CRs
        List<String> monsterList = getMonsterListsByCr(crList);
        if (monsterList.isEmpty()) {
            log.error("Monster list is empty when it should be populated: CRList - {}, MonsterList - {}", crList, monsterList);
            return new EncounterGenerationResult(encounterGenerationQuery, budget, crList, monsterList,
                    "Internal error - Either generate an appropriate encounter yourself or try again later");
        }

        log.info("Random encounter generated successfully: CRList - {}, MonsterList - {}", crList, monsterList);
        return new EncounterGenerationResult(encounterGenerationQuery, budget, crList, monsterList, null);
    }


    private boolean argsAreGood(EncounterGenerationQuery query) {

        // defensive null/empty checks
        if (query.pcs() == null || query.pcs().isEmpty() || query.difficulty() == null) return false;

        // PC range is 1-20
        return query.pcs().stream().noneMatch(p -> p < 1 || p > 20);
    }


    private int calculateEncounterBudget(EncounterGenerationQuery encounterGenerationQuery) {

        // get our lookup table
        Map<Integer, List<String>> xpBudgetPerCharTable = csvParserService.getXpBudgetPerCharTable();
        if (xpBudgetPerCharTable == null || xpBudgetPerCharTable.isEmpty()) {
            return -1;
        }

        // map the PC level to XP via lookup table and sum
        int difficultyOrdinal = encounterGenerationQuery.difficulty().ordinal(); // L, M, H enum

        return encounterGenerationQuery.pcs()
                .stream()
                // catch any parseInt exceptions and return -1 if so
                .mapToInt(p -> {
                    int xp = 0;
                    try { xp = Integer.parseInt(xpBudgetPerCharTable.get(p).get(difficultyOrdinal)); }
                    catch (NumberFormatException e) { return -1; }
                    return xp;
                })
                // this is essentially a .sum() but if it encounters a -1 from above it will short-circuit.
                .reduce(0, (subtotal, element) -> {
                    if (element == -1) return -1;
                    return subtotal + element;
                });
    }


    private List<Integer> generateRandomCrListFromBudget(Integer budget) {

        Map<Integer, Integer> xpByCRTable = csvParserService.getXpByCrTable();
        if (xpByCRTable == null || xpByCRTable.isEmpty()) {
            return null;
        }

        // TODO: reverse the table for more efficient code

        List<Integer> selectedCrs = new ArrayList<>();
        int remainingBudget = budget;

        // find the random var range based on the budget > CR

        // generate a random CR between 1 and random range add to selected

        // subtract that CR from budget and repeat until budget is < 200 (CR 1)

        // repeat random CR




        return selectedCrs;
    }
}
