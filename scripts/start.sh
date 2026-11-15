#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1; then
  engine=docker
elif command -v podman >/dev/null 2>&1 && podman info >/dev/null 2>&1; then
  engine=podman
else
  echo "Start Docker Desktop or a Podman machine, then retry."
  exit 1
fi
"$engine" compose up --build -d
for attempt in {1..90}; do
  if curl -fsS "http://127.0.0.1:${WEB_PORT:-8080}/health/claims" >/dev/null && curl -fsS "http://127.0.0.1:${WEB_PORT:-8080}/health/insights" >/dev/null; then
    echo "Ready: http://localhost:${WEB_PORT:-8080} (demo password: see README)"
    exit 0
  fi
  sleep 2
done
echo "Startup timed out. Inspect: $engine compose logs --tail=100"
exit 1
