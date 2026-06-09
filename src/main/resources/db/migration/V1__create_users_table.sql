CREATE TYPE user_role AS ENUM ('ADMIN', 'PROFESSIONAL', 'RECEPTIONIST', 'PATIENT');

CREATE TABLE users (
                       id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email         VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role          user_role NOT NULL,
                       active        BOOLEAN NOT NULL DEFAULT true,
                       created_at    TIMESTAMP NOT NULL DEFAULT now(),
                       updated_at    TIMESTAMP NOT NULL DEFAULT now()
);