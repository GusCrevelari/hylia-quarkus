// infrastructure/web/resource/ProfessionalResource.java
package br.com.fiap.hylia.infrastructure.web.controller;

import br.com.fiap.hylia.application.usecase.professional.CadastrarProfessional;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.hylia.domain.repository.ProfessionalRepository;
import br.com.fiap.hylia.infrastructure.web.dto.professional.ProfessionalInDto;
import br.com.fiap.hylia.infrastructure.web.dto.professional.ProfessionalUpdateDto;
import br.com.fiap.hylia.infrastructure.web.mapper.ProfessionalMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/api/professionals")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessionalController {

    @Inject CadastrarProfessional cadastrar;
    @Inject ProfessionalRepository professionals;

    @POST
    public Response create(ProfessionalInDto in) {
        var saved = cadastrar.execute(ProfessionalMapper.toDomain(in));
        return Response.status(Response.Status.CREATED)
                .entity(ProfessionalMapper.toOut(saved))
                .build();
    }

    @GET
    public Response list() {
        var out = professionals.listar().stream()
                .map(ProfessionalMapper::toOut)
                .toList();
        return Response.ok(out).build();
    }

    @GET
    @Path("{crm}")
    public Response getByCrm(@PathParam("crm") String crm) throws EntidadeNaoLocalizada {
        var p = professionals.buscarPorCrm(crm);
        return Response.ok(ProfessionalMapper.toOut(p)).build();
    }

    // ---- Single, portable update endpoint (use this from the frontend) ----
    @POST
    @Path("{crm}/update")
    public Response updateViaPost(@PathParam("crm") String crm, ProfessionalUpdateDto in)
            throws EntidadeNaoLocalizada {
        if (in == null || (in.nome() == null && in.idade() == null && in.email() == null && in.especialidade() == null)) {
            throw new BadRequestException("Nothing to update");
        }
        var current = professionals.buscarPorCrm(crm);

        // allow partial / blanks -> keep current if null or blank
        String nome  = blankToNull(in.nome()) == null ? current.getNome() : in.nome().trim();
        Integer idade = in.idade() == null ? current.getIdade() : in.idade();
        String email = blankToNull(in.email());
        if (email == null) email = current.getEmail();
        String esp = blankToNull(in.especialidade());
        if (esp == null) esp = current.getEspecialidade();

        professionals.atualizarPorCrm(crm, nome, idade, email, esp);
        var updated = professionals.buscarPorCrm(crm);
        return Response.ok(ProfessionalMapper.toOut(updated)).build();
    }

    // Optional: keep PUT if you want a RESTful alternative
    @PUT
    @Path("{crm}")
    public Response updatePut(@PathParam("crm") String crm, ProfessionalUpdateDto in)
            throws EntidadeNaoLocalizada {
        return updateViaPost(crm, in);
    }

    @DELETE
    @Path("{crm}")
    public Response delete(@PathParam("crm") String crm) throws EntidadeNaoLocalizada {
        professionals.deletarPorCrm(crm);
        return Response.noContent().build();
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
