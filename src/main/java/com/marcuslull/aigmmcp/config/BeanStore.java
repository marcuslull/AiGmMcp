package com.marcuslull.aigmmcp.config;

import com.marcuslull.aigmmcp.resources.vectordb.VectorDbResourceService;
import com.marcuslull.aigmmcp.tools.diceroller.DiceRollerService;
import com.marcuslull.aigmmcp.tools.randomencountergenerator.RandomEncounterGeneratorService;
import com.marcuslull.aigmmcp.tools.treasuregenerator.TreasureGeneratorService;
import com.marcuslull.aigmmcp.tools.unstructuredsearch.UnstructuredSearchService;
import io.modelcontextprotocol.server.McpServerFeatures;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

@Configuration
public class BeanStore {


    /**
     * Creates and configures a {@link VertexAiTextEmbeddingModel} bean.
     * <p>
     * This bean is responsible for providing text embedding capabilities using Google's Vertex AI.
     * It is configured with specific project and location details for the Vertex AI service,
     * and uses the default embedding model.
     * </p>
     *
     * @return A configured {@link VertexAiTextEmbeddingModel} instance.
     */
    @Bean
    public VertexAiTextEmbeddingModel vertexAiTextEmbeddingModel() {

        // TODO: ENV file
        VertexAiEmbeddingConnectionDetails connectionDetails = VertexAiEmbeddingConnectionDetails
                .builder()
                .projectId("aigm-451721")
                .location("us-central1").build();

        VertexAiTextEmbeddingOptions options = VertexAiTextEmbeddingOptions
                .builder()
                .model(VertexAiTextEmbeddingOptions.DEFAULT_MODEL_NAME)
                .build();

        return new VertexAiTextEmbeddingModel(connectionDetails, options);
    }

    /**
     * Provides a {@link ToolCallbackProvider} bean that aggregates various service tools.
     * <p>
     * This bean is responsible for making different service functionalities available as tools
     * that can be invoked by the AI model.
     * </p>
     * @return A configured {@link ToolCallbackProvider} instance.
     */
    @Bean
    public ToolCallbackProvider tools(
            DiceRollerService diceRollerService,
            TreasureGeneratorService treasureGeneratorService,
            RandomEncounterGeneratorService randomEncounterGeneratorService,
            UnstructuredSearchService unstructuredSearchService
    ) {
        return MethodToolCallbackProvider.builder().toolObjects(diceRollerService, treasureGeneratorService, randomEncounterGeneratorService, unstructuredSearchService).build();
    }

    /**
     * Provides a list of {@link McpServerFeatures.SyncResourceSpecification} beans.
     * <p>
     * This bean is responsible for defining the resources that the Model Context Protocol (MCP) server
     * will expose. Each resource specification includes metadata about the resource (e.g., name, version,
     * input mimetype) and the logic for handling read requests to that resource.
     * </p>
     *
     * @return A list of configured {@link McpServerFeatures.SyncResourceSpecification} instances.
     */
    @Bean
    public List<McpServerFeatures.SyncResourceSpecification> resources(VectorDbResourceService vectorDbResourceService) {
        McpServerFeatures.SyncResourceSpecification specification = vectorDbResourceService.getResourceSpecification();
        return List.of(specification);
    }

    @Bean
    public Random random() {
        return new Random();
    }
}
