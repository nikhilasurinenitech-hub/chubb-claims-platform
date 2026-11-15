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
