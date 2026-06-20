CREATE TABLE IF NOT EXISTS users (
    id        BIGSERIAL PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    email     VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS tours (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users(id),
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    from_location   VARCHAR(200) NOT NULL,
    to_location     VARCHAR(200) NOT NULL,
    transport_type  VARCHAR(50)  NOT NULL,
    distance_km     DOUBLE PRECISION,
    estimated_time  INTEGER,  -- minutes
    map_image_path  VARCHAR(500),
    created_at      TIMESTAMP DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS tour_logs (
    id              BIGSERIAL PRIMARY KEY,
    tour_id         BIGINT       NOT NULL REFERENCES tours(id) ON DELETE CASCADE,
    log_datetime    TIMESTAMP    NOT NULL,
    comment         TEXT,
    difficulty      INTEGER      CHECK (difficulty BETWEEN 1 AND 5),
    total_distance  DOUBLE PRECISION,
    total_time      INTEGER,  -- minutes
    rating          INTEGER      CHECK (rating BETWEEN 1 AND 5),
    created_at      TIMESTAMP DEFAULT NOW()
    );