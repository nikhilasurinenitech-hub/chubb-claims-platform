# Image delivery

The application consists of three application images plus standard PostgreSQL and Kafka images. Compose describes how they connect and supplies the local demonstration configuration.

## Build directly from source

Run docker compose up --build -d or podman compose up --build -d from the repository root. The build uses the host runtime's Linux architecture. Host Java and Node are not required.

## Load a supplied archive

Choose the image archive matching the examiner's machine:

| Machine | Architecture |
| --- | --- |
| Apple Silicon Mac or ARM64 Linux | arm64 |
| Typical Intel/AMD Windows, Linux or Intel Mac | amd64 |

The supplied archives use Docker's archive format with gzip compression. They contain the three application images; Compose pulls the standard PostgreSQL and Kafka images from their public registries on first use. Internet access is required for those first pulls.

For ARM64:

~~~bash
docker load -i claimspace-images-linux-arm64.tar.gz
docker compose --env-file release/arm64.env up --no-build -d
~~~

For Intel/AMD:

~~~bash
docker load -i claimspace-images-linux-amd64.tar.gz
docker compose --env-file release/amd64.env up --no-build -d
~~~

Use podman load and podman compose in place of the Docker commands when using Podman. Open http://localhost:8080 after readiness. The source repository is still needed for Compose, environment selection and the database bootstrap script.

Each archive has a SHA-256 sidecar and an image manifest listing tags, IDs and architecture. The local execution platform is recorded in docs/verification.json. Building an additional architecture is distinct from executing it; do not infer a successful native run from archive metadata alone.

## Produce the archives

Build the normal application images first, then:

~~~bash
python3 scripts/export-images.py --engine podman --arch arm64
python3 scripts/export-images.py --engine podman --arch amd64
~~~

The helper reuses a matching native image. For another architecture it copies compiled JVM bytecode and browser assets from the built images into the corresponding runtime base image. Those release Dockerfiles contain no RUN instructions, so packaging portable artifacts does not require CPU emulation. This does not itself establish runtime verification on the other CPU architecture.

Archives are written under the ignored artifacts/images directory by default. An explicit output directory can be supplied. No registry push or publication is performed.
