package br.com.fiap.hylia.infrastructure.web.controller;

import br.com.fiap.hylia.application.usecase.notificacao.ListarNotificacoesDoPaciente;
import br.com.fiap.hylia.application.usecase.notificacao.MarcarNotificacaoComoLida;
import br.com.fiap.hylia.infrastructure.web.dto.notificacao.NotificacaoOutDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/api/pacientes/{idPaciente}/notificacoes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotificacoesPacienteController {

    @Inject ListarNotificacoesDoPaciente listar;
    @Inject MarcarNotificacaoComoLida marcar;

    @GET
    public Response list(@PathParam("idPaciente") Long idPaciente) {
        var out = listar.handle(idPaciente).stream().map(n ->
                new NotificacaoOutDto(
                        n.getId(), n.getIdUsuario(), n.getIdConsulta(),
                        n.getTipo(), n.getCanal(),
                        n.getDtEnvio() == null ? null : n.getDtEnvio().toString(),
                        n.isFoiLida()
                )
        ).toList();
        return Response.ok(out).build();
    }

    @PATCH
    @Path("{idNotificacao}/lida")
    public Response markRead(@PathParam("idNotificacao") Long idNotificacao) {
        marcar.handle(idNotificacao);
        return Response.noContent().build();
    }
}
