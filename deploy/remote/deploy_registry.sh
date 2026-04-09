#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="${1:-/opt/shop}"
BACKEND_IMAGE="${2:?BACKEND_IMAGE is required}"
FRONTEND_IMAGE="${3:?FRONTEND_IMAGE is required}"
DEPLOY_SHA="${4:-unknown}"
GHCR_USERNAME="${GHCR_USERNAME:-}"
GHCR_TOKEN="${GHCR_TOKEN:-}"

upsert_env() {
  local key="$1"
  local value="$2"
  local env_file="$3"

  touch "$env_file"
  if grep -q "^${key}=" "$env_file"; then
    python3 - "$env_file" "$key" "$value" <<'PY'
from pathlib import Path
import sys

path = Path(sys.argv[1])
key = sys.argv[2]
value = sys.argv[3]
lines = path.read_text().splitlines()
updated = []
for line in lines:
    if line.startswith(f"{key}="):
        updated.append(f"{key}={value}")
    else:
        updated.append(line)
path.write_text("\n".join(updated) + "\n")
PY
  else
    printf '%s=%s\n' "$key" "$value" >> "$env_file"
  fi
}

upsert_env_if_present() {
  local key="$1"
  local value="${2:-}"
  local env_file="$3"

  if [ -n "$value" ]; then
    upsert_env "$key" "$value" "$env_file"
  fi
}

sync_env_if_defined() {
  local key="$1"
  local env_file="$2"

  if [ -n "${!key+x}" ]; then
    upsert_env "$key" "${!key}" "$env_file"
  fi
}

cd "$PROJECT_DIR"

bash deploy/remote/bootstrap-server.sh "$PROJECT_DIR" prepare

if [ -n "$GHCR_USERNAME" ] && [ -n "$GHCR_TOKEN" ]; then
  printf '%s' "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USERNAME" --password-stdin
fi

upsert_env "BACKEND_IMAGE" "$BACKEND_IMAGE" ".env"
upsert_env "FRONTEND_IMAGE" "$FRONTEND_IMAGE" ".env"
upsert_env "DEPLOY_SHA" "$DEPLOY_SHA" ".env"
sync_env_if_defined "PAYMENT_DEFAULT_PROVIDER" ".env"
sync_env_if_defined "PAYMENT_ALIPAY_ENABLED" ".env"
sync_env_if_defined "PAYMENT_ALIPAY_SANDBOX" ".env"
sync_env_if_defined "PAYMENT_ALIPAY_FALLBACK_TO_MOCK" ".env"
sync_env_if_defined "PAYMENT_ALIPAY_GATEWAY" ".env"
sync_env_if_defined "PAYMENT_ALIPAY_PARTNER" ".env"
sync_env_if_defined "PAYMENT_ALIPAY_APP_ID" ".env"
sync_env_if_defined "PAYMENT_ALIPAY_APP_PRIVATE_KEY" ".env"
sync_env_if_defined "PAYMENT_ALIPAY_PUBLIC_KEY" ".env"
sync_env_if_defined "PAYMENT_ALIPAY_RETURN_URL" ".env"
sync_env_if_defined "PAYMENT_ALIPAY_NOTIFY_URL" ".env"
sync_env_if_defined "PAYMENT_ALIPAY_SUBJECT_PREFIX" ".env"
chmod 600 .env

bash deploy/remote/bootstrap-server.sh "$PROJECT_DIR" images

docker image prune -f >/dev/null 2>&1 || true
docker logout ghcr.io >/dev/null 2>&1 || true
