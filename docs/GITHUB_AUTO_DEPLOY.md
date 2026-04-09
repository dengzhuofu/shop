# GitHub Auto Deploy

## Goal

Push to `main` and let GitHub Actions:

1. build frontend and backend images on GitHub-hosted runners
2. push them to GHCR
3. hand off deployment to a self-hosted runner running on the production server
4. pull immutable image digests locally on the server
5. update the running stack with Docker Compose

This avoids the unstable `ssh/scp` hop from GitHub-hosted runners to the server.

The workflow file is `.github/workflows/deploy-production.yml`.

## Release Model

- CI builds images in GitHub, not on the server
- server deploys immutable image digests, not mutable tags
- deployment runs on a self-hosted runner on the production server
- database data stays in the persistent Docker volume `pg_data`

## Required GitHub Secrets

Create these repository secrets in GitHub:

- `PROD_PROJECT_DIR`
  Example: `/opt/shop`
- `PROD_GHCR_USERNAME`
  Example: `dengzhuofu`
- `PROD_GHCR_READ_TOKEN`
  Token with at least `read:packages`
- `PROD_PAYMENT_DEFAULT_PROVIDER`
  Example: `alipay`
- `PROD_PAYMENT_ALIPAY_ENABLED`
  Example: `true`
- `PROD_PAYMENT_ALIPAY_SANDBOX`
  Example: `true`
- `PROD_PAYMENT_ALIPAY_FALLBACK_TO_MOCK`
  Example: `false`
- `PROD_PAYMENT_ALIPAY_GATEWAY`
  Optional: override the SDK gateway URL when your Alipay account requires a non-default endpoint
- `PROD_PAYMENT_ALIPAY_PARTNER`
  Optional: set this only for cross-border / MAPI-style Alipay accounts
- `PROD_PAYMENT_ALIPAY_APP_ID`
  Your Alipay app id
- `PROD_PAYMENT_ALIPAY_APP_PRIVATE_KEY`
  Your one-line base64 app private key
- `PROD_PAYMENT_ALIPAY_PUBLIC_KEY`
  Your one-line base64 Alipay public key
- `PROD_PAYMENT_ALIPAY_RETURN_URL`
  Example: `https://your-domain.example.com/payment/alipay-return`
- `PROD_PAYMENT_ALIPAY_NOTIFY_URL`
  Example: `https://your-domain.example.com/api/payment/alipay/notify`
- `PROD_PAYMENT_ALIPAY_SUBJECT_PREFIX`
  Example: `isinwheel`

For production, do not leave Alipay return and notify URLs on a bare server IP unless that exact public IP is the real public entrypoint registered in Alipay. These URLs should match the public domain and protocol the browser actually uses.
The deploy script now syncs these values into the server `.env` even when a secret is blank, so an empty GitHub secret will clear stale server-side Alipay values instead of silently keeping the old ones.

The old SSH secrets are no longer used by the workflow.

## One-Time Server Setup

### 1. Install the self-hosted runner on the server

Get a one-time runner registration token from GitHub:

1. Open repository `Settings`
2. Open `Actions`
3. Open `Runners`
4. Click `New self-hosted runner`
5. Copy the registration token

Then run on the server:

```bash
cd /opt/shop
chmod +x deploy/remote/install_self_hosted_runner.sh
sudo bash deploy/remote/install_self_hosted_runner.sh \
  "https://github.com/dengzhuofu/shop" \
  "<runner-registration-token>"
```

The script installs a runner with label `shop-prod`.

### 2. Make sure the runner user can deploy

The install script defaults to user `deploy`, which should:

- exist on the server
- belong to the `docker` group
- have write access to `/opt/shop`

## What Happens On Each Push

1. GitHub-hosted runner builds and pushes both images to GHCR.
2. GitHub uploads a small deploy bundle as a workflow artifact.
3. The self-hosted runner on the server downloads that artifact.
4. The server updates `.env` with:
   - `BACKEND_IMAGE`
   - `FRONTEND_IMAGE`
   - `DEPLOY_SHA`
5. The server runs `deploy_registry.sh`.
6. `deploy_registry.sh` pulls images and runs `docker compose -f compose.prod.yml up -d`.

## Rollback

Because deploys use immutable digests, rollback is simple:

1. pick a previous successful workflow run
2. reuse its backend and frontend image digests
3. rerun `deploy_registry.sh` with those old digests

## Manual Verification

After a deployment:

```bash
cd /opt/shop
docker compose -f compose.prod.yml ps
curl -I http://127.0.0.1
curl http://127.0.0.1/api/category/tree
```
