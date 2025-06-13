package com.example.backend.repositories;

import com.example.backend.domain.container.Container;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, String> {
}
