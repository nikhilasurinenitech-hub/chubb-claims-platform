# Testing the important flows

```text
Add meaningful tests around the important behaviour rather than testing every getter or copying the implementation into assertions.

Cover the claim lifecycle, permissions, duplicate requests and conflicting updates. Include database integration checks and frontend tests for the key journeys. We should also check that Kafka updates reporting correctly.

Run the relevant tests, fix any failures, and summarise what was actually verified and what remains untested.
```

## Follow-up prompts

```text
Which failures would our unit tests miss? Pick the highest-risk integration gaps and suggest a practical way to test them.
```
