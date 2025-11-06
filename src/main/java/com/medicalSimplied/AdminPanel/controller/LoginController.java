// com.medicalSimplied.AdminPanel.controller.LoginController.java
package com.medicalSimplied.AdminPanel.controller;

import com.medicalSimplied.AdminPanel.model.AdminAuthInfo;
import com.medicalSimplied.AdminPanel.service.LoginService;
import com.medicalSimplied.AdminPanel.service.TokenService;
import com.medicalSimplied.AdminPanel.util.GlobalResponse;
import com.medicalSimplied.AdminPanel.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<GlobalResponse<Map<String, Object>>> loginAdmin(
            @RequestBody Map<String, String> requestBody) {

        String username = requestBody.get("username");
        String password = requestBody.get("password");

        AdminAuthInfo admin = loginService.loginAdmin(username, password);
        Map<String, Object> tokenPair = tokenService.issueTokenPair(admin);

        return ResponseEntity.ok(
                ResponseBuilder.success(tokenPair, "Login successful", "/api/admin/login")
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<GlobalResponse<Map<String, Object>>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        Map<String, Object> pair = tokenService.rotateRefresh(refreshToken);
        return ResponseEntity.ok(
                ResponseBuilder.success(pair, "Tokens refreshed", "/api/admin/refresh")
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<GlobalResponse<Void>> logout(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        tokenService.revokeRefreshToken(refreshToken);
        return ResponseEntity.ok(
                ResponseBuilder.success(null, "Logged out", "/api/admin/logout")
        );
    }



    @PostMapping("/greet")
    String greet(){
        return "Hello from Login Controller";
    }
}
