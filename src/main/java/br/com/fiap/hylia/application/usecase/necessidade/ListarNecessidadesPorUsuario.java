package br.com.fiap.hylia.application.usecase.necessidade;

import br.com.fiap.hylia.domain.model.NecessidadeEspecial;
import br.com.fiap.hylia.domain.repository.NecessidadeEspecialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ListarNecessidadesPorUsuario {
    private final NecessidadeEspecialRepository repo;
    @Inject public ListarNecessidadesPorUsuario(NecessidadeEspecialRepository repo) { this.repo = repo; }
    public List<NecessidadeEspecial> handle(Long idUsuario) {
        return repo.listarPorUsuario(idUsuario);
    }
}
