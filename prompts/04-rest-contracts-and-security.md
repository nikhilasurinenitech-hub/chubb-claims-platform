# REST contracts and security

Use this to agree frontend/backend behaviour and verify permissions independently of the UI.

## Developer prompt

```text
Design or reconcile Claimspace's REST API with the domain rules and Angular client. Inspect openapi/claims.yaml and the controllers first. Cover markets, paginated/filterable claims, creation, details, officer commands, claimant responses and staff reporting.

Use typed request/response contracts, explicit field validation and consistent Problem Details errors. Distinguish malformed input, missing authentication, forbidden actions, hidden claimant resources and version conflicts. Include a version in mutation requests and an Idempotency-Key on submission. Document actual response codes and examples from the implementation.

Enforce Spring Security roles, claimant ownership and assigned-officer rules on the backend. Do not trust an owner or actor supplied by the browser. Keep the demonstration self-contained with BCrypt-backed HTTP Basic identities. The Angular client must send credentials only to the intended same-origin API and hold them in memory.

Keep controllers thin, transact in the application service and leave state transitions in the domain. Validate the OpenAPI contract with a strict validator and keep a runnable Postman workflow. Document TLS/OIDC as production follow-up work rather than pretending the local demo implements them.
```

## Challenge prompt

```text
Show what happens if I bypass the Angular guard and call another claimant's detail endpoint, or mutate a claim assigned to a different officer. Then show a stale version request. Which layer rejects each case, and how does the frontend recover?
```

## Artifacts to review

- [OpenAPI](../openapi/claims.yaml) and [Postman collection](../postman/claimspace.postman_collection.json).
- [Claims controller](../backend/claims-service/src/main/java/com/example/claims/api/ClaimController.java).
- [Security configuration](../backend/platform-common/src/main/java/com/example/platform/SecurityConfig.java), [API errors](../backend/platform-common/src/main/java/com/example/platform/ApiErrors.java) and [frontend identity](../frontend/projects/shared/src/lib/auth.ts).
