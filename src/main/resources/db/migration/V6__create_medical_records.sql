CREATE TABLE medical_records
(
    id         UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    patient_id UUID      NOT NULL REFERENCES patients (id),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_patient_record UNIQUE (patient_id)
);

CREATE TABLE record_entries
(
    id                UUID PRIMARY KEY           DEFAULT gen_random_uuid(),
    medical_record_id UUID              NOT NULL REFERENCES medical_records (id),
    appointment_id    UUID              NOT NULL REFERENCES appointments (id),
    professional_id   UUID              NOT NULL REFERENCES professionals (id),
    area              professional_area NOT NULL,
    notes             TEXT              NOT NULL,
    created_at        TIMESTAMP         NOT NULL DEFAULT now(),
    updated_at        TIMESTAMP         NOT NULL DEFAULT now(),

    CONSTRAINT uq_appointment_entry UNIQUE (appointment_id)
);

CREATE INDEX idx_record_entries_medical_record ON record_entries (medical_record_id);
CREATE INDEX idx_record_entries_professional ON record_entries (professional_id);