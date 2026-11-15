# Walkthrough and interview preparation

Start by running the application and completing both the settlement and rejection journeys yourself. The notes below explain the implemented code; they do not substitute for reviewing it.

## The one-minute explanation

Claimspace has one Angular application for claimants and officers. Two separately built Angular remotes provide the two workspaces. A Java 21 / Spring Boot claims service owns the workflow in PostgreSQL. A second Java / Spring Boot service consumes Kafka snapshots to maintain workload and exposure reporting.

User actions use REST because they need an immediate committed result or validation error. Reporting uses Kafka because it can update asynchronously and should not make submission depend on the reporting service. An outbox prevents the database-versus-broker dual-write problem. Aggregate versions make repeated or older events safe.

## A 15 to 20 minute presentation

| Time | Show | Explain |
| --- | --- | --- |
| 0–2 min | README and architecture diagram | Problem, two personas, selected scope and backend-first priority |
| 2–5 min | Domain Claim, ClaimService, ClaimStore and JDBC adapter | Lifecycle invariants, application transactions and the dependency boundary |
| 5–9 min | New claim, officer assignment and information cycle | Identity-derived ownership, two browser sessions, explicit version checks |
| 9–12 min | Assessment, approval and settlement; second rejection | Positive reserve, amount limits, reason and audit history |
| 12–14 min | Team dashboard | Async projection, freshness, per-currency reserves and unassessed counts |
| 14–17 min | Tests, outbox and recovery evidence | Real SQL/Kafka boundaries, at-least-once delivery and replay |
| 17–20 min | AI journal, decisions and limitations | What AI generated, what checks changed the implementation, and what remains |

Use separate tabs for maya and alex. The browser holds credentials in memory, so each tab signs in independently. In the same tab, sign out before changing persona.

## Follow one action through the code

For an officer assessment:

1. StaffPage sends the selected aggregate version, note and amount through ClaimsApi.
2. ClaimController authenticates the role and passes an application command.
3. ClaimService loads the claim and checks the observed version.
4. Claim.assess checks assignment, current status and decimal amount.
5. The JDBC adapter updates with an ID-and-version predicate.
6. The same transaction appends history and a versioned snapshot in the outbox.
7. REST returns the updated detail immediately.
8. The publisher sends the snapshot to Kafka and marks the row only after acknowledgement.
9. Insights applies it only if the version is newer, then acknowledges consumption.
10. The dashboard's next refresh shows the new reserve.

Open these files in that order. Be able to point to the exact transaction annotation, update predicate and projection version predicate.

## Questions the panel may ask

**Why two services?**  
Claims and reporting have different consistency and availability needs. Claims keeps workflow invariants together; reporting owns a derived model. A modular monolith would reduce deployment work and is a credible alternative. More service splits need stronger ownership or scaling reasons.

**Why use REST and Kafka?**  
REST gives a claimant an immediate reference and validation feedback. Kafka decouples reporting and provides replay. Making the initial command asynchronous would require a different acknowledgement and pending-result UX.

**What is the dual-write problem?**  
A database commit could succeed while a separate broker publish fails, or publishing could succeed while the database rolls back. The outbox stores the pending event with the business change in one SQL transaction.

**Is delivery exactly once?**  
No. Publication and consumer processing can repeat. The design achieves an idempotent projection using claim ID and aggregate version. A full snapshot at a newer version can safely supersede an older event.

**What if the broker is unavailable?**  
A valid claim still commits. Its event remains pending. Publication retries after the broker returns. This behaviour was exercised against the actual containers.

**What if the consumer database is down?**  
Processing retries are bounded. Persistent failures go to the dead-letter topic. An operator must fix the cause and replay. This differs from the publisher's retained outbox retry and must not be described as automatic recovery from every possible outage.

**How do two officers avoid overwriting each other?**  
Both might read version 0, but only one update can match ID plus version 0. The losing transaction rolls back and returns 409. All later actions also require the assigned officer.

**What happens when someone clicks Submit twice?**  
The client reuses a key for the same attempted payload. The server scopes that key to the authenticated claimant, locks it in the transaction and compares a request fingerprint. Identical retries return the original claim. Reusing it with changed content conflicts.

**Why SQL and JDBC?**  
Transactions, constraints, ownership, precise money and explicit locking fit this workflow. JDBC makes the SQL visible in a small model. JPA would be reasonable with more mappings, and would still need careful concurrency and outbox handling.

**Why not add every currency together?**  
SGD, AUD and JPY amounts cannot produce a meaningful single exposure total without agreed rates, rate dates and conversion policy. The dashboard shows separate totals and identifies unassessed claims.

**Are the remotes really separate builds?**  
Yes. Shell, claimant and staff each produce their own remote entry and compiled output. Native Federation loads exposed components at runtime. They share Angular and the workspace library as singletons. Local deployment places the outputs in one Nginx image; separate deployment would add release coordination and compatibility checks.

**Why signals instead of a larger state library?**  
Feature state is small and local. Signals make loading, error, selection and pending commands explicit, while typed services own IO. A larger shared domain state or offline requirements would justify reevaluating this.

**Why do browser reloads require signing in again?**  
The demonstration keeps credentials in memory. Production would use an appropriate identity provider and session/token lifecycle rather than persisting Basic credentials in browser storage.

**How was AI used?**  
AI generated the implementation and documentation. The user chose Angular/Java, approved the additions and service split, and delegated remaining design. Tests found real issues, including password encoder registration, proxy DNS after container IP changes and later-day fixture replay. The developer prompt set organises requirements, design, implementation and review questions; it was prepared after the initial build and is not a verbatim build transcript. The journal records actual corrections and checks. Add personal review notes only after inspecting the code and behaviour.

**Why can average resolution show 0.0 hours?**  
The fictional seed claims progress immediately at startup. The metric measures actual submission-to-final-decision time, which rounds to 0.0 hours for that instant fixture setup. Incident age is a different measure.

**What would you do next?**  
Prioritise real identity/TLS, policy validation and safe document handling; then operational metrics, dead-letter reprocessing, retention/backup rules and production deployment/HA. Add load and failure testing around the expected service objectives before splitting more services.

## Practice before the panel

- Complete a fresh claim without reading the guide.
- Explain every allowed status transition and the approval/settlement distinction.
- Identify which tests cover ownership, concurrency and event replay.
- Explain why Kafka can lag while a claimant sees the committed result.
- Locate the SQL schemas, migration, API and event contracts.
- Read the AI-generated code and record any corrections in the journal.
- Read the [developer prompt guide](../prompts/README.md), try its challenge questions and use the [review log template](review-log-template.md) for completed reviews.
- Be clear about what was actually executed locally and what remains a CI/Kubernetes reference.
