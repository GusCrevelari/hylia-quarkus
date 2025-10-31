package br.com.fiap.hylia.application.usecase.necessidade;

import br.com.fiap.hylia.domain.repository.NecessidadeEspecialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RemoverNecessidadeEspecial {
    private final NecessidadeEspecialRepository repo;
    @Inject public RemoverNecessidadeEspecial(NecessidadeEspecialRepository repo) { this.repo = repo; }
    public void handle(Long idNecessidade) { repo.deletar(idNecessidade); }
}
