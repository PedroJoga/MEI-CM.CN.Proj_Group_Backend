package com.example.backend.repositories;

import com.example.backend.domain.response.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    List<Response> findByComment_Id(Long commentId);
}
