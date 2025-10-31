// infrastructure/web/security/CurrentUser.java
package br.com.fiap.hylia.infrastructure.web.security;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CurrentUser {
    private Long userId;
    private String role; // PACIENTE / PROFISSIONAL / ADMIN / null

    public Long getUserId() { return userId; }
    public String getRole() { return role; }
    public void set(Long userId, String role) { this.userId = userId; this.role = role; }

    public boolean isPaciente() { return "PACIENTE".equalsIgnoreCase(role); }
    public boolean isProfessional() { return "PROFISSIONAL".equalsIgnoreCase(role); }
    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }
}
