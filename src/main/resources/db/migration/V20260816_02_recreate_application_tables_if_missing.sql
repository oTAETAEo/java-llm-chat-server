-- Recreate core application tables when the production schema was manually cleared
-- but Flyway history or other schema objects still remain.
--
-- This migration is intentionally idempotent. Hibernate may still refine indexes,
-- foreign keys, or column metadata after startup, but these CREATE TABLE statements
-- guarantee that the application has all required base tables again.

BEGIN;

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS members (
    id bigserial PRIMARY KEY,
    created_at timestamp,
    updated_at timestamp,
    deleted_at timestamp,
    email varchar(255) UNIQUE,
    password varchar(255),
    nickname varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS terms (
    id bigserial PRIMARY KEY,
    created_at timestamp,
    updated_at timestamp,
    deleted_at timestamp,
    type varchar(50) NOT NULL,
    code varchar(100) NOT NULL UNIQUE,
    title varchar(255) NOT NULL,
    version varchar(50) NOT NULL,
    content_url varchar(255) NOT NULL,
    content text,
    required boolean NOT NULL,
    active boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS member_terms_agreements (
    id bigserial PRIMARY KEY,
    created_at timestamp,
    updated_at timestamp,
    deleted_at timestamp,
    member_id bigint NOT NULL,
    terms_id bigint NOT NULL,
    terms_code varchar(100) NOT NULL,
    terms_type varchar(50),
    terms_title varchar(255),
    terms_version varchar(50) NOT NULL,
    terms_content_url varchar(255),
    terms_content text,
    terms_required boolean,
    CONSTRAINT uk_member_terms_agreement_member_terms UNIQUE (member_id, terms_id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id bigserial PRIMARY KEY,
    created_at timestamp,
    updated_at timestamp,
    deleted_at timestamp,
    member_id bigint NOT NULL,
    token varchar(255),
    expires_at timestamp,
    revoked boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS running_workout (
    id bigserial PRIMARY KEY,
    created_at timestamp,
    updated_at timestamp,
    deleted_at timestamp,
    member_id bigint NOT NULL,
    tier varchar(255) NOT NULL,
    title varchar(255) NOT NULL,
    input_source varchar(255) NOT NULL,
    feedback_count bigint NOT NULL DEFAULT 0,
    started_at timestamp,
    ended_at timestamp,
    distance double precision,
    elev_gain double precision,
    elevation_max double precision,
    moving_time integer,
    calories text,
    avg_cadence text,
    max_cadence text,
    max_heart_rate text,
    avg_heart_rate text,
    avg_pace text,
    max_pace text,
    steps text
);

CREATE TABLE IF NOT EXISTS cycling_workout (
    id bigserial PRIMARY KEY,
    created_at timestamp,
    updated_at timestamp,
    deleted_at timestamp,
    member_id bigint NOT NULL,
    tier varchar(255) NOT NULL,
    title varchar(255) NOT NULL,
    input_source varchar(255) NOT NULL,
    feedback_count bigint NOT NULL DEFAULT 0,
    started_at timestamp,
    ended_at timestamp,
    distance double precision,
    elev_gain double precision,
    elevation_max double precision,
    moving_time integer,
    calories text,
    avg_cadence text,
    max_cadence text,
    max_heart_rate text,
    avg_heart_rate text,
    avg_speed text,
    max_speed text,
    avg_power text,
    max_power text,
    ftp text
);

CREATE TABLE IF NOT EXISTS running_workout_sensor_data (
    id bigserial PRIMARY KEY,
    created_at timestamp,
    updated_at timestamp,
    deleted_at timestamp,
    running_workout_id bigint NOT NULL UNIQUE,
    samples_encrypted text NOT NULL
);

CREATE TABLE IF NOT EXISTS cycling_workout_sensor_data (
    id bigserial PRIMARY KEY,
    created_at timestamp,
    updated_at timestamp,
    deleted_at timestamp,
    cycling_workout_id bigint NOT NULL UNIQUE,
    samples_encrypted text NOT NULL
);

CREATE TABLE IF NOT EXISTS feedback_room (
    id uuid PRIMARY KEY,
    created_at timestamp,
    updated_at timestamp,
    deleted_at timestamp,
    member_id bigint NOT NULL,
    title varchar(255) NOT NULL,
    pinned boolean NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS feedback_message (
    id bigserial PRIMARY KEY,
    created_at timestamp,
    updated_at timestamp,
    deleted_at timestamp,
    room_id uuid NOT NULL,
    role varchar(255) NOT NULL,
    workout_type varchar(255),
    workout_id bigint,
    content text NOT NULL
);

CREATE TABLE IF NOT EXISTS feedback_room_workout (
    id bigserial PRIMARY KEY,
    created_at timestamp,
    updated_at timestamp,
    deleted_at timestamp,
    room_id uuid NOT NULL,
    workout_type varchar(255) NOT NULL,
    workout_id bigint NOT NULL
);

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

COMMIT;
