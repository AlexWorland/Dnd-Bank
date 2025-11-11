#!/usr/bin/env bash
set -euo pipefail

BACKUP_DIR=${BACKUP_DIR:-/project-backups}
INTERVAL=${BACKUP_INTERVAL_SECONDS:-3600}
DB_URI="postgresql://${POSTGRES_USER}:${POSTGRES_PASSWORD}@db:5432/${POSTGRES_DB}"

echo "Starting backup service. Interval: ${INTERVAL}s"
mkdir -p "$BACKUP_DIR"

while true; do
  TIMESTAMP=$(date +%Y%m%d-%H%M%S)
  FILE="$BACKUP_DIR/dnd-bank-${TIMESTAMP}.sql"
  echo "[backup] Writing $FILE"
  pg_dump --dbname="$DB_URI" > "$FILE"
  find "$BACKUP_DIR" -type f -mtime +7 -delete || true
  sleep "$INTERVAL"
done
