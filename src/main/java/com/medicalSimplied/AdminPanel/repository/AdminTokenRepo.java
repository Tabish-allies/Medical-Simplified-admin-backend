
package com.medicalSimplied.AdminPanel.repository;

import com.medicalSimplied.AdminPanel.model.AdminToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminTokenRepo extends JpaRepository<AdminToken, Long> {
    Optional<AdminToken> findByTokenHash(String tokenHash);
}
