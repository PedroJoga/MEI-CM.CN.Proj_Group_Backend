package com.example.backend.domain.environmentVariable;

import com.example.backend.domain.container.Container;
import com.example.backend.infra.security.EncryptedStringConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    @NotNull
    private String key;

    @NotNull
    @Convert(converter = EncryptedStringConverter.class)
    private String value;
}
