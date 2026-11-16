# Testing and verification

Checks are selected for behaviour and boundaries rather than test-count targets. The latest machine-readable results and platform details are recorded in docs/verification.json.

## Automated suites

| Layer | Cases | What they establish |
| --- | --- | --- |
| Java domain | 10 | State transitions, officer/claimant ownership, money precision, settlement limits and terminal behaviour |
| Claims PostgreSQL integration | 5 | HTTP authentication/roles, claimant isolation, submission replay/conflict, stale commands, full workflow, atomic history/outbox, invalid request responses and replaying demo fixtures after a clock advance |
| Insights PostgreSQL integration | 3 | Duplicate/older snapshot rejection, currency separation, settled exposure removal and unsupported schema rejection |
| Angular components/auth | 10 | Readable status/currency, accessible feedback, in-memory identity, credential boundaries, permission-aware actions and draft preservation |
| Playwright browser | 3 | Claimant/officer settlement journey, rejection, claimant isolation and mobile validation/layout |

Run backend checks with Maven verify; Testcontainers starts actual PostgreSQL containers. The claims integration suite deliberately disables scheduled outbox publishing because it tests transaction boundaries independently. Actual Kafka transport is checked against the full Compose system.

The Angular suite uses Angular TestBed with Vitest and the HTTP testing boundary. Browser tests exercise the production builds through Nginx and both backend services.

## Commands

~~~bash
mvn -f backend/pom.xml verify
npm --prefix frontend ci
npm --prefix frontend test
npm --prefix frontend run build
python3 scripts/smoke.py
cd frontend
npx playwright install chromium
npm run e2e
~~~

See the root README for this Mac's Testcontainers/Podman socket settings. Host frontend checks require Node 24; running the application with Compose supplies build tools inside containers.

## Coverage

JaCoCo reports are generated under each backend service's target/site/jacoco directory by Maven verify. The verified JUnit run measured:

| Scope | Covered lines | Total lines | Line coverage |
| --- | --- | --- | --- |
| Claim domain aggregate | 42 | 48 | 87.5% |
| Claims service module | 312 | 391 | 79.8% |
| Insights service module | 79 | 86 | 91.9% |

These are JUnit instrumentation results, not combined system-test coverage. Demo seeding is included through the later-day restart regression. Live outbox publication is exercised in container checks and is not included in those percentages. Coverage is evidence of exercised code; it does not establish that every failure mode is handled.

## Runtime checks

The API smoke script submits a claim, replays its submission key, checks claimant isolation, runs information/assessment/settlement, inspects history, waits for publication and verifies staff-only reporting.

The optional recovery test stops this project's Kafka and always attempts to start it again:

~~~bash
python3 scripts/recovery-check.py
~~~

Use CONTAINER_ENGINE=docker when running it with Docker. It verifies a claim commits while the broker is stopped, remains readable, and reaches the SQL reporting projection after recovery.

Container recreation with persistent volumes and Kafka offset replay are also checked during delivery. Run the optional scripts/persistence-check.py helper to repeat that check. It briefly stops this project’s services, retains volumes, resets the insights consumer offsets and verifies that replay leaves the SQL state unchanged. Run disruptive checks when no other user is editing the local demo. Replay must leave projection row counts, versions and money unchanged. The local results record whether these checks passed.

## Validation limits

GitHub Actions now executes on the public repository. The first remote run exposed a fresh Docker volume-ownership failure in Kafka; ADR 012 records the correction. Current job results are available in the [Actions tab](https://github.com/nikhilasurinenitech-hub/chubb-claims-platform/actions). All 13 Kubernetes resources passed strict kubeconform schema validation; no live cluster is claimed. ARM64 is the native local test platform. All three browser scenarios also passed against AMD64 application images running through QEMU in the ARM64 VM. This is emulated AMD64 validation, not a native Intel/AMD machine test.

Performance/load tests, formal accessibility auditing, production identity integration, actual payment processing and multi-node failure tests are outside this assessment scope.
