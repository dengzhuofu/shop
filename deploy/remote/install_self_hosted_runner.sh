#!/usr/bin/env bash
set -euo pipefail

if [ "${EUID:-$(id -u)}" -ne 0 ]; then
  echo "Please run as root." >&2
  exit 1
fi

REPO_URL="${1:?GitHub repository URL is required, e.g. https://github.com/dengzhuofu/shop}"
RUNNER_TOKEN="${2:?Runner registration token is required}"
RUNNER_USER="${RUNNER_USER:-deploy}"
RUNNER_HOME="${RUNNER_HOME:-/opt/actions-runner}"
RUNNER_VERSION="${RUNNER_VERSION:-2.330.0}"
RUNNER_ARCHIVE="actions-runner-linux-x64-${RUNNER_VERSION}.tar.gz"
RUNNER_DOWNLOAD_URL="https://github.com/actions/runner/releases/download/v${RUNNER_VERSION}/${RUNNER_ARCHIVE}"
RUNNER_NAME="${RUNNER_NAME:-$(hostname)-shop-prod}"
RUNNER_LABELS="${RUNNER_LABELS:-shop-prod}"

install_packages() {
  apt-get update
  DEBIAN_FRONTEND=noninteractive apt-get install -y curl tar gzip jq ca-certificates
}

ensure_user() {
  if ! id "$RUNNER_USER" >/dev/null 2>&1; then
    useradd -m -s /bin/bash "$RUNNER_USER"
  fi
  if getent group docker >/dev/null 2>&1; then
    usermod -aG docker "$RUNNER_USER"
  fi
}

install_packages
ensure_user

mkdir -p "$RUNNER_HOME"
chown -R "$RUNNER_USER:$RUNNER_USER" "$RUNNER_HOME"

if [ ! -f "$RUNNER_HOME/config.sh" ]; then
  tmp_archive="/tmp/${RUNNER_ARCHIVE}"
  curl -fL "$RUNNER_DOWNLOAD_URL" -o "$tmp_archive"
  tar -xzf "$tmp_archive" -C "$RUNNER_HOME"
  rm -f "$tmp_archive"
  chown -R "$RUNNER_USER:$RUNNER_USER" "$RUNNER_HOME"
fi

"$RUNNER_HOME/bin/installdependencies.sh"

if [ -f "$RUNNER_HOME/.runner" ]; then
  "$RUNNER_HOME/svc.sh" stop || true
  su - "$RUNNER_USER" -c "cd '$RUNNER_HOME' && ./config.sh remove --token '$RUNNER_TOKEN'" || true
fi

su - "$RUNNER_USER" -c "cd '$RUNNER_HOME' && ./config.sh --unattended --replace --url '$REPO_URL' --token '$RUNNER_TOKEN' --name '$RUNNER_NAME' --labels '$RUNNER_LABELS' --work '_work'"

"$RUNNER_HOME/svc.sh" install "$RUNNER_USER"
"$RUNNER_HOME/svc.sh" start
"$RUNNER_HOME/svc.sh" status || true

echo "Self-hosted runner installed."
echo "Runner name: $RUNNER_NAME"
echo "Runner labels: $RUNNER_LABELS"
