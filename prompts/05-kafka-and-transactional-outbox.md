# Kafka and reporting

```text
Let's hook up reporting through Kafka. My main concern is what happens if we save a claim and then fail to publish the message. How should we handle that?

Reporting can be a little behind, but we shouldn't lose the claim or count it twice when a message gets retried. Suggest an approach and talk me through the failure cases before implementing it.
```

## Follow-up prompts

```text
Can we test this by stopping Kafka, saving a claim and then bringing Kafka back? Check that the same claim eventually shows up in reporting.
```
