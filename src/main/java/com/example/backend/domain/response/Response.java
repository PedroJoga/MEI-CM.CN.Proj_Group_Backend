package com.example.backend.domain.response;

import com.example.backend.domain.comment.Comment;
import com.example.backend.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Table(name = "responses")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String text;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @NotNull
    private boolean isAnonymous; // default is false
    @ManyToOne
    @NotNull
    @JoinColumn(name = "comment_id")
    private Comment comment;
    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    public Long getUserId() {
        return user.getId();
    }
}
