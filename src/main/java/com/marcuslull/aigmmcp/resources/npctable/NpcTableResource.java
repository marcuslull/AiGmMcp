package com.marcuslull.aigmmcp.resources.npctable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcuslull.aigmmcp.data.structured.repositories.NpcRepository;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NpcTableResource {

    private final String FIELDS = "[\"id\", \"name\", \"race\", \"sex\", \"age\", \"description\", \"personality\", \"background\", \"npc_class\", \"level\", \"status\", \"location\", \"notes\"]";
    private final String TOOLS = "";
    private final String ACCESS = "[\"read\", \"write\"]";
    private final String EXAMPLE =  """
                                    
                                    """;

    private final NpcRepository npcRepository;
    private final ObjectMapper objectMapper;

    public NpcTableResource(NpcRepository npcRepository, ObjectMapper objectMapper) {
        this.npcRepository = npcRepository;
        this.objectMapper = objectMapper;
    }


    public McpServerFeatures.SyncResourceSpecification getResourceSpecification() {

        log.info("Creating resource specification");

        // resource meta data
        McpSchema.Resource resourceMeta = new McpSchema.Resource(
                "/resources/npcTable",
                "npcTable",
                "Table for NPC information",
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


    public Map<String, Object> buildResourceDetails() {
        return Map.of(
                "Fields", FIELDS,
                "Access", ACCESS,
                "Associated Tools", TOOLS,
                "Example Query", EXAMPLE
        );
    }
}
