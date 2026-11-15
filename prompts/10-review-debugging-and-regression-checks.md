# Review and debugging

```text
Before we call this finished, go back through the code and look for things we've missed. Start with anything that could let the wrong person update a claim, lose data or leave a claim in the wrong state.

Show me the issues in order of importance. Fix confirmed bugs one at a time and check the fix, rather than doing a big refactor at this stage.
```

## Follow-up prompts

```text
If the containers are healthy but the browser gets an error after a restart, where would you look first? Trace the request through the proxy as well.
```

```text
Check that the sample data loads safely on later restarts too. Don't clear the claims we've already entered to get it working.
```
