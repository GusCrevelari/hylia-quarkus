package br.com.fiap.hylia.infrastructure.web.exception;

import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.*;

@Provider
public class NotFoundMapper implements ExceptionMapper<EntidadeNaoLocalizada> {
    @Override public Response toResponse(EntidadeNaoLocalizada ex) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(java.util.Map.of("error","not_found","message", ex.getMessage()))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
