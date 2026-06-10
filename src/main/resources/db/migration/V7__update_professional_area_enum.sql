ALTER TYPE professional_area RENAME TO professional_area_old;

CREATE TYPE professional_area AS ENUM (
    'DENTISTRY',
    'NUTRITION',
    'PSYCHOLOGY',
    'NURSING'
);

ALTER TABLE professionals
ALTER COLUMN area TYPE professional_area
    USING area::text::professional_area;

ALTER TABLE area_configs
ALTER COLUMN area TYPE professional_area
    USING area::text::professional_area;

ALTER TABLE appointments
ALTER COLUMN area TYPE professional_area
    USING area::text::professional_area;

ALTER TABLE record_entries
ALTER COLUMN area TYPE professional_area
    USING area::text::professional_area;

DROP TYPE professional_area_old;