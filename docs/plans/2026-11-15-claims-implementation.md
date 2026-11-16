# Claims Platform Implementation Plan

**Goal:** Build, verify and package the approved claimant and staff claims workflow for local assessment.

**Architecture:** Two Spring Boot services own transactional claims and Kafka-driven insights. One Angular workspace contains a shell and claimant/staff remotes. PostgreSQL schemas isolate data ownership and Compose provides local infrastructure.

**Tech Stack:** Java 21, Spring Boot, Maven, Angular, Native Federation, PostgreSQL, Flyway, Kafka, Podman/Compose, JUnit, Testcontainers, Angular component testing and Playwright.

---

Implementation completed. Verification is recorded in docs/verification.json. During implementation, application contracts moved into service/Contracts.java, ClaimStore became the persistence port, shared errors moved into platform-common, and the Nginx configuration lives in frontend/. These refinements are explained in the journal and decisions.

### Task 1: Record design and initialise repository

Files: `docs/plans/2026-11-15-claims-design.md`, this plan, `docs/ai-journal.md`, `prompts/ALL-PROMPTS.md`, `.gitignore`. The prompt file contains the consolidated developer prompt list.

1. Save approved boundaries and acceptance criteria.
2. Record original prompts and real approvals, with source requirements distinguished from user instructions.
3. Initialise a dedicated repository and commit the design before production code.

### Task 2: Implement and test domain rules

Files: `backend/claims-service/src/main/java/com/example/claims/domain/Claim.java`, `ClaimStatus.java`, `DomainException.java`; `backend/claims-service/src/test/java/com/example/claims/domain/ClaimTest.java`; `backend/pom.xml`.

1. Add JUnit tests for assignment, information cycle, approval/settlement, rejection, invalid transitions and terminal immutability. Use fixed timestamps and principals.
2. Run `mvn -f backend/pom.xml -pl claims-service -am test`; expect missing-domain compilation failures initially.
3. Implement the state machine with decimal money and explicit state guards, for example `if (status != UNDER_REVIEW) throw new DomainException("INVALID_TRANSITION", "Assessment requires review");`.
4. Run the same command; expect all domain tests to pass.
5. Commit the tested domain model.

### Task 3: Build transactional claims API

Files: claims `api/ClaimController.java`, `backend/platform-common/src/main/java/com/example/platform/ApiErrors.java`, `service/ClaimService.java`, `infrastructure/ClaimRepository.java`, `infrastructure/OutboxPublisher.java`, `src/main/resources/db/migration/V1__claims.sql`, `application.yml`, common security configuration; `openapi/claims.yaml`.

1. Define validated create/command DTOs, authenticated ownership and stable error contracts.
2. Create Flyway schema with optimistic version and unique submission key constraints.
3. Write each command and its full-snapshot outbox event in one SQL transaction.
4. Publish pending rows, mark only acknowledged messages sent, retain failed events for retry.
5. Add real PostgreSQL integration cases for create/replay, stale version, ownership, assignment and workflow.
6. Run `mvn -f backend/pom.xml verify`; unit tests pass and integration tests require a reachable container engine.
7. Commit API, persistence and tests.

### Task 4: Build and verify insights projection

Files: insights `infrastructure/ClaimEventConsumer.java`, `service/InsightsService.java`, `api/InsightsController.java`, `src/main/resources/db/migration/V1__insights.sql`; `src/test/java/com/example/insights/ProjectionIT.java`; `openapi/events/claim-snapshot-v1.json`.

1. Consume versioned snapshots keyed by claim UUID.
2. Upsert only where `excluded.aggregate_version > claim_projection.aggregate_version`.
3. Expose counts by status/officer, closure duration and exposure by currency, with freshness metadata.
4. Test duplicate, old and newer events and currency separation against PostgreSQL; verify actual Kafka delivery in the system smoke test.
5. Commit projection and read API.

### Task 5: Build Angular portals

Files: `frontend/angular.json`, `frontend/projects/shell/src/app/`, `frontend/projects/claimant/src/app/`, `frontend/projects/staff/src/app/`, `frontend/projects/shared/`, federation configuration and component specs.

1. Pin a compatible Angular, Node, TypeScript and federation set, and commit lockfile.
2. Generate shell and two runtime-loaded remotes. Share Angular and workspace UI/contracts.
3. Add login, role-aware navigation, claimant forms/history, staff queue/detail/actions and dashboard.
4. Implement signal-based loading/data/error state and typed HTTP clients.
5. Component tests cover status display, form validation and error/loading feedback.
6. Run `npm --prefix frontend test -- --watch=false` and `npm --prefix frontend run build`; expect passing tests and three production builds.
7. Commit working UI.

### Task 6: Package and run full system

Files: `docker-compose.yml`, backend/frontend `Dockerfile`, `frontend/nginx.conf`, `scripts/start.sh`, `scripts/stop.sh`, `scripts/export-images.sh`, `scripts/smoke.py`, `frontend/e2e/claims.spec.ts`, `.github/workflows/ci.yml`, `deploy/k8s/`.

1. Add PostgreSQL/Kafka health checks and persistent volumes, application images and same-origin web proxy.
2. Build and start with `podman compose up --build -d`; verify readiness.
3. Execute API smoke flow against both services and verify Kafka-driven totals.
4. Execute Playwright claimant-information-settlement and rejection scenarios, including a claimant isolation assertion.
5. Restart containers and confirm persisted claims survive. Exercise outbox recovery with Kafka temporarily stopped.
6. Export application images; verify archive format/load instructions and record actual platform.
7. Add CI and Kubernetes deployment reference; validate manifests to the extent the environment allows.
8. Commit packaging and verification fixes.

### Task 7: Document, review and deliver

Files: `README.md`, `NOTES.md`, `docs/architecture.md`, `docs/decisions/`, `docs/walkthrough.md`, `docs/testing.md`, `docs/ai-journal.md`, `postman/claims.json`.

1. Explain setup, demo identities, commands, exact verification results and limitations.
2. Add architecture/ER/sequence diagrams and rubric-to-evidence mapping.
3. Review actual journal entries; distinguish AI suggestions from user decisions. Never fabricate challenges or reviews.
4. Document a 15–20-minute walkthrough, trade-off answers and follow-up work.
5. Copy the complete Git repository to `/Users/nikhilasurineni/Downloads/chubb-claims-platform`, and save the submission archive under the chat outputs directory.
6. Verify delivered file tree and archive; report concise run commands and any unverified checks.
