package br.com.fiap.hylia.application.usecase.necessidade;

import br.com.fiap.hylia.domain.model.NecessidadeEspecial;
import br.com.fiap.hylia.domain.repository.NecessidadeEspecialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CriarNecessidadeEspecial {
    private final NecessidadeEspecialRepository repo;
    @Inject public CriarNecessidadeEspecial(NecessidadeEspecialRepository repo) { this.repo = repo; }
    public NecessidadeEspecial handle(Long idUsuario, String descricao) {
        return repo.criar(idUsuario, descricao);
    }
}
