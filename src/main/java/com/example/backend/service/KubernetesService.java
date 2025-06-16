package com.example.backend.service;

import com.example.backend.domain.container.Container;
import com.example.backend.dto.ContainerResponseDTO;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBuilder;
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

    @Autowired
    private ContainerService containerService;

    public DeploymentList getPods() {
        return client.apps().deployments().list();
    }

    public void addContainer(ContainerResponseDTO container) {
        createDeployment(container.subDomain(), container.dockerImage(), container.exposedPort());
        createService(container.subDomain(), container.exposedPort());
        createIngress(container.subDomain(), container.exposedPort());
    }

    public void deleteContainer(String subDomain) {
        if (containerService.subDomainExists(subDomain)) {
            deleteDeployment(subDomain);
            deleteService(subDomain);
            deleteIngress(subDomain);
        }
    }

    public void getStatus(String subDomain) {
        // TODO
    }

    public void createDeployment(String name, String image, int port) {
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
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
}
