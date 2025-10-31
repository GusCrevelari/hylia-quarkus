package br.com.fiap.hylia.application.usecase.consulta;

import br.com.fiap.hylia.domain.repository.ConfirmacaoRepository;
import br.com.fiap.hylia.domain.repository.ConsultaRepository;
import br.com.fiap.hylia.domain.repository.NotificacaoRepository;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConfirmarConsultaPaciente {
    private final ConsultaRepository consultas;
    private final ConfirmacaoRepository confirmacoes;
    private final NotificacaoRepository notificacoes;

    @Inject
    public ConfirmarConsultaPaciente(ConsultaRepository consultas,
                                     ConfirmacaoRepository confirmacoes,
                                     NotificacaoRepository notificacoes) {
        this.consultas = consultas;
        this.confirmacoes = confirmacoes;
        this.notificacoes = notificacoes;
    }

    public void handle(Long idConsulta, Long idPaciente, String canal) throws EntidadeNaoLocalizada {
        String canalEfetivo = (canal == null || canal.isBlank()) ? "WEB" : canal;
        confirmacoes.criar(idConsulta, canalEfetivo);
        consultas.marcarConfirmacao(idConsulta);
        if (idPaciente != null) {
            notificacoes.criar(idPaciente, idConsulta, "CONSULTA_CONFIRMADA", canalEfetivo, true);
        }
    }
}
