package br.com.fiap.hylia.domain.repository;

import br.com.fiap.hylia.domain.model.CuidadorVinculo;
import java.util.List;

public interface CuidadorVinculoRepository {
    CuidadorVinculo vincular(Long idPaciente, Long idUsuarioCuidador);
    List<CuidadorVinculo> listarPorPaciente(Long idPaciente);
    void desvincular(Long idPaciente, Long idUsuarioCuidador);
}
