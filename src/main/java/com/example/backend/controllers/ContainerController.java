package com.example.backend.controllers;

import com.example.backend.domain.container.Container;
import com.example.backend.domain.user.User;
import com.example.backend.dto.ContainerRequestDTO;
import com.example.backend.dto.ContainerResponseDTO;
import com.example.backend.service.ContainerService;
import com.example.backend.service.KubernetesService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/containers")
public class ContainerController {

    @Autowired
    private ContainerService containerService;

    @Autowired
    private KubernetesService kubernetesService;

    @GetMapping("/k8s")
    public ResponseEntity<DeploymentList> getPods() {
        return ResponseEntity.ok(kubernetesService.getPods());
    }

    @PostMapping("")
    public ResponseEntity<Void> addContainer(Authentication authentication, @RequestBody @Valid ContainerRequestDTO body) {
        User user = (User) authentication.getPrincipal();
        Container createdContainer = containerService.addContainer(user.getId(), body.subDomain(), body.name(), body.dockerImage(), body.exposedPort());
        kubernetesService.addContainer(createdContainer);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<ContainerResponseDTO>> addContainersByUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<ContainerResponseDTO> containers = containerService.getContainersByUser(user);
        return ResponseEntity.ok(containers);
    }

    @PutMapping("/{containerId}")
    public ResponseEntity<ContainerResponseDTO> editContainerById(Authentication authentication, @PathVariable Long containerId, @RequestBody @Valid ContainerRequestDTO body) {
        User user = (User) authentication.getPrincipal();
        ContainerResponseDTO responseDTO = containerService.editContainerById(user, body.subDomain(), body.name(), body.dockerImage(), body.exposedPort(), containerId);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{containerId}")
    public ResponseEntity<Void> deleteContainerById(Authentication authentication, @PathVariable Long containerId) {
        User user = (User) authentication.getPrincipal();
        containerService.deleteContainerById(user.getId(), containerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
