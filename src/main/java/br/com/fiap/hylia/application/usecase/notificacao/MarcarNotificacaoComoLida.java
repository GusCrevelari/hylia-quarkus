package br.com.fiap.hylia.application.usecase.notificacao;

import br.com.fiap.hylia.domain.repository.NotificacaoQueryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MarcarNotificacaoComoLida {
    private final NotificacaoQueryRepository repo;
    @Inject public MarcarNotificacaoComoLida(NotificacaoQueryRepository repo) { this.repo = repo; }
    public void handle(Long idNotificacao) { repo.marcarComoLida(idNotificacao); }
}
