package br.com.fiap.hylia.domain.model;

public class NecessidadeEspecial {
    private Long id;
    private Long idUsuario;
    private String descricao;

    public NecessidadeEspecial(Long id, Long idUsuario, String descricao) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public Long getIdUsuario() { return idUsuario; }
    public String getDescricao() { return descricao; }

    public NecessidadeEspecial withId(Long newId) {
        this.id = newId;
        return this;
    }

    @Override public String toString() {
        return "NecessidadeEspecial{id=%d, idUsuario=%d, descricao='%s'}"
                .formatted(id, idUsuario, descricao);
    }
}
