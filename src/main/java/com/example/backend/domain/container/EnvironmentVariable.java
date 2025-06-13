package com.example.backend.domain.container;

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
    @Pattern(regexp = "^[a-zA-Z0-9]{3,50}$", message = "ID must be 3-50 alphanumeric characters")
    private Container container;

    @NotNull
    private String key;

    @NotNull
    @Convert(converter = EncryptedStringConverter.class)
    private String value;
}
