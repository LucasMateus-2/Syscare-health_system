CREATE TYPE professional_area AS ENUM ('PSYCHOLOGY', 'NURSING', 'NUTRITION');

CREATE TABLE professionals (
                               id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id       UUID NOT NULL REFERENCES users(id),
                               full_name     VARCHAR(255) NOT NULL,
                               area          professional_area NOT NULL,
                               record_number VARCHAR(50) NOT NULL,
                               active        BOOLEAN NOT NULL DEFAULT true,
                               created_at    TIMESTAMP NOT NULL DEFAULT now(),
                               updated_at    TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_professionals_user_id ON professionals(user_id);
CREATE INDEX idx_professionals_area    ON professionals(area);

CREATE TABLE area_configs (
                              id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              area             professional_area UNIQUE NOT NULL,
                              duration_minutes INT NOT NULL,
                              updated_at       TIMESTAMP NOT NULL DEFAULT now()
);

INSERT INTO area_configs (area, duration_minutes) VALUES
                                                      ('PSYCHOLOGY', 50),
                                                      ('NURSING',    30),
                                                      ('NUTRITION',  45);