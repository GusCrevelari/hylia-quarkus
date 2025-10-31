
package br.com.fiap.hylia.infrastructure.web.dto.professional;

public record ProfessionalOutDto(
        Long id,
        String nome,
        String cpf,
        Integer idade,
        String email,
        String especialidade,
        String crm,
        Long idUnidade
) {}
