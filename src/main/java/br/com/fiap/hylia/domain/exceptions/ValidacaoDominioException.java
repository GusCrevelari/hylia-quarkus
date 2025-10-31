package br.com.fiap.hylia.domain.exceptions;

public class ValidacaoDominioException extends RuntimeException {
    public ValidacaoDominioException(String message) { super(message); }
    public ValidacaoDominioException(String message, Throwable cause) { super(message, cause); }
}
