package com.example.keycloak_user_exporter.dto;

import java.util.List;


public record UserInfo(String id,
                       String username,
                       String firstName,
                       String lastName,
                       String email,
                       boolean enabled,
                       List<GroupInfo> groups) {
}
