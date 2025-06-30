package com.marcuslull.aigmmcp.tools.unstructuredsearch;

import com.marcuslull.aigmmcp.data.vector.VectorQuery;
import com.marcuslull.aigmmcp.resources.vectordb.VectorDbResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class UnstructuredSearchService {

    private final VectorDbResourceService vectorDbResourceService;
    private final VectorQuery vectorQuery;

    public UnstructuredSearchService(VectorDbResourceService vectorDbResourceService, VectorQuery vectorQuery) {
        this.vectorDbResourceService = vectorDbResourceService;
        this.vectorQuery = vectorQuery;
    }


    /**
     * Retrieves the current state and metadata of the vector database.
     * <p>
     * This method is exposed as a tool for an AI model to query information about the vector database.
     * It serves as a workaround because the AI Development Kit (ADK) may not directly support
     * Model Context Protocol (MCP) resources. This tool provides the same information that is available
     * through the {@code /resources/vectorDb} MCP resource, making it accessible for AI-driven queries
     * about the database's contents and search capabilities.
     *
     * @return A {@link Map} containing details about the vector database, including available filters,
     *         access rights, and the current set of publications, tags, and sessions.
     * @see com.marcuslull.aigmmcp.resources.vectordb.VectorDbResourceService#buildResourceDetails()
     */
    @Tool(name = "VectorDbResource", description = "Get current information about the vector database, including available filters for searching.")
    public Map<String, Object> getCurrentVectorDBInformation() {

        log.info("VectorDbResource Tool invoked to get current Vector DB information");
        // This is a workaround tool because ADK does not support MCP resources
        return vectorDbResourceService.buildResourceDetails();
    }


    @Tool(name = "VectorDbFilteredSearch", description = "Perform a similarity search based on document metadata. Use 'VectorDbResource' tool for current DB info.")
    public VectorSearchResult vectorQuery(VectorSearchQuery vectorSearchQuery) {

        log.info("Vector search tool invoked: {}", vectorSearchQuery);
        VectorSearchResult result = vectorQuery.query(vectorSearchQuery);

        log.info("Vector search result: {}", result);
        return result;
    }
}
