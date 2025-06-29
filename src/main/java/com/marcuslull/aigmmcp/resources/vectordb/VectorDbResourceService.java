package com.marcuslull.aigmmcp.resources.vectordb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcuslull.aigmmcp.data.vector.VectorQuery;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VectorDbResourceService {

    private final String AVAILABLE_FILTERS = "[Session, Source, Tag]. Session - The game session number (0 is reserved for non session related content). Source - The publication title or document name. Tag - The friendly reference tag.";
    private final String TOOLS = ""; // TODO: need to design the tool then populate this
    private final String EXAMPLE = ""; // TODO: need to design the tool then populate this
    private final String ACCESS = "Read-only";

    private final VectorQuery vectorQuery;
    private final Logger logger = LoggerFactory.getLogger(VectorDbResourceService.class);
    private final ObjectMapper objectMapper;

    public VectorDbResourceService(VectorQuery vectorQuery, ObjectMapper objectMapper) {
        this.vectorQuery = vectorQuery;
        this.objectMapper = objectMapper;
    }


    /**
     * Constructs the resource specification for the vector database, compliant with the Model Context Protocol.
     * <p>
     * This method defines the metadata for the {@code /resources/vectorDb} resource, including its name,
     * description, and content type. It also provides the logic for handling read requests for this resource.
     * <p>
     * When a request is received, it dynamically builds a map of the vector database's current state by
     * calling {@link #buildResourceDetails()}. This map includes details such as access rights, available filters,
     * and the current set of publications, tags, and sessions present in the vector store.
     * <p>
     * The collected details are then serialized into a JSON string. If serialization fails, a JSON error
     * message is generated instead. The final JSON content is wrapped in an {@link McpSchema.ReadResourceResult}
     * and returned to the caller.
     *
     * @return A {@link McpServerFeatures.SyncResourceSpecification} containing the metadata and the handler
     *         for the vector database resource.
     * @see #buildResourceDetails()
     * @see com.marcuslull.aigmmcp.data.vector.VectorQuery
     */
    public McpServerFeatures.SyncResourceSpecification getResourceSpecification() {

        logger.info("Creating resource specification");

        // resource meta data
        McpSchema.Resource resourceMeta = new McpSchema.Resource(
                "/resources/vectorDb",
                "vectorDb",
                "Vector filtered search for game related unstructured documents and data",
                "application/json",
                null);

        // resource specific details
        return new McpServerFeatures.SyncResourceSpecification(resourceMeta, (exchange, request) -> {

            String jsonContent;
            Map<String, Object> resourceDetails = buildResourceDetails();

            try {
                jsonContent = objectMapper.writeValueAsString(resourceDetails);
            } catch (JsonProcessingException e) {
                logger.warn("JsonParsingError while serializing resource details: ()", e);
                jsonContent = "{\"error\":\"Unexpected Error - JsonParsingError while serializing resource details. Please try again later.\"}";
            }

            // combine into a full returnable resource specification
            McpSchema.ReadResourceResult readResourceResult = new McpSchema.ReadResourceResult(
                    List.of(new McpSchema.TextResourceContents(request.uri(), "application/json", jsonContent)));

            logger.info("Resource specification created successful - Result: {}", readResourceResult);
            return readResourceResult;
        });
    }


    private Map<String, Object> buildResourceDetails() {
        return Map.of(
                "Access", ACCESS,
                "Associated Tools", TOOLS,
                "Example Query", EXAMPLE,
                "Available Filters", AVAILABLE_FILTERS,
                "Current Embedded Publications", vectorQuery.getCurrentPublications(),
                "Current Embedded Tags", vectorQuery.getCurrentTags(),
                "Current Embedded Sessions", vectorQuery.getCurrentSessions()
        );
    }
}
