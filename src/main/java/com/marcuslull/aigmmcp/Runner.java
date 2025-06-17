package com.marcuslull.aigmmcp;

import com.marcuslull.aigmmcp.tools.diceroller.DiceRollerService;
import com.marcuslull.aigmmcp.tools.treasuregenerator.TreasureGeneratorService;
import com.marcuslull.aigmmcp.data.vector.VectorIngestion;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class Runner implements CommandLineRunner {

    private final VectorIngestion vectorIngestion;
    private final DiceRollerService diceRollerService;
    private final TreasureGeneratorService treasureGeneratorService;

    public Runner(VectorIngestion vectorIngestion, DiceRollerService diceRollerService, TreasureGeneratorService treasureGeneratorService) {
        this.vectorIngestion = vectorIngestion;
        this.diceRollerService = diceRollerService;
        this.treasureGeneratorService = treasureGeneratorService;
    }

    @Override
    public void run(String... args) throws Exception {
//        vectorIngestion.ingest("SRD_CC_v5.2.1.pdf", 0, "officialRules");

//        diceRollerService.rollDice(new DiceRoll(DiceType.D4, 4));
//        treasureGeneratorService.generateTreasure(new TreasureGeneratorQuery(11, TreasureDisposition.TROVE));
//        treasureGeneratorService.generateTreasure(new TreasureGeneratorQuery(11, TreasureDisposition.TROVE));
    }
}
