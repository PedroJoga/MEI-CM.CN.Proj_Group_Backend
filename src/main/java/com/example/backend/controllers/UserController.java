package com.example.backend.controllers;

import com.example.backend.domain.user.User;
import com.example.backend.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new UserResponseDTO(user.getUsername(), user.getEmail(), user.getUserPhotoLink()));
    }
}
