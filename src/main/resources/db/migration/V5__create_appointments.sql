CREATE TYPE appointment_status AS ENUM ('SCHEDULED', 'CANCELLED', 'COMPLETED', 'RESCHEDULED');

CREATE TABLE appointments (
                              id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              patient_id      UUID NOT NULL REFERENCES patients(id),
                              professional_id UUID NOT NULL REFERENCES professionals(id),
                              area            professional_area NOT NULL,
                              scheduled_at    TIMESTAMP NOT NULL,
                              ends_at         TIMESTAMP NOT NULL,
                              status          appointment_status NOT NULL DEFAULT 'SCHEDULED',
                              cancelled_at    TIMESTAMP,
                              cancel_reason   TEXT,
                              rescheduled_to  UUID REFERENCES appointments(id),
                              created_by      UUID NOT NULL REFERENCES users(id),
                              created_at      TIMESTAMP NOT NULL DEFAULT now(),
                              updated_at      TIMESTAMP NOT NULL DEFAULT now(),

                              CONSTRAINT chk_appointment_times CHECK (ends_at > scheduled_at)
);

-- Evita conflito de horário do paciente
CREATE UNIQUE INDEX idx_patient_schedule_conflict
    ON appointments(patient_id, scheduled_at)
    WHERE status NOT IN ('CANCELLED', 'RESCHEDULED');

-- Evita conflito de horário do profissional
CREATE UNIQUE INDEX idx_professional_schedule_conflict
    ON appointments(professional_id, scheduled_at)
    WHERE status NOT IN ('CANCELLED', 'RESCHEDULED');

CREATE INDEX idx_appointments_patient      ON appointments(patient_id);
CREATE INDEX idx_appointments_professional ON appointments(professional_id);
CREATE INDEX idx_appointments_scheduled_at ON appointments(scheduled_at);