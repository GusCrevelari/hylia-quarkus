// infrastructure/web/dto/profissional/ProfessionalInDto.java
package br.com.fiap.hylia.infrastructure.web.dto.professional;

public record ProfessionalInDto(
        String nome,
        String cpf,
        Integer idade,
        String email,
        String especialidade, // domain-only (not in DB yet)
        String crm,
        Long idUnidade // optional, currently not persisted in our repo impl (null OK)
) {}
