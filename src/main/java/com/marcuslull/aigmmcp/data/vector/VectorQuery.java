package com.marcuslull.aigmmcp.data.vector;

import com.marcuslull.aigmmcp.tools.unstructuredsearch.VectorSearchQuery;
import com.marcuslull.aigmmcp.tools.unstructuredsearch.VectorSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class VectorQuery {

    private static final int DEFAULT_TOP_K = 4;

    private final JdbcTemplate jdbcTemplate;
    private final VectorStore vectorStore;


    public VectorQuery(JdbcTemplate jdbcTemplate, VectorStore vectorStore) {
        this.jdbcTemplate = jdbcTemplate;
        this.vectorStore = vectorStore;
    }


    /**
     * Retrieves a set of all unique publication sources currently in the vector store.
     * <p>
     * This method queries the vector store for all distinct values associated with the 'source' metadata key.
     * The 'source' typically represents the original document name or publication title.
     *
     * @return A {@link Set} of strings, where each string is a unique publication source. The set may be empty if no sources are found.
     * @see #getCurrentMetadataValues(String)
     */
    public Set<String> getCurrentPublications() {
        return getCurrentMetadataValues("source");
    }


    /**
     * Retrieves a set of all unique tags currently in the vector store.
     * <p>
     * This method queries the vector store for all distinct values associated with the 'tag' metadata key.
     * Tags are used to categorize or identify documents.
     *
     * @return A {@link Set} of strings, where each string is a unique tag. The set may be empty if no tags are found.
     * @see #getCurrentMetadataValues(String)
     */
    public Set<String> getCurrentTags() {
        return getCurrentMetadataValues("tag");
    }


    /**
     * Retrieves a set of all unique session identifiers currently in the vector store.
     * <p>
     * This method queries the vector store for all distinct values associated with the 'session' metadata key.
     * Sessions are typically represented by numbers, which are returned as strings in the set.
     *
     * @return A {@link Set} of strings, where each string is a unique session identifier. The set may be empty if no sessions are found.
     * @see #getCurrentMetadataValues(String)
     */
    public Set<String> getCurrentSessions() {
        return getCurrentMetadataValues("session");
    }


    /**
     * Performs a filtered similarity search in the vector store based on the provided query.
     * <p>
     * This method constructs a filter expression from the {@link VectorSearchQuery} object,
     * which can include criteria for 'source', 'session', and 'tag'. It then executes a
     * similarity search against the vector store using the search text from the query.
     * The search returns the top K results, where K is defined by {@code DEFAULT_TOP_K}.
     * If the search fails or returns a null list, a {@link VectorSearchResult} with an error message is returned.
     *
     * @param query The {@link VectorSearchQuery} containing the search text and metadata filters.
     * @return A {@link VectorSearchResult} containing the original query, a list of matching {@link org.springframework.ai.document.Document}s,
     *         and an error message if the search was unsuccessful.
     * @see #buildFilters(VectorSearchQuery)
     * @see VectorSearchQuery
     * @see VectorSearchResult
     * @see org.springframework.ai.vectorstore.VectorStore#similaritySearch(SearchRequest)
     */
    public VectorSearchResult query(VectorSearchQuery query) {

        log.info("Performing vector db filtered search for: {}", query);
        Filter.Expression filterExpression = buildFilters(query);

        SearchRequest searchRequest = SearchRequest.builder()
                        .topK(DEFAULT_TOP_K)
                        .filterExpression(filterExpression)
                        .query(query.search())
                        .build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);
        if (results == null) {
            log.error("Error during vector search for query: {}", query);
            return new VectorSearchResult(query, null, "Vector search failed - please try again later.");
        }

        log.info("Found {} results for query: {}", results.size(), query.search());
        return new VectorSearchResult(query, results, null);
    }

    private Filter.Expression buildFilters(VectorSearchQuery query) {

        // lets build a string representation of the query filters
        List<String> filters = new ArrayList<>();
        if (query.source() != null) {
            filters.add("source == '" + query.source() + "'");
        }
        if (query.session() != null) {
            filters.add("session == '" + query.session() + "'");
        }
        if (query.tag() != null) {
            filters.add("tag == '" + query.tag() + "'");
        }

        if (filters.isEmpty()) return null; // no need to join nothing

        String filterString = String.join(" || ", filters);
        log.debug("Constructed filter expression: {}", filterString);

        return new FilterExpressionTextParser().parse(filterString);
    }


    private Set<String> getCurrentMetadataValues(String metadataKey) {

        // SQL query for Vector DB metadata source, tag, or session
        String sql = String.format("SELECT DISTINCT (metadata ->> '%s') FROM vector_store WHERE (metadata ->> '%s') IS NOT NULL", metadataKey, metadataKey);

        log.info("Vector query for current {}: {}", metadataKey, sql);
        Set<String> results = new HashSet<>(jdbcTemplate.queryForList(sql, String.class));

        log.info("Vector result for current {}: {}", metadataKey, results);
        return results;
    }
}
