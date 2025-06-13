package com.example.backend.domain.container;

import com.example.backend.infra.security.EncryptedStringConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "environment_variables")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentVariable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "container_id")
    private Long containerId;

    @NotNull
    private String key;

    @NotNull
    @Convert(converter = EncryptedStringConverter.class)
    private String value;
}
