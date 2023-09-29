CREATE SCHEMA IF NOT exists care_plan;

CREATE TABLE IF NOT EXISTS care_plan.patient  (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name TEXT,
    last_name TEXT,
    gender TEXT,
    date_of_birth DATE
);

CREATE TABLE IF NOT EXISTS care_plan.provider_availability (
    id uuid PRIMARY KEY,
    from_date TEXT,
    to_date TEXT,
    last_name TEXT,
    first_name TEXT,
    gender TEXT,
    speciality TEXT,
    provider_id uuid
);

CREATE TABLE IF NOT EXISTS care_plan.booking (
    id uuid PRIMARY KEY,
    provider_availability_id uuid REFERENCES care_plan.provider_availability(id),
    patient_id uuid REFERENCES care_plan.patient(id)
);

CREATE TABLE IF NOT EXISTS care_plan.outbox_event (
    id uuid PRIMARY KEY,
    event_type TEXT,
    topic TEXT,
    payload TEXT,
    created_at TIMESTAMP,
    publish_status TEXT
);