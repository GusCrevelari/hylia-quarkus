// infrastructure/web/exception/BadRequestMapper.java
package br.com.fiap.hylia.infrastructure.web.exception;

import br.com.fiap.hylia.domain.exceptions.ValidacaoDominioException;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.*;

@Provider
public class BadRequestMapper implements ExceptionMapper<RuntimeException> {
    @Override public Response toResponse(RuntimeException ex) {
        if (ex instanceof ValidacaoDominioException || ex instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(java.util.Map.of("error","bad_request","message", ex.getMessage()))
                    .type(MediaType.APPLICATION_JSON).build();
        }
        return Response.serverError()
                .entity(java.util.Map.of("error","server_error","message", ex.getMessage()))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
