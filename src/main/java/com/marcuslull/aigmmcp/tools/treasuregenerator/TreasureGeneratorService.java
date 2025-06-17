package com.marcuslull.aigmmcp.tools.treasuregenerator;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TreasureGeneratorService {

    private final CsvParserService csvParserService;

    public TreasureGeneratorService(CsvParserService csvParserService) {
        this.csvParserService = csvParserService;
    }

    @Tool(name = "treasureGenerator", description = "Generate treasure based on PC level and treasure disposition")
    public TreasureGeneratorResult generateTreasure(TreasureGeneratorQuery treasureGeneratorQuery) {

        if (treasureGeneratorQuery == null || treasureGeneratorQuery.cr() < 1 || treasureGeneratorQuery.disposition() == null) {
            return new TreasureGeneratorResult(treasureGeneratorQuery, null, "TreasureGeneratorQuery has an invalid format");
        }

        // cr is bucketized in the treasure table
        int bucket;
        try {
            bucket = mapCrToBucket(treasureGeneratorQuery.cr());
        } catch (RuntimeException e) {
            return new TreasureGeneratorResult(treasureGeneratorQuery, null, e.getMessage());
        }

        Map<Integer, List<String>> treasureTable = csvParserService.getTreasureTable();
        if (treasureTable.isEmpty()) {
            return new TreasureGeneratorResult(treasureGeneratorQuery, null, "Error in parsing the treasure table");
        }

        // dispositions are ordered so use enum ordinal as index
        String result = treasureTable.get(bucket).get(treasureGeneratorQuery.disposition().ordinal());

        return new TreasureGeneratorResult(treasureGeneratorQuery, result, null);
    }


    private int mapCrToBucket(int cr) {

        if (cr < 1 || cr > 30) throw new RuntimeException("cr value must be 1-30");

        // cr is bucketized in the treasure table - 1,4,7,10,13,16,19,22,25,28
        int bucketIndex = (cr - 1) / 3; // normalize cr to zero-index then int division to get the bucket index
        return 1 + (3 * bucketIndex); // calculate and return the bucket value
    }
}
