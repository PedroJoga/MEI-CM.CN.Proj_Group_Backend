package com.example.backend.service;

import com.example.backend.domain.container.Container;
import com.example.backend.domain.user.User;
import com.example.backend.dto.ContainerResponseDTO;
import com.example.backend.repositories.ContainerRepository;
import com.example.backend.repositories.EnvironmentVariableRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ContainerService {

    @Autowired
    private ContainerRepository containerRepository;

    public void addContainer(User user, String subDomain, String name, String dockerImage, int exposedPort) {

        Container container = new Container();
        container.setUser(user);
        container.setSubDomain(subDomain);
        container.setName(name);
        container.setDockerImage(dockerImage);
        container.setExposedPort(exposedPort);

        containerRepository.save(container);
    }

    public List<ContainerResponseDTO> getContainersByUser(User user) {
        List<Container> containers = containerRepository.findByUser(user);

        return containers.stream()
                .map(container -> new ContainerResponseDTO(
                        container.getId(),
                        container.getSubDomain(),
                        container.getName(),
                        container.getDockerImage(),
                        container.getExposedPort()))
                .collect(Collectors.toList());
    }

    public ContainerResponseDTO editContainerById(User user, String subDomain, String name, String dockerImage, int exposedPort, Long containerId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));

        if (!Objects.equals(container.getUser().getId(), user.getId())) {
            throw new RuntimeException("Container does not belong to the user");
        }

        container.setSubDomain(subDomain);
        container.setName(name);
        container.setDockerImage(dockerImage);
        container.setExposedPort(exposedPort);

        return new ContainerResponseDTO(
                containerId,
                container.getSubDomain(),
                container.getName(),
                container.getDockerImage(),
                container.getExposedPort()
        );
    }

    public void deleteContainerById(User user, Long containerId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new RuntimeException("Container not found"));

        if (!Objects.equals(container.getUser().getId(), user.getId())) {
            throw new RuntimeException("Container does not belong to the user");
        }

        containerRepository.deleteById(containerId);
    }
}
