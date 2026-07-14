package com.example.aisocket.week3.arg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ReactivePgVectorSchemaInitializer {

    private final DatabaseClient databaseClient;
    private final boolean initializeSchema;
    private final String tableName;
    private final int dimensions;

    public ReactivePgVectorSchemaInitializer(
            DatabaseClient databaseClient,
            @Value("${app.vector-store.initialize-schema:true}") boolean initializeSchema,
            @Value("${app.vector-store.table-name:vector_store}") String tableName,
            @Value("${app.vector-store.dimensions:1536}") int dimensions
    ) {
        this.databaseClient = databaseClient;
        this.initializeSchema = initializeSchema;
        this.tableName = tableName;
        this.dimensions = dimensions;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        if (!initializeSchema) {
            return;
        }

        databaseClient.sql("CREATE EXTENSION IF NOT EXISTS vector")
                .then()
                .then(databaseClient.sql("""
                        CREATE TABLE IF NOT EXISTS public.%s (
                            id uuid PRIMARY KEY,
                            content text NOT NULL,
                            metadata jsonb NOT NULL DEFAULT '{}'::jsonb,
                            embedding vector(%d) NOT NULL
                        )
                        """.formatted(tableName, dimensions)).then())
                .doOnSuccess(ignored -> log.info("Reactive pgvector schema initialized. table={}", tableName))
                .doOnError(error -> log.error("Reactive pgvector schema initialization failed", error))
                .subscribe();
    }
}
