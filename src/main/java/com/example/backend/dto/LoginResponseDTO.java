package com.example.backend.dto;

public record LoginResponseDTO(UserResponseDTO user, String token) {
}
