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
        Map<Integer, List<String>> treasureTable = csvParserService.getTreasureTable();
        return new TreasureGeneratorResult(treasureGeneratorQuery, "placeholder", null);
    }
}
