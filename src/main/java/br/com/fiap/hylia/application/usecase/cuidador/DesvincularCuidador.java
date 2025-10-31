package br.com.fiap.hylia.application.usecase.cuidador;

import br.com.fiap.hylia.domain.repository.CuidadorVinculoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DesvincularCuidador {
    private final CuidadorVinculoRepository repo;
    @Inject public DesvincularCuidador(CuidadorVinculoRepository repo) { this.repo = repo; }
    public void handle(Long idPaciente, Long idUsuarioCuidador) {
        repo.desvincular(idPaciente, idUsuarioCuidador);
    }
}
