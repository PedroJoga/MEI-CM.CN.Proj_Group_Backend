.DEFAULT_GOAL := deploy

ifneq (,$(wildcard ./.env))
    include .env
    export
endif

up:
	docker compose up -d

down:
	docker compose down

down-clear:
	docker compose down -v

build:
	mvn clean package -DskipTests

build-image:
	docker-compose build

deploy: build build-image up

sql:
	docker compose exec db psql --username ${DB_USER} --password ${DB_PASS}

ps:
	docker compose ps

logs:
	docker compose logs -f backend-api

monitor: deploy logs

