package com.marcuslull.aigmmcp.tools.randomencountergenerator;

import java.util.List;

public record EncounterGenerationQuery(
        List<Integer> pcs,
        EncounterDifficulty difficulty
) {
}
