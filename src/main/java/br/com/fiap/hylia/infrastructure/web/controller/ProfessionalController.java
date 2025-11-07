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

    @PATCH
    @Path("{crm}")
    public Response update(@PathParam("crm") String crm, ProfessionalUpdateDto in)
            throws EntidadeNaoLocalizada {
        if (in == null || (in.nome() == null && in.idade() == null && in.email() == null && in.especialidade() == null)) {
            throw new BadRequestException("Nothing to update");
        }
        var current = professionals.buscarPorCrm(crm);
        var nome  = in.nome()         == null ? current.getNome()         : in.nome();
        var idade = in.idade()        == null ? current.getIdade()        : in.idade();
        var email = in.email()        == null ? current.getEmail()        : in.email();
        var esp   = in.especialidade() == null ? current.getEspecialidade() : in.especialidade();

        professionals.atualizarPorCrm(crm, nome, idade, email, esp);
        var updated = professionals.buscarPorCrm(crm);
        return Response.ok(ProfessionalMapper.toOut(updated)).build();
    }

    @POST
    @Path("{crm}/update")
    public Response updateViaPost(@PathParam("crm") String crm, ProfessionalUpdateDto in)
            throws EntidadeNaoLocalizada {
        // Reuse the same behavior as PATCH
        if (in == null || (in.nome() == null && in.idade() == null && in.email() == null && in.especialidade() == null)) {
            throw new BadRequestException("Nothing to update");
        }
        var current = professionals.buscarPorCrm(crm);
        var nome  = in.nome()          == null ? current.getNome()          : in.nome();
        var idade = in.idade()         == null ? current.getIdade()         : in.idade();
        var email = in.email()         == null ? current.getEmail()         : in.email();
        var esp   = in.especialidade() == null ? current.getEspecialidade() : in.especialidade();

        professionals.atualizarPorCrm(crm, nome, idade, email, esp);
        var updated = professionals.buscarPorCrm(crm);
        return Response.ok(ProfessionalMapper.toOut(updated)).build();
    }

    @DELETE
    @Path("{crm}")
    public Response delete(@PathParam("crm") String crm) throws EntidadeNaoLocalizada {
        professionals.deletarPorCrm(crm);
        return Response.noContent().build();
    }

    @PUT
    @Path("{crm}")
    public Response updatePut(@PathParam("crm") String crm, ProfessionalUpdateDto in)
            throws EntidadeNaoLocalizada {
        // Reuse the same logic
        return update(crm, in);
    }
}
