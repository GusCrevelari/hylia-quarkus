package br.com.fiap.hylia.infrastructure.web.mapper;

import br.com.fiap.hylia.domain.model.Paciente;
import br.com.fiap.hylia.infrastructure.web.dto.paciente.PacienteInDto;
import br.com.fiap.hylia.infrastructure.web.dto.paciente.PacienteOutDto;

public final class PacienteMapper {
    private PacienteMapper() {}
    public static Paciente toDomain(PacienteInDto dto) {
        return new Paciente(null, dto.cpf(), dto.nome(), dto.idade() == null ? 0 : dto.idade());
    }
    public static PacienteOutDto toOut(Paciente p) {
        return new PacienteOutDto(p.getId(), p.getNome(), p.getCpf(), p.getIdade());
    }
}
