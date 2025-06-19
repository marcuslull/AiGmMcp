package com.marcuslull.aigmmcp.tools.randomencountergenerator;

import com.marcuslull.aigmmcp.data.csv.CsvParserService;
import com.marcuslull.aigmmcp.data.structured.entities.Srd521MonsterCr;
import com.marcuslull.aigmmcp.data.structured.repositories.Srd521MonsterCrRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RandomEncounterGeneratorService {

    private final CsvParserService csvParserService;
    private final Srd521MonsterCrRepository srd521MonsterCrRepository;
    private final Random random;

    private static final int MIN_MONSTER_XP_THRESHOLD = 200;


    public RandomEncounterGeneratorService(CsvParserService csvParserService, Srd521MonsterCrRepository srd521MonsterCrRepository, Random random) {
        this.csvParserService = csvParserService;
        this.srd521MonsterCrRepository = srd521MonsterCrRepository;
        this.random = random;
    }


    /**
     * Generates a random monster encounter based on the provided query, which includes
     * a list of player character (PC) levels and a desired encounter difficulty.
     * <p>
     * This method serves as a tool for an AI model, identified by the {@code @Tool} annotation,
     * allowing the AI to request random encounter generation.
     * <p>
     * The process involves:
     * <ol>
     *     <li>Validating the input query (PC levels between 1-20, valid difficulty).</li>
     *     <li>Calculating the total experience point (XP) budget for the encounter based on PC levels and difficulty.</li>
     *     <li>Generating a list of monster Challenge Ratings (CRs) that fit within the calculated budget.</li>
     *     <li>Fetching a list of monster names for each unique CR generated.</li>
     * </ol>
     * If any step fails (e.g., invalid input, internal error during budget calculation or monster selection),
     * an {@link EncounterGenerationResult} with an appropriate error message is returned.
     *
     * @param encounterGenerationQuery The query object containing a list of PC levels and the desired encounter difficulty.
     *                                 PC levels must be between 1 and 20. Difficulty can be Low (L), Moderate (M), or Hard (H).
     * @return An {@link EncounterGenerationResult} containing the original query, the calculated total party budget,
     *         a list of generated monster CRs, a map of CRs to lists of monster names, and an error message if applicable.
     *         If successful, the error field will be null.
     */
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
        if (budget < MIN_MONSTER_XP_THRESHOLD) {
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
        Map<Integer, List<String>> monsterList = getMonsterListsByCr(crList);
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
        if (query == null || query.pcs() == null || query.pcs().isEmpty() || query.difficulty() == null) return false;

        // PC range is 1-20
        return query.pcs().stream().allMatch(l -> l >= 1 && l <= 20);
    }


    private int calculateEncounterBudget(EncounterGenerationQuery encounterGenerationQuery) {

        // get our lookup table
        Map<Integer, List<String>> xpBudgetPerCharTable = csvParserService.getXpBudgetPerCharTable();
        if (xpBudgetPerCharTable == null || xpBudgetPerCharTable.isEmpty()) {
            log.error("XP Budget Per Character table is null or empty. Check the CSV");
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
                    catch (Exception e) {
                        log.warn("ParseInt, no budget found for pc level, or enum ordinal out of range");
                        return -1; // bail out to the method
                    }
                    return xp;
                })
                // this is essentially a .sum() but if it encounters a -1 from above it will short-circuit.
                .reduce(0, (subtotal, element) -> {
                    if (subtotal == -1 ||element == -1) return -1; // any -1s need to trigger bail out
                    return subtotal + element;
                });
    }


    private List<Integer> generateRandomCrListFromBudget(Integer budget) {

        // get the table
        Map<Integer, Integer> xpByCRTable = csvParserService.getXpByCrTable();
        if (xpByCRTable == null || xpByCRTable.isEmpty()) {
            return null;
        }

        List<Integer> selectedCrs = new ArrayList<>();
        int remainingBudget = budget;

        // might want to switch to greedy after first random pick to minimize monster quantity
        while (remainingBudget >= MIN_MONSTER_XP_THRESHOLD) {

            // find the random upper bounds such that the CR has a value that is close but not over the remaining budget
            int currentBudgetForLambda = remainingBudget;

            // this assumes the table is complete - no missing CRs - perhaps add a check for this?
            int randomUpperBounds = xpByCRTable.entrySet()
                    .stream()
                    // we only want entries that have a value lower than our remaining budget
                    .filter(e -> e.getValue() <= currentBudgetForLambda)
                    // now we can select the biggest key from the set of lower than remaining budget values
                    .map(Map.Entry::getKey)
                    .max(Comparator.naturalOrder()).orElse(0);

            // generate a random CR between 1 and random range add to selected
            if (randomUpperBounds == 0) break; // shouldn't happen but just incase
            int randomCr = random.nextInt(1,randomUpperBounds + 1);
            selectedCrs.add(randomCr);

            // subtract that CR value from budget and repeat until remaining budget is < 200 (CR 1)
            remainingBudget -= xpByCRTable.get(randomCr);
        }

        return selectedCrs;
    }


    private Map<Integer, List<String>> getMonsterListsByCr(List<Integer> crList) {

        Map<Integer, List<String>> monsterMap = new HashMap<>();
        Set<Integer> uniqueCrs = new HashSet<>(crList); // dont need duplicate CR lists

        // fetch the entries from the db
        for (int cr : uniqueCrs) {
            // could reduce DB calls with a custom findAllByCrIn(<List of CRs>)
            List<Srd521MonsterCr> monsters = srd521MonsterCrRepository.findAllByCr(cr);
            List<String> monsterNames = monsters.stream().map(Srd521MonsterCr::name).toList();

            monsterMap.put(cr, monsterNames);
        }

        return monsterMap;
    }
}
