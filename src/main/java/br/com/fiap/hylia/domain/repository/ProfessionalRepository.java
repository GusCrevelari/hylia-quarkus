package br.com.fiap.hylia.domain.repository;

import br.com.fiap.hylia.domain.model.Professional;
import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import java.util.List;

public interface ProfessionalRepository {
    Professional salvar(Professional p);
    Professional buscarPorCrm(String crm) throws EntidadeNaoLocalizada;
    List<Professional> listar();
    void atualizarPorCrm(String crmChave, String novoNome, int novaIdade,
                         String novoEmail, String novaEspecialidade) throws EntidadeNaoLocalizada;
    void deletarPorCrm(String crm) throws EntidadeNaoLocalizada;
}
