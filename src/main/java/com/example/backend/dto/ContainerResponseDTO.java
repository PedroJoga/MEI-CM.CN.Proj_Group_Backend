package com.example.backend.dto;

public record ContainerResponseDTO (Long id, String subDomain, String name, String dockerImage, int exposedPort) {
}
