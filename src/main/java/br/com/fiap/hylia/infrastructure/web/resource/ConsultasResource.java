// infrastructure/web/resource/ConsultasResource.java
package br.com.fiap.hylia.infrastructure.web.resource;

import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.hylia.domain.repository.ConsultaRepository;
import br.com.fiap.hylia.infrastructure.web.dto.consulta.ConsultaReagendarDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;

@RequestScoped
@Path("/api/consultas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConsultasResource {

    @Inject ConsultaRepository consultas;

    @PATCH
    @Path("{idConsulta}")
    public Response patch(@PathParam("idConsulta") Long idConsulta,
                          ConsultaReagendarDto dto) throws EntidadeNaoLocalizada {
        if (dto == null || (dto.newDtHoraIso() == null && dto.newLocal() == null)) {
            throw new BadRequestException("Nothing to update");
        }
        LocalDateTime newDt = null;
        if (dto.newDtHoraIso() != null && !dto.newDtHoraIso().isBlank()) {
            newDt = LocalDateTime.parse(dto.newDtHoraIso());
        }
        consultas.reagendar(idConsulta, newDt, dto.newLocal());
        return Response.noContent().build();
    }

    @POST
    @Path("{idConsulta}/reagendar")
    public Response reagendar(@PathParam("idConsulta") Long idConsulta,
                              ConsultaReagendarDto dto) throws EntidadeNaoLocalizada {
        if (dto == null || (dto.newDtHoraIso() == null && dto.newLocal() == null)) {
            throw new BadRequestException("Nothing to update");
        }
        LocalDateTime newDt = null;
        if (dto.newDtHoraIso() != null && !dto.newDtHoraIso().isBlank()) {
            newDt = LocalDateTime.parse(dto.newDtHoraIso());
        }
        consultas.reagendar(idConsulta, newDt, dto.newLocal());
        return Response.noContent().build();
    }

    @DELETE
    @Path("{idConsulta}")
    public Response delete(@PathParam("idConsulta") Long idConsulta) throws EntidadeNaoLocalizada {
        consultas.deletar(idConsulta);
        return Response.noContent().build();
    }
}
