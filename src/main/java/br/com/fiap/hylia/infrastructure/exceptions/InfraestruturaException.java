package br.com.fiap.hylia.infrastructure.exceptions;


public class InfraestruturaException extends RuntimeException {
    public InfraestruturaException(String msg) {
        super(msg);
    }

    public InfraestruturaException(String msg, Throwable c) {
        super(msg, c);
    }
}