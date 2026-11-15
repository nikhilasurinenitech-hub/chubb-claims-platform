# Domain and SQL model

Use this to establish business rules before wiring controllers and screens.

## Developer prompt

```text
Implement or review the Claimspace domain and PostgreSQL migrations against the approved design. Model a claim, information requests/responses, history, submission receipts, pending outbox events and the separate insights projection.

Enforce these transitions on the server: SUBMITTED -> UNDER_REVIEW; UNDER_REVIEW -> AWAITING_INFORMATION -> UNDER_REVIEW; UNDER_REVIEW -> APPROVED -> SETTLED; UNDER_REVIEW -> REJECTED. Only the assigned officer progresses a picked-up claim. Only its owning claimant responds. Final decisions cannot reopen in this scope.

Keep requested amount, assessed reserve and settlement separate. Use BigDecimal and SQL numeric, validate currency precision including whole-unit JPY, require a positive saved reserve before approval, and require 0 < settlement <= reserve. Report currencies separately without inventing FX rates.

Use authenticated identity for ownership, UUIDs and public references, an optimistic version, audit timestamps, SQL constraints and Flyway migrations. Make submission retries idempotent per claimant/key and reject changed payloads under the same key. Keep receipt, claim, history and outbox changes atomic.

Use fictional fixtures with stable payloads and versioned seed keys so restarts on later days do not cause idempotency conflicts. Explain each table's owner and access pattern before editing migrations.
```

## Challenge prompt

```text
Walk through two concurrent submissions with the same idempotency key and two officers picking up the same claim version. Point to the database safeguards. How do stable fixture dates differ from the current date used to validate a real submission?
```

## Artifacts to review

- [Claim aggregate](../backend/claims-service/src/main/java/com/example/claims/domain/Claim.java).
- [Claims migration](../backend/claims-service/src/main/resources/db/migration/V1__claims.sql) and [insights migration](../backend/insights-service/src/main/resources/db/migration/V1__insights.sql).
- [Use cases](../backend/claims-service/src/main/java/com/example/claims/service/ClaimService.java) and [later-day regression](../backend/claims-service/src/test/java/com/example/claims/DemoRestartIT.java).
