package com.service.dao;

import com.service.entity.model.AdminToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminTokenRepository extends JpaRepository<AdminToken, Long> {

    Optional<AdminToken> findByToken(String token);

}
