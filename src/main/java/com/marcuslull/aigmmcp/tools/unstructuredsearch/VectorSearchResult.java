package com.marcuslull.aigmmcp.tools.unstructuredsearch;

import org.springframework.ai.document.Document;

import java.util.List;

public record VectorSearchResult(
        VectorSearchQuery query,
        List<Document> results,
        String error
) {
}
