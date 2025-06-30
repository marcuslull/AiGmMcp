package com.marcuslull.aigmmcp.tools.unstructuredsearch;

public record VectorSearchQuery(
        String source,
        String session,
        String tag,
        String search
) {
}
