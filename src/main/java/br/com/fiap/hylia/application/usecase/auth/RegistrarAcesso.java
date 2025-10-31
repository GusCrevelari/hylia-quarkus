package br.com.fiap.hylia.application.usecase.auth;

import br.com.fiap.hylia.domain.repository.AcessoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RegistrarAcesso {
    private final AcessoRepository acesso;
    @Inject public RegistrarAcesso(AcessoRepository acesso) { this.acesso = acesso; }

    public Long sucesso(String canal) { return acesso.inserir(canal, true); }
    public Long falha(String canal)   { return acesso.inserir(canal, false); }
}
