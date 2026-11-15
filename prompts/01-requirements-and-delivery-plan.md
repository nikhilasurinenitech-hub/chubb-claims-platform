# Requirements and delivery plan

Use this before choosing the implementation details.

## Developer prompt

```text
I am building a full-stack motor and property claims assessment for HCLTech's Chubb interview. Read the supplied assessment DOCX, the Java Full Stack Engineer and Areas of Assessment slide images, and the repository-structure video. If an attachment is unavailable, identify it and use docs/requirements.md as the recorded interpretation; do not invent missing requirements.

Use Angular for the frontend and Java 21/Spring Boot for the backend. PostgreSQL, Kafka, Native Federation, Flyway, Nginx, Podman/Compose and the existing test libraries are approved. Ask before adding another technology.

Extract claimant and officer journeys, required reporting, non-functional needs and examiner deliverables. Separate explicit requirements from our assumptions. Produce acceptance criteria for submission, ownership, assignment, the information cycle, assessment, approval, settlement, rejection and reporting.

Propose a backend-first plan against the 65/45/25 scoring areas. Explain what fits a three-to-five-hour target and what must be deferred. Save the requirements interpretation, proposed design, implementation sequence and risks before changing application code. Do not invent an elapsed duration or a guaranteed score.

Include README, layered source, tests, migrations, API/event contracts, Compose, decisions, prompts and an AI journal in the delivery checklist.
```

## Challenge prompt

```text
Which decisions did the assessment actually prescribe, and which did we introduce? Give three assumptions that could change the design. If time is cut in half, prioritise a runnable vertical slice with real tests and explain what you would remove.
```

## Artifacts to review

- [Requirements](../docs/requirements.md), [design](../docs/plans/2026-11-15-claims-design.md) and [implementation plan](../docs/plans/2026-11-15-claims-implementation.md).
- [Assumptions](../NOTES.md) and [assessment evidence](../docs/rubric-mapping.md).
