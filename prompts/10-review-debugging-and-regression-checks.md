# Review debugging and regression checks

Use this to challenge AI-generated code and explain defects through evidence.

## Developer prompt

```text
Review Claimspace as a maintainer preparing the assessment handoff. Inspect the code, contracts, migrations, logs and recorded checks. Prioritise broken invariants, data loss, permission gaps, stale writes, event consistency and restart failures over cosmetic refactoring. Return findings with file locations, impact and a way to reproduce them.

For each confirmed defect, explain the evidence, reproduce the failure, propose the smallest correction and run a focused regression check. Keep the journal accurate about the original hypothesis and the result. Do not invent a human review or assume a green health endpoint proves the public request path works.

Include these targeted inspections: password encoder registration and explicit Basic headers; proxy DNS after backend IP changes; fixed idempotency keys with date-dependent seed payloads; API contracts versus actual errors; duplicate/older snapshots; and workload counts versus underlying statuses.

When diagnosing a dashboard discrepancy, compare current operational claims with the reporting projection, filter scope and freshness. Waiting is included in Open. Separate eventual-consistency lag from a wrong query or stale selected detail.

Preserve all current user claims. Do not run destructive fixture resets or disruptive recovery tests against a demo someone is using. End with resolved findings, remaining risks and evidence for each conclusion.
```

## Challenge prompts

```text
A container is healthy but the public API returns 502 after a restart. Trace DNS, networking and Nginx upstream resolution before changing application logic. What observation would disprove your first hypothesis?
```

```text
The app starts today but fails tomorrow with an idempotency conflict while replaying fixtures. Reproduce it with a controlled clock and stable keys. Correct the fixture design without weakening real submission idempotency or future-incident validation.
```

## Artifacts to review

- [AI journal and real corrections](../docs/ai-journal.md).
- [Nginx routing](../frontend/nginx.conf), [container entrypoint](../frontend/container-entrypoint.sh) and [restart regression](../backend/claims-service/src/test/java/com/example/claims/DemoRestartIT.java).
- [Reporting calculations](../backend/insights-service/src/main/java/com/example/insights/service/InsightsService.java).
