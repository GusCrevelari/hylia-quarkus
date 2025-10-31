package br.com.fiap.hylia.domain.repository;

import br.com.fiap.hylia.domain.model.Paciente;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;

import java.util.List;

public interface PacienteRepository {
    Paciente salvar(Paciente p);
    Paciente buscarPorCpf(String cpf) throws EntidadeNaoLocalizada;
    List<Paciente> listar();
    void deletarPorCpf(String cpf) throws EntidadeNaoLocalizada;
    void atualizarNomeEIdadePorCpf(String cpf, String novoNome, int novaIdade) throws EntidadeNaoLocalizada;
}
