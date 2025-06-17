package com.marcuslull.aigmmcp.data.vector;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
public class VectorIngestion {

    private final String PATH = "classpath:ingestion/";

    private final VectorStore vectorStore;
    private final ResourceLoader resourceLoader;

    @Autowired
    public VectorIngestion(VectorStore vectorStore, ResourceLoader resourceLoader) {
        this.vectorStore = vectorStore;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Ingests a document into the vector store.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Loads the specified document resource from the classpath.</li>
     *   <li>If the resource does not exist, an error is logged, and the method returns.</li>
     *   <li>Reads the document content using {@link TikaDocumentReader}.</li>
     *   <li>Splits the document content into manageable chunks using {@link TokenTextSplitter}.</li>
     *   <li>Creates a metadata map containing the document name, session number, and tag.</li>
     *   <li>If no documents are extracted after splitting, a warning is logged, and the method returns.</li>
     *   <li>Adds the custom metadata to each document chunk.</li>
     *   <li>Accepts the processed documents into the {@link VectorStore} for embedding and storage.</li>
     *   <li>Logs success or failure of the ingestion process.
     * </ol>
     *
     * @param documentName  The name of the document file to be ingested (e.g., "myDocument.pdf").
     *                      This file is expected to be in the "classpath:ingestion/" directory.
     * @param sessionNumber An integer representing a session number to be associated with the ingested document.
     * @param tag           A string tag to categorize or identify the ingested document.
     */
    public void ingest(String documentName, int sessionNumber, String tag) {

        log.info("Attempting to load resource from: {}", PATH + documentName);
        Resource resource = resourceLoader.getResource(PATH + documentName);
        if (!resource.exists()) {
            log.error("Ingestion resource not found: {}", PATH + documentName);
            return;
        }
        log.info("Successfully loaded resource: {}", PATH + documentName);

        TikaDocumentReader reader = new TikaDocumentReader(resource); // reads the document (resource) from original format
        TextSplitter textSplitter = new TokenTextSplitter(); // splits document into manageable chunks for the embedding model

        // the custom metadata we want to add to our embeddings
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", documentName);
        metadata.put("session", sessionNumber);
        metadata.put("tag", tag);

        List<Document> fromSplitter = textSplitter.apply(reader.get());
        if (fromSplitter.isEmpty()) {
            log.warn("No documents extracted by TikaDocumentReader from: {}", resource.getFilename());
            return;
        }

        log.info("Adding custom metadata to extracted resource: {}", resource.getFilename());
        List<Document> fromMetadataAdder = addCustomMetadata(metadata, fromSplitter); // adds our custom metadata

        try {
            vectorStore.accept(fromMetadataAdder); // saves our ingested document as vector embeddings to the vector db
            log.info("Successfully embedded resource: {}", PATH + documentName);
        } catch (Exception e) {
            log.error("Failed to ingest resource: {}", resource.getFilename(),  e);
        }
    }

    private List<Document> addCustomMetadata(Map<String, Object> metadata, List<Document> fromSplitter) {
        return fromSplitter.stream().map(document ->
                        new Document(document.getId(), Objects.requireNonNull(document.getText()), metadata))
                .collect(Collectors.toList());
    }
}
