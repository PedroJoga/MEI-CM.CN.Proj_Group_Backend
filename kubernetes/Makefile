.DEFAULT_GOAL := deploy

deploy-database:
	kubectl apply -f configmaps/
	kubectl apply -f secrets/
	kubectl apply -f postgres/

deploy: deploy-database