CREATE TABLE claim_projection (
 claim_id uuid PRIMARY KEY, aggregate_version bigint NOT NULL, reference varchar(32) NOT NULL,
 status varchar(32) NOT NULL, market varchar(2) NOT NULL, currency varchar(3) NOT NULL,
 reserve_amount numeric(15,2) NOT NULL, settlement_amount numeric(15,2) NOT NULL,
 assignee varchar(80), created_at timestamptz NOT NULL, updated_at timestamptz NOT NULL, closed_at timestamptz,
 projected_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_projection_status ON claim_projection(status);
