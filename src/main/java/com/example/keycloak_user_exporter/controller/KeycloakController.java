package com.example.keycloak_user_exporter.controller;

import com.example.keycloak_user_exporter.dto.AuthRequest;
import com.example.keycloak_user_exporter.dto.AuthResponse;
import com.example.keycloak_user_exporter.dto.UserInfo;
import com.example.keycloak_user_exporter.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/keycloak")
@RequiredArgsConstructor
public class KeycloakController {
    private final KeycloakService keycloakService;

    /**
     * 6.1 Аутентификация
     * @param request
     * @return
     */
    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        String token = keycloakService.authenticate(request.username(), request.password());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * 6.2 + 6.3 Получение пользователей с группами
     * @param token
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserInfo>> getUsersWithGroups(@RequestHeader("Authorization") String token) {
        List<UserInfo> users = keycloakService.getUsersWithGroups(token);
        return ResponseEntity.ok(users);
    }

    /**
     * 6.4 Вывод в консоль
     * @param token
     * @return
     */
    @GetMapping(value = "/users/print", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> printUsers(@RequestHeader("Authorization") String token) {
        String result = keycloakService.printUsersFormatted(token);
        return ResponseEntity.ok(result);
    }
}
