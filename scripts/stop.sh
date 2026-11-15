#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1; then engine=docker; else engine=podman; fi
"$engine" compose down
echo "Stopped. Data remain in named volumes."
