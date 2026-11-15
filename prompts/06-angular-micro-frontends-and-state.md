# Angular micro-frontends and state

Use this to organise one Angular application for two personas without duplicating the domain rules.

## Developer prompt

```text
Build or review the Claimspace frontend in one Angular workspace with a shell, claimant remote, staff remote and shared library. Use the already approved Native Federation setup and pinned dependencies. Explain why these are separately built/runtime-loaded features and how the local Nginx deployment hosts them together.

The shell owns login, navigation, role-aware routes and remote-load failure handling. Claimant owns reporting an incident, listing personal claims, detail/history and responses. Staff owns the workqueue, pickup, assessment, information requests, approval, settlement, rejection and reporting.

Put typed API contracts and HTTP access behind the shared boundary. Share identity and Angular injection dependencies consistently across remotes. Keep business enforcement on the backend; UI permission checks improve navigation and action availability.

Use signals for small feature-local state and RxJS for HTTP/polling. Make selected detail, loading, errors, pending commands and draft inputs explicit. Stop polling on destroy, pause it when hidden and make eventual reporting freshness visible. Preserve drafts during failed or pending actions and tell users to refresh after 409 conflicts.

Avoid adding a state-management library without a concrete need. Explain when a lazy modular frontend would be simpler than federation and what independent remote deployment would additionally require.
```

## Challenge prompt

```text
Follow a staff assessment from the component through ClaimsApi to the returned detail. Where does its observed version come from? What happens on a failed request, a remote-load failure and a route change while polling? Does refreshing a list also refresh its selected detail? Verify the actual code rather than assuming it does.
```

## Artifacts to review

- [Angular workspace](../frontend/angular.json) and [shell federation manifest](../frontend/projects/shell/public/federation.manifest.json).
- [Shared API](../frontend/projects/shared/src/lib/api.ts), [claimant page](../frontend/projects/claimant/src/app/claimant-page.ts) and [staff page](../frontend/projects/staff/src/app/staff-page.ts).
- [Frontend design](../docs/architecture.md).
