package br.com.fiap.hylia.domain.repository;

public interface CancelamentoRepository {
    Long criar(String motivo, String canceladoPor);
}
