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

    /**
     * Generates treasure based on a given Challenge Rating (CR) and treasure disposition.
     * This method is exposed as a tool that can be called by an AI model, as indicated by the {@code @Tool} annotation.
     * <p>
     * The method first validates the input {@link TreasureGeneratorQuery}. If the query is null,
     * if the CR is outside the valid range (1-30), or if the disposition is null,
     * it returns a {@link TreasureGeneratorResult} with an appropriate error message.
     * <p>
     * The CR from the query is then mapped to a specific "bucket" value, which corresponds
     * to how CRs are grouped in the underlying treasure table. If this mapping fails (e.g., due to an invalid CR
     * that somehow passed initial validation a {@link TreasureGeneratorResult} with an error is returned.
     * <p>
     * It then attempts to retrieve the treasure table using {@link CsvParserService#getTreasureTable()}.
     * If the treasure table cannot be loaded or is empty (e.g., due to a parsing error in the CSV file),
     * a {@link TreasureGeneratorResult} with an error message indicating an internal server error is returned.
     * <p>
     * Finally, it uses the CR bucket and the ordinal value of the {@link TreasureDisposition} enum
     * to look up the specific treasure string from the table.
     *
     * @param treasureGeneratorQuery An object encapsulating the treasure generation parameters:
     *                               the Challenge Rating (CR) of the encounter or party level,
     *                               and the desired {@link TreasureDisposition}.
     * @return A {@link TreasureGeneratorResult} object containing the original {@code treasureGeneratorQuery},
     *         the generated treasure string, and an optional error message.
     *         If the input is invalid or an internal error occurs (e.g., table parsing issues),
     *         the treasure string will be null, and an error message will be present.
     *         Otherwise, the treasure string will be populated, and the error message will be null.
     */
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
