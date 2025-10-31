package br.com.fiap.hylia.domain.repository;

public interface NotificacaoRepository {
    Long criar(Long idUsuario, Long idConsulta, String tipo, String canal, boolean foiLida);
}
