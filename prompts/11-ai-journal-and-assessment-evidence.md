# AI journal and assessment evidence

Use this throughout development to make decisions and corrections inspectable.

## Developer prompt

```text
Maintain an AI collaboration journal for Claimspace alongside the implementation. For each meaningful iteration, record the goal, relevant prompt or task, AI proposal, decision and reason, changed artifacts, check performed and observed outcome. Distinguish an AI recommendation, a developer decision and a verified result.

When a test or review challenges the first answer, record the failed assumption, evidence, correction and regression check. Discuss actual examples such as identity configuration, proxy DNS recovery and date-stable fixtures only to the extent supported by the repository and recorded results.

Map the 12 assessment areas to concrete files, tests and design records. Include planning and alternatives, API contracts, layering, data/integration, micro-frontends, state, UX, both user journeys and AI collaboration. Treat the 135 available points as a coverage guide, not a promised mark.

Keep prompts discoverable in prompts/ and link decisions to architecture/notes. Make commit messages describe meaningful changes. Keep developer review notes separate from AI-generated suggestions, and leave an unperformed review marked as pending. Do not manufacture previous prompts, elapsed time, test results, approvals or overrides.

Use docs/verification.json for recorded execution facts. Preserve distinctions between local tests, emulated architecture checks, schema validation and unexecuted remote CI/live-cluster work.
```

## Challenge prompt

```text
Audit the handoff for claims stronger than their evidence. Can each reported correction be traced to a code change or test? Which statements describe actual execution, which describe design intent, and which need a future developer review? Revise ambiguous wording.
```

## Artifacts to review

- [AI journal](../docs/ai-journal.md), [assessment evidence map](../docs/rubric-mapping.md) and [verification record](../docs/verification.json).
- [Review log template](review-log-template.md) and [design decisions](../docs/decisions/README.md).
