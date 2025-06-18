package com.marcuslull.aigmmcp.tools.randomencountergenerator;

import java.util.List;

public record EncounterGenerationResult(
        EncounterGenerationQuery encounterGenerationQuery,
        Integer totalPartyBudget,
        List<Integer> encounterCRs,
        List<String> monstersByCR,
        String error
) {
}
