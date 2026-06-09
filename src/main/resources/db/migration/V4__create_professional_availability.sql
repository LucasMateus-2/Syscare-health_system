CREATE TYPE day_of_week AS ENUM ('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT');

CREATE TABLE professional_availability (
                                           id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                           professional_id UUID NOT NULL REFERENCES professionals(id),
                                           day_of_week     day_of_week NOT NULL,
                                           start_time      TIME NOT NULL,
                                           end_time        TIME NOT NULL,
                                           active          BOOLEAN NOT NULL DEFAULT true,

                                           CONSTRAINT chk_availability_times CHECK (end_time > start_time)
);

CREATE INDEX idx_availability_professional ON professional_availability(professional_id);