// infrastructure/web/resource/HospitalResource.java
package br.com.fiap.hylia.infrastructure.web.controller;

import br.com.fiap.hylia.application.usecase.hospital.CadastrarHospital;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.hylia.domain.repository.HospitalRepository;
import br.com.fiap.hylia.infrastructure.web.dto.hospital.HospitalInDto;
import br.com.fiap.hylia.infrastructure.web.mapper.HospitalMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/api/hospitais")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HospitalController {

    @Inject CadastrarHospital cadastrar;
    @Inject HospitalRepository hospitais;

    @POST
    public Response create(HospitalInDto in) {
        var saved = cadastrar.execute(HospitalMapper.toDomain(in));
        return Response.status(Response.Status.CREATED).entity(HospitalMapper.toOut(saved)).build();
    }

    @GET
    public Response list() {
        var out = hospitais.listar().stream().map(HospitalMapper::toOut).toList();
        return Response.ok(out).build();
    }

    @GET
    @Path("{nome}")
    public Response getByNome(@PathParam("nome") String nome) throws EntidadeNaoLocalizada {
        var h = hospitais.buscarPorNome(nome);
        return Response.ok(HospitalMapper.toOut(h)).build();
    }

    public record HospitalUpdateDto(String nome, String endereco, String telefone) {}

    @PATCH
    @Path("{nomeChave}")
    public Response update(@PathParam("nomeChave") String nomeChave, HospitalUpdateDto in)
            throws EntidadeNaoLocalizada {
        hospitais.atualizarPorNome(
                nomeChave,
                in.nome(),
                in.endereco(),
                in.telefone()
        );
        var updated = hospitais.buscarPorNome(in.nome() != null ? in.nome() : nomeChave);
        return Response.ok(HospitalMapper.toOut(updated)).build();
    }

    @DELETE
    @Path("{nome}")
    public Response delete(@PathParam("nome") String nome) throws EntidadeNaoLocalizada {
        hospitais.deletarPorNome(nome);
        return Response.noContent().build();
    }
}
