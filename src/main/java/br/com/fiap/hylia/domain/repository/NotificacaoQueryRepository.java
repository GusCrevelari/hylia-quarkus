package br.com.fiap.hylia.domain.repository;

import br.com.fiap.hylia.domain.model.Notificacao;
import java.util.List;

public interface NotificacaoQueryRepository {
    List<Notificacao> listarPorUsuario(Long idUsuario);
    void marcarComoLida(Long idNotificacao);
}
