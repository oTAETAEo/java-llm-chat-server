-- Enable application-level encryption for sensitive workout data.
--
-- Assumption:
-- - Production has no workout data yet.
--
-- Application behavior after this migration:
-- - Sensitive workout metric entity fields remain Double/Integer in Java.
-- - JPA AttributeConverters store those values as AES-256-GCM encrypted text.
-- - Sensor sample JSON is stored in samples_encrypted as AES-256-GCM encrypted text.
-- - Query-critical metadata stays plaintext: started_at, ended_at, distance,
--   moving_time, workout type, tier, input source, title, feedback count.
--
-- Key handling:
-- - Set WORKOUT_DATA_ENCRYPTION_KEY_BASE64 to a Base64-encoded 32-byte key in production.
-- - Do not store encryption keys in SQL, DB rows, source code, or logs.

BEGIN;

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

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'running_workout_sensor_data'
          AND column_name = 'samples_json'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'running_workout_sensor_data'
          AND column_name = 'samples_encrypted'
    ) THEN
        ALTER TABLE running_workout_sensor_data
            RENAME COLUMN samples_json TO samples_encrypted;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'cycling_workout_sensor_data'
          AND column_name = 'samples_json'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'cycling_workout_sensor_data'
          AND column_name = 'samples_encrypted'
    ) THEN
        ALTER TABLE cycling_workout_sensor_data
            RENAME COLUMN samples_json TO samples_encrypted;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.running_workout_sensor_data') IS NOT NULL THEN
        ALTER TABLE running_workout_sensor_data
            ADD COLUMN IF NOT EXISTS samples_encrypted text;
        ALTER TABLE running_workout_sensor_data
            ALTER COLUMN samples_encrypted TYPE text USING samples_encrypted::text,
            ALTER COLUMN samples_encrypted SET NOT NULL;
        COMMENT ON COLUMN running_workout_sensor_data.samples_encrypted
            IS 'AES-256-GCM encrypted sensor samples JSON envelope';
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.cycling_workout_sensor_data') IS NOT NULL THEN
        ALTER TABLE cycling_workout_sensor_data
            ADD COLUMN IF NOT EXISTS samples_encrypted text;
        ALTER TABLE cycling_workout_sensor_data
            ALTER COLUMN samples_encrypted TYPE text USING samples_encrypted::text,
            ALTER COLUMN samples_encrypted SET NOT NULL;
        COMMENT ON COLUMN cycling_workout_sensor_data.samples_encrypted
            IS 'AES-256-GCM encrypted sensor samples JSON envelope';
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.running_workout') IS NOT NULL THEN
        ALTER TABLE running_workout
            ALTER COLUMN calories TYPE text USING calories::text,
            ALTER COLUMN avg_cadence TYPE text USING avg_cadence::text,
            ALTER COLUMN max_cadence TYPE text USING max_cadence::text,
            ALTER COLUMN max_heart_rate TYPE text USING max_heart_rate::text,
            ALTER COLUMN avg_heart_rate TYPE text USING avg_heart_rate::text,
            ALTER COLUMN avg_pace TYPE text USING avg_pace::text,
            ALTER COLUMN max_pace TYPE text USING max_pace::text,
            ALTER COLUMN steps TYPE text USING steps::text;

        COMMENT ON COLUMN running_workout.calories IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN running_workout.avg_cadence IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN running_workout.max_cadence IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN running_workout.max_heart_rate IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN running_workout.avg_heart_rate IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN running_workout.avg_pace IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN running_workout.max_pace IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN running_workout.steps IS 'AES-256-GCM encrypted Integer';
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.cycling_workout') IS NOT NULL THEN
        ALTER TABLE cycling_workout
            ALTER COLUMN calories TYPE text USING calories::text,
            ALTER COLUMN avg_cadence TYPE text USING avg_cadence::text,
            ALTER COLUMN max_cadence TYPE text USING max_cadence::text,
            ALTER COLUMN max_heart_rate TYPE text USING max_heart_rate::text,
            ALTER COLUMN avg_heart_rate TYPE text USING avg_heart_rate::text,
            ALTER COLUMN avg_speed TYPE text USING avg_speed::text,
            ALTER COLUMN max_speed TYPE text USING max_speed::text,
            ALTER COLUMN avg_power TYPE text USING avg_power::text,
            ALTER COLUMN max_power TYPE text USING max_power::text,
            ALTER COLUMN ftp TYPE text USING ftp::text;

        COMMENT ON COLUMN cycling_workout.calories IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN cycling_workout.avg_cadence IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN cycling_workout.max_cadence IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN cycling_workout.max_heart_rate IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN cycling_workout.avg_heart_rate IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN cycling_workout.avg_speed IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN cycling_workout.max_speed IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN cycling_workout.avg_power IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN cycling_workout.max_power IS 'AES-256-GCM encrypted Double';
        COMMENT ON COLUMN cycling_workout.ftp IS 'AES-256-GCM encrypted Double';
    END IF;
END $$;

COMMIT;
