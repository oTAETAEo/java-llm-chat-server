#!/usr/bin/env bash
set -euo pipefail

DOMAIN="${DOMAIN:-api.activity-coaching.com}"
EMAIL="${LETSENCRYPT_EMAIL:?Set LETSENCRYPT_EMAIL before running this script.}"

cd "$(dirname "$0")/.."

mkdir -p letsencrypt certbot-www

if docker ps --format '{{.Names}}' | grep -qx 'chatbot-nginx'; then
  docker stop chatbot-nginx
fi

docker run --rm \
  -p 80:80 \
  -v "$PWD/letsencrypt:/etc/letsencrypt" \
  -v "$PWD/certbot-www:/var/www/certbot" \
  certbot/certbot:latest certonly \
    --standalone \
    --email "$EMAIL" \
    --agree-tos \
    --no-eff-email \
    -d "$DOMAIN"

echo "Issued certificate for $DOMAIN under $PWD/letsencrypt."
