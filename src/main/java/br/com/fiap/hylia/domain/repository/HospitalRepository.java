package br.com.fiap.hylia.domain.repository;

import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.hylia.domain.model.Hospital;

import java.util.List;

public interface HospitalRepository {
    Hospital salvar(Hospital h);
    Hospital buscarPorNome(String nome) throws EntidadeNaoLocalizada;
    List<Hospital> listar();
    void atualizarPorNome(String nomeChave, String novoNome, String novoEndereco, String novoTelefone)
            throws EntidadeNaoLocalizada;
    void deletarPorNome(String nome) throws EntidadeNaoLocalizada;
}
