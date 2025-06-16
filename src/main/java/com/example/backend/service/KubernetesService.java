package com.example.backend.service;

import com.example.backend.domain.container.Container;
import com.example.backend.domain.environmentVariable.EnvironmentVariable;
import com.example.backend.dto.ContainerResponseDTO;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KubernetesService {
    @Autowired
    private KubernetesClient client;

    @Value("${k8s.namespace}")
    private String namespace;

    @Autowired
    private ContainerService containerService;

    public DeploymentList getPods() {
        return client.apps().deployments().list();
    }

    public void addContainer(ContainerResponseDTO container) {
        Container fullContainer = containerService.getContainerById(container.id());
        List<EnvironmentVariable> envVars = fullContainer != null ?
                fullContainer.getEnvironmentVariables() : new ArrayList<>();

        createDeployment(container.subDomain(), container.dockerImage(), container.exposedPort(), envVars);
        createService(container.subDomain(), container.exposedPort());
        createIngress(container.subDomain(), container.exposedPort());
    }

    public void deleteContainer(String subDomain) {
        if (containerService.subDomainExists(subDomain)) {
            deleteDeployment(subDomain);
            deleteSecret(subDomain);
            deleteService(subDomain);
            deleteIngress(subDomain);
        }
    }

    public void getStatus(String subDomain) {
        // TODO
    }

    public void createDeployment(String name, String image, int port, List<EnvironmentVariable> environmentVariables) {
        DeploymentBuilder deploymentBuilder = new DeploymentBuilder();
        Deployment deployment = null;

        // If container has associated envVariables, create secret
        if (environmentVariables != null) {
            // Create Secret for environment variables
            createSecret(name, environmentVariables);

            // Create EnvFromSource for the secret
            EnvFromSource envFromSource = new EnvFromSource();
            envFromSource.setSecretRef(new SecretEnvSource(name + "-secret", false));

             deployment = deploymentBuilder.withNewMetadata()
                    .withName(name + "-deployment")
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
                    // Add environment variables from Secret
                    .withEnvFrom(envFromSource)
                    .endContainer()
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();
        } else {
            deployment = deploymentBuilder.withNewMetadata()
                    .withName(name + "-deployment")
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
        }

        client.apps().deployments().create(deployment);
    }

    public void createService(String name, int port) {
        io.fabric8.kubernetes.api.model.Service service = new ServiceBuilder()
                .withNewMetadata()
                .withName(name + "-service")
                .withNamespace(namespace)
                .endMetadata()
                .withNewSpec()
                .addToSelector("app", name)
                .addNewPort()
                .withPort(port)
                .withTargetPort(new IntOrString(port))
                .endPort()
                .endSpec()
                .build();

        client.services().create(service);
    }

    public void createIngress(String name, int port) {
        Ingress ingress = new IngressBuilder()
                .withNewMetadata()
                .withName(name + "-ingress")
                .withNamespace(namespace)
                .endMetadata()
                .withNewSpec()
                .addNewRule()
                .withHost(String.format("%s.containercraft.duckdns.org", name))
                .withNewHttp()
                .addNewPath()
                .withPath("/")
                .withPathType("Prefix")
                .withNewBackend()
                .withNewService()
                .withName(name + "-service")
                .withNewPort()
                .withNumber(port)
                .endPort()
                .endService()
                .endBackend()
                .endPath()
                .endHttp()
                .endRule()
                .endSpec()
                .build();

        client.network().v1().ingresses().create(ingress);
    }

    private void deleteDeployment(String subDomain) {
        client.apps().deployments()
                .inNamespace(namespace)
                .withName(subDomain + "-deployment")
                .delete();
    }

    private void deleteService(String subDomain) {
        client.services()
                .inNamespace(namespace)
                .withName(subDomain + "-service")
                .delete();
    }

    private void deleteIngress(String subDomain) {
        client.network().v1().ingresses()
                .inNamespace(namespace)
                .withName(subDomain + "-ingress")
                .delete();
    }

    public void createSecret(String name, List<EnvironmentVariable> envVars) {
        Map<String, String> data = new HashMap<>();

        // Convert your EnvironmentVariable entities to key-value pairs
        for (EnvironmentVariable envVar : envVars) {
            data.put(envVar.getKey(), envVar.getValue());
        }

        Secret secret = new SecretBuilder()
                .withNewMetadata()
                .withName(name + "-secret")
                .withNamespace(namespace)
                .endMetadata()
                .withStringData(data) // Use stringData for plain text values
                .build();

        client.secrets().create(secret);
    }

    private void deleteSecret(String subDomain) {
        String secretName = subDomain + "-secret";
        System.out.println("Attempting to delete secret: " + secretName + " in namespace: " + namespace);

        Secret existing = client.secrets()
                .inNamespace(namespace)
                .withName(secretName)
                .get();

        if (existing != null) {
            System.out.println("Found secret: " + secretName + ", proceeding with deletion");
            List<StatusDetails> result = client.secrets()
                    .inNamespace(namespace)
                    .withName(secretName)
                    .delete();
            System.out.println("Secret deletion result: " + (result != null && !result.isEmpty() ? "Success" : "Failed"));
            if (result != null) {
                result.forEach(status -> System.out.println("Status: " + status.getName() + " - " + status.getKind()));
            }
        } else {
            System.out.println("Secret " + secretName + " not found");
        }
    }

    public void updateDeployment(Long containerId) {
        Container container = containerService.getContainerById(containerId);
        deleteDeployment(container.getSubDomain());
        deleteSecret(container.getSubDomain());
        createDeployment(container.getSubDomain(), container.getDockerImage(), container.getExposedPort(), container.getEnvironmentVariables());
    }
}
