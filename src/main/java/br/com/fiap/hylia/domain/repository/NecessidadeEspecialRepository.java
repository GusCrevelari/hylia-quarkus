package br.com.fiap.hylia.domain.repository;

import br.com.fiap.hylia.domain.model.NecessidadeEspecial;
import java.util.List;

public interface NecessidadeEspecialRepository {
    NecessidadeEspecial criar(Long idUsuario, String descricao);
    List<NecessidadeEspecial> listarPorUsuario(Long idUsuario);
    void deletar(Long idNecessidade);
}
