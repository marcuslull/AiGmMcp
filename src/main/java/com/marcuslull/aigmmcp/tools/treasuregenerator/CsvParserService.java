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
