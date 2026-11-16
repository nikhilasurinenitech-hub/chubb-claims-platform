# Assessment evidence map

This maps the supplied scoring areas to inspectable evidence. It is not a prediction of the panel's score.

| Area | Points | Evidence |
| --- | ---: | --- |
| Planning, architecture and design thinking | 15 | Approved design and implementation plan committed first; architecture diagrams and decision records |
| Service and API design | 10 | OpenAPI, versioned event schema, validated commands and Problem Details errors |
| Clean architecture and discipline | 15 | Pure claim state machine, application contracts/port, JDBC adapter, transactions and separated reporting |
| Backend test automation | 8 | 18 Java tests against domain rules and real PostgreSQL; live Kafka outage/replay checks |
| Data and integration | 12 | Flyway, isolated SQL schemas/roles, decimal money, outbox and versioned projection |
| Backend functional completeness | 5 | Submission through information cycle to settlement/rejection, audit history and reporting |
| MFE architecture and components | 15 | Shell, claimant and staff builds; runtime federation; shared presentation and API library |
| Frontend test automation | 7 | 10 component/auth tests and 3 browser scenarios |
| State management and data flow | 8 | Feature signals, typed HTTP facade, pending/error state, version-bearing commands and visible async refresh |
| Theming, accessibility and UX | 8 | Shared responsive theme, labels, keyboard controls, focus styles, status/error announcements and mobile test |
| Frontend functional completeness | 7 | Both personas and decisions exercised in production browser builds |
| AI collaboration process | 25 | Saved developer prompt list, dated AI journal, design decisions, development commits and documented corrections; completed candidate reviews belong in the journal |
| Total available | 135 | Evidence and limitations are recorded throughout the repository |
