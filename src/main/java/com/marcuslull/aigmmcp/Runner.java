package com.marcuslull.aigmmcp;

import com.marcuslull.aigmmcp.tools.treasuregenerator.CsvParserService;
import com.marcuslull.aigmmcp.vector.VectorIngestion;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class Runner implements CommandLineRunner {

    private final VectorIngestion vectorIngestion;
    private final CsvParserService csvParserService;

    public Runner(VectorIngestion vectorIngestion, CsvParserService csvParserService) {
        this.vectorIngestion = vectorIngestion;
        this.csvParserService = csvParserService;
    }

    @Override
    public void run(String... args) throws Exception {
//        vectorIngestion.ingest("SRD_CC_v5.2.1.pdf", 0, "officialRules");

//        Map<Integer, List<String>> treasureTable = csvParserService.getTreasureTable();
//        System.out.println("treasureTable = " + treasureTable.get(1));
    }
}
