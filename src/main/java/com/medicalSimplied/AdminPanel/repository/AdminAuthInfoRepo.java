package com.medicalSimplied.AdminPanel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medicalSimplied.AdminPanel.model.AdminAuthInfo;


@Repository
public interface AdminAuthInfoRepo extends JpaRepository<AdminAuthInfo, Integer> {

    Optional<AdminAuthInfo> findByUsername(String username);
    
}
