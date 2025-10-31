package br.com.fiap.hylia.domain.model;

import br.com.fiap.hylia.domain.exceptions.ValidacaoDominioException;
import br.com.fiap.hylia.domain.util.Validators;

public class Paciente extends Pessoa {
    private Long id;

    public Paciente(Long id, String cpf, String nome, int idade) {
        super(cpf, nome, idade);
        if (!Validators.isValidCpf(cpf)) throw new ValidacaoDominioException("Invalid CPF");
        if (idade < 0 || idade > 120) throw new ValidacaoDominioException("Age must be 0..120");
        this.id = id;
        setNome(Validators.normalizeSpaces(nome, "name"));
    }

    public Long getId() {
        return id;
    }

    public Paciente withId(Long newId) {
        this.id = newId;
        return this;
    }

    public void atualizarNomeEIdade(String novoNome, int novaIdade) {
        if (novaIdade + 2 < getIdade()) throw new ValidacaoDominioException("Inconsistent new age");
        setNome(Validators.normalizeSpaces(novoNome, "name"));
        if (novaIdade < 0 || novaIdade > 120) throw new ValidacaoDominioException("Age must be 0..120");
        setIdade(novaIdade);
    }

    public boolean elegivelTeleconsulta(boolean hasCompanion) {
        int idade = getIdade();
        if (idade >= 65) return true;
        if (idade < 16) return hasCompanion;
        return true;
    }

    @Override
    public String toString() {
        return "Paciente{id=%d, name='%s', cpf='%s', age=%d}"
                .formatted(getId(), getNome(), getCpf(), getIdade());
    }
}
