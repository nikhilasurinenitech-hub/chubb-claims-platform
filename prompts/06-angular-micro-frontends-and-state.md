# Angular frontend and state

```text
Build the Angular frontend for both user types in one workspace. Give the claimant and staff flows clear boundaries, with shared components where that makes sense.

The assessment mentions micro-frontends, so propose a sensible shell and feature structure without making the local setup difficult. Keep API calls and state handling easy to follow.

Let's get the main workflow working first, including loading states, errors and refreshing data after an action.
```

## Follow-up prompts

```text
Is the shared code doing too much? Keep business decisions on the backend, and check that moving between pages does not leave polling or stale state behind.
```
