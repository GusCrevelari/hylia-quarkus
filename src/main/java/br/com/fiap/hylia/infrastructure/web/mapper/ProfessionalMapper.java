// infrastructure/web/mapper/ProfessionalMapper.java
package br.com.fiap.hylia.infrastructure.web.mapper;

import br.com.fiap.hylia.domain.model.Professional;
import br.com.fiap.hylia.infrastructure.web.dto.professional.ProfessionalInDto;
import br.com.fiap.hylia.infrastructure.web.dto.professional.ProfessionalOutDto;

public final class ProfessionalMapper {
    private ProfessionalMapper() {}

    private static String digitsOrNull(String s) {
        if (s == null) return null;
        String d = s.replaceAll("\\D", "");
        return d.isEmpty() ? null : d;
    }

    public static Professional toDomain(ProfessionalInDto dto) {
        return new Professional(
                null,
                digitsOrNull(dto.cpf()),                      // ← sanitize
                dto.nome(),
                dto.idade() == null ? 0 : dto.idade(),        // 0 = unspecified
                (dto.email() == null || dto.email().isBlank()) ? null : dto.email(),
                (dto.especialidade() == null || dto.especialidade().isBlank()) ? null : dto.especialidade(),
                dto.crm()
        );
    }

    public static ProfessionalOutDto toOut(Professional p) {
        return new ProfessionalOutDto(
                p.getId(),
                p.getNome(),
                p.getCpf(),
                p.getIdade(),
                p.getEmail(),
                p.getEspecialidade(),
                p.getCrm(),
                null // idUnidade, se aplicável
        );
    }
}
