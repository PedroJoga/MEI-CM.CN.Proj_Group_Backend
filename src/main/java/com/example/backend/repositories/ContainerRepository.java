package com.example.backend.repositories;

import com.example.backend.domain.container.Container;
import com.example.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContainerRepository extends JpaRepository<Container, Long> {
    List<Container> findByUser(User user);
    Container findBySubDomain(String subDomain);
}
