package br.com.fiap.hylia.infrastructure.web.dto.consulta;

public record ConsultaUpdateDto(
        String newDtHoraIso,
        String newLocal
) {}
