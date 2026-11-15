# Domain and SQL model

```text
Start with the claim model and its lifecycle. We need claimants to report incidents, officers to pick up and assess claims, an information-request loop, and a final settlement or rejection.

Suggest the tables and relationships, then implement the model and database migrations. Keep the status rules in the backend. Think about ownership, assignment, history and money values, including the different currencies.

Please explain the important rules before wiring this into controllers or screens.
```

## Follow-up prompts

```text
What invalid status changes could slip through if someone calls the API directly? Also, are we keeping the requested amount, assessed reserve and final settlement separate?
```
