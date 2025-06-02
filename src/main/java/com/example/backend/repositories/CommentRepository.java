package com.example.backend.repositories;

import com.example.backend.domain.comment.Comment;
import com.example.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUser(User user);
    //List<Comment> findByUser_IdNotOrUserIsNull(Long userId);
}

