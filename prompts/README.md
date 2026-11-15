# Developer prompt guide

This prompt set describes how a full-stack developer can direct AI through the Claimspace project: clarify the assessment, compare designs, implement one workflow at a time, challenge the result, and verify the delivery. Prompts are written in the developer's voice and can be copied into an AI coding session with the repository available.

Prepared on 16 November 2026 from the assessment materials and the implemented project. These are reusable developer prompts, rather than a verbatim transcript of the original build. The dated [AI working journal](../docs/ai-journal.md) records what was implemented, corrected and actually verified.

## Reference materials

| Reference | How to use it |
| --- | --- |
| `V2_FullStack_candidate_assessment_brief - FrontEnd BackEnd.docx` | Domain scope, two personas, six markets, backend priority and walkthrough expectations |
| Assessment slide image: **Java Full Stack Engineer** | Angular, Java/Spring Boot, REST, Kafka, service design, engineering practice and AI-assisted delivery |
| Assessment slide image: **Full Stack Developer: Areas of Assessment** | 65 backend, 45 frontend and 25 AI-collaboration points; use as a coverage checklist |
| `How to Create Take home repository_Video.mp4` | Repository organisation and examiner handoff. Around 01:00: save prompts; 02:00: prompts folder; around 02:06: AI journal |
| [Requirements interpretation](../docs/requirements.md) | Portable summary of the supplied materials and explicit scope boundaries |
| [Repository guide](../docs/repository-guide.md) | Mapping from the video's recommended shape to the actual Java/Angular workspace |

The DOCX, slide images and video were supplied separately. They are references, not application dependencies. The video allows Claude or a similar LLM; this project was developed with Codex.

## Prompt sequence

| Step | Prompt | Review focus |
| --- | --- | --- |
| 01 | [Requirements and delivery plan](01-requirements-and-delivery-plan.md) | Acceptance criteria, assumptions, priorities and time-box trade-offs |
| 02 | [Architecture and design decisions](02-architecture-and-design-decisions.md) | Boundaries, alternatives, consistency and dependency direction |
| 03 | [Domain and SQL model](03-domain-and-sql-model.md) | Valid transitions, money, ownership, migrations and idempotency |
| 04 | [REST contracts and security](04-rest-contracts-and-security.md) | DTOs, errors, permissions and concurrency |
| 05 | [Kafka and the transactional outbox](05-kafka-and-transactional-outbox.md) | Failure windows, duplicate delivery, ordering and replay |
| 06 | [Angular micro-frontends and state](06-angular-micro-frontends-and-state.md) | Shell/remotes, shared boundaries and predictable data flow |
| 07 | [User experience and accessibility](07-user-experience-and-accessibility.md) | Complete journeys, validation, keyboard use and async feedback |
| 08 | [Behaviour and integration tests](08-behaviour-and-integration-tests.md) | Meaningful tests at actual system boundaries |
| 09 | [Container images and local delivery](09-container-images-and-local-delivery.md) | Reproducible builds, image loading, readiness and persistence |
| 10 | [Review, debugging and regression checks](10-review-debugging-and-regression-checks.md) | Challenge generated code; reproduce and fix failures |
| 11 | [AI journal and assessment evidence](11-ai-journal-and-assessment-evidence.md) | Traceable decisions, actual checks and review records |
| 12 | [Interview walkthrough and handoff](12-interview-walkthrough-and-handoff.md) | Explain one action end to end and defend trade-offs |

## How I would use the prompts

1. Start with requirements and the design. Read the AI's assumptions before accepting an implementation plan.
2. Give one scoped prompt at a time. Ask the AI to inspect existing code, explain a proposed change, and keep unrelated files untouched.
3. Review the listed artifacts and use the challenge prompt in each file. A plausible explanation is followed by a code reference or an observable check.
4. Record the result and any correction in the [review log template](review-log-template.md), then append completed reviews to the AI journal. The blank template is not evidence that a review happened.
5. Use the final walkthrough to explain the implementation in your own words. Distinguish a verified local capability from a proposed production improvement.

The approved stack is Angular, Java 21/Spring Boot, PostgreSQL, Kafka, Native Federation, Flyway, Nginx, Docker-compatible images/Podman Compose and the existing test tools. Use the versions already pinned in the repository. Propose and explain any additional technology before introducing it. Use fictional data, keep money in separate currencies, and retain existing claims when checking a running demo.
