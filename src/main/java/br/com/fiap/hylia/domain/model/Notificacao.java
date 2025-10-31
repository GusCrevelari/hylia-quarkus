package br.com.fiap.hylia.domain.model;

import java.time.OffsetDateTime;

public class Notificacao {
    private Long id;
    private Long idUsuario;
    private Long idConsulta;
    private String tipo;
    private String canal;
    private OffsetDateTime dtEnvio;
    private boolean foiLida;  // 0/1

    public Notificacao(Long id, Long idUsuario, Long idConsulta, String tipo, String canal,
                       OffsetDateTime dtEnvio, boolean foiLida) {
        this.id = id; this.idUsuario = idUsuario; this.idConsulta = idConsulta;
        this.tipo = tipo; this.canal = canal; this.dtEnvio = dtEnvio; this.foiLida = foiLida;
    }

    public Long getId() { return id; }
    public Long getIdUsuario() { return idUsuario; }
    public Long getIdConsulta() { return idConsulta; }
    public String getTipo() { return tipo; }
    public String getCanal() { return canal; }
    public OffsetDateTime getDtEnvio() { return dtEnvio; }
    public boolean isFoiLida() { return foiLida; }

    public Notificacao withId(Long newId) { this.id = newId; return this; }
}
