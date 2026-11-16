# AI Working Journal

This is a chronological record of actual collaboration. The AI generated code and documentation; the candidate must review and understand the submission. Entries do not imply a human reviewed each line.

## 15 November 2026 — Requirements and approvals

- User requested a runnable full-stack take-home project using Angular and Java/Spring Boot with SQL, Docker-compatible delivery, repository structure from the supplied video, and explanations for an interview.
- Evidence reviewed: two attached JD/rubric screenshots; the user-specified assessment DOCX; the 5 minute 4 second repository video, sampled throughout with visible captions and detailed structure frames.
- AI identified that the brief is deliberately underspecified and prioritises a sound backend. The video recommends README, layered source, real tests, API contract, migrations, notes, prompts and journal.
- AI proposed PostgreSQL, Kafka, Podman/Compose and supporting migration/federation/web/test libraries. User explicitly approved these additions.
- AI compared a modular monolith, two bounded services, and a larger service split. Recommended claims plus insights, with a transactional outbox and eventual reporting.
- User explicitly approved the two-service design, then said to make architecture/design choices and explain them later.
- AI selected synthetic sample data. No real insurance or customer records are needed.
- AI installed Podman/Compose as authorised. Initial macOS video decoding failed in the sandbox; the read-only extraction succeeded with access to macOS media services.
- AI checked official Spring Boot, Angular, Native Federation and Podman documentation. The installed Node 23 is outside Angular's supported ranges; the bundled Node 24 is compatible and will be used for builds.
- No human overrides or code-review outcomes have occurred yet. Do not invent them for rubric credit.

## Backend implementation and verification

- Added ten domain tests first and confirmed compilation failed before the domain existed. Implemented the state machine and all ten passed.
- Added real PostgreSQL integration tests for API ownership, staff permissions, idempotency, stale writes, information cycle, settlement, rejection, outbox atomicity and versioned insights.
- Integration tests exposed a missing Spring Security password encoder registration. AI initially suspected the HTTP test client's challenge behaviour; logs showed the password encoding prefix was the actual cause. Registered the delegating encoder explicitly and kept preemptive-header tests matching the frontend.
- Recreated the newly created empty Podman VM after its interrupted first boot left a Linux account lock. A persistent terminal allowed clean initialisation; the VM responds on ARM64.
- Full backend `mvn verify` passed: ten unit tests and six PostgreSQL integration tests. Kafka transport remains to be exercised in the full-system test.
- Angular registry checks found CLI 22.2.7 and framework 22.2.6. Corrected the initial assumption that patch numbers matched. Federation adapter is 22.2.5. Dependency installation reported zero vulnerabilities at that point.

## Frontend and packaging

- Built one Angular workspace with a shell and separately built claimant/staff remotes, shared via Native Federation. Shared UI includes status, currency, feedback and detail presentation. Feature pages own HTTP state.
- Added ten component/authentication tests covering readable statuses, currency precision, accessible error feedback, credential lifetime, preventing credentials on non-API requests, permission-based actions and preserving draft text during pending requests. All ten passed.
- All three Angular production builds passed. Added five-second reporting refresh with teardown on navigation and paused polling for hidden tabs.
- Refined claims layering: controllers delegate to the application service; application contracts and the ClaimStore port belong to the service layer; the PostgreSQL adapter implements that port. Domain objects have no Spring annotations.
- Added client-error tests for missing required headers and malformed IDs, and reran the full backend suite: 17 tests passed.
- Added a compacted Kafka topic so current snapshots can be replayed, atomic demo seeding, and health probes using a small Java class instead of adding shell utilities to runtime images.
- Initial Dockerfile builds succeeded. Full-system browser, transport and recovery checks are next; no success has been claimed for these yet.

## Full-system checks and a corrected failure assumption

- Actual REST/Kafka smoke flow passed through Nginx: submission replay, claimant isolation, information response, assessment, approval, settlement and seven history entries. The outbox drained and reporting contained all six currencies separately.
- All three production browser scenarios passed on native ARM64: settlement, rejection and mobile form/layout validation.
- Stopped the actual Kafka container. A claim still committed and remained readable. After restarting Kafka, the exact claim appeared in the reporting projection at version 0.
- Container recreation preserved claims and reporting data. A replay test then exposed a real proxy weakness: Nginx had resolved backend IPs only at startup. After Podman assigned a new insights IP, internal health was green but the public proxy returned 502.
- AI replaced static upstream resolution with the container's runtime DNS resolver, a five-second cache, explicit upstream environment variables and a /tmp-rendered configuration. The same persistence/replay test then passed without restarting web; Kafka lag reached zero and SQL checksums were unchanged.
- The generated OpenAPI document initially contained empty required arrays. The strict validator rejected them; the document was corrected and passed. An actual emitted snapshot also passed the versioned JSON event schema.
- All 13 Kubernetes resources passed strict schema validation. No live Kubernetes cluster or remote GitHub Actions run was performed.
- Exported Docker-format gzip archives for ARM64 and AMD64. The AMD64 application images passed all three browser scenarios under the VM's pre-existing QEMU emulator. This was emulated AMD64 execution, not a native Intel/AMD host.
- Verified both database roles lack usage permission on the other service's schema.
- Visual inspection covered desktop reporting, claimant information response and mobile layout. No clipping or horizontal overflow was found.
- Audited the runtime data before cleanup: eight untouched seed claims and eight automated-test claims, with no unknown records. Backed up the fixtures and reset only this project. A fresh start produced eight claims, eight projections and no pending events.
- The user has not yet completed a line-by-line review. These checks and corrections are AI work, not invented candidate overrides.

- Removed unused IDE launch scaffolding and an empty stylesheet so the supported entry points remain the documented Compose run, build and test commands.

## Final restart review and date regression

- AI spotted a date-dependent fixture problem during final handoff review: seed keys were fixed but incident dates used the current day. A later-day restart could reuse a key with a changed payload.
- Added a PostgreSQL integration regression with a mutable clock. It reproduced the failure after advancing from 15 to 16 November: the replay raised an idempotency conflict.
- Replaced current-day fixture dates with fixed assessment dates and removed the seeder's clock dependency. This preserves the existing fixture payloads and keeps subsequent restarts deterministic.
- The complete backend suite now passes 18 tests, including the new later-day regression. The affected application image and both delivery archives are being refreshed.

- After the later-day fix, all three browser scenarios passed again on native ARM64 and on the refreshed AMD64 images under QEMU. Both refreshed image archives passed load checks.
- Restored the clean eight-claim demo from the Downloads repository after auditing and backing up only the four new automated-test records. The final verification record contains 18 backend tests, 10 component tests and 3 browser scenarios.

## 16 November 2026 — Developer prompt documentation

- The developer requested a prompt set written from a developer's perspective using the assessment brief, slides and repository video, instead of saved chat excerpts.
- Re-read the video's visible captions and structure frames, including the instruction to save prompts around 01:00 and the prompts-folder guidance around 02:00. The supplied screenshot also highlights the journal of decisions and iterations.
- Replaced the two chat-excerpt files in the current prompts folder with twelve focused developer prompts covering requirements/planning, architecture, domain/SQL, REST/security, Kafka/outbox, Angular federation/state, UX, tests, container delivery, debugging, collaboration evidence and panel preparation.
- Each prompt includes a follow-up challenge and links to implemented artifacts. Added a reference/index guide, an unfilled developer review template and a mapping from the video's layout to conventional Maven/Angular paths.
- The prompt guide identifies this as a reusable set prepared after the initial implementation. It does not turn the new prompts into a historical transcript or claim that template reviews were completed.
- This update changes documentation only. Existing test results remain the previously recorded execution results; application images and saved user claims do not require changes.
- Documentation checks passed for all twelve numbered prompts, 120 local Markdown links, balanced code fences and a documentation-only diff. Application test suites were not rerun for this documentation update.
- Generated a self-contained HTML prompt guide for reading alongside the source ZIP. A headless browser checked twelve sections, 25 copyable developer/challenge blocks, internal navigation and desktop/mobile widths; screenshots were visually inspected.

## 16 November 2026 — Direct developer prompt list

- Revised the prompt set to read as natural developer requests, with less implementation prescription and shorter follow-up questions.
- Removed the per-file usage introductions and the instructional guide text. Added prompts/ALL-PROMPTS.md containing the complete numbered set, alongside the twelve individual files and their index.
- Kept supplied-material references in the prompt index and moved the review-log template into docs so the prompts folder contains the prompts and their index.
- This remains a documentation-only update; the dated journal continues to distinguish actual implementation and verification from the prepared prompt set.
- Verified that the combined list exactly matches the twelve individual files and their thirteen follow-up prompts. All 53 local links in the changed Markdown passed; the updated HTML list passed desktop/mobile navigation and overflow checks.

- Refined the prompt wording again into shorter, conversational developer requests with varied sentence structure and practical follow-up questions. The numbered files and combined list retain the same twelve topics.
- Reordered the first prompt to introduce the claims application, its two user types and the intended stack before requesting a review of the brief, slides and repository video and a proposed plan.

## 16 November 2026 — Image startup instructions

- Added the exact combined image-load/Compose line to the root README, image delivery document and a tracked RUN-INSTRUCTIONS.txt. The earlier handoff listed these as separate commands.
- Standardised the archive location: extract the source ZIP beside the chosen archive and run from the extracted project folder, using ../ for the archive path.
- Explained image importing, Compose, background startup, runtime prerequisites, first-use downloads and later restarts. Kept separate-command instructions for terminals without && support.
- Updated packaging to refresh the source ZIP and run instructions in the existing ignored submission folder, so the copies shared from Downloads stay in sync with the delivery outputs.

## Repository publication preparation

- Preserved the existing local edits consolidating the developer prompts into prompts/ALL-PROMPTS.md and removing the individual prompt files and their index.
- Updated the README, walkthrough, repository guide, evidence map and implementation-plan references to the consolidated file. No application code, schema or container configuration changed.
- Checked the tracked file set and Git history for accidentally included image archives and generated dependencies. The prepared images and submission ZIP remain outside source control under the ignored artifacts folder.
