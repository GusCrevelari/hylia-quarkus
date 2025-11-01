package br.com.fiap.hylia.infrastructure.web.controller;

import br.com.fiap.hylia.application.usecase.consulta.CancelarConsulta;
import br.com.fiap.hylia.application.usecase.consulta.ConfirmarConsultaPaciente;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.hylia.domain.repository.ConsultaRepository;
import br.com.fiap.hylia.infrastructure.web.dto.consulta.ConsultaOutDto;
import br.com.fiap.hylia.infrastructure.web.dto.consulta.ConfirmarDto;
import br.com.fiap.hylia.infrastructure.web.dto.consulta.CancelarDto;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/api/pacientes/{idPaciente}/consultas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConsultasPacienteController {

    @Inject ConsultaRepository consultas;
    @Inject ConfirmarConsultaPaciente confirmar;
    @Inject CancelarConsulta cancelar;

    @GET
    public Response list(@PathParam("idPaciente") Long idPaciente) {
        var list = consultas.listarPorPaciente(idPaciente).stream()
                .map(c -> new ConsultaOutDto(c.getId(), c.getIdPaciente(), c.getIdProfissional(),
                        c.getStatus(), c.getDtHora().toString(), c.getLocal()))
                .toList();
        return Response.ok(list).build();
    }

    @POST
    @Path("{idConsulta}/confirmar")
    public Response confirm(@PathParam("idPaciente") Long idPaciente,
                            @PathParam("idConsulta") Long idConsulta,
                            ConfirmarDto dto) throws EntidadeNaoLocalizada {
        final String canal = (dto == null || dto.canal() == null || dto.canal().isBlank())
                ? "WEB" : dto.canal();
        confirmar.handle(idConsulta, idPaciente, canal);
        return Response.noContent().build();
    }

    @POST
    @Path("{idConsulta}/cancelar")
    public Response cancel(@PathParam("idPaciente") Long idPaciente,
                           @PathParam("idConsulta") Long idConsulta,
                           CancelarDto dto) throws EntidadeNaoLocalizada {
        final String motivo = (dto == null) ? null : dto.motivo();
        final String canceladoPor = (dto == null || dto.canceladoPor() == null || dto.canceladoPor().isBlank())
                ? "PACIENTE" : dto.canceladoPor();

        cancelar.handle(idConsulta, idPaciente, motivo, canceladoPor);
        return Response.noContent().build();
    }
}
