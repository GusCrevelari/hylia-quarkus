package br.com.fiap.hylia.infrastructure.web.dto.consulta;

public record ConsultaInDto(
        Long idPaciente,
        String crmProfissional,
        String dtHoraIso,
        String local
) {}
