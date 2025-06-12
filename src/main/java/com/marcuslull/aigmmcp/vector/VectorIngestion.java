package com.marcuslull.aigmmcp.vector;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class VectorIngestion {

    private final VectorStore vectorStore;

    @Value("classpath:ingestion/SRD_CC_v5.2.1.pdf")
    Resource resource;
    String source = "SRD_CC_v5.2.1.pdf";
    String session = "0";
    String tag = "officialRules";

    @Autowired
    public VectorIngestion(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingest() {

        // TODO: figure out what document reader to use
//        TikaDocumentReader reader = new TikaDocumentReader(resource);
//        TextSplitter textSplitter = new TokenTextSplitter();
//
//        Map<String, Object> metadata = new HashMap<>();
//        metadata.put("source", source);
//        metadata.put("session", session);
//        metadata.put("tag", tag);
//
//        List<Document> fromSplitter = textSplitter.apply(reader.get());
//        List<Document> fromMetadataAdder = addCustomMetadata(metadata, fromSplitter);
//
//        vectorStore.accept(fromMetadataAdder);
//        System.out.println("Vector store loaded.");
    }

    private List<Document> addCustomMetadata(Map<String, Object> metadata, List<Document> fromSplitter) {
        return fromSplitter.stream().map(document ->
                        new Document(document.getId(), Objects.requireNonNull(document.getText()), metadata))
                .collect(Collectors.toList());
    }
}
