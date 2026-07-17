CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS workout_vector_store (
    id uuid PRIMARY KEY,
    member_id bigint,
    workout_id bigint NOT NULL,
    workout_type varchar(30) NOT NULL,
    content text NOT NULL,
    metadata jsonb NOT NULL DEFAULT '{}'::jsonb,
    embedding vector(1536) NOT NULL,
    created_at timestamp NOT NULL
);
