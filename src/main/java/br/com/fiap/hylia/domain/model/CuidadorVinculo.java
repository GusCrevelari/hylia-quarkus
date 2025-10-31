package br.com.fiap.hylia.domain.model;

public class CuidadorVinculo {
    private Long id;              // ID_CUIDADOR
    private Long idPaciente;      // ID_PACIENTE (usuario PACIENTE)
    private Long idUsuario;       // ID_USUARIO (cuidador)

    public CuidadorVinculo(Long id, Long idPaciente, Long idUsuario) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.idUsuario = idUsuario;
    }

    public Long getId() { return id; }
    public Long getIdPaciente() { return idPaciente; }
    public Long getIdUsuario() { return idUsuario; }

    public CuidadorVinculo withId(Long newId) {
        this.id = newId;
        return this;
    }

    @Override public String toString() {
        return "CuidadorVinculo{id=%d, idPaciente=%d, idUsuario=%d}".formatted(id, idPaciente, idUsuario);
    }
}
