package br.com.fiap.hylia.application.usecase.consulta;

import br.com.fiap.hylia.domain.model.Consulta;
import br.com.fiap.hylia.domain.repository.ConsultaRepository;
import br.com.fiap.hylia.domain.repository.ProfessionalRepository;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ListarConsultasDoProfessional {
    private final ConsultaRepository consultas;
    private final ProfessionalRepository professionals;

    @Inject
    public ListarConsultasDoProfessional(ConsultaRepository consultas, ProfessionalRepository professionals) {
        this.consultas = consultas;
        this.professionals = professionals;
    }

    public List<Consulta> handle(String crm) throws EntidadeNaoLocalizada {
        var pro = professionals.buscarPorCrm(crm); // id = ID_PROFISSIONAL
        return consultas.listarPorProfissional(pro.getId());
    }
}
