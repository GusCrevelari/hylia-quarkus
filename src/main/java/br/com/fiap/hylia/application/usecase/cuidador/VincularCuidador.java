package br.com.fiap.hylia.application.usecase.cuidador;

import br.com.fiap.hylia.domain.model.CuidadorVinculo;
import br.com.fiap.hylia.domain.repository.CuidadorVinculoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VincularCuidador {
    private final CuidadorVinculoRepository repo;
    @Inject public VincularCuidador(CuidadorVinculoRepository repo) { this.repo = repo; }
    public CuidadorVinculo handle(Long idPaciente, Long idUsuarioCuidador) {
        return repo.vincular(idPaciente, idUsuarioCuidador);
    }
}
