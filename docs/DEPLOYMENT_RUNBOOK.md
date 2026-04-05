# Shop Cloud Deployment Runbook

## 1. Deployment Goal

Deploy the project to a single cloud server with:

- Nuxt frontend
- Spring Boot backend
- PostgreSQL database
- Nginx reverse proxy
- Docker Compose orchestration

The production entrypoint is `http://101.200.239.103`.

## 2. Target Topology

Only Nginx is exposed to the public network.

- `nginx`: public port `80`
- `frontend`: internal port `3000`
- `backend`: internal port `8081`
- `postgres`: internal port `5432`

The database is not published to the public internet.

## 3. Files Added For Deployment

- `compose.yml`: production compose stack
- `Dockerfile.frontend`: Nuxt production image
- `backend/Dockerfile`: Spring Boot production image
- `deploy/nginx/default.conf`: Nginx reverse proxy
- `deploy/.env.example`: production environment template
- `deploy/remote/bootstrap-server.sh`: server bootstrap and deploy script
- `deploy/remote/deploy_remote.py`: upload and remote deploy helper

## 4. Server Access Status

On `2026-04-05`, connectivity checks from the current workspace to `101.200.239.103` all timed out for these ports:

- `22`
- `80`
- `443`
- `3389`
- `2222`
- `22222`
- `60022`
- `8080`
- `8443`
- `8888`

This means deployment could not be executed from this workspace yet. The most likely causes are:

- the server is powered off
- the public IP is wrong
- the cloud security group blocks inbound traffic
- SSH is bound to a different port that was not provided

## 5. Required Port Policy

Open only the minimum public ports:

- `22/tcp` for SSH management
- `80/tcp` for HTTP access

Do not publish:

- `5432/tcp`
- `8081/tcp`
- `3000/tcp`

If HTTPS is added later, also open `443/tcp`.

## 6. Server-Side Deployment Steps

1. Upload the repository to the server, for example into `/opt/shop`.
2. Copy `deploy/.env.example` to `.env` in the project root and set a strong `POSTGRES_PASSWORD`.
3. Run `bash deploy/remote/bootstrap-server.sh /opt/shop`.
4. The script will:
   - install Docker if missing
   - enable the Docker service
   - open HTTP in `firewalld` or `ufw` when present
   - generate `.env` automatically if it does not exist
   - run `docker compose up -d --build`

## 7. One-Command Remote Deployment

Once SSH port `22` is reachable, the current workspace can deploy directly with:

```powershell
py deploy/remote/deploy_remote.py 101.200.239.103 root "<server-password>"
```

The script will:

- verify SSH reachability
- upload the project archive
- extract it to `/opt/shop`
- run the bootstrap script

## 8. Verification Commands

After deployment, validate on the server:

```bash
docker compose ps
docker compose logs nginx --tail=100
docker compose logs frontend --tail=100
docker compose logs backend --tail=100
curl http://127.0.0.1
curl http://127.0.0.1/api/category/tree
curl -X POST http://127.0.0.1/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@isinwheel.local","password":"123456"}'
```

## 9. Rollback

Rollback is image-based:

1. Keep the previous deployed directory or previous images.
2. Restore the prior compose file and `.env` if changed.
3. Run `docker compose up -d --build`.

The database volume `pg_data` is persistent, so rollback does not remove data unless the volume is deleted manually.
