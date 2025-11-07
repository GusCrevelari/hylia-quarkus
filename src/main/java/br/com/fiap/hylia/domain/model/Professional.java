package br.com.fiap.hylia.domain.model;

import br.com.fiap.hylia.domain.exceptions.ValidacaoDominioException;
import br.com.fiap.hylia.domain.util.Validators;

public class Professional extends Pessoa {
    private Long id;
    private String email;
    private String especialidade;
    private final String crm;

    /** STRICT constructor — used by API/use cases (kept as-is) */
    public Professional(Long id, String cpf, String nome, int idade,
                        String email, String especialidade, String crm) {
        super(cpf, nome, idade);

        if (!Validators.isValidCpf(cpf))   throw new ValidacaoDominioException("Invalid CPF");
        if (!Validators.isValidCrm(crm))   throw new ValidacaoDominioException("Invalid CRM");
        if (!Validators.isValidEmail(email)) throw new ValidacaoDominioException("Invalid email");
        if (idade < 23) throw new ValidacaoDominioException("Professional must be 23+");

        this.id = id;
        this.email = email;

        if (especialidade == null || especialidade.trim().isEmpty()) {
            this.especialidade = null;
        } else {
            this.especialidade = Validators.normalizeSpaces(especialidade, "specialty");
        }

        setNome(Validators.normalizeSpaces(nome, "name"));
        this.crm = crm;
    }


    public static Professional fromDb(Long id,
                                      String cpf,
                                      String nome,
                                      Integer idade,
                                      String email,
                                      String especialidade,
                                      String crm) {
        // choose a benign default when idade is null
        int safeAge = (idade == null ? 0 : idade);

        // go through a private skip-validation constructor
        return new Professional(
                id,
                cpf,
                (nome == null ? "" : nome),
                safeAge,
                email,
                especialidade,
                crm,
                 true
        );
    }

    /** Private ctor used by fromDb() to bypass validations */
    private Professional(Long id, String cpf, String nome, int idade,
                         String email, String especialidade, String crm,
                         boolean skipValidation) {
        super(cpf, nome, idade);

        if (!skipValidation) {

            if (!Validators.isValidCpf(cpf))   throw new ValidacaoDominioException("Invalid CPF");
            if (!Validators.isValidCrm(crm))   throw new ValidacaoDominioException("Invalid CRM");
            if (!Validators.isValidEmail(email)) throw new ValidacaoDominioException("Invalid email");
            if (idade < 23) throw new ValidacaoDominioException("Professional must be 23+");
        }

        this.id = id;
        this.email = email;

        if (especialidade == null || especialidade.trim().isEmpty()) {
            this.especialidade = null;
        } else {

            this.especialidade = Validators.normalizeSpaces(especialidade, "specialty");
        }


        if (nome != null && !nome.isBlank()) {
            setNome(Validators.normalizeSpaces(nome, "name"));
        }

        this.crm = crm;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getEspecialidade() { return especialidade; }
    public String getCrm() { return crm; }

    public Professional withId(Long newId) {
        this.id = newId;
        return this;
    }

    public void atualizarPerfil(String novoNome, int novaIdade, String novoEmail, String novaEspecialidade) {
        setNome(Validators.normalizeSpaces(novoNome, "name"));
        if (novaIdade < 23) throw new ValidacaoDominioException("Professional must be 23+");
        setIdade(novaIdade);
        if (!Validators.isValidEmail(novoEmail)) throw new ValidacaoDominioException("Invalid email");
        this.email = novoEmail;
        this.especialidade = Validators.normalizeSpaces(novaEspecialidade, "specialty");
    }

    @Override
    public String toString() {
        return "Professional{id=%d, name='%s', cpf='%s', age=%d, email='%s', specialty='%s', crm='%s'}"
                .formatted(id, getNome(), getCpf(), getIdade(), email, especialidade, crm);
    }
}
