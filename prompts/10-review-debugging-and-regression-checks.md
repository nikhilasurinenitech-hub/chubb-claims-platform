# Review and debugging

```text
Please review what we have before the final handoff. Look for bugs in permissions, status changes, concurrent updates, error handling and data consistency.

Start with the most important findings and point me to the relevant code. For a confirmed issue, reproduce it, make a focused fix and add a regression check where it helps. Avoid a broad rewrite just to change the style.
```

## Follow-up prompts

```text
The service health checks pass, but a request through the browser fails after a container restart. Trace the full request path before changing the application logic.
```

```text
Check startup and sample-data loading as well. Will this still work after another restart tomorrow? Keep the claims already entered in the running demo.
```
