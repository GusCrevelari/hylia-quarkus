package br.com.fiap.hylia.infrastructure.web.mapper;

import br.com.fiap.hylia.domain.model.Hospital;
import br.com.fiap.hylia.infrastructure.web.dto.hospital.HospitalInDto;
import br.com.fiap.hylia.infrastructure.web.dto.hospital.HospitalOutDto;

public class HospitalMapper {
    public static Hospital toDomain(HospitalInDto in) {
        return new Hospital(null, in.nome(), in.endereco(), in.telefone());
    }
    public static HospitalOutDto toOut(Hospital h) {
        return new HospitalOutDto(h.getId(), h.getNome(), h.getEndereco(), h.getTelefone());
    }
}