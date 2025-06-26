package com.marcuslull.aigmmcp.data.csv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Slf4j
@Service
public class CsvParserService {

    private static final String TREASURE_TABLE_NAME = "treasureTable.csv";
    private static final String XP_BUDGET_TABLE_NAME = "xpBudgetPerChar.csv";
    private static final String XP_CR_TABLE_NAME = "xpByCRTable.csv";

    private final String TREASURE_TABLE_PATH = "classpath:csvs/" + TREASURE_TABLE_NAME;
    private final String XP_BUDGET_PER_CHAR_TABLE_PATH = "classpath:csvs/" + XP_BUDGET_TABLE_NAME;
    private final String XP_BY_CR_TABLE_PATH = "classpath:csvs/"+ XP_CR_TABLE_NAME;

    private final ResourceLoader resourceLoader;

    // caching parsed tables for later use
    private final Map<String, Map<Integer, ?>> tableCache = new HashMap<>();


    public CsvParserService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    /**
     * Returns a cached map of the Treasure Table.
     * @return A map where the key is the Challenge Rating (CR) and the value is a list of treasure descriptions.
     */
    public Map<Integer, List<String>> getTreasureTable() {
        return getOrParseTable(TREASURE_TABLE_NAME, () -> parseTable(TREASURE_TABLE_PATH, 11, 8, this::parseStringListRow));
    }


    /**
     * Returns a cached map of the XP Budget Per Character Table.
     * @return A map where the key is the Character Level and the value is a list of XP budget strings.
     */
    public Map<Integer, List<String>> getXpBudgetPerCharTable() {
        return getOrParseTable(XP_BUDGET_TABLE_NAME, () -> parseTable(XP_BUDGET_PER_CHAR_TABLE_PATH, 21, 4, this::parseStringListRow));
    }


    /**
     * Returns a cached map of the XP by Challenge Rating (CR) Table.
     * @return A map where the key is the Challenge Rating (CR) and the value is the corresponding XP.
     */
    public Map<Integer, Integer> getXpByCrTable() {
        return getOrParseTable(XP_CR_TABLE_NAME, () -> parseTable(XP_BY_CR_TABLE_PATH, 31, 2, this::parseIntRow));
    }


    // Boo! my casting is safe!!!
    @SuppressWarnings("unchecked")
    private <V> Map<Integer, V> getOrParseTable(String tableName, Supplier<Map<Integer, V>> parsingFunction) {
        return (Map<Integer, V>) tableCache.computeIfAbsent(tableName, key -> parsingFunction.get());
    }


    private <V> Map<Integer, V> parseTable(String csvPath, int expectedLineCount, int expectedLineLength, BiConsumer<Map<Integer, V>, String[]> rowParser) {
        Resource resource = getResource(csvPath);
        if (resource == null) {
            return Collections.emptyMap();
        }

        log.info("Parsing {}", csvPath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            List<String> allLines = reader.lines().toList();

            if (allLines.size() != expectedLineCount) {
                log.error("Mismatched line count for {}. Expected: {}, Found: {}", csvPath, expectedLineCount, allLines.size());
                return Collections.emptyMap();
            }

            Map<Integer, V> resultMap = new HashMap<>();
            // Skip header row (i = 1)
            for (int i = 1; i < allLines.size(); i++) {
                String[] lineArray = allLines.get(i).split(",");
                if (lineArray.length != expectedLineLength) {
                    log.error("Mismatched line length for {} at line {}. Expected: {}, Found: {}", csvPath, i + 1, expectedLineLength, lineArray.length);
                    return Collections.emptyMap();
                }
                rowParser.accept(resultMap, lineArray);
            }
            log.info("Successfully parsed {}", csvPath);
            return resultMap;

        } catch (IOException e) {
            log.error("Unexpected error parsing: {}", csvPath, e);
            return Collections.emptyMap();
        }
    }


    private void parseIntRow(Map<Integer, Integer> map, String[] lineArray) {

        try {
            map.put(Integer.valueOf(lineArray[0]), Integer.valueOf(lineArray[1]));
        } catch (NumberFormatException e) {
            log.error("Could not parse integer from row: {}", Arrays.toString(lineArray), e);
        }
    }


    private void parseStringListRow(Map<Integer, List<String>> map, String[] lineArray) {

        try {
            String[] restOfRow = Arrays.copyOfRange(lineArray, 1, lineArray.length);
            map.put(Integer.valueOf(lineArray[0]), List.of(restOfRow));
        } catch (NumberFormatException e) {
            log.error("Could not parse integer key from row: {}", Arrays.toString(lineArray), e);
        }
    }


    private Resource getResource(String path) {

        log.info("Attempting to load resource from: {}", path);
        Resource resource = resourceLoader.getResource(path);

        if (!resource.exists()) {
            log.error("Ingestion resource not found: {}", path);
            return null;
        }

        log.info("Successfully loaded resource: {}", path);
        return resource;
    }
}

