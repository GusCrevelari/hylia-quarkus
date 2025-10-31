package br.com.fiap.hylia.domain.exceptions;


public class EntidadeNaoLocalizada extends Exception {
    public EntidadeNaoLocalizada(String msg) { super(msg); }
    public EntidadeNaoLocalizada(String msg, Throwable c) { super(msg, c); }
}