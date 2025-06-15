.DEFAULT_GOAL := deploy

config:
	kubectl apply -f configmaps/
	kubectl apply -f secrets/

deploy-database: config
	kubectl apply -f postgres/

deploy-backend: config
	kubectl apply -f service-accounts/
	kubectl apply -f backend/

ingress:
	kubectl apply -f ingress.yaml

up: deploy-database deploy-backend ingress

clean:
	kubectl delete -f configmaps/
	kubectl delete -f secrets/
	kubectl delete -f postgres/
	kubectl delete -f backend/
	kubectl delete -f service-accounts/
	kubectl delete -f ingress.yaml

clean-backend:
	kubectl delete -f backend/

deploy: clean-backend deploy-backend ingress