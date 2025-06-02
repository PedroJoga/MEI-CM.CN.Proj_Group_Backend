package com.example.backend.controllers;

import com.example.backend.domain.user.User;
import com.example.backend.dto.ResponseRequestDTO;
import com.example.backend.dto.ResponseResponseDTO;
import com.example.backend.service.ResponseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/responses")
public class ResponseController {

    @Autowired
    private ResponseService responseService;

    @PostMapping("")
    public ResponseEntity addResponse(Authentication authentication, @RequestBody @Valid ResponseRequestDTO body) {
        User user = null;
        boolean isAnonymous = body.isAnonymous();

        if (authentication == null || !authentication.isAuthenticated()) {
            // Guest, so always true
            isAnonymous = true;
        } else {
            user = (User) authentication.getPrincipal();
        }

        responseService.addResponse(body.text(), user, isAnonymous, body.commentId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<ResponseResponseDTO>> getResponsesByComment(@PathVariable Long commentId) {
        List<ResponseResponseDTO> responses = responseService.getResponsesByComment(commentId);
        return ResponseEntity.ok(responses);
    }

}
