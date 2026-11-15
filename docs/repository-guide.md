# Repository structure and video guidance

The repository follows the supplied video's recommended responsibilities while retaining standard Maven and Angular workspace paths. The example's root `src/`, `test/` and `resources/` are represented inside the backend modules, where Maven and IDEs expect them.

## Mapping the recommended shape

| Video item | Location in this project | What to inspect |
| --- | --- | --- |
| `README.md` | [Root README](../README.md) and [run instructions](../RUN-INSTRUCTIONS.txt) | Purpose, one-line image startup, accounts, demo, tests and navigation |
| `src/api/` | [Claims API](../backend/claims-service/src/main/java/com/example/claims/api/ClaimController.java) and [insights API](../backend/insights-service/src/main/java/com/example/insights/api/InsightsController.java) | HTTP transport and delegation |
| `src/domain/` | [Claim domain](../backend/claims-service/src/main/java/com/example/claims/domain/Claim.java) | Lifecycle and business invariants without Spring/SQL dependencies |
| `src/service/` | [ClaimService](../backend/claims-service/src/main/java/com/example/claims/service/ClaimService.java), [ClaimStore port](../backend/claims-service/src/main/java/com/example/claims/service/ClaimStore.java), insights services | Use cases, transactions and owned contracts |
| `src/infrastructure/` | [ClaimRepository](../backend/claims-service/src/main/java/com/example/claims/infrastructure/ClaimRepository.java), [outbox](../backend/claims-service/src/main/java/com/example/claims/infrastructure/OutboxPublisher.java), insights consumer | SQL and broker adapters |
| `test/` | Backend module `src/test/java/`, [Angular component tests](../frontend/projects/shared/src/lib/ui.spec.ts), [browser tests](../frontend/e2e/claims.spec.ts) | Rules, real SQL boundaries and complete user journeys |
| `resources/application.yml` | [Claims configuration](../backend/claims-service/src/main/resources/application.yml), [insights configuration](../backend/insights-service/src/main/resources/application.yml) | Runtime settings |
| `resources/db/migration/` | [Claims migration](../backend/claims-service/src/main/resources/db/migration/V1__claims.sql), [insights migration](../backend/insights-service/src/main/resources/db/migration/V1__insights.sql) | Versioned schema setup |
| `openapi/` | [API contract](../openapi/claims.yaml) and event schema in root `openapi/` | Frontend/backend and event boundaries |
| `docker-compose.yml` | [Root Compose](../docker-compose.yml), backend/frontend Dockerfiles, `release/` | Source builds and supplied-image startup |
| `NOTES.md` | [Assumptions](../NOTES.md), [design decisions](decisions/README.md) | Scope, rationale and trade-offs |
| `prompts/` | [Developer prompts](../prompts/README.md) and twelve numbered prompts | Planning, implementation, challenge and verification instructions |
| AI journal | [AI working journal](ai-journal.md) | Real iterations, corrections and check outcomes |

The prompts and journal are separate: prompts guide the work; the journal records what happened. The AI journal records when the developer prompt set was prepared and revised.

## Frontend structure

- `frontend/projects/shell/`: sign-in, navigation, routing and remote loading.
- `frontend/projects/claimant/`: the claimant journey.
- `frontend/projects/staff/`: officer workflow and team reporting.
- `frontend/projects/shared/`: typed API/identity boundary and reusable UI.

## Suggested review order

1. Read the README and run the demo.
2. Read the requirements, approved plan, architecture and decision records.
3. Follow one claim action through the domain, service, adapter and event projection.
4. Inspect and run the relevant tests using [testing.md](testing.md).
5. Use the [prompt guide](../prompts/README.md), journal and [walkthrough](walkthrough.md) to discuss AI direction, verification and remaining scope.
