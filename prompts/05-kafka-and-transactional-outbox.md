# Kafka and the transactional outbox

Use this to design the failure behaviour as carefully as the successful path.

## Developer prompt

```text
Implement or inspect reliable claim-to-reporting communication without making submission depend on Kafka availability. Store each business change and a full claim snapshot in an outbox in the same PostgreSQL transaction.

Publish pending snapshots to claims.changed.v1, keyed by claim UUID. Mark publication only after broker acknowledgement. Explain how row locking and retries work and why a crash can still deliver the same event twice. Do not claim exactly-once delivery.

Include schema version, event identity and aggregate version in the contract. In insights-service, apply a snapshot only when it is newer than the stored claim version. Keep projection persistence transactional with safe Kafka acknowledgement. Explain duplicate and out-of-order handling, bounded consumer retries, the dead-letter topic and operator replay.

Use a compacted topic for retaining current snapshots. Derive workload and reserves from the projection, keep currencies separate, and show reporting freshness in the UI. Waiting claims are a subset of open claims; settled and rejected claims are closed.

Provide a real broker-outage and recovery check, a duplicate/older-event check and a replay plan. Run disruptive checks only in an isolated test environment or an agreed maintenance window; preserve existing user claims.
```

## Challenge prompt

```text
Trace a crash immediately before Kafka acknowledgement and immediately after acknowledgement but before the outbox transaction commits. Then deliver version 5 before version 4. Explain the outcome using the actual publisher and projection SQL. What requires manual intervention after a record reaches the DLT?
```

## Artifacts to review

- [Outbox publisher](../backend/claims-service/src/main/java/com/example/claims/infrastructure/OutboxPublisher.java).
- [Projection](../backend/insights-service/src/main/java/com/example/insights/service/ProjectionService.java), [consumer](../backend/insights-service/src/main/java/com/example/insights/infrastructure/ClaimEventConsumer.java) and [topic configuration](../backend/platform-common/src/main/java/com/example/platform/KafkaTopics.java).
- [Recovery check](../scripts/recovery-check.py) and [verification evidence](../docs/verification.json).
