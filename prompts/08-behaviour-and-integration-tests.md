# Testing the important flows

```text
Let's add tests around the parts most likely to break: status changes, permissions, duplicate submissions and two people updating the same claim. I'd also like a browser test for the customer-to-staff flow.

Use real database checks where mocks would hide a problem. Run the tests and tell me what's covered and what we still haven't checked.
```

## Follow-up prompts

```text
Have we actually checked the Kafka connection, or just tested our own code with mocked messages?
```
