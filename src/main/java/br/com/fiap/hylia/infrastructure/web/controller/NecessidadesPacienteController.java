package br.com.fiap.hylia.infrastructure.web.controller;

import br.com.fiap.hylia.application.usecase.necessidade.CriarNecessidadeEspecial;
import br.com.fiap.hylia.application.usecase.necessidade.ListarNecessidadesPorUsuario;
import br.com.fiap.hylia.application.usecase.necessidade.RemoverNecessidadeEspecial;
import br.com.fiap.hylia.infrastructure.web.dto.necessidade.NecessidadeInDto;
import br.com.fiap.hylia.infrastructure.web.dto.necessidade.NecessidadeOutDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/api/pacientes/{idPaciente}/necessidades")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NecessidadesPacienteController {

    @Inject CriarNecessidadeEspecial criar;
    @Inject ListarNecessidadesPorUsuario listar;
    @Inject RemoverNecessidadeEspecial remover;

    @GET
    public Response list(@PathParam("idPaciente") Long idPaciente) {
        var out = listar.handle(idPaciente).stream()
                .map(n -> new NecessidadeOutDto(n.getId(), n.getIdUsuario(), n.getDescricao()))
                .toList();
        return Response.ok(out).build();
    }

    @POST
    public Response create(@PathParam("idPaciente") Long idPaciente, NecessidadeInDto in) {
        var descricao = (in == null || in.descricao() == null || in.descricao().isBlank())
                ? "(sem descrição)" : in.descricao().trim();
        var created = criar.handle(idPaciente, descricao);
        var out = new NecessidadeOutDto(created.getId(), created.getIdUsuario(), created.getDescricao());
        return Response.status(Response.Status.CREATED).entity(out).build();
    }

    @DELETE
    @Path("{idNecessidade}")
    public Response delete(@PathParam("idNecessidade") Long idNecessidade) {
        remover.handle(idNecessidade);
        return Response.noContent().build();
    }
}
