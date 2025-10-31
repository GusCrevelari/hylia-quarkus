// infrastructure/web/resource/AuthResource.java
package br.com.fiap.hylia.infrastructure.web.resource;

import br.com.fiap.hylia.application.usecase.auth.RegistrarAcesso;
import br.com.fiap.hylia.infrastructure.web.dto.auth.LoginDto;
import br.com.fiap.hylia.infrastructure.web.dto.auth.SessionDto;
import br.com.fiap.hylia.domain.repository.PacienteRepository;
import br.com.fiap.hylia.domain.repository.ProfessionalRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/api/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    @Inject RegistrarAcesso registrar;
    @Inject PacienteRepository pacientes;
    @Inject ProfessionalRepository professionals;

    @POST
    public Response login(LoginDto in) {
        Long userId = 0L;
        String display = in.email();

        try {
            if ("PACIENTE".equalsIgnoreCase(in.role())) {
                // you can add a buscarPorEmail if you want; else skip and keep 0L
            } else if ("PROFISSIONAL".equalsIgnoreCase(in.role())) {
                // if you prefer CRM: add LoginDto {crm,...} and fetch professionals.buscarPorCrm(crm)
            }
        } catch (Exception ignore) { /* keep 0L */ }

        registrar.sucesso(in.canal()); // writes T_HC_ACESSO with sucesso=1
        return Response.ok(new SessionDto(userId, in.role(), display)).build();
    }
}
