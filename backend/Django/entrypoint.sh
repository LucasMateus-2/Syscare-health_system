#!/bin/sh
set -e

DB_HOST="${DB_HOST:-db}"
DB_PORT="${DB_PORT:-5432}"
DB_USER="${DB_USER:-${POSTGRES_USER:-syscare}}"
DB_NAME="${DB_NAME:-${POSTGRES_DB:-syscare}}"

echo "Waiting for PostgreSQL at ${DB_HOST}:${DB_PORT}..."

elapsed=0
until pg_isready -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME"
do
    elapsed=$((elapsed + 1))

    if [ "$elapsed" -ge 30 ]; then
        echo "PostgreSQL did not become ready within 30 seconds."
        exit 1
    fi

    sleep 1
done

python manage.py migrate --noinput

if [ "${DJANGO_ENV:-development}" = "production" ]; then
    python manage.py collectstatic --noinput
fi

exec gunicorn syscare.wsgi:application \
    --bind 0.0.0.0:8000 \
    --workers 2
