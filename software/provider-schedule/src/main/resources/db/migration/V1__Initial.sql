CREATE SCHEMA IF NOT exists provider_schedule;

CREATE TABLE provider_schedule.provider (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name TEXT,
    last_name TEXT,
    speciality TEXT,
    gender TEXT
);

CREATE TABLE provider_schedule.availability (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    from_date TEXT,
    to_date TEXT,
    provider_id uuid REFERENCES provider_schedule.provider(id),
    booking_reference_id uuid
);

CREATE TABLE IF NOT EXISTS provider_schedule.outbox_event (
    id uuid PRIMARY KEY,
    event_type TEXT,
    topic TEXT,
    payload TEXT,
    created_at TIMESTAMP,
    publish_status TEXT
);