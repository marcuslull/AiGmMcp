package com.marcuslull.aigmmcp.tools.treasuregenerator;

public record TreasureGeneratorResult(
        TreasureGeneratorQuery query,
        String result,
        String error
) {
}
