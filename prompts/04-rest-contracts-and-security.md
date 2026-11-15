# REST contracts and security

```text
Let's add the APIs for the claimant and staff flows. Keep the endpoints and response shapes consistent and document them in OpenAPI.

Make sure a claimant only sees their own claims and an officer can only progress work assigned to them. Validate requests on the server and return useful errors.

Also think through duplicate submissions and two people trying to update the same claim. Explain how the frontend should handle those cases.
```

## Follow-up prompts

```text
Check the API without relying on the UI restrictions. What happens if I try another claimant's ID, use the wrong role, or submit an update from an old screen?
```
