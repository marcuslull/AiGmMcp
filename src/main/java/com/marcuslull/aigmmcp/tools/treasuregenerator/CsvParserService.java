package com.marcuslull.aigmmcp.tools.treasuregenerator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CsvParserService {

    private final Path TREASURE_TABLE_PATH = Paths.get("src/main/java/com/marcuslull/aigmmcp/tools/treasuregenerator/treasureTable.csv");

    private Map<Integer, List<String>> treasureTable;


    /**
     * Retrieves the treasure table, parsing it from a CSV file if not already loaded.
     * <p>
     * This method implements a lazy loading pattern. On the first call, it reads the
     * CSV file specified by {@code TREASURE_TABLE_PATH}, parses its content, and stores
     * it in an internal {@code treasureTable} map. Subsequent calls will return the cached map
     * directly without re-parsing the file.
     * <p>
     * The CSV file is expected to have a specific format:
     * <ul>
     *     <li>It must contain exactly 11 lines. The first line is assumed to be a header and is skipped.</li>
     *     <li>Each subsequent line (10 data lines) must contain 8 comma-separated values.</li>
     *     <li>The first value on each data line is an integer representing a "level".</li>
     *     <li>The remaining 7 values on each data line are strings representing "disposition".</li>
     * </ul>
     * If the file is not found, is malformed (e.g., incorrect number of lines or columns per line),
     * or any other {@link Exception} occurs during parsing, an error is logged, and this method
     * returns {@code null}. The table is only cached if parsing is entirely successful.
     *
     * @return A {@code Map<Integer, List<String>>} where the key is the treasure level (integer)
     *         and the value is a list of disposition strings. Returns {@code null} if the
     *         CSV file cannot be parsed successfully or if an error occurs.
     */
    public Map<Integer, List<String>> getTreasureTable() {

        if (treasureTable != null) return treasureTable;

        log.info("Parsing {}", TREASURE_TABLE_PATH);
        Map<Integer, List<String>> tempTreasureTable = new HashMap<>();

        try {
            List<String> allLines = Files.readAllLines(TREASURE_TABLE_PATH);

            if (allLines.size() != 11) {
                log.error("{} is malformed", TREASURE_TABLE_PATH);
                return null;
            }

            for (int i = 1; i < allLines.size(); i++) {
                String[] lineArray = allLines.get(i).split(",");

                if (lineArray.length != 8) {
                    log.error("{} is malformed", TREASURE_TABLE_PATH);
                    return null;
                }

                String level = lineArray[0];
                String[] disposition = Arrays.copyOfRange(lineArray, 1, lineArray.length);

                tempTreasureTable.put(Integer.valueOf(level), List.of(disposition));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

        this.treasureTable = tempTreasureTable; // only want to store it if it's a good parse
        log.info("Successfully parsed {}", TREASURE_TABLE_PATH);
        return this.treasureTable;
    }
}
