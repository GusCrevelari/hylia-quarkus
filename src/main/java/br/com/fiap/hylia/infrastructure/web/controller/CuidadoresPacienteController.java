package br.com.fiap.hylia.infrastructure.web.controller;

import br.com.fiap.hylia.application.usecase.cuidador.DesvincularCuidador;
import br.com.fiap.hylia.application.usecase.cuidador.ListarCuidadoresDoPaciente;
import br.com.fiap.hylia.application.usecase.cuidador.VincularCuidador;
import br.com.fiap.hylia.infrastructure.web.dto.cuidador.CuidadorVinculoInDto;
import br.com.fiap.hylia.infrastructure.web.dto.cuidador.CuidadorVinculoOutDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/api/pacientes/{idPaciente}/cuidadores")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CuidadoresPacienteController {

    @Inject VincularCuidador vincular;
    @Inject ListarCuidadoresDoPaciente listar;
    @Inject DesvincularCuidador desvincular;

    @GET
    public Response list(@PathParam("idPaciente") Long idPaciente) {
        var out = listar.handle(idPaciente).stream()
                .map(v -> new CuidadorVinculoOutDto(v.getId(), v.getIdPaciente(), v.getIdUsuario()))
                .toList();
        return Response.ok(out).build();
    }

    @POST
    public Response create(@PathParam("idPaciente") Long idPaciente, CuidadorVinculoInDto in) {
        if (in == null || in.idUsuarioCuidador() == null)
            throw new BadRequestException("idUsuarioCuidador é obrigatório");
        var created = vincular.handle(idPaciente, in.idUsuarioCuidador());
        var out = new CuidadorVinculoOutDto(created.getId(), created.getIdPaciente(), created.getIdUsuario());
        return Response.status(Response.Status.CREATED).entity(out).build();
    }

    @DELETE
    @Path("{idUsuarioCuidador}")
    public Response delete(@PathParam("idPaciente") Long idPaciente,
                           @PathParam("idUsuarioCuidador") Long idUsuarioCuidador) {
        desvincular.handle(idPaciente, idUsuarioCuidador);
        return Response.noContent().build();
    }
}
