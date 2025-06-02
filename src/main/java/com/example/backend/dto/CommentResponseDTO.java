package com.example.backend.dto;

import java.sql.Timestamp;

public record CommentResponseDTO(Long id, String text, String userPhotoLink, String username, Timestamp createdAt) {
}
