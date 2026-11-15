# Interview walkthrough and handoff

Use this to practise explaining the implementation from business behaviour to code and trade-offs.

## Developer prompt

```text
Help me prepare a clear technical walkthrough of Claimspace for the HCLTech/Chubb assessment panel. Use the actual code, design records and verification evidence. Create a five-minute runnable demo, a 15-to-20-minute technical route and deeper discussion points for the panel's longer session.

Start with the two personas and a new claim. Demonstrate pickup, a request for information, the claimant's response, reserve assessment, approval, settlement and the claimant's history. Use a separate claim for rejection. Explain requested amount versus reserve versus settlement and show how workload changes.

Trace one assessment from Angular state and ClaimsApi through the controller, application transaction, domain rule, JDBC version check, history/outbox, Kafka and the insights projection. Identify the exact ownership, idempotency and concurrency safeguards.

Ask me questions one at a time about service boundaries, REST versus Kafka, outbox crash windows, at-least-once delivery, projection replay, SQL, currency separation, federation, local state, testing and container delivery. Assess each explanation against the implementation and point me to files I should read when an answer is incomplete.

Explain which AI suggestions were implemented, which defects were corrected and what I have personally reviewed. Keep production identity, uploads, real payments, monitoring and HA as explicit follow-up work. Do not claim employer policy knowledge, production readiness or unexecuted validation.
```

## Challenge prompt

```text
Act as a reviewer and choose the weakest design trade-off. Ask me to defend it, then introduce a requirement such as confidential staff notes, real payments, a prolonged consumer outage or independently deployed remotes. Help me reason about the changes without pretending they are already implemented.
```

## Artifacts to review

- [Walkthrough and panel questions](../docs/walkthrough.md).
- [Architecture](../docs/architecture.md), [decisions](../docs/decisions/README.md), [limitations](../NOTES.md) and [image delivery](../docs/image-delivery.md).
