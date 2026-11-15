# Kafka and reporting

```text
Now connect claim changes to the reporting side through Kafka. Reporting can update a little later, but saving a valid claim should not fail just because Kafka is unavailable.

Suggest a reliable way to handle that database-and-message boundary. Account for retries, repeated messages and messages arriving out of order. Then implement the approach and explain how we would recover reporting after a failure.
```

## Follow-up prompts

```text
Suppose the claim is saved but publishing fails. Or the message is published twice. Walk me through what happens in our implementation and how we can check it.
```
