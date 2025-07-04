# Cloud Computing Group Project
Pedro Shan Chen - 2240603

Joel Simões Batista - 2240594

## Introduction
This project is a simple container hosting app that hosts user's containers in the school's local kubernetes cluster.

This API serves as the backend to the web app, and is responsible for communicating with k8s to create deployments, services and ingress rules for the user's containers.

## Setup
There's two different Makefiles, one in the root directory, and one in the `kubernetes/` directory.

To get the project running in a cluster:
- Copy the .env.example to a .env file, and set the `K8S_NAMESPACE` to:
    - `cc-group-4` for the school's cluster
    - `default` for minikube
- Run `make up-cluster`
    - If there are any errors, the `kubernetes/` folder has another makefile to setup specific k8s resources.

The apps can be accessed here:
- [Frontend](http://containercraft.duckdns.org)
- [Backend](http://api.containercraft.duckdns.org)

## Other notes
- The `containercraft.duckdns.org` is currently poiting to the school's cluster IP (172.22.21.101)
- There's a `bruno/` directory with sample HTTP requests for common CRUD operations with the API
- Repository for this code: [Github PedroJoga/MEI-CM.CN.Proj_Group_Backend](https://github.com/PedroJoga/MEI-CM.CN.Proj_Group_Backend)


# ContainerCraft

## 📦 Project Setup

## 🚀 How to run the project in kubernetes

1. **Prepare [Frontend Image](https://github.com/PedroJoga/MEI-CM.CN.Proj_Group_Frontend)**:

2. **Copy the `.env` file**:

```bash
cp .env.example .env
```

Change the DOCKER_HUB_USER in `.env` for your docker hub username

Change the FRONTEND_FILE_PATH in `.env` for your [Frontend](https://github.com/PedroJoga/MEI-CM.CN.Proj_Group_Frontend) path

3. **Change for your frontend image in [frontend-deployment](./kubernetes/frontend/frontend-deployment.yaml)**:

4. **Change for your backend image in [backend-deployment](./kubernetes/backend/backend-deployment.yaml)**:

5. **Deploy to kubernetes cluster**:

NOTE: you will need Java and maven to compile backend

```bash
make up-push
```

That's it!