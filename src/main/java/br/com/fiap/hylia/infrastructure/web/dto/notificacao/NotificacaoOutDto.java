package br.com.fiap.hylia.infrastructure.web.dto.notificacao;

public record NotificacaoOutDto(
        Long id, Long idUsuario, Long idConsulta,
        String tipo, String canal, String dtEnvioIso, boolean foiLida
) {}
