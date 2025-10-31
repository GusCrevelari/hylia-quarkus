package br.com.fiap.hylia.application.usecase.consulta;

import br.com.fiap.hylia.domain.model.Consulta;
import br.com.fiap.hylia.domain.repository.ConsultaRepository;
import br.com.fiap.hylia.domain.repository.NotificacaoRepository;
import br.com.fiap.hylia.domain.repository.ProfessionalRepository;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

@ApplicationScoped
public class CriarConsultaProfessional {
    private final ConsultaRepository consultas;
    private final ProfessionalRepository professionals;
    private final NotificacaoRepository notificacoes;

    @Inject
    public CriarConsultaProfessional(ConsultaRepository consultas,
                                     ProfessionalRepository professionals,
                                     NotificacaoRepository notificacoes) {
        this.consultas = consultas;
        this.professionals = professionals;
        this.notificacoes = notificacoes;
    }

    public Consulta handle(Long idPaciente, String crmProfissional, LocalDateTime dtHora, String local)
            throws EntidadeNaoLocalizada {
        var pro = professionals.buscarPorCrm(crmProfissional); // throws if not found
        var c = consultas.criar(idPaciente, pro.getId(), dtHora, local);
        notificacoes.criar(idPaciente, c.getId(), "CONSULTA_CRIADA", "WEB", false);
        return c;
    }
}
