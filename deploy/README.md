# Lightsail backend deployment

This backend is intended to run on one Lightsail instance at:

- API domain: `api.chatbot.한국`
- TLS/domain value for nginx/certbot: `api.chatbot.xn--3e0b707e`

## 1. Lightsail firewall

In the Lightsail instance networking tab, allow:

- TCP `22` from your IP for SSH
- TCP `80` from anywhere for HTTP/Let's Encrypt
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

## 3. First TLS certificate

After DNS `api.chatbot.한국` points to this instance, run once on the server:

```bash
cd /opt/chatbot-backend
set -a
source .env
set +a
bash deploy/init-letsencrypt.sh
```

## 4. GitHub Actions secrets

Add these repository secrets:

- `LIGHTSAIL_HOST`: the instance static public IP or DNS name
- `LIGHTSAIL_USER`: usually `ubuntu`
- `LIGHTSAIL_SSH_PRIVATE_KEY`: private key that can SSH into the instance
- `POSTGRES_PASSWORD`: production PostgreSQL password
- `JWT_SECRET`: long random JWT signing secret
- `OPENAI_API_KEY`: OpenAI API key
- `LETSENCRYPT_EMAIL`: email for Let's Encrypt notices

Optional repository secrets:

- `POSTGRES_DB`: defaults to `ai_rag_db`
- `POSTGRES_USER`: defaults to `admin`
- `APP_CORS_ALLOWED_ORIGINS`: defaults to `https://chatbot.xn--3e0b707e,https://chatbot.한국`

Push to `main` or run the workflow manually. The workflow uploads the backend source to `/opt/chatbot-backend` and runs:

```bash
docker compose -f compose.prod.yml up -d --build
```

## 5. Useful checks

```bash
docker compose -f compose.prod.yml ps
docker compose -f compose.prod.yml logs -f app
curl -i https://api.chatbot.xn--3e0b707e/api/v1/auth/reissue
```
