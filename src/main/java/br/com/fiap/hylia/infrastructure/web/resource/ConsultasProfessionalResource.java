package br.com.fiap.hylia.infrastructure.web.resource;

import br.com.fiap.hylia.application.usecase.consulta.CriarConsultaProfessional;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.hylia.domain.repository.ConsultaRepository;
import br.com.fiap.hylia.domain.repository.ProfessionalRepository;
import br.com.fiap.hylia.infrastructure.web.dto.consulta.ConsultaInDto;
import br.com.fiap.hylia.infrastructure.web.dto.consulta.ConsultaOutDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;

@RequestScoped
@Path("/api/professionals/{crm}/consultas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConsultasProfessionalResource {

    @Inject CriarConsultaProfessional criar;
    @Inject ConsultaRepository consultas;
    @Inject ProfessionalRepository professionals;

    @POST
    public Response create(@PathParam("crm") String crm, ConsultaInDto in)
            throws EntidadeNaoLocalizada {
        var c = criar.handle(in.idPaciente(), crm, LocalDateTime.parse(in.dtHoraIso()), in.local());
        var out = new ConsultaOutDto(
                c.getId(), c.getIdPaciente(), c.getIdProfissional(),
                c.getStatus(), c.getDtHora().toString(), c.getLocal()
        );
        return Response.status(Response.Status.CREATED).entity(out).build();
    }

    @GET
    public Response list(@PathParam("crm") String crm) throws EntidadeNaoLocalizada {
        var pro = professionals.buscarPorCrm(crm);          // crm → Professional (has id)
        var list = consultas.listarPorProfissional(pro.getId());
        var out = list.stream()
                .map(c -> new ConsultaOutDto(
                        c.getId(), c.getIdPaciente(), c.getIdProfissional(),
                        c.getStatus(), c.getDtHora().toString(), c.getLocal()))
                .toList();
        return Response.ok(out).build();
    }
}
