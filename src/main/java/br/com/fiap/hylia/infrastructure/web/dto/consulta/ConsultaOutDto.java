package br.com.fiap.hylia.infrastructure.web.dto.consulta;

public record ConsultaOutDto(
        Long id,
        Long idPaciente,
        Long idProfissional,
        String status,
        String dtHoraIso,
        String local
) {}
