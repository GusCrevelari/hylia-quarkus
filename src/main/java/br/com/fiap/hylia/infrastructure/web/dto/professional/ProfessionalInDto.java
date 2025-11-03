// infrastructure/web/dto/profissional/ProfessionalInDto.java
package br.com.fiap.hylia.infrastructure.web.dto.professional;

public record ProfessionalInDto(
        String nome,
        String cpf,
        Integer idade,
        String email,
        String especialidade,
        String crm,
        Long idUnidade
) {}
