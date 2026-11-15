# Architecture and data flow

Claimspace keeps the invariant-heavy claim workflow in one transactional service. A second service owns the read model used for workload and exposure reporting. This is a small service split with a concrete reason for asynchronous communication.

## Runtime boundaries

~~~mermaid
flowchart LR
  Browser[Angular shell]
  Claimant[Claimant remote]
  Staff[Staff remote]
  Web[Nginx same-origin router]
  Claims[Java Spring Boot claims service]
  Insights[Java Spring Boot insights service]
  SQL1[(PostgreSQL claims schema)]
  SQL2[(PostgreSQL insights schema)]
  Kafka[Kafka claims.changed.v1]
  Browser --> Claimant
  Browser --> Staff
  Claimant --> Web
  Staff --> Web
  Web -->|REST commands and claim reads| Claims
  Web -->|REST reporting reads| Insights
  Claims -->|one transaction| SQL1
  SQL1 -->|pending outbox rows| Claims
  Claims -->|acknowledged snapshots| Kafka
  Kafka -->|versioned snapshots| Insights
  Insights --> SQL2
~~~

The database schemas have different owners. Insights never queries the claims schema. There is no distributed transaction between the services. Nginx is a small same-origin router, not a business-logic gateway. It uses the container DNS resolver at runtime and refreshes service-name resolution, so changed backend IPs do not require a web restart. Resolver-specific configuration is rendered into /tmp to support the read-only Kubernetes container filesystem.

## Backend layers

In claims-service:

- api parses HTTP, validates request shape, identifies the principal and returns typed responses.
- service coordinates business operations, visibility, idempotency, database transactions, history and event recording. ClaimStore is its persistence port.
- domain holds the immutable Claim record and allowed transitions. It has no Spring annotations, HTTP types or SQL.
- infrastructure implements the port with JDBC, performs schema mapping, publishes the outbox and loads explicitly enabled demo fixtures.

Application contracts belong to the service layer so the application does not depend on controller DTOs. JDBC was chosen for a small model with explicit locking, optimistic updates and an outbox; an ORM would also be reasonable.

Insights contains the Kafka adapter, version validation, SQL upsert and reporting queries. It is intentionally a small query-oriented service, rather than duplicating the full claims domain.

## Data model

~~~mermaid
erDiagram
  CLAIM ||--o{ CLAIM_HISTORY : records
  CLAIM ||--o{ INFORMATION_REQUEST : receives
  CLAIM ||--o{ SUBMISSION_RECEIPT : identifies
  CLAIM ||--o{ OUTBOX_EVENT : emits
  CLAIM {
    uuid id PK
    string reference UK
    string claimant_id
    string status
    string assignee
    decimal requested_amount
    decimal reserve_amount
    decimal settlement_amount
    bigint version
  }
  INFORMATION_REQUEST {
    uuid id PK
    uuid claim_id FK
    string question
    string answer
    timestamp answered_at
  }
  CLAIM_HISTORY {
    bigint id PK
    uuid claim_id FK
    string actor
    string action
    string note
    timestamp occurred_at
  }
  SUBMISSION_RECEIPT {
    string claimant_id PK
    string idempotency_key PK
    string request_hash
    uuid claim_id FK
  }
  OUTBOX_EVENT {
    uuid id PK
    uuid aggregate_id FK
    bigint aggregate_version
    string payload
    timestamp published_at
  }
  CLAIM_PROJECTION {
    uuid claim_id PK
    bigint aggregate_version
    string status
    string currency
    decimal reserve_amount
    decimal settlement_amount
  }
~~~

CLAIM_PROJECTION lives in the insights schema and has no cross-service foreign key. Claim history is an audit trail; it is not the mechanism for reconstructing the aggregate.

## Workflow

~~~mermaid
stateDiagram-v2
  [*] --> SUBMITTED
  SUBMITTED --> UNDER_REVIEW: Officer picks up
  UNDER_REVIEW --> AWAITING_INFORMATION: Officer requests information
  AWAITING_INFORMATION --> UNDER_REVIEW: Claimant answers
  UNDER_REVIEW --> APPROVED: Positive assessment exists
  APPROVED --> SETTLED: Record settlement within reserve
  UNDER_REVIEW --> REJECTED: Reason required
  SETTLED --> [*]
  REJECTED --> [*]
~~~

All progression after assignment belongs to the assigned officer. Final claims cannot be reopened. The backend enforces the state machine even when someone bypasses the UI.

## Submission and event delivery

~~~mermaid
sequenceDiagram
  participant UI as Angular
  participant API as Claims API
  participant DB as Claims SQL
  participant P as Outbox publisher
  participant K as Kafka
  participant I as Insights
  participant R as Reporting SQL
  UI->>API: Submit with Idempotency-Key
  API->>DB: Lock principal/key, validate receipt
  API->>DB: Insert claim, receipt, history and outbox
  DB-->>API: Commit
  API-->>UI: 201 with claim reference
  P->>DB: Lock pending rows with SKIP LOCKED
  P->>K: Publish snapshot keyed by claim UUID
  K-->>P: Acknowledge
  P->>DB: Mark published and commit
  K->>I: Deliver snapshot
  I->>R: Upsert only when version is newer
  R-->>I: Commit
  I-->>K: Commit consumer offset
~~~

A crash after Kafka acknowledgement and before marking published may repeat an event. The projection's version predicate prevents double counting and rejects older snapshots. A newer full snapshot can safely arrive before an older one.

SQL stores the event and business change atomically. Broker availability therefore does not decide whether a valid claim submission succeeds. The publisher retries retained rows.

The consumer retries processing errors and sends persistent poison records to the DLT. A dead-lettered record requires diagnosis and replay after correction. It is not silently considered successfully projected.

## Concurrency and consistency

Each client command includes the last seen claim version. Updates use a version predicate; a conflicting update affects zero rows and rolls back the transaction with 409. This prevents two officers picking up the same version or overwriting one another's work.

Submission keys are scoped to the claimant. A transaction-level advisory lock serialises competing uses of the same key, and a unique constraint provides a final database guard. Identical replay returns the existing claim; changed content conflicts.

Reporting uses a separate transaction snapshot for its related SQL queries. Its lastUpdated value is the latest applied projection timestamp. It does not promise zero consumer lag. Claims always read from their operational source of truth.

## Frontend state and delivery

The shell owns sign-in, routing and navigation. Claimant and staff remotes own their feature state and use the typed ClaimsApi boundary. Shared components render data and emit events. Angular signals make loading, error, selection and pending mutation states explicit.

Native Federation shares Angular and the workspace library as singletons. This keeps identity state and injection tokens consistent across the shell and remotes. A runtime manifest points to the remote entry files. All are hosted by one Nginx container locally; the manifest is the seam for separate deployment later.

The staff reporting poll runs every five seconds while the page is visible and stops when the feature is destroyed. Draft notes remain intact while a command is pending or fails. Stale commands require a refresh.

## Version references

The pinned runtime combination was checked against the [Spring Boot 3.5 requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html), [Angular compatibility table](https://angular.dev/reference/versions), [Native Federation Angular documentation](https://native-federation.com/docs/v4/angular-adapter/) and [Podman Compose documentation](https://docs.podman.io/en/latest/markdown/podman-compose.1.html). See lockfiles and image delivery records for the actual built versions and platforms.
