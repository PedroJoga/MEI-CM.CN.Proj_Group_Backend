package com.example.backend.service;

import com.example.backend.domain.container.Container;
import com.example.backend.domain.environmentVariable.EnvironmentVariable;
import com.example.backend.domain.user.User;
import com.example.backend.dto.EnvironmentVariableResponseDTO;
import com.example.backend.repositories.ContainerRepository;
import com.example.backend.repositories.EnvironmentVariableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EnvironmentVariableService {

    @Autowired
    private EnvironmentVariableRepository environmentVariableRepository;
    @Autowired
    private ContainerRepository containerRepository;

    public EnvironmentVariableResponseDTO addEnvironmentVariable(User user, Long containerId, String key, String value) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new RuntimeException("Container not found."));

        if (!Objects.equals(container.getUser().getId(), user.getId())) {
            throw new RuntimeException("Container does not belong to the user");
        }

        EnvironmentVariable environmentVariable = new EnvironmentVariable();
        environmentVariable.setKey(key);
        environmentVariable.setValue(value);

        container.addEnvironmentVariable(environmentVariable);

        environmentVariableRepository.save(environmentVariable);

        return new EnvironmentVariableResponseDTO(
                environmentVariable.getId(),
                environmentVariable.getKey(),
                environmentVariable.getValue()
        );
    }

    public List<EnvironmentVariableResponseDTO> getEnvironmentVariablesByContainer(User user, Long containerId){
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new RuntimeException("Container not found."));

        if (!Objects.equals(container.getUser().getId(), user.getId())) {
            throw new RuntimeException("Container does not belong to the user");
        }

        List<EnvironmentVariable> environmentVariables = environmentVariableRepository.findByContainer_Id(containerId);
        return environmentVariables.stream()
                .map(environmentVariable -> new EnvironmentVariableResponseDTO(
                        environmentVariable.getId(),
                        environmentVariable.getKey(),
                        environmentVariable.getValue()))
                .collect(Collectors.toList());
    }

    public EnvironmentVariableResponseDTO editEnvironmentVariablesById(User user, String key, String value, Long environmentVariableId) {
        EnvironmentVariable environmentVariable = environmentVariableRepository.findById(environmentVariableId)
                .orElseThrow(() -> new RuntimeException("Environment variable not found"));

        if (!Objects.equals(environmentVariable.getContainer().getUser().getId(), user.getId())) {
            throw new RuntimeException("Environment variable does not belong to the user");
        }

        environmentVariable.setKey(key);
        environmentVariable.setValue(value);

        environmentVariableRepository.save(environmentVariable);

        return new EnvironmentVariableResponseDTO(
                environmentVariableId,
                environmentVariable.getKey(),
                environmentVariable.getValue()
        );
    }

    public void deleteEnvironmentVariableById(User user, Long environmentVariableId) {
        EnvironmentVariable environmentVariable = environmentVariableRepository.findById(environmentVariableId)
                .orElseThrow(() -> new RuntimeException("Environment variable not found"));

        if (!Objects.equals(environmentVariable.getContainer().getUser().getId(), user.getId())) {
            throw new RuntimeException("Environment variable does not belong to the user");
        }

        environmentVariable.getContainer().removeEnvironmentVariable(environmentVariable);

        environmentVariableRepository.delete(environmentVariable);
    }
}
