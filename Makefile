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
	docker build -t ${DOCKER_HUB_USER}/cc-proj-group-backend .

deploy: build build-image up

sql:
	docker compose exec db psql --username ${DB_USER} --password -d ${DB_NAME}

ps:
	docker compose ps

logs:
	docker compose logs -f backend-api

monitor: deploy logs

build-image-amd64:
	docker buildx build --platform linux/amd64 -t ${DOCKER_HUB_USER}/cc-proj-group-backend .

push-image:
	docker push ${DOCKER_HUB_USER}/cc-proj-group-backend

push: build build-image-amd64 push-image

up-minikube: build
	minikube image build -t minikube-backend .
	make -C kubernetes/ deploy

push-and-cluster: push
	make -C kubernetes/ deploy
