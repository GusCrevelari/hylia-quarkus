package br.com.fiap.hylia.infrastructure.web.exception;

import java.sql.SQLIntegrityConstraintViolationException;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.*;

@Provider
public class SqlConstraintMapper implements ExceptionMapper<SQLIntegrityConstraintViolationException> {
    @Override public Response toResponse(SQLIntegrityConstraintViolationException ex) {
        return Response.status(Response.Status.CONFLICT)
                .entity(java.util.Map.of("error","conflict","message","Unique constraint violated"))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
