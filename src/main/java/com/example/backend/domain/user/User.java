package com.example.backend.domain.user;

import com.example.backend.domain.comment.Comment;
import com.example.backend.domain.response.Response;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userPhotoLink;
    @NotNull
    private String username;
    @NotNull
    @Email
    @Column(unique=true)
    private String email;
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Response> responses = new ArrayList<>();

    public void addComment(Comment comment) {
        comment.setUser(this);
        this.comments.add(comment);
    }

    public void addResponse(Response response) {
        response.setUser(this);
        this.responses.add(response);
    }
}
