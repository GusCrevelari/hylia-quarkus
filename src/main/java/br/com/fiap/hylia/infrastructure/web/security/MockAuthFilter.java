// infrastructure/web/security/MockAuthFilter.java
package br.com.fiap.hylia.infrastructure.web.security;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class MockAuthFilter implements ContainerRequestFilter {

    @Inject CurrentUser current;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String idHeader = ctx.getHeaderString("X-User-Id");
        String role = ctx.getHeaderString("X-Role");
        Long id = null;
        try { if (idHeader != null && !idHeader.isBlank()) id = Long.parseLong(idHeader); }
        catch (NumberFormatException ignored) {

        }
        current.set(id, role);
    }
}
