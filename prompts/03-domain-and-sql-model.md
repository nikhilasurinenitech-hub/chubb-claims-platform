# Domain and SQL model

```text
Let's start with the backend model. A customer reports a claim, someone from the claims team picks it up, and they may need to ask the customer for more information before deciding.

Work out the tables and status changes around that flow. We'll also need the history of what happened to each claim. Show me the model first, then add the migrations and business rules.
```

## Follow-up prompts

```text
One thing to check: the amount requested, the reserve and the final settlement aren't necessarily the same. Are we handling that, including different currencies?
```
