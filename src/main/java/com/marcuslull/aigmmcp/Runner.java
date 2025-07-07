package com.marcuslull.aigmmcp;

import com.marcuslull.aigmmcp.data.csv.CsvParserService;
import com.marcuslull.aigmmcp.data.vector.VectorIngestion;
import com.marcuslull.aigmmcp.resources.vectordb.VectorDbResource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!pack")
public class Runner implements CommandLineRunner {

    private final CsvParserService csvParserService;
    private final VectorIngestion vectorIngestion;
    private final VectorDbResource vectorDbResource;

    public Runner(CsvParserService csvParserService, VectorIngestion vectorIngestion, VectorDbResource vectorDbResource) {
        this.csvParserService = csvParserService;
        this.vectorIngestion = vectorIngestion;
        this.vectorDbResource = vectorDbResource;
    }

    @Override
    public void run(String... args) {

//        vectorIngestion.ingest("Curse_of_Strahd_Introductory_Adventure.pdf", 0, "Adventure");

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
