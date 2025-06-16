# ContainerCraft

## 📦 Project Setup

## 🚀 How to run the project in kubernetes

1. **Prepare [Frontend Image](https://github.com/PedroJoga/MEI-CM.CN.Proj_Group_Frontend)**:

2. **Copy the `.env` file**:

```bash
cp .env.example .env
```

Change the DOCKER_HUB_USER in `.env` for your docker hub username

3. **Push backend image to docker hub**:

NOTE: you will need Java and maven to compile backend

```bash
make push
```


That's it!