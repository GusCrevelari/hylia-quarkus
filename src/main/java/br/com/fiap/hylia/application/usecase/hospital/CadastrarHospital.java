package br.com.fiap.hylia.application.usecase.hospital;

import br.com.fiap.hylia.domain.model.Hospital;
import br.com.fiap.hylia.domain.repository.HospitalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CadastrarHospital {
    private final HospitalRepository repo;

    @Inject
    public CadastrarHospital(HospitalRepository repo) {
        this.repo = repo;
    }

    public Hospital execute(Hospital h) {
        return repo.salvar(h);
    }
}
