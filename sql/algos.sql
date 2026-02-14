-- Ensure scenario exists when this script runs before Flyway migrations
CREATE TABLE IF NOT EXISTS scenario (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    data_json TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

-- Algorithm run metadata
CREATE TABLE IF NOT EXISTS algorithm_run (
    id BIGSERIAL PRIMARY KEY,
    scenario_id BIGINT REFERENCES scenario(id) ON DELETE SET NULL,
    algorithm VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ended_at TIMESTAMPTZ,
    found BOOLEAN,
    total_steps INT,
    metadata_json JSONB NOT NULL DEFAULT '{}'::jsonb
);

-- Step-by-step state snapshots used by visualizers
CREATE TABLE IF NOT EXISTS algorithm_step (
    id BIGSERIAL PRIMARY KEY,
    run_id BIGINT NOT NULL REFERENCES algorithm_run(id) ON DELETE CASCADE,
    step_index INT NOT NULL,
    current_json JSONB,
    visited_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    frontier_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    finished BOOLEAN NOT NULL DEFAULT FALSE,
    found BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (run_id, step_index)
);

CREATE INDEX IF NOT EXISTS idx_algorithm_run_started_at ON algorithm_run(started_at DESC);
CREATE INDEX IF NOT EXISTS idx_algorithm_run_algorithm ON algorithm_run(algorithm);
CREATE INDEX IF NOT EXISTS idx_algorithm_step_run_id ON algorithm_step(run_id);
CREATE INDEX IF NOT EXISTS idx_algorithm_step_run_step ON algorithm_step(run_id, step_index);
