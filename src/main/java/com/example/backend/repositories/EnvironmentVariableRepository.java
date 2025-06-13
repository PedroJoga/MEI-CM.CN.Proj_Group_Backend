package com.example.backend.repositories;

import com.example.backend.domain.container.EnvironmentVariable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentVariableRepository extends JpaRepository<EnvironmentVariable, Long> {
}
