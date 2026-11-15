# Claims Platform Design

Approved in chat on 15 November 2026. The user approved Angular, Java/Spring Boot, PostgreSQL, Kafka, Podman/Compose, supporting libraries, and the two-service design. The user subsequently delegated remaining design decisions.

## Problem and scope

Deliver an explainable take-home claims platform for motor and property incidents. Claimants submit and track their own claims and answer information requests. Staff claim incoming work, assess liability, request information, and settle or reject. Team reporting covers status, workload, turnaround and outstanding exposure. The screenshots assign 65 marks to backend, 45 frontend, and 25 AI collaboration. The brief prioritises backend quality and describes a five-hour cap. Record actual work honestly; never invent time spent, user reviews or AI overrides.

## Alternatives and decision

A modular monolith reduces operating cost and is a sound time-boxed alternative. We choose two bounded services to demonstrate the required synchronous and event-driven communication: claims owns operational state; insights owns disposable projections. Splitting submission, assessment and settlement into separate services would distribute one invariant-heavy workflow across several transactions without independent team or scaling requirements. Keep those capabilities together.

Use one Angular workspace: shell plus separately built claimant/staff remotes, shared presentation components and typed contracts. Native Federation loads the remotes at runtime. One browser entry point serves both personas. Federation demonstrates the rubric's MFE criterion; a smaller product would normally start with lazy feature routes. Deployment together is a deliberate local-demo convenience, not a claim of independent production delivery.

## Components and boundaries

Claims service: API DTOs and Problem Details errors; domain state machine; application service for permissions, transactions and idempotency; infrastructure for PostgreSQL, security and Kafka outbox. Insights service: Kafka projection adapter and read API. Each owns a separate PostgreSQL schema; one server is a local resource-saving choice. No cross-schema reads. OpenAPI and versioned event contracts are checked in.

REST commits user-visible commands synchronously. Each write also stores an outbox event in the same SQL transaction. Publication is at least once: a crash after sending and before marking sent can repeat an event. Full claim snapshots carry aggregate version and event ID. Insights applies only a newer aggregate version, so duplicate and out-of-order delivery cannot regress the projection. Retry publication on failure; retry consumers and route persistent poison events to a dead-letter topic. Reporting is eventually consistent and shows last-update time. Claims remain usable when Kafka is unavailable.

## Data and workflow

Entities: claim (UUID, public reference, claimant, market, currency, type, incident details, requested amount, reserve, settlement, assignee, status, version, timestamps); information request (question, answer, lifecycle); claim history (actor, action, narrative, timestamp); idempotency receipt; outbox event; insights claim snapshot. Money uses decimal arithmetic. Exposures are grouped by currency with no fictional FX conversion. Six seeded APAC markets are illustrative demo configuration, not asserted company policy.

Allowed workflow: SUBMITTED -> UNDER_REVIEW -> AWAITING_INFORMATION -> UNDER_REVIEW; UNDER_REVIEW -> APPROVED -> SETTLED; UNDER_REVIEW -> REJECTED. Staff must own an assignment to progress it. A claim cannot settle without an approval and a positive amount within the assessed reserve. Rejection needs a reason. Final claims are immutable. Claimant ownership is enforced in the backend. Optimistic versions detect competing commands; assignment is transactional. Submission idempotency keys cannot be reused with different content.

Authentication is deliberately local demo identity with Spring Security HTTP Basic, BCrypt and roles; browser credentials are held in memory. It needs TLS and a real identity provider before production. No real identity documents or customer data. Seeded credentials are explicitly demo-only. No external emails, real payments, document upload, live insurance policy verification, FX provider, or identity-provider service. Settlement records a decision; it does not transfer money.

## UI and errors

Responsive, keyboard-accessible claimant and staff workspaces. Shared status badges, money display, feedback and form styles. Angular signals hold feature state; HTTP services isolate IO. No extra state-management framework. Role route guards improve navigation; API checks provide security. Forms validate before submission, preserve inputs on failure and show field errors. API errors use stable codes and Problem Details. Concurrent changes return 409 and prompt refresh. Feature import failures render recovery instructions. Reports show freshness and provide refresh. Short-lived in-process caching is limited to static market/reference data; mutable claims are never cached across requests.

## Acceptance and verification

1. Start from a clean database using Compose and reach the UI without Java/Node installed on the examiner host.
2. Claimant creates a claim; another claimant cannot read it; staff sees and assigns it.
3. Staff asks a question, claimant answers, staff assesses and approves then settles; timeline records every transition.
4. A separate claim can be rejected with a visible reason.
5. Invalid transitions, duplicate submissions, stale versions and unauthorised actions have deterministic responses.
6. Kafka failure does not roll back a committed claim; publication resumes after recovery.
7. Duplicate/older events do not double count or regress workload/exposure; amounts in different currencies stay separate.
8. Unit, integration, component and browser tests run with documented commands. CI uses real PostgreSQL/Kafka containers.
9. README, architecture/ER/sequence diagrams, ADRs, API contract, prompt log, honest AI journal, limitations and panel walkthrough accompany the code.
10. Compose is the supported local runtime. Kubernetes manifests are an additional deployment reference and their actual validation level must be disclosed.

## Open work for production

OIDC/SSO, TLS, managed secrets, malware-scanned uploads, real policy checks, payment integration, observability backend, retention rules and multi-region disaster recovery require separate scope. Versions and verified platforms are recorded in the final runbook rather than assumed.
