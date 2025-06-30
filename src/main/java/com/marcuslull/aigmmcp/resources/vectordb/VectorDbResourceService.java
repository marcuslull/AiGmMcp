package com.marcuslull.aigmmcp.resources.vectordb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcuslull.aigmmcp.data.vector.VectorQuery;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class VectorDbResourceService {

    private final String AVAILABLE_FILTERS = "[\"Session\", \"Source\", \"Tag\"]. Session - The game session number (0 is reserved for non session related content). Source - The publication title or document name. Tag - The friendly reference tag.";
    private final String TOOLS = "[\"VectorDbFilteredSearch\"]";
    private final String ACCESS = "Read-only";
    private final String EXAMPLE = """
                                       {
                                         "source": null,
                                         "session": null,
                                         "tag": "Official_Rules",
                                         "search": "Initiative"
                                       }
                                   """;

    private final VectorQuery vectorQuery;
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

        log.info("Creating resource specification");

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
                log.warn("JsonParsingError while serializing resource details: ()", e);
                jsonContent = "{\"error\":\"Unexpected Error - JsonParsingError while serializing resource details. Please try again later.\"}";
            }

            // combine into a full returnable resource specification
            McpSchema.ReadResourceResult readResourceResult = new McpSchema.ReadResourceResult(
                    List.of(new McpSchema.TextResourceContents(request.uri(), "application/json", jsonContent)));

            log.info("Resource specification created successful - Result: {}", readResourceResult);
            return readResourceResult;
        });
    }


    /**
     * Assembles a map containing the current state and metadata of the vector database.
     * <p>
     * This method gathers both static information (like access rights and available filters) and dynamic data
     * by querying the vector store for the current set of publications, tags, and sessions. The resulting map
     * provides a comprehensive overview of the vector database's contents and capabilities, which can be
     * exposed as a resource or used by other tools to inform an AI model.
     *
     * @return A {@link Map} where keys are descriptive strings and values are the corresponding details.
     *         The map includes:
     *         <ul>
     *             <li>"Access": The access level for the database (e.g., "Read-only").</li>
     *             <li>"Associated Tools": A description of tools that interact with this resource.</li>
     *             <li>"Example Query": An example of how to query the vector store.</li>
     *             <li>"Available Filters": A description of the metadata filters that can be applied.</li>
     *             <li>"Current Embedded Publications": A set of all unique publication sources.</li>
     *             <li>"Current Embedded Tags": A set of all unique tags.</li>
     *             <li>"Current Embedded Sessions": A set of all unique session identifiers.</li>
     *         </ul>
     * @see com.marcuslull.aigmmcp.data.vector.VectorQuery#getCurrentPublications()
     * @see com.marcuslull.aigmmcp.data.vector.VectorQuery#getCurrentTags()
     * @see com.marcuslull.aigmmcp.data.vector.VectorQuery#getCurrentSessions()
     */
    public Map<String, Object> buildResourceDetails() {
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
