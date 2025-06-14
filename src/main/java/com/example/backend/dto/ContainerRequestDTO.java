package com.example.backend.dto;

public record ContainerRequestDTO (String subDomain, String name, String dockerImage, int exposedPort) {
}
