package com.example.keycloak_user_exporter.service;

import com.example.keycloak_user_exporter.dto.GroupInfo;
import com.example.keycloak_user_exporter.dto.UserInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KeycloakService {
    @Value("${keycloak.url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Логика получения токена
     */
    public String authenticate(String username, String password) {
        String authUrl = keycloakUrl + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=password&client_id=admin-cli&username=" +
                username + "&password=" + password;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    /**
     * Реализация получения пользователей
     */
    private List<UserInfo> getUsers(String token) {
        String usersUrl = keycloakUrl + "/admin/realms/" + realm + "/users";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                usersUrl, HttpMethod.GET, entity, String.class);

        try {
            return objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<List<UserInfo>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("Error parsing users", e);
        }
    }

    /**
     * Получение пользователей и их групп
     */
    public List<UserInfo> getUsersWithGroups(String token) {
        List<UserInfo> users = getUsers(token);
        return users.stream()
                .map(user -> {
                    List<GroupInfo> groups = getUserGroups(token, user.id());
                    return new UserInfo(
                            user.id(), user.username(), user.firstName(),
                            user.lastName(), user.email(), user.enabled(), groups
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Реализация получения групп пользователя
     */
    private List<GroupInfo> getUserGroups(String token, String userId) {
        String groupsUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/groups";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                groupsUrl, HttpMethod.GET, entity, String.class);

        try {
            return objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<List<GroupInfo>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("Error parsing groups for user: " + userId, e);
        }
    }

    public String printUsersFormatted(String token) {
        List<UserInfo> users = getUsersWithGroups(token);
        StringBuilder sb = new StringBuilder();

        for (UserInfo user : users) {
            sb.append("=== Пользователь ===\n")
                    .append("ID: ").append(user.id()).append("\n")
                    .append("Username: ").append(user.username()).append("\n")
                    .append("Имя: ").append(user.firstName()).append("\n")
                    .append("Фамилия: ").append(user.lastName()).append("\n")
                    .append("Email: ").append(user.email()).append("\n")
                    .append("Активен: ").append(user.enabled()).append("\n")
                    .append("Группы:\n");

            if (user.groups().isEmpty()) {
                sb.append("  - Нет групп\n");
            } else {
                for (GroupInfo group : user.groups()) {
                    sb.append("  - ").append(group.name())
                            .append(" (path: ").append(group.path()).append(")\n");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

}
