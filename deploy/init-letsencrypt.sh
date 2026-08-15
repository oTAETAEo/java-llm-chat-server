#!/usr/bin/env bash
set -euo pipefail

DOMAIN="${DOMAIN:-api.activity-coaching.com}"
EMAIL="${LETSENCRYPT_EMAIL:?Set LETSENCRYPT_EMAIL before running this script.}"

cd "$(dirname "$0")/.."

cp deploy/nginx/conf.d/api.conf deploy/nginx/conf.d/api.conf.tls
cp deploy/nginx/conf.d/api.http-only.conf deploy/nginx/conf.d/api.conf
docker compose -f compose.prod.yml up -d nginx

docker compose -f compose.prod.yml run --rm certbot certonly \
  --webroot \
  --webroot-path /var/www/certbot \
  --email "$EMAIL" \
  --agree-tos \
  --no-eff-email \
  -d "$DOMAIN"

mv deploy/nginx/conf.d/api.conf.tls deploy/nginx/conf.d/api.conf
docker compose -f compose.prod.yml up -d nginx
