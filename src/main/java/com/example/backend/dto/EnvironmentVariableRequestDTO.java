package com.example.backend.dto;

public record EnvironmentVariableRequestDTO(Long containerId, String key, String value) {
}
