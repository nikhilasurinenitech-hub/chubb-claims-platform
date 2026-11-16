# Design decisions

These decisions were proposed by the AI and approved or delegated by the user. They are discussion material for the panel, not evidence of a human review that did not occur.

## ADR 001 Two services

Claims owns lifecycle invariants; insights owns a read model. This supplies a real reason for Kafka without distributing assessment and settlement across separate transactions. A modular monolith would be a strong lower-complexity alternative for a stricter time box. Additional services would need independent ownership, scaling or failure-isolation requirements.

## ADR 002 REST for commands and Kafka for projections

REST returns immediate validation and the committed reference. Kafka decouples reporting availability and provides replay. Sending a submission only to Kafka would complicate acknowledgement, immediate errors and read-after-write behaviour.

## ADR 003 Transactional outbox

A database commit and a broker publish cannot be made atomic by calling them next to each other. Store the pending event in the same transaction as the claim. Publish later with acknowledgement, accept at-least-once delivery, and make the consumer idempotent. An outbox adds a table, publisher and operational retention work.

## ADR 004 PostgreSQL and explicit JDBC

The workflow needs relational ownership, transactions, constraints, decimal money and concurrency control. PostgreSQL provides these directly. JDBC keeps SQL and the outbox lock visible in a small assessment. More mappings would make JPA or another persistence approach worth reconsidering.

## ADR 005 Two Angular remotes

One workspace and shell serve both roles. Separate claimant and staff builds demonstrate the MFE rubric and isolate the two journeys, with a shared UI/API library. Runtime federation adds deployment and dependency compatibility work. Local deployment deliberately packages the builds together; a lazy modular frontend would be simpler for a small team.

## ADR 006 Identity and production scope

Local HTTP Basic identities keep the demo self-contained. Role checks and ownership are implemented on the server, while route guards improve the UI. Browser credentials stay in memory. The next step is TLS and an external OIDC provider with the same principal-based ownership rules. The demo is not presented as production identity infrastructure.

## ADR 007 Money and exposure

BigDecimal and SQL numeric preserve decimal arithmetic. Report reserves only for open assessed claims. Show unassessed counts and separate currency totals. Adding amounts across markets without an agreed conversion source and rate date would misstate exposure.

## ADR 008 Testing allocation

Unit tests focus on state transitions and permissions; PostgreSQL tests cover transactions and SQL; component tests cover reusable presentation and identity state; browser tests cover the user journeys. Real Kafka smoke, outage recovery and replay checks test the integration boundary that unit mocks cannot establish.

## ADR 009 Containers and Kubernetes

Multi-stage Dockerfiles build the application without requiring host Java/Node. Compose is the primary assessment run path. Kubernetes reference manifests demonstrate workload, storage, readiness and service configuration, while avoiding a mandatory local cluster. Their verification level is stated explicitly.

## ADR 010 Caching and async UX

Keep immutable reference data in process and avoid a distributed cache for this workload. Do not cache mutable claim details. Reporting refreshes on a visible five-second poll and advertises its last processed time. This makes eventual consistency visible without adding a browser event-stream transport.

## ADR 011 Runtime DNS resolution

A persistence/replay test exposed stale Nginx upstream addresses after Podman changed a restarted backend’s IP. The initial static configuration was replaced with runtime DNS resolution using the container’s configured resolver and a five-second cache. An explicit template substitution preserves Nginx variables, and generated configuration lives in /tmp. The restart and replay check then passed without a web restart.

## ADR 012 Deterministic demo fixture payloads

A final review found that fixed seed idempotency keys were paired with dates calculated from the current day. A PostgreSQL regression advancing the application clock reproduced a later-day conflict. Fixture incident dates now use a fixed assessment anchor, while live claim validation and audit timestamps still use the clock. The same key therefore represents the same fixture payload on every restart. Intentional fixture changes require a new fixture-key version.

## ADR 012 Kafka volume ownership across container engines

The first GitHub-hosted Docker run exposed an AccessDeniedException while Kafka formatted a fresh volume mounted at /tmp/kraft-combined-logs. The local Podman-created volume had allowed the same configuration to start, so local verification alone did not establish fresh Docker portability.

Mount the existing kafka-data volume at /var/lib/kafka/data and set KAFKA_LOG_DIRS to that same directory. The official Kafka 4.1.0 image creates this directory for appuser and declares it as a volume. Docker can therefore initialise a fresh named volume with the image's directory ownership. Kafka continues running as its normal non-root user.

The named volume and its contents are retained; only its mount location inside the container changes. No data reset, extra service, root Kafka process or custom application image is required. Kubernetes uses the same directory with its existing filesystem-group configuration. Fresh Docker startup, API/Kafka smoke and browser scenarios are exercised by the GitHub workflow.

Reference: [official Kafka 4.1.0 Dockerfile](https://github.com/apache/kafka/blob/4.1.0/docker/jvm/Dockerfile).
