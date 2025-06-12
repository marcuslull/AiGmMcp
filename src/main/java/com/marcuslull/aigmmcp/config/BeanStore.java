package com.marcuslull.aigmmcp.config;

import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
