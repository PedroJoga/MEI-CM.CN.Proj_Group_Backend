package com.example.backend.controllers;

import com.example.backend.domain.user.User;
import com.example.backend.dto.ContainerRequestDTO;
import com.example.backend.dto.ContainerResponseDTO;
import com.example.backend.dto.EnvironmentVariableRequestDTO;
import com.example.backend.dto.EnvironmentVariableResponseDTO;
import com.example.backend.service.EnvironmentVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/environmentvariables")
public class EnvironmentVariableController {

    @Autowired
    private EnvironmentVariableService environmentVariableService;

    @PostMapping("")
    public ResponseEntity<Void> addEnvironmentVariable(Authentication authentication, EnvironmentVariableRequestDTO body) {
        User user = (User) authentication.getPrincipal();
        environmentVariableService.addEnvironmentVariable(user, body.containerId(), body.key(), body.value());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/container/{containerId}")
    public ResponseEntity<List<EnvironmentVariableResponseDTO>> getEnvironmentVariablesByContainer(Authentication authentication, @PathVariable Long containerId) {
        User user = (User) authentication.getPrincipal();
        List<EnvironmentVariableResponseDTO> environmentVariables = environmentVariableService.getEnvironmentVariablesByContainer(user, containerId);
        return ResponseEntity.ok(environmentVariables);
    }

    @PutMapping("/{environmentVariableId}")
    public ResponseEntity<EnvironmentVariableResponseDTO> editEnvironmentVariableById(Authentication authentication, @PathVariable Long environmentVariableId, EnvironmentVariableRequestDTO body) {
        User user = (User) authentication.getPrincipal();
        EnvironmentVariableResponseDTO environmentVariable = environmentVariableService.editEnvironmentVariablesById(user, body.key(), body.value(), environmentVariableId);
        return ResponseEntity.ok(environmentVariable);
    }

    @DeleteMapping("/{environmentVariableId}")
    public ResponseEntity<Void> deleteEnvironmentVariableById(Authentication authentication, @PathVariable Long environmentVariableId) {
        User user = (User) authentication.getPrincipal();
        environmentVariableService.deleteEnvironmentVariableById(user, environmentVariableId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
