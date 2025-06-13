package com.example.backend.domain.container;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Long userId;

    @NotNull
    private String name;

    @NotNull
    private String dockerImage;

    private int exposedPort;

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnvironmentVariable> environmentVariables;

    public void addEnvironmentVariable(EnvironmentVariable environmentVariable) {
        if (environmentVariable == null) {
            return;
        }

        environmentVariable.setContainerId(this.id);
        environmentVariables.add(environmentVariable);
    }
}
