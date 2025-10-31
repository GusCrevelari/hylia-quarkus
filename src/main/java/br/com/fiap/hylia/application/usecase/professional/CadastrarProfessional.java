package br.com.fiap.hylia.application.usecase.professional;

import br.com.fiap.hylia.domain.model.Professional;
import br.com.fiap.hylia.domain.repository.ProfessionalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CadastrarProfessional {
    private final ProfessionalRepository repo;

    @Inject
    public CadastrarProfessional(ProfessionalRepository repo) {
        this.repo = repo;
    }

    public Professional execute(Professional p) {
        return repo.salvar(p);
    }
}
