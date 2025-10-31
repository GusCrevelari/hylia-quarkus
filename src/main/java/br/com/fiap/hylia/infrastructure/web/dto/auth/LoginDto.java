package br.com.fiap.hylia.infrastructure.web.dto.auth;

public record LoginDto(String email, String role, String canal) {}
