package com.example.backend.repositories;

import com.example.backend.domain.environmentVariable.EnvironmentVariable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnvironmentVariableRepository extends JpaRepository<EnvironmentVariable, Long> {
    List<EnvironmentVariable> findByContainer_Id(Long containerId);
}
