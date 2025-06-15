.DEFAULT_GOAL := deploy

config:
	kubectl apply -f configmaps/
	kubectl apply -f secrets/

database: config
	kubectl apply -f postgres/

backend: config
	kubectl apply -f service-accounts/
	kubectl apply -f backend/

frontend:
	kubectl apply -f frontend/

ingress:
	kubectl apply -f ingress.yaml

up: database backend frontend ingress

clean:
	kubectl delete -f configmaps/
	kubectl delete -f secrets/
	kubectl delete -f postgres/
	kubectl delete -f backend/
	kubectl delete -f service-accounts/
	kubectl delete -f frontend/
	kubectl delete -f ingress.yaml

clean-backend:
	kubectl delete -f backend/

clean-frontend:
	kubectl delete -f frontend/

deploy: clean-backend clean-frontend backend frontend ingress

deploy-backend: clean-backend backend ingress