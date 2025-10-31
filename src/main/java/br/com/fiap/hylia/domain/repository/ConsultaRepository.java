package br.com.fiap.hylia.domain.repository;

import br.com.fiap.hylia.domain.model.Consulta;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository {
    Consulta criar(Long idPaciente, Long idProfissional, LocalDateTime dtHora, String local);
    void reagendar(Long idConsulta, LocalDateTime novoHorario, String novoLocal) throws EntidadeNaoLocalizada;
    void deletar(Long idConsulta) throws EntidadeNaoLocalizada;
    Consulta buscarPorId(Long idConsulta) throws EntidadeNaoLocalizada;
    List<Consulta> listarPorProfissional(Long idProfissional);
    List<Consulta> listarPorPaciente(Long idPaciente);
    void marcarCancelamento(Long idConsulta, Long idCancelamento) throws EntidadeNaoLocalizada; // also sets STATUS='CANCELADA'
    void marcarConfirmacao(Long idConsulta) throws EntidadeNaoLocalizada; // sets STATUS='CONFIRMADA'
}
