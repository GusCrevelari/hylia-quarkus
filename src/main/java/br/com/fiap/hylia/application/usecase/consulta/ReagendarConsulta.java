package br.com.fiap.hylia.application.usecase.consulta;

import br.com.fiap.hylia.domain.repository.ConsultaRepository;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public class ReagendarConsulta {
    private final ConsultaRepository consultas;

    @Inject
    public ReagendarConsulta(ConsultaRepository consultas) {
        this.consultas = consultas;
    }

    public void handle(Long idConsulta, LocalDateTime novoDtHora, String novoLocal) throws EntidadeNaoLocalizada {
        consultas.reagendar(idConsulta, novoDtHora, novoLocal);
    }
}
