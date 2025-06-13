package com.example.backend.domain.container;

import com.example.backend.domain.environmentVariable.EnvironmentVariable;
import com.example.backend.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Table(name = "containers")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Container {
    @Id
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]{3,50}$", message = "ID must be 3-50 alphanumeric characters")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private String name;

    @NotNull
    private String dockerImage;

    private int exposedPort;

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnvironmentVariable> environmentVariables = new ArrayList<>();;

    public void addEnvironmentVariable(EnvironmentVariable environmentVariable) {
        if (environmentVariable == null) {
            return;
        }

        environmentVariable.setContainer(this);
        environmentVariables.add(environmentVariable);
    }
}
