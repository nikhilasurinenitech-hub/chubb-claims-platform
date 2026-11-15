# Container images and local delivery

Use this to make the examiner's first run reproducible.

## Developer prompt

```text
Package Claimspace so an examiner can run it with Docker Compose or Podman Compose without installing host Java, Maven or Node. Inspect the existing Dockerfiles and docker-compose.yml before editing them.

Use multi-stage builds, a small non-root runtime where supported, readiness checks, persistent database/Kafka volumes and service-name networking. Publish only the local web entry point. Keep claims-service and insights-service configuration separate. Ensure Nginx resolves backend services after container IP changes and can render configuration with the configured read-only filesystem.

Provide two documented paths: build from source, or load the prepared Docker-format image archive and start with --no-build. Supply ARM64 and AMD64 application bundles, matching release environment files, image manifests and SHA-256 checksums. Explain that PostgreSQL and Kafka are separate public images pulled on first use and that Compose and the database bootstrap files are still needed.

Document exact load/start/status/log/stop/restart commands, the URL, demo users and persistent-data behaviour. Keep Kubernetes optional and label its actual validation level. Test archive loading and the supported local startup. State whether each architecture was executed natively or under emulation.

Do not upload images, publish a repository or erase saved user data as part of packaging.
```

## Challenge prompt

```text
Assume the examiner has a different CPU architecture and an empty image cache. What files and network access do they need? Now restart a backend with a changed IP: why should the web proxy recover? Show how stopping containers differs from deleting their volumes.
```

## Artifacts to review

- [Backend Dockerfile](../backend/Dockerfile), [frontend Dockerfile](../frontend/Dockerfile) and [Compose](../docker-compose.yml).
- [Image delivery](../docs/image-delivery.md), [ARM64 configuration](../release/arm64.env), [AMD64 configuration](../release/amd64.env) and [export helper](../scripts/export-images.py).
- [Kubernetes scope](../deploy/k8s/README.md).
