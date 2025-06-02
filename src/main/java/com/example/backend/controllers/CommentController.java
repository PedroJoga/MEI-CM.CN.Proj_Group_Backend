package com.example.backend.controllers;

import com.example.backend.domain.user.User;
import com.example.backend.dto.CommentRequestDTO;
import com.example.backend.dto.CommentResponseDTO;
import com.example.backend.dto.SuggestionResponseDTO;
import com.example.backend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("")
    public ResponseEntity addComment(Authentication authentication, @RequestBody @Valid CommentRequestDTO body) {
        User user = null;
        boolean isAnonymous = body.isAnonymous();

        if (authentication == null || !authentication.isAuthenticated()) {
            // Guest, so always true
            isAnonymous = true;
        } else {
            user = (User) authentication.getPrincipal();
        }

        commentService.addComment(body.text(), user, isAnonymous);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<CommentResponseDTO> comments = commentService.getCommentsByUser(user);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/random")
    public ResponseEntity<CommentResponseDTO> getRandomComment(Authentication authentication) {
        User user = null;
        if (authentication != null && authentication.isAuthenticated()) {
            user = (User) authentication.getPrincipal();
        }

        CommentResponseDTO randomComment = commentService.getRandomComment(user);
        return ResponseEntity.ok(randomComment);
    }

    @GetMapping("/{commentId}/suggestions")
    public ResponseEntity<List<SuggestionResponseDTO>> getCommentSuggestions(@PathVariable Long commentId) {

        List<SuggestionResponseDTO> suggestions = commentService.getCommentSuggestions(commentId);
        //String suggestions = commentService.getCommentSuggestions(commentId);
        return ResponseEntity.ok(suggestions);
    }
}
