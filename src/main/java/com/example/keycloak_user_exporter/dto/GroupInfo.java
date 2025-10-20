package com.example.keycloak_user_exporter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GroupInfo(String id,
                        String name,
                        String path) {
}
