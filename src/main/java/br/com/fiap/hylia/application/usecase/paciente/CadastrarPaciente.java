package br.com.fiap.hylia.application.usecase.paciente;

import br.com.fiap.hylia.domain.model.Paciente;
import br.com.fiap.hylia.domain.repository.PacienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CadastrarPaciente {
    private final PacienteRepository repo;

    @Inject
    public CadastrarPaciente(PacienteRepository repo) {
        this.repo = repo;
    }

    public Paciente execute(Paciente p) {
        return repo.salvar(p);
    }
}
