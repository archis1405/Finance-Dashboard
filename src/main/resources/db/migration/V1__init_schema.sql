CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(150) NOT NULL
);

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

CREATE TABLE business_units (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    business_unit_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_users_business_unit FOREIGN KEY (business_unit_id) REFERENCES business_units(id)
);

CREATE TABLE financial_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(14,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    category VARCHAR(80) NOT NULL,
    entry_date DATE NOT NULL,
    notes VARCHAR(500),
    deleted BOOLEAN NOT NULL,
    business_unit_id BIGINT NOT NULL,
    created_by_user_id BIGINT NOT NULL,
    updated_by_user_id BIGINT,
    deleted_by_user_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_financial_records_business_unit FOREIGN KEY (business_unit_id) REFERENCES business_units(id),
    CONSTRAINT fk_financial_records_created_by FOREIGN KEY (created_by_user_id) REFERENCES users(id),
    CONSTRAINT fk_financial_records_updated_by FOREIGN KEY (updated_by_user_id) REFERENCES users(id),
    CONSTRAINT fk_financial_records_deleted_by FOREIGN KEY (deleted_by_user_id) REFERENCES users(id)
);

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    token_type VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action_type VARCHAR(30) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(100),
    actor_user_id BIGINT,
    details VARCHAR(1500),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_audit_logs_actor FOREIGN KEY (actor_user_id) REFERENCES users(id)
);

CREATE TABLE dashboard_snapshots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    period_type VARCHAR(20) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    business_unit_id BIGINT,
    total_income DECIMAL(14,2) NOT NULL,
    total_expense DECIMAL(14,2) NOT NULL,
    net_balance DECIMAL(14,2) NOT NULL,
    total_records BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_dashboard_snapshots_business_unit FOREIGN KEY (business_unit_id) REFERENCES business_units(id)
);

CREATE INDEX idx_financial_records_scope ON financial_records (business_unit_id, entry_date, deleted);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens (user_id, revoked);
CREATE INDEX idx_dashboard_snapshots_scope ON dashboard_snapshots (business_unit_id, period_type, period_start);