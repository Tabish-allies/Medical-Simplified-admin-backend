package com.medicalSimplied.AdminPanel.service;

import com.medicalSimplied.AdminPanel.model.AdminAuthInfo;
import com.medicalSimplied.AdminPanel.repository.AdminAuthInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private AdminAuthInfoRepo adminAuthInfoRepo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AdminAuthInfo loginAdmin(String username, String password) {

        AdminAuthInfo admin = adminAuthInfoRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username not found"));

        // 1️⃣ Check account status first
        if ("INACTIVE".equalsIgnoreCase(admin.getStatus())) {
            throw new RuntimeException("Admin account is inactive");
        }

        // 2️⃣ Check password validity using BCrypt
        if (!encoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 3️⃣ Return valid admin
        return admin;
    }
}
