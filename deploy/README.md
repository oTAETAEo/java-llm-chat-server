# Lightsail backend deployment

This backend is intended to run on one Lightsail instance at:

- Frontend domain: `activity-coaching.com`
- API domain: `api.activity-coaching.com`

## 1. Lightsail firewall

In the Lightsail instance networking tab, allow:

- TCP `22` from your IP for SSH
- TCP `80` from anywhere for HTTP redirects
- TCP `443` from anywhere for HTTPS

Do not expose PostgreSQL `5432` or Redis `6379` publicly.

## 2. Initial server setup

SSH into the instance and run:

```bash
bash /opt/chatbot-backend/deploy/server-setup.sh
```

If this is the first upload, create the directory first:

```bash
sudo mkdir -p /opt/chatbot-backend
sudo chown -R "$USER":"$USER" /opt/chatbot-backend
```

The GitHub Actions workflow writes `/opt/chatbot-backend/.env` automatically from repository secrets.

## 3. Cloudflare Origin Certificate

The public DNS records are managed by Cloudflare. The recommended production
TLS mode is:

- DNS proxy: enabled for `api.activity-coaching.com`
- SSL/TLS mode: `Full (strict)`
- Origin certificate: Cloudflare Origin Certificate installed on Lightsail

The certificate files must exist on the server before starting nginx:

```bash
/opt/chatbot-backend/cloudflare-origin/api.activity-coaching.com.pem
/opt/chatbot-backend/cloudflare-origin/api.activity-coaching.com.key
```

Generate the private key and CSR on the server, submit only the CSR to
Cloudflare, then save the issued certificate as the `.pem` file above.

## 4. GitHub Actions secrets

Add these repository secrets:

- `LIGHTSAIL_HOST`: the instance static public IP or DNS name
- `LIGHTSAIL_USER`: usually `ubuntu`
- `LIGHTSAIL_SSH_PRIVATE_KEY`: private key that can SSH into the instance
- `POSTGRES_PASSWORD`: production PostgreSQL password
- `JWT_SECRET`: long random JWT signing secret
- `OPENAI_API_KEY`: OpenAI API key

Optional repository secrets:

- `POSTGRES_DB`: defaults to `ai_rag_db`
- `POSTGRES_USER`: defaults to `admin`
- `APP_CORS_ALLOWED_ORIGINS`: defaults to `https://activity-coaching.com`

Push to `main` or run the workflow manually. The workflow uploads the backend source to `/opt/chatbot-backend` and runs:

```bash
docker compose -f compose.prod.yml up -d --build
```

## 5. Useful checks

```bash
docker compose -f compose.prod.yml ps
docker compose -f compose.prod.yml logs -f app
curl -i https://api.activity-coaching.com/api/v1/auth/login
```
