package com.marcuslull.aigmmcp;

import com.marcuslull.aigmmcp.vector.VectorIngestion;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class Runner implements CommandLineRunner {

    private final VectorIngestion vectorIngestion;

    public Runner(VectorIngestion vectorIngestion) {
        this.vectorIngestion = vectorIngestion;
    }

    @Override
    public void run(String... args) throws Exception {
//        vectorIngestion.ingest("SRD_CC_v5.2.1.pdf", 0, "officialRules");
    }
}
