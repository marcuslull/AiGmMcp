package com.marcuslull.aigmmcp;

import com.marcuslull.aigmmcp.data.csv.CsvParserService;
import com.marcuslull.aigmmcp.data.vector.VectorIngestion;
import com.marcuslull.aigmmcp.resources.vectordb.VectorDbResourceService;
import io.modelcontextprotocol.server.McpServerFeatures;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!pack")
public class Runner implements CommandLineRunner {

    private final CsvParserService csvParserService;
    private final VectorIngestion vectorIngestion;
    private final VectorDbResourceService vectorDbResourceService;

    public Runner(CsvParserService csvParserService, VectorIngestion vectorIngestion, VectorDbResourceService vectorDbResourceService) {
        this.csvParserService = csvParserService;
        this.vectorIngestion = vectorIngestion;
        this.vectorDbResourceService = vectorDbResourceService;
    }

    @Override
    public void run(String... args) {

//        vectorIngestion.ingest("SRD_CC_v5.2.1.pdf", 0, "Official_Rules");

//        Map<Integer, List<String>> treasureTable = csvParserService.getTreasureTable();
//        System.out.println("treasureTable = " + treasureTable);
//
//        Map<Integer, List<String>> xpBudgetPerCharTable = csvParserService.getXpBudgetPerCharTable();
//        System.out.println("xpBudgetPerCharTable = " + xpBudgetPerCharTable);
//
//        Map<Integer, Integer> xpByCrTable = csvParserService.getXpByCrTable();
//        System.out.println("xpByCrTable = " + xpByCrTable);

//        McpServerFeatures.SyncResourceSpecification resourceSpecification = vectorDbResourceService.getResourceSpecification();
    }
}
