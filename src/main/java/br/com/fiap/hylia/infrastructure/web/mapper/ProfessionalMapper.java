// infrastructure/web/mapper/ProfessionalMapper.java
package br.com.fiap.hylia.infrastructure.web.mapper;

import br.com.fiap.hylia.domain.model.Professional;
import br.com.fiap.hylia.infrastructure.web.dto.professional.ProfessionalInDto;
import br.com.fiap.hylia.infrastructure.web.dto.professional.ProfessionalOutDto;

public final class ProfessionalMapper {
    private ProfessionalMapper() {}
    public static Professional toDomain(ProfessionalInDto dto) {
        return new Professional(
                null,
                dto.cpf(),
                dto.nome(),
                dto.idade() == null ? 0 : dto.idade(),
                dto.email(),
                dto.especialidade(),
                dto.crm()
        );
    }
    public static ProfessionalOutDto toOut(Professional p) {
        return new ProfessionalOutDto(
                p.getId(), p.getNome(), p.getCpf(), p.getIdade(),
                p.getEmail(), p.getEspecialidade(), p.getCrm(), null
        );
    }
}
