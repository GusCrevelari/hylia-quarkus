package br.com.fiap.hylia.infrastructure.web.dto.auth;

public record SessionDto(Long userId, String role, String displayName) {}
