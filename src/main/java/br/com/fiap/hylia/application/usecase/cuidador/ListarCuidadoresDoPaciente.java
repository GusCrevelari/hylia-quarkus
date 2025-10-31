package br.com.fiap.hylia.application.usecase.cuidador;

import br.com.fiap.hylia.domain.model.CuidadorVinculo;
import br.com.fiap.hylia.domain.repository.CuidadorVinculoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ListarCuidadoresDoPaciente {
    private final CuidadorVinculoRepository repo;
    @Inject public ListarCuidadoresDoPaciente(CuidadorVinculoRepository repo) { this.repo = repo; }
    public List<CuidadorVinculo> handle(Long idPaciente) {
        return repo.listarPorPaciente(idPaciente);
    }
}
