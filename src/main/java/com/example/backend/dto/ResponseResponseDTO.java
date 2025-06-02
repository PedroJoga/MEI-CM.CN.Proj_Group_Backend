package com.example.backend.dto;

import java.sql.Timestamp;

public record ResponseResponseDTO(Long id, String text, String userPhotoLink, String username, Timestamp createdAt) {
}
