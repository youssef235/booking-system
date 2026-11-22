CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE providers (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     BIGINT                   NOT NULL,
    name        VARCHAR(100)             NOT NULL,
    description VARCHAR(500),
    status      VARCHAR(20)              NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_providers_user UNIQUE (user_id),
    CONSTRAINT fk_providers_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_providers_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))
);

CREATE TABLE services (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    provider_id      BIGINT                   NOT NULL,
    name             VARCHAR(100)             NOT NULL,
    description      VARCHAR(500),
    price            NUMERIC(10, 2)           NOT NULL,
    duration_seconds BIGINT                   NOT NULL,
    active           BOOLEAN                  NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_services_provider FOREIGN KEY (provider_id) REFERENCES providers (id),
    CONSTRAINT ck_services_price CHECK (price >= 0),
    CONSTRAINT ck_services_duration CHECK (duration_seconds > 0)
);
CREATE INDEX idx_services_provider ON services (provider_id);

CREATE TABLE availabilities (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    provider_id BIGINT                   NOT NULL,
    start_time  TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time    TIMESTAMP WITH TIME ZONE NOT NULL,
    status      VARCHAR(20)              NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_availabilities_provider FOREIGN KEY (provider_id) REFERENCES providers (id),
    CONSTRAINT ck_availabilities_window CHECK (end_time > start_time),
    CONSTRAINT ck_availabilities_status CHECK (status IN ('OPEN', 'CLOSED'))
);
CREATE INDEX idx_availabilities_provider_start ON availabilities (provider_id, start_time);

CREATE TABLE bookings (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     BIGINT                   NOT NULL,
    service_id  BIGINT                   NOT NULL,
    provider_id BIGINT                   NOT NULL,
    start_time  TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time    TIMESTAMP WITH TIME ZONE NOT NULL,
    status      VARCHAR(20)              NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_bookings_service FOREIGN KEY (service_id) REFERENCES services (id),
    CONSTRAINT fk_bookings_provider FOREIGN KEY (provider_id) REFERENCES providers (id),
    CONSTRAINT ck_bookings_window CHECK (end_time > start_time),
    CONSTRAINT ck_bookings_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'REJECTED', 'COMPLETED')),
    CONSTRAINT ex_bookings_no_overlap EXCLUDE USING gist (
        provider_id WITH =,
        tstzrange(start_time, end_time, '[)') WITH &&
    ) WHERE (status IN ('PENDING', 'CONFIRMED'))
);
CREATE INDEX idx_bookings_user ON bookings (user_id, start_time);
CREATE INDEX idx_bookings_provider_start ON bookings (provider_id, start_time);