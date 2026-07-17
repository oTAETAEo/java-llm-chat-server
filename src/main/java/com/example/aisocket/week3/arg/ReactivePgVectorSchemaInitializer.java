package com.example.aisocket.week3.arg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReactivePgVectorSchemaInitializer {

    private final JdbcTemplate jdbcTemplate;
    private final boolean initializeSchema;
    private final String tableName;
    private final int dimensions;

    public ReactivePgVectorSchemaInitializer(
            JdbcTemplate jdbcTemplate,
            @Value("${app.vector-store.initialize-schema:true}") boolean initializeSchema,
            @Value("${app.vector-store.table-name:vector_store}") String tableName,
            @Value("${app.vector-store.dimensions:1536}") int dimensions
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.initializeSchema = initializeSchema;
        this.tableName = tableName;
        this.dimensions = dimensions;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        if (!initializeSchema) {
            return;
        }

        try {
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS public.%s (
                        id uuid PRIMARY KEY,
                        content text NOT NULL,
                        metadata jsonb NOT NULL DEFAULT '{}'::jsonb,
                        embedding vector(%d) NOT NULL
                    )
                    """.formatted(tableName, dimensions));
            log.info("pgvector schema initialized. table={}", tableName);
        } catch (Exception error) {
            log.error("pgvector schema initialization failed", error);
        }
    }
}
