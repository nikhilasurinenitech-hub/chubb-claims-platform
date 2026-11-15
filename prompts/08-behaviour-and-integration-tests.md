# Behaviour and integration tests

Use this to prove business behaviour and integration boundaries rather than maximise the test count.

## Developer prompt

```text
Create or review a risk-based test plan for Claimspace using the existing Java, PostgreSQL Testcontainers, Angular TestBed/Vitest and Playwright setup. Start by inspecting current tests and avoid duplicating their implementation in assertions.

Use domain tests for allowed/forbidden transitions, assignment, claimant responses, reserve/settlement rules, currency precision and terminal states. Use real PostgreSQL integration tests for ownership, roles, idempotency replay/conflict, concurrent versions, atomic history/outbox writes and duplicate/older reporting snapshots.

Cover frontend identity lifetime, credential boundaries, permission-aware actions, readable money/statuses, accessible feedback and draft preservation. Exercise production builds through Nginx for the information-to-settlement journey, rejection, claimant isolation and mobile validation/layout.

Verify Kafka transport with the full container system, separately from SQL integration tests that disable background publishing. Check broker outage recovery, persistent data and safe event replay in an isolated environment. Never reset a user's running claims to make a test pass.

For a defect, reproduce it first, make the smallest correction and rerun the relevant checks. Record actual commands, results, coverage scope and limitations. Distinguish native ARM64, AMD64 under emulation, static Kubernetes validation and unexecuted remote CI. Do not report a test as passing without execution evidence.
```

## Challenge prompt

```text
Which important failure could still happen even if every mocked unit test passes? Identify the smallest real-boundary check for it. Explain what the recorded coverage percentages measure and what they do not establish.
```

## Artifacts to review

- [Domain tests](../backend/claims-service/src/test/java/com/example/claims/domain/ClaimTest.java), [claims API tests](../backend/claims-service/src/test/java/com/example/claims/ClaimsApiIT.java) and [projection tests](../backend/insights-service/src/test/java/com/example/insights/ProjectionIT.java).
- [Component/auth tests](../frontend/projects/shared/src/lib/ui.spec.ts) and [browser tests](../frontend/e2e/claims.spec.ts).
- [Testing commands and limits](../docs/testing.md) and [recorded results](../docs/verification.json).
