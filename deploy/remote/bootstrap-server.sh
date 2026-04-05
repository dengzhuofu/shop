#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="${1:-/opt/shop}"
DEPLOY_MODE="${2:-build}"
COMPOSE_CMD=""
COMPOSE_IS_V1="false"
DOCKER_MIRROR="${DOCKER_MIRROR:-https://docker.m.daocloud.io}"
IS_ROOT="false"

if [ "${EUID:-$(id -u)}" -eq 0 ]; then
  IS_ROOT="true"
fi

if [ ! -d "$PROJECT_DIR" ]; then
  echo "Project directory not found: $PROJECT_DIR" >&2
  exit 1
fi

cd "$PROJECT_DIR"

if ! command -v curl >/dev/null 2>&1 && [ "$IS_ROOT" = "true" ]; then
  if command -v apt-get >/dev/null 2>&1; then
    apt-get update
    apt-get install -y curl ca-certificates
  elif command -v dnf >/dev/null 2>&1; then
    dnf install -y curl ca-certificates
  elif command -v yum >/dev/null 2>&1; then
    yum install -y curl ca-certificates
  fi
fi

install_docker_from_apt() {
  apt-get update
  DEBIAN_FRONTEND=noninteractive apt-get install -y docker.io docker-compose
}

install_compose_plugin_from_apt() {
  apt-get update
  DEBIAN_FRONTEND=noninteractive apt-get install -y docker-compose-plugin
}

configure_docker_mirror() {
  mkdir -p /etc/docker
  if [ ! -f /etc/docker/daemon.json ]; then
    cat > /etc/docker/daemon.json <<EOF
{
  "registry-mirrors": ["${DOCKER_MIRROR}"]
}
EOF
  fi
}

if ! command -v docker >/dev/null 2>&1; then
  if [ "$IS_ROOT" != "true" ]; then
    echo "Docker is not installed and bootstrap is running without root privileges." >&2
    exit 1
  fi
  if command -v apt-get >/dev/null 2>&1; then
    install_docker_from_apt || curl -fsSL https://get.docker.com | sh
  else
    curl -fsSL https://get.docker.com | sh
  fi
fi

if [ "$IS_ROOT" = "true" ]; then
  configure_docker_mirror
  systemctl enable --now docker
  systemctl restart docker
fi

if ! docker compose version >/dev/null 2>&1 && [ "$IS_ROOT" = "true" ] && command -v apt-get >/dev/null 2>&1; then
  install_compose_plugin_from_apt || true
fi

if docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD="docker compose"
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE_CMD="docker-compose"
  COMPOSE_IS_V1="true"
else
  if [ "$IS_ROOT" != "true" ]; then
    echo "Docker Compose is not available and bootstrap is running without root privileges." >&2
    exit 1
  fi
  if command -v apt-get >/dev/null 2>&1; then
    DEBIAN_FRONTEND=noninteractive apt-get install -y docker-compose
  fi
  if docker compose version >/dev/null 2>&1; then
    COMPOSE_CMD="docker compose"
  elif command -v docker-compose >/dev/null 2>&1; then
    COMPOSE_CMD="docker-compose"
  else
    echo "Docker Compose is unavailable after Docker installation." >&2
    exit 1
  fi
fi

if [ ! -f .env ]; then
  DB_PASSWORD="$(od -An -N16 -tx1 /dev/urandom | tr -d ' \n')"
  cat > .env <<EOF
POSTGRES_DB=shop
POSTGRES_USER=shop
POSTGRES_PASSWORD=${DB_PASSWORD}
HTTP_PORT=80
JAVA_OPTS=-Xms256m -Xmx768m
EOF
fi

chmod 600 .env

if [ "$IS_ROOT" = "true" ] && systemctl is-active --quiet firewalld; then
  firewall-cmd --permanent --add-service=http
  firewall-cmd --reload
fi

if [ "$IS_ROOT" = "true" ] && command -v ufw >/dev/null 2>&1 && ufw status | grep -q "Status: active"; then
  ufw allow 80/tcp
fi

if [ "$DEPLOY_MODE" = "prepare" ]; then
  exit 0
fi

if [ "$DEPLOY_MODE" = "images" ]; then
  if [ "$COMPOSE_IS_V1" = "true" ]; then
    $COMPOSE_CMD -f compose.prod.yml pull --ignore-pull-failures
    $COMPOSE_CMD -f compose.prod.yml rm -sf backend frontend nginx || true
    $COMPOSE_CMD -f compose.prod.yml up -d db backend frontend nginx
  else
    $COMPOSE_CMD -f compose.prod.yml pull
    $COMPOSE_CMD -f compose.prod.yml up -d --remove-orphans
  fi
  exit 0
fi

$COMPOSE_CMD up -d --build
