ALTER TABLE users
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE financial_records
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE refresh_tokens
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE dashboard_snapshots
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE dashboard_snapshots
    ADD CONSTRAINT uk_dashboard_snapshot_scope
        UNIQUE (period_type, period_start, period_end, business_unit_id);
