package br.com.fiap.hylia.application.usecase.consulta;

import br.com.fiap.hylia.domain.repository.CancelamentoRepository;
import br.com.fiap.hylia.domain.repository.ConsultaRepository;
import br.com.fiap.hylia.domain.repository.NotificacaoRepository;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CancelarConsulta {
    private final ConsultaRepository consultas;
    private final CancelamentoRepository cancelamentos;
    private final NotificacaoRepository notificacoes;

    @Inject
    public CancelarConsulta(ConsultaRepository consultas,
                            CancelamentoRepository cancelamentos,
                            NotificacaoRepository notificacoes) {
        this.consultas = consultas;
        this.cancelamentos = cancelamentos;
        this.notificacoes = notificacoes;
    }

    public void handle(Long idConsulta, Long idPaciente, String motivo, String canceladoPor)
            throws EntidadeNaoLocalizada {
        Long idCanc = cancelamentos.criar(motivo, canceladoPor);
        consultas.marcarCancelamento(idConsulta, idCanc);
        if (idPaciente != null) {
            notificacoes.criar(idPaciente, idConsulta, "CONSULTA_CANCELADA", "WEB", false);
        }
    }
}
