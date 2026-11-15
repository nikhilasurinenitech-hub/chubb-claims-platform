CREATE TABLE claim (
 id uuid PRIMARY KEY, reference varchar(32) UNIQUE NOT NULL, claimant_id varchar(80) NOT NULL,
 type varchar(16) NOT NULL CHECK(type IN ('MOTOR','PROPERTY')), market varchar(2) NOT NULL, currency varchar(3) NOT NULL,
 incident_date date NOT NULL, location varchar(200) NOT NULL, description varchar(4000) NOT NULL,
 requested_amount numeric(15,2) NOT NULL CHECK(requested_amount > 0),
 status varchar(32) NOT NULL CHECK(status IN ('SUBMITTED','UNDER_REVIEW','AWAITING_INFORMATION','APPROVED','SETTLED','REJECTED')),
 assignee varchar(80), reserve_amount numeric(15,2) NOT NULL DEFAULT 0 CHECK(reserve_amount >= 0),
 settlement_amount numeric(15,2) NOT NULL DEFAULT 0 CHECK(settlement_amount >= 0),
 version bigint NOT NULL DEFAULT 0, created_at timestamptz NOT NULL, updated_at timestamptz NOT NULL, closed_at timestamptz,
 CHECK(settlement_amount <= reserve_amount)
);
CREATE INDEX idx_claim_queue ON claim(status, updated_at DESC);
CREATE INDEX idx_claim_owner ON claim(claimant_id, updated_at DESC);
CREATE INDEX idx_claim_assignee ON claim(assignee, status);
CREATE TABLE claim_history (id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY, claim_id uuid NOT NULL REFERENCES claim(id), actor varchar(80) NOT NULL, action varchar(64) NOT NULL, note varchar(4000) NOT NULL, occurred_at timestamptz NOT NULL);
CREATE INDEX idx_history_claim ON claim_history(claim_id,id);
CREATE TABLE information_request (id uuid PRIMARY KEY, claim_id uuid NOT NULL REFERENCES claim(id), question varchar(2000) NOT NULL, requested_by varchar(80) NOT NULL, requested_at timestamptz NOT NULL, answer varchar(4000), answered_at timestamptz);
CREATE UNIQUE INDEX one_open_request ON information_request(claim_id) WHERE answered_at IS NULL;
CREATE TABLE submission_receipt (claimant_id varchar(80) NOT NULL, idempotency_key varchar(100) NOT NULL, request_hash varchar(64) NOT NULL, claim_id uuid NOT NULL REFERENCES claim(id), PRIMARY KEY(claimant_id,idempotency_key));
CREATE TABLE outbox_event (id uuid PRIMARY KEY, aggregate_id uuid NOT NULL REFERENCES claim(id), aggregate_version bigint NOT NULL, payload text NOT NULL, created_at timestamptz NOT NULL, published_at timestamptz, UNIQUE(aggregate_id,aggregate_version));
CREATE INDEX idx_outbox_pending ON outbox_event(created_at) WHERE published_at IS NULL;
