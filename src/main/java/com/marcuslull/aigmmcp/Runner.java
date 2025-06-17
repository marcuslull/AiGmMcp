package com.marcuslull.aigmmcp;

import com.marcuslull.aigmmcp.data.csv.CsvParserService;
import com.marcuslull.aigmmcp.data.structured.repositories.Srd521MonsterCrRepository;
import com.marcuslull.aigmmcp.data.vector.VectorIngestion;
import com.marcuslull.aigmmcp.tools.diceroller.DiceRollerService;
import com.marcuslull.aigmmcp.tools.treasuregenerator.TreasureGeneratorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class Runner implements CommandLineRunner {

    private final VectorIngestion vectorIngestion;
    private final DiceRollerService diceRollerService;
    private final CsvParserService csvParserService;
    private final TreasureGeneratorService treasureGeneratorService;
    private final Srd521MonsterCrRepository srd521MonsterCrRepository;

    public Runner(VectorIngestion vectorIngestion, DiceRollerService diceRollerService, CsvParserService csvParserService, TreasureGeneratorService treasureGeneratorService, Srd521MonsterCrRepository srd521MonsterCrRepository) {
        this.vectorIngestion = vectorIngestion;
        this.diceRollerService = diceRollerService;
        this.csvParserService = csvParserService;
        this.treasureGeneratorService = treasureGeneratorService;
        this.srd521MonsterCrRepository = srd521MonsterCrRepository;
    }

    @Override
    public void run(String... args) throws Exception {
//        vectorIngestion.ingest("SRD_CC_v5.2.1.pdf", 0, "officialRules");

//        diceRollerService.rollDice(new DiceRoll(DiceType.D4, 4));
//        treasureGeneratorService.generateTreasure(new TreasureGeneratorQuery(11, TreasureDisposition.TROVE));
//        treasureGeneratorService.generateTreasure(new TreasureGeneratorQuery(11, TreasureDisposition.TROVE));

//        List<Srd521MonsterCr> srd521MonsterCrs = srd521MonsterCrRepository.findAllByCr(1);
//        System.out.println("srd521MonsterCrs = " + srd521MonsterCrs);

        Map<Integer, Integer> xpByCrTable = csvParserService.getXpByCrTable();
        System.out.println("xpByCrTable = " + xpByCrTable);
        Map<Integer, List<String>> xpBudgetPerCharTable = csvParserService.getXpBudgetPerCharTable();
        System.out.println("xpBudgetPerCharTable = " + xpBudgetPerCharTable);
    }
}
