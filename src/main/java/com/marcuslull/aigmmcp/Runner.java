package com.marcuslull.aigmmcp;

import com.marcuslull.aigmmcp.data.csv.CsvParserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Profile("!pack")
public class Runner implements CommandLineRunner {

    private final CsvParserService csvParserService;

    public Runner(CsvParserService csvParserService) {
        this.csvParserService = csvParserService;
    }

    @Override
    public void run(String... args) throws Exception {
        Map<Integer, List<String>> treasureTable = csvParserService.getTreasureTable();
        System.out.println("treasureTable = " + treasureTable);

        Map<Integer, List<String>> xpBudgetPerCharTable = csvParserService.getXpBudgetPerCharTable();
        System.out.println("xpBudgetPerCharTable = " + xpBudgetPerCharTable);

        Map<Integer, Integer> xpByCrTable = csvParserService.getXpByCrTable();
        System.out.println("xpByCrTable = " + xpByCrTable);
    }
}
