# Architecture and design decisions

Use this to compare approaches and make boundaries explicit.

## Developer prompt

```text
Design Claimspace using the agreed requirements and stack. Compare a modular monolith, two services for claims and reporting, and a larger microservice split. Compare consistency, operational cost, failure isolation, team ownership and assessment time. Recommend the smallest design that demonstrates the required REST and Kafka communication with a clear business reason.

For the two-service option, keep submission, assessment and settlement rules together in claims-service. Let insights-service own an asynchronous reporting projection. Use separate PostgreSQL schemas and application roles; neither service should query the other's schema.

Describe api, domain, service and infrastructure responsibilities. Keep the claim state machine independent of Spring, HTTP and SQL. Put transactions and use cases in the service layer and persistence adapters behind an application-owned port.

Show the runtime architecture, claim lifecycle, data model and a submission-to-reporting sequence. Explain synchronous versus eventual consistency, the database/broker dual-write risk, caching choices and what happens during failures.

Write decision records with the chosen option, alternatives, reason and cost. Map the layout to the repository video while keeping standard Maven and Angular conventions. Review existing implementation before proposing a redesign.
```

## Challenge prompt

```text
Defend two services over a modular monolith. What deployment and recovery work does the split add? Identify one boundary we deliberately kept together and one requirement that would justify another split later.
```

## Artifacts to review

- [Architecture and diagrams](../docs/architecture.md).
- [Decision records](../docs/decisions/README.md) and [repository layout](../docs/repository-guide.md).
- [Application persistence port](../backend/claims-service/src/main/java/com/example/claims/service/ClaimStore.java).
