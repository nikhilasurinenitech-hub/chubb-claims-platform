# User experience and accessibility

Use this to make each business journey clear enough to demonstrate without explaining the UI first.

## Developer prompt

```text
Review Claimspace as both a claimant and an officer using the implemented Angular pages. Build a consistent responsive theme and reusable status, money, feedback and claim-detail components. Use fictional, readable sample data.

Make the full claimant/officer information loop and final decision visible through clear status labels and history. Separate saving an assessment, approving a claim and confirming settlement. Show the currency beside monetary values and explain that settlement records a decision without transferring money.

Use associated labels, keyboard-operable controls, visible focus, sensible headings, table headers and accessible error/status feedback. Check narrow screens for clipping and horizontal overflow. Handle empty, loading, error, success and stale-detail states. Preserve entered text after a failed submission.

Make reporting understandable: Open includes waiting cases; Waiting is a subset, and Closed includes settled/rejected cases. Keep per-currency reserves separate, show unassessed counts, and indicate asynchronous freshness rather than promising immediate totals.

List concrete manual checks and implement scoped improvements only after inspecting the relevant component. Keep implementation details out of ordinary user flows unless they help a user decide what to do next.
```

## Challenge prompt

```text
Complete a new claim using only the keyboard, then repeat on a narrow viewport. Can I discover why an action is unavailable, recover from an error without retyping everything, and understand why a workload row may not change after approval? Show observable checks and any remaining limitations.
```

## Artifacts to review

- [Shared UI](../frontend/projects/shared/src/lib/ui.ts) and [claim detail template](../frontend/projects/shared/src/lib/claim-detail.html).
- [Claimant template](../frontend/projects/claimant/src/app/claimant-page.html) and [staff template](../frontend/projects/staff/src/app/staff-page.html).
- [Browser scenarios](../frontend/e2e/claims.spec.ts).
