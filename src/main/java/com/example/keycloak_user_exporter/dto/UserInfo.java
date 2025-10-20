package com.example.keycloak_user_exporter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserInfo(String id,
                       String username,
                       String firstName,
                       String lastName,
                       String email,
                       boolean enabled,
                       List<GroupInfo> groups) {
}
