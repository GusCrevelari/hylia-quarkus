package br.com.fiap.hylia.application.usecase.notificacao;

import br.com.fiap.hylia.domain.model.Notificacao;
import br.com.fiap.hylia.domain.repository.NotificacaoQueryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ListarNotificacoesDoPaciente {
    private final NotificacaoQueryRepository repo;
    @Inject public ListarNotificacoesDoPaciente(NotificacaoQueryRepository repo) { this.repo = repo; }
    public List<Notificacao> handle(Long idUsuario) { return repo.listarPorUsuario(idUsuario); }
}
