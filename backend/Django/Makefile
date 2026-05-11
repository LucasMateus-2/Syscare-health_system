COMPOSE = docker compose
WEB = web

.PHONY: up down logs shell migrate test

up:
	$(COMPOSE) up --build

down:
	$(COMPOSE) down

logs:
	$(COMPOSE) logs -f $(WEB)

shell:
	$(COMPOSE) exec $(WEB) python manage.py shell

migrate:
	$(COMPOSE) exec $(WEB) python manage.py migrate

test:
	$(COMPOSE) exec $(WEB) pytest
