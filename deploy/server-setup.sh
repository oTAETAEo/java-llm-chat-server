#!/usr/bin/env bash
set -euo pipefail

if ! command -v docker >/dev/null 2>&1; then
  curl -fsSL https://get.docker.com | sh
  sudo usermod -aG docker "$USER"
fi

sudo mkdir -p /opt/chatbot-backend
sudo chown -R "$USER":"$USER" /opt/chatbot-backend

echo "Docker is installed. Open the Lightsail firewall for TCP 22, 80, and 443."
echo "Then create /opt/chatbot-backend/.env with production secrets."
