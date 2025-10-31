package br.com.fiap.hylia.domain.model;

import br.com.fiap.hylia.domain.util.Validators;

public class Hospital {
    private Long id;            // T_HC_UNIDADE_DE_SAUDE.ID_UNIDADE
    private String nome;        // UNIQUE
    private String endereco;    // CLOB (treat as String)
    private String telefone;    // VARCHAR2(20)

    public Hospital(Long id, String nome, String endereco, String telefone) {
        this.id = id;
        this.nome = Validators.normalizeSpaces(nome, "name");
        this.endereco = endereco == null ? null : endereco.trim();
        this.telefone = telefone == null ? null : telefone.trim();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public String getTelefone() { return telefone; }

    public Hospital withId(Long newId) { this.id = newId; return this; }

    public void atualizar(String novoNome, String novoEndereco, String novoTelefone) {
        this.nome = Validators.normalizeSpaces(novoNome, "name");
        this.endereco = novoEndereco == null ? null : novoEndereco.trim();
        this.telefone = novoTelefone == null ? null : novoTelefone.trim();
    }

    @Override public String toString() {
        return "Hospital{id=%d, nome='%s', telefone='%s'}".formatted(id, nome, telefone);
    }
}
