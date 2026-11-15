# Assumptions and scope decisions

The brief deliberately leaves service boundaries, data models and priorities open. The user approved the proposed two-service design and delegated remaining decisions.

- One incident belongs to one claim. Motor and property share a workflow; incident details are owned by the claim.
- Two roles cover the brief. Officers can see team reporting; a separate manager role adds no essential demonstration flow.
- A claimant owns a claim through authenticated identity. The create API does not accept an owner ID from the browser.
- Staff pick up claims for themselves. Reassignment, supervisor overrides and reopened decisions are deferred.
- Only the assigned officer can assess, request information, approve, reject or settle.
- The assessment reserve represents estimated outstanding liability. Unassessed claims are counted separately; their requested amounts are not silently treated as reserves.
- Money uses BigDecimal and PostgreSQL numeric. JPY accepts whole units. Currencies are reported separately with no invented exchange rate.
- Incident dates are evaluated against UTC. A production APAC product should agree market-specific business-date rules and expand the reference configuration.
- Audit notes are visible to the owning claimant in this demo. Production should distinguish shared messages from confidential internal notes.
- Information requests and responses are text. Upload storage, document scanning and policy validation need additional scope.
- Approval requires a saved positive reserve. Settlement must be positive and no greater than that reserve. The demo records a settlement decision, not an actual payment.
- Claim mutations are synchronous REST operations. Kafka feeds reporting and is outside the request's success path.
- Full snapshots make projection application idempotent by aggregate version. This is an outbox with a derived read model, not event sourcing.
- Kafka uses a compacted topic keyed by claim UUID. The latest retained snapshot supports projection rebuilds; database backup is still required for operational claims and audit history.
- A single PostgreSQL server hosts isolated schemas and roles to save local resources. Production could use separate databases.
- The two Angular remotes are separately built and runtime loaded. One Nginx image deploys them together for local simplicity. Independent hosting would require deployment/version compatibility controls.
- Local demo identity uses explicit HTTP Basic headers, BCrypt and server-side roles/ownership. Production needs TLS, OIDC/SSO, managed secrets and appropriate controls.
- The immutable market catalogue is kept in process. Mutable claims are not cached. There is no separate cache service.
- A five-second reporting poll is intentionally visible and simple. WebSockets and event streaming to the browser are deferred.
- Published outbox retention, dead-letter operations, monitoring, data retention, payment integration, production security and high availability need further work.

The provided brief describes a sprint and prioritises backend quality. The repository records real development commits and actual checks. It does not claim that the candidate has reviewed every AI-generated line or that unexecuted cloud/cluster checks have passed.
