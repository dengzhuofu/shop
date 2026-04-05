# GitHub Auto Deploy

## Goal

Push to `main` and let GitHub Actions:

1. build frontend and backend images
2. push them to GHCR
3. SSH into the server
4. pull immutable image digests
5. update the running stack with Docker Compose

The workflow file is `.github/workflows/deploy-production.yml`.

## Release Model

- CI builds images in GitHub, not on the server
- server deploys immutable image digests, not mutable tags
- database data stays in the persistent Docker volume `pg_data`
- runtime server only needs Docker, Compose, Nginx config, schema init file, and `.env`

## Required GitHub Secrets

Create these repository secrets in GitHub:

- `PROD_SERVER_HOST`
  Example: `101.200.239.103`
- `PROD_SERVER_PORT`
  Example: `22`
- `PROD_SERVER_USER`
  Example: `root`
- `PROD_PROJECT_DIR`
  Example: `/opt/shop`
- `PROD_SSH_PRIVATE_KEY`
  Private key used by GitHub Actions to SSH into the server
- `PROD_GHCR_USERNAME`
  GitHub username or machine user that can read GHCR packages
- `PROD_GHCR_READ_TOKEN`
  Token with at least `read:packages`

## One-Time Server Setup

Add the matching SSH public key to the server user:

```bash
mkdir -p /root/.ssh
chmod 700 /root/.ssh
cat >> /root/.ssh/authorized_keys <<'EOF'
<your-github-actions-public-key>
EOF
chmod 600 /root/.ssh/authorized_keys
```

The first successful workflow run will upload the deploy bundle and bootstrap Docker automatically.

You can also use the helper script in this repo to write all GitHub repository secrets in one shot:

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\remote\set-github-secrets.ps1 `
  -Repo "dengzhuofu/shop" `
  -ServerHost "101.200.239.103" `
  -ServerPort "22" `
  -ServerUser "root" `
  -ProjectDir "/opt/shop" `
  -SshPrivateKeyPath "C:\path\to\github_actions_key" `
  -GhcrUsername "<your-github-username>" `
  -GhcrReadToken "<your-ghcr-read-token>"
```

## GHCR Notes

The workflow pushes images to:

- `ghcr.io/<owner>/<repo>-backend`
- `ghcr.io/<owner>/<repo>-frontend`

Best practice is to keep package read access scoped to a dedicated token instead of using a broad personal token.

## What Happens On Each Push

1. GitHub Actions builds and pushes both images.
2. The workflow uploads a small deploy bundle to the server.
3. The server updates `.env` with:
   - `BACKEND_IMAGE`
   - `FRONTEND_IMAGE`
   - `DEPLOY_SHA`
4. The server runs `docker-compose -f compose.prod.yml pull`.
5. The server runs `docker-compose -f compose.prod.yml up -d --remove-orphans`.

## Rollback

Because deploys use immutable digests, rollback is simple:

1. pick a previous successful workflow run
2. reuse its backend and frontend image digests
3. rerun `deploy_registry.sh` with those old digests

## Manual Verification

After a deployment:

```bash
cd /opt/shop
docker-compose -f compose.prod.yml ps
curl -I http://127.0.0.1
curl http://127.0.0.1/api/category/tree
```
