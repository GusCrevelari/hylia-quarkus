package br.com.fiap.hylia.domain.model;

import java.util.Objects;

public abstract class Pessoa {
    private String cpf;
    private String nome;
    private int idade;

    protected Pessoa(String cpf, String nome, int idade) {
        this.cpf = cpf;
        this.nome = nome;
        this.idade = idade;
    }

    public String getCpf()   { return cpf; }
    public String getNome()  { return nome; }
    public int getIdade()    { return idade; }

    protected void setCpf(String cpf)   { this.cpf = cpf; }
    protected void setNome(String nome) { this.nome = nome; }
    protected void setIdade(int idade)  { this.idade = idade; }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pessoa that)) return false;
        return cpf != null && cpf.equals(that.cpf);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(cpf);
    }

    @Override
    public String toString() {
        return "CPF: " + cpf + ", Nome: " + nome + ", Idade: " + idade;
    }
}
