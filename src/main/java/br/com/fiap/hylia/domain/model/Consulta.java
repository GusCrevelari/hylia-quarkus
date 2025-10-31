package br.com.fiap.hylia.domain.model;

import java.time.LocalDateTime;

public class Consulta {
    private Long id;
    private Long idPaciente;
    private Long idProfissional;
    private LocalDateTime dtHora;
    private String status; // AGENDADA, CONFIRMADA, CANCELADA, REALIZADA, PENDENTE
    private String local;

    public Consulta(Long id, Long idPaciente, Long idProfissional, LocalDateTime dtHora, String status, String local) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.idProfissional = idProfissional;
        this.dtHora = dtHora;
        this.status = status;
        this.local = local;
    }

    public Long getId() { return id; }
    public Long getIdPaciente() { return idPaciente; }
    public Long getIdProfissional() { return idProfissional; }
    public LocalDateTime getDtHora() { return dtHora; }
    public String getStatus() { return status; }
    public String getLocal() { return local; }

    public Consulta withId(Long newId) { this.id = newId; return this; }
}
