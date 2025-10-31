package br.com.fiap.hylia.domain.repository;

public interface ConfirmacaoRepository {
    Long criar(Long idConsulta, String canal);
}
