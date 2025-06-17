package com.marcuslull.aigmmcp.tools.treasuregenerator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TreasureGeneratorService {

    private final CsvParserService csvParserService;

    public TreasureGeneratorService(CsvParserService csvParserService) {
        this.csvParserService = csvParserService;
    }

    @Tool(name = "treasureGenerator", description = "Generate treasure based on PC level and treasure disposition")
    public TreasureGeneratorResult generateTreasure(TreasureGeneratorQuery treasureGeneratorQuery) {

        // TODO logic - Jackson error message on conversion failure

        log.info("New treasure generator query: {}", treasureGeneratorQuery);

        if (treasureGeneratorQuery == null || treasureGeneratorQuery.cr() < 1 || treasureGeneratorQuery.cr() > 30 || treasureGeneratorQuery.disposition() == null) {
            log.warn("Treasure generator query is malformed: {}", treasureGeneratorQuery);
            return new TreasureGeneratorResult(treasureGeneratorQuery, null, "TreasureGeneratorQuery is malformed");
        }

        // cr is bucketized in the treasure table
        int bucket;
        try {
            bucket = mapCrToBucket(treasureGeneratorQuery.cr());
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
            return new TreasureGeneratorResult(treasureGeneratorQuery, null, e.getMessage());
        }

        Map<Integer, List<String>> treasureTable = csvParserService.getTreasureTable();
        if (treasureTable == null || treasureTable.isEmpty()) {
            log.error("Error in parsing the treasure table");
            return new TreasureGeneratorResult(treasureGeneratorQuery, null, "Internal error in parsing the treasure table. Generate your own treasure or try again later");
        }

        // dispositions are ordered so use enum ordinal as index
        String result = treasureTable.get(bucket).get(treasureGeneratorQuery.disposition().ordinal());

        log.info("Treasure generation completed successfully: {}", result);
        return new TreasureGeneratorResult(treasureGeneratorQuery, result, null);
    }


    private int mapCrToBucket(int cr) {

        // cr is bucketized in the treasure table - 1,4,7,10,13,16,19,22,25,28
        int bucketIndex = (cr - 1) / 3; // normalize cr to zero-index then int division to get the bucket index
        return 1 + (3 * bucketIndex); // calculate and return the bucket value
    }
}
