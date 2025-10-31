// infrastructure/web/resource/PacienteResource.java
package br.com.fiap.hylia.infrastructure.web.controller;

import br.com.fiap.hylia.application.usecase.paciente.CadastrarPaciente;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.hylia.domain.repository.PacienteRepository;
import br.com.fiap.hylia.infrastructure.web.dto.paciente.PacienteInDto;
import br.com.fiap.hylia.infrastructure.web.dto.paciente.PacienteUpdateDto;
import br.com.fiap.hylia.infrastructure.web.mapper.PacienteMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/api/pacientes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PacienteController {

    @Inject CadastrarPaciente cadastrar;
    @Inject PacienteRepository pacientes;

    @POST
    public Response create(PacienteInDto in) {
        var saved = cadastrar.execute(PacienteMapper.toDomain(in));
        return Response.status(Response.Status.CREATED)
                .entity(PacienteMapper.toOut(saved))
                .build();
    }

    @GET
    public Response list() {
        var out = pacientes.listar().stream()
                .map(PacienteMapper::toOut)
                .toList();
        return Response.ok(out).build();
    }

    @GET
    @Path("{cpf}")
    public Response getByCpf(@PathParam("cpf") String cpf) throws EntidadeNaoLocalizada {
        var p = pacientes.buscarPorCpf(cpf);
        return Response.ok(PacienteMapper.toOut(p)).build();
    }

    @PATCH
    @Path("{cpf}")
    public Response update(@PathParam("cpf") String cpf, PacienteUpdateDto in)
            throws EntidadeNaoLocalizada {
        if (in == null || (in.nome() == null && in.idade() == null)) {
            throw new BadRequestException("Nothing to update");
        }
        // Fill missing fields from current state
        var current = pacientes.buscarPorCpf(cpf);
        var novoNome = in.nome()  == null ? current.getNome()  : in.nome();
        var novaIdade = in.idade() == null ? current.getIdade() : in.idade();

        pacientes.atualizarNomeEIdadePorCpf(cpf, novoNome, novaIdade);
        var updated = pacientes.buscarPorCpf(cpf);
        return Response.ok(PacienteMapper.toOut(updated)).build();
    }

    @DELETE
    @Path("{cpf}")
    public Response delete(@PathParam("cpf") String cpf) throws EntidadeNaoLocalizada {
        pacientes.deletarPorCpf(cpf);
        return Response.noContent().build();
    }
}
