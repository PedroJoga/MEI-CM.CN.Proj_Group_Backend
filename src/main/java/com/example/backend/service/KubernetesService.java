package com.example.backend.service;

import com.example.backend.domain.container.Container;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KubernetesService {
    @Autowired
    private KubernetesClient client;

    @Value("${k8s.namespace}")
    private String namespace;

    public DeploymentList getPods() {
        return client.apps().deployments().list();
    }

    public void addContainer(Container container) {
        createDeployment(container.getSubDomain(), container.getDockerImage(), container.getExposedPort());

    }

    public void createDeployment(String name, String image, int port) {
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName(name)
                .withNamespace(namespace)
                .endMetadata()
                .withNewSpec()
                .withReplicas(1)
                .withNewSelector()
                .addToMatchLabels("app", name)
                .endSelector()
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app", name)
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(name)
                .withImage(image)
                .addNewPort()
                .withContainerPort(port)
                .endPort()
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();

        client.apps().deployments().create(deployment);
    }
}
