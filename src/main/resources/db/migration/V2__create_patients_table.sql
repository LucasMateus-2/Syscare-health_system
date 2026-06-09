CREATE TYPE patient_status AS ENUM ('ACTIVE', 'INACTIVE');

CREATE TABLE patients (
                          id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          user_id    UUID NOT NULL REFERENCES users(id),
                          full_name  VARCHAR(255) NOT NULL,
                          cpf        VARCHAR(11) UNIQUE NOT NULL,
                          birth_date DATE NOT NULL,
                          phone      VARCHAR(20),
                          status     patient_status NOT NULL DEFAULT 'ACTIVE',
                          created_at TIMESTAMP NOT NULL DEFAULT now(),
                          updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_patients_cpf      ON patients(cpf);
CREATE INDEX idx_patients_user_id  ON patients(user_id);