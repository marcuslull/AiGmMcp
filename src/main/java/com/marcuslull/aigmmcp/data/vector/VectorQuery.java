package com.marcuslull.aigmmcp.data.vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class VectorQuery {

    Logger logger = LoggerFactory.getLogger(VectorQuery.class);
    private final JdbcTemplate jdbcTemplate;


    public VectorQuery(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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


    private Set<String> getCurrentMetadataValues(String metadataKey) {

        // SQL query for Vector DB metadata source, tag, or session
        String sql = String.format("SELECT DISTINCT (metadata ->> '%s') FROM vector_store WHERE (metadata ->> '%s') IS NOT NULL", metadataKey, metadataKey);

        logger.info("Vector query for current {}: {}", metadataKey, sql);
        Set<String> results = new HashSet<>(jdbcTemplate.queryForList(sql, String.class));

        logger.info("Vector result for current {}: {}", metadataKey, results);
        return results;
    }
}
