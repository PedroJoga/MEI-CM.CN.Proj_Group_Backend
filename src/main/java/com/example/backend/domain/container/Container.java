package com.example.backend.domain.container;

import com.example.backend.domain.environmentVariable.EnvironmentVariable;
import com.example.backend.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Column(unique=true)
    @Pattern(regexp = "^(?!^(backend|frontend|postgres|group4-project)$)[a-z0-9]{3,50}$", message = "Subdomain must be 3-50 lowercase alphanumeric characters")
    private String subDomain;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String dockerImage;

    private int exposedPort;

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnvironmentVariable> environmentVariables = new ArrayList<>();;

    public void addEnvironmentVariable(EnvironmentVariable environmentVariable) {
        if (environmentVariable == null) {
            return;
        }
        this.environmentVariables.add(environmentVariable);
        environmentVariable.setContainer(this);
    }

    public void removeEnvironmentVariable(EnvironmentVariable environmentVariable) {
        if (environmentVariable == null) {
            return;
        }
        this.environmentVariables.remove(environmentVariable);
        environmentVariable.setContainer(null);
    }

}
