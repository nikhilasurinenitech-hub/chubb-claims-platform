# Claimspace

A runnable motor and property claims platform with an Angular frontend, Java 21 / Spring Boot backend, PostgreSQL storage and Kafka-driven team reporting. Claimants and claims officers use one web application.

[Public source repository](https://github.com/nikhilasurinenitech-hub/chubb-claims-platform) · [Download the runnable release](https://github.com/nikhilasurinenitech-hub/chubb-claims-platform/releases/tag/v1.0.0)

The complete workflow is implemented: submit, pick up, request and supply information, assess, approve, settle or reject, and track the decision. Settlement records a decision; it does not transfer money. All data and identities are fictional assessment fixtures.

## Start locally

Install Docker Desktop with Compose, or Podman with a Compose provider. Allocate about 6 GB RAM and 4 CPUs to the container runtime. Java and Node are supplied inside the image builds.

From this repository's root, run either:

~~~bash
docker compose up --build -d
~~~

or:

~~~bash
podman compose up --build -d
~~~

Open http://localhost:8080. The first build downloads images and dependencies. Subsequent starts use cached images. Wait until the web container is healthy:

~~~bash
podman compose ps
~~~

Use docker in place of podman for all Compose commands when using Docker.

On the development Mac, Podman and Compose have already been installed and the dedicated VM is named chubb-takehome. If it is stopped, start it first:

~~~bash
podman machine start chubb-takehome
~~~

On another Mac, initialise and start your own Podman machine if needed. The scripts/start.sh helper detects an available runtime and waits for the application.

## Start from the supplied images

The examiner can run the compiled application without building the source. Send `claimspace-project.zip` plus the image archive matching their machine. Extract the ZIP beside the archive, then open a terminal in the extracted `chubb-claims-platform` folder. Start Docker Desktop first.

Intel/AMD — one-line start (Bash, zsh or PowerShell 7):

~~~bash
docker load -i ../claimspace-images-linux-amd64.tar.gz && docker compose --env-file release/amd64.env up --no-build -d
~~~

Apple Silicon/ARM64:

~~~bash
docker load -i ../claimspace-images-linux-arm64.tar.gz && docker compose --env-file release/arm64.env up --no-build -d
~~~

`docker load` imports the prepared application images. `&&` runs the next part only after loading succeeds. `docker compose ... up --no-build -d` starts all five containers in the background using those images. PostgreSQL and Kafka download on first use, so that first start needs internet access. Docker/Podman must be running; host Java and Node are not required.

Check `docker compose ps` and wait for all services to be healthy, then open http://localhost:8080. Podman users can replace `docker` with `podman`. For terminals without `&&` support, run the load and Compose parts as two separate commands.

The same commands are in [RUN-INSTRUCTIONS.txt](RUN-INSTRUCTIONS.txt) and [image delivery](docs/image-delivery.md). On the development Mac, the files to send are collected in the ignored `artifacts/submission/` folder inside the Downloads project.

## Demo accounts

All four accounts use the password **ClaimsDemo!2026**.

| Account | Role | Purpose |
| --- | --- | --- |
| maya | Claimant | Submit and track Maya's claims |
| leo | Claimant | Demonstrate claimant isolation |
| alex | Claims officer | Pick up and assess claims |
| priya | Claims officer | Demonstrate assignment and concurrent-update controls |

Choose an account on the sign-in page. Eight sample claims span six illustrative APAC markets and several lifecycle states. These markets and coverage assumptions are demo configuration, not company policy.

Identity is kept in browser memory. Reloading the browser requires signing in again. Use the in-app Refresh controls to retrieve current claim data. Two tabs can be signed in as different personas.

## Suggested five-minute demo

1. Sign in as maya and report a Singapore motor incident for SGD 5,000.
2. In another tab, sign in as alex, search the new reference and pick it up.
3. Enter a question and request information.
4. As maya, refresh, open the claim and send a response.
5. As alex, refresh, save a reserve assessment of SGD 4,000, approve, then settle for SGD 3,800.
6. As maya, refresh to see the settlement and complete history.
7. Open team reporting. It refreshes every five seconds while visible; Kafka updates the read model asynchronously.
8. For rejection, create another claim, pick it up and reject it with a reason.

Only the assigned officer can progress a picked-up claim. Refresh a stale detail view after a 409 conflict.

## What is running

| Container | Responsibility |
| --- | --- |
| web | Angular shell, claimant/staff remotes and same-origin Nginx API routing |
| claims-service | Spring Boot REST API, claim rules, ownership, transactions, history and outbox |
| insights-service | Spring Boot Kafka consumer and workload/exposure read API |
| postgres | Separate claims and insights schemas with separate application roles |
| kafka | Versioned, compacted claim snapshots and a dead-letter topic |

Only the web port is published, bound to 127.0.0.1. Data persist in named PostgreSQL and Kafka volumes.

## Stop and restart

~~~bash
podman compose down
podman compose up -d
~~~

These commands retain saved claims. To release VM resources on the development Mac after stopping containers:

~~~bash
podman machine stop chubb-takehome
~~~

Removing the project's named volumes deletes its claims and events. Do that only when intentionally resetting the demo.

## Tests and build

Backend checks require Java 21, Maven and a working Docker-compatible API:

~~~bash
mvn -f backend/pom.xml verify
~~~

For this Mac's Podman VM, first configure the Testcontainers connection:

~~~bash
export DOCKER_HOST="unix://$(podman machine inspect chubb-takehome --format '{{.ConnectionInfo.PodmanSocket.Path}}')"
export TESTCONTAINERS_RYUK_DISABLED=true
export TESTCONTAINERS_HOST_OVERRIDE=localhost
mvn -f backend/pom.xml verify
~~~

Frontend checks require a compatible Node 24 release:

~~~bash
npm --prefix frontend ci
npm --prefix frontend test
npm --prefix frontend run build
~~~

Against the running Compose system:

~~~bash
python3 scripts/smoke.py
cd frontend
npx playwright install chromium
npm run e2e
~~~

See [Testing and verified results](docs/testing.md) for coverage, recovery tests and exact limits.

## Repository map

~~~text
backend/
  claims-service/src/main/java/com/example/claims/
    api/             HTTP transport
    domain/          Framework-free claim state machine
    service/         Transactions, application contracts and ClaimStore port
    infrastructure/  PostgreSQL adapter, outbox publisher and demo fixtures
  insights-service/  Kafka projection and reporting API
  platform-common/   Event contract, demo security, errors and health probe
  */src/main/resources/db/migration/
  */src/test/
frontend/projects/
  shell/             Login, navigation and runtime remote loading
  claimant/          Claimant feature
  staff/             Officer and reporting feature
  shared/            Typed API boundary, identity facade and reusable UI
openapi/             HTTP and event contracts
postman/             Executable API walkthrough collection
docs/                Design, decisions, tests, AI journal and panel walkthrough
prompts/             Developer prompts for planning, building and reviewing
deploy/              Database bootstrap and Kubernetes reference
scripts/             Start, stop, smoke and recovery helpers
.github/workflows/   Backend, frontend and full-system CI
docker-compose.yml   Supported local runtime
NOTES.md             Assumptions and deliberately deferred work
~~~

This follows the video's layered structure while retaining standard Maven source, resource and test locations.

## Read next

- [Architecture and data flow](docs/architecture.md)
- [Approved design](docs/plans/2026-11-15-claims-design.md)
- [Implementation plan](docs/plans/2026-11-15-claims-implementation.md)
- [Design decisions](docs/decisions/README.md)
- [Walkthrough and interview preparation](docs/walkthrough.md)
- [Assessment evidence map](docs/rubric-mapping.md)
- [Repository structure and video mapping](docs/repository-guide.md)
- [Developer prompts](prompts/ALL-PROMPTS.md)
- [AI working journal](docs/ai-journal.md)
- [API contract](openapi/claims.yaml)
- [Image delivery](docs/image-delivery.md)

The GitHub Actions workflow runs verification on pushes to main and pull requests. Check the repository's Actions tab for remote execution results. Kubernetes manifests are a deployment reference; Compose is the locally verified runtime. Prepared application images are supplied as Docker-format archives.
