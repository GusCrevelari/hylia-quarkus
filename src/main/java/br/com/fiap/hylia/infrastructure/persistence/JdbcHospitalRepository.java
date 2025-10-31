package br.com.fiap.hylia.infrastructure.persistence;

import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.hylia.domain.model.Hospital;
import br.com.fiap.hylia.domain.repository.HospitalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JdbcHospitalRepository implements HospitalRepository {

    private final DatabaseConnection databaseConnection;

    @Inject
    public JdbcHospitalRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Hospital salvar(Hospital h) {
        final String sql = """
            INSERT INTO T_HC_UNIDADE_DE_SAUDE (NOME, ENDERECO, TELEFONE)
            VALUES (?, ?, ?)
        """;
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_UNIDADE"})) {

            ps.setString(1, h.getNome());
            if (h.getEndereco() == null) ps.setNull(2, Types.CLOB); else ps.setString(2, h.getEndereco());
            ps.setString(3, h.getTelefone());

            if (ps.executeUpdate() == 0) throw new RuntimeException("Nenhuma linha inserida (UNIDADE)");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return h.withId(keys.getLong(1));
            }
            return h;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar unidade de saúde", e);
        }
    }

    @Override
    public Hospital buscarPorNome(String nome) throws EntidadeNaoLocalizada {
        final String sql = """
            SELECT ID_UNIDADE, NOME, ENDERECO, TELEFONE
              FROM T_HC_UNIDADE_DE_SAUDE
             WHERE NOME = ?
        """;
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Hospital(
                            rs.getLong("ID_UNIDADE"),
                            rs.getString("NOME"),
                            rs.getString("ENDERECO"),
                            rs.getString("TELEFONE")
                    );
                }
            }
            throw new EntidadeNaoLocalizada("Hospital não encontrado");
        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao buscar por nome", e);
        }
    }

    @Override
    public List<Hospital> listar() {
        final String sql = "SELECT ID_UNIDADE, NOME, ENDERECO, TELEFONE FROM T_HC_UNIDADE_DE_SAUDE ORDER BY NOME";
        var out = new ArrayList<Hospital>();
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Hospital(
                        rs.getLong("ID_UNIDADE"),
                        rs.getString("NOME"),
                        rs.getString("ENDERECO"),
                        rs.getString("TELEFONE")
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar unidades de saúde", e);
        }
    }

    @Override
    public void atualizarPorNome(String nomeChave, String novoNome, String novoEndereco, String novoTelefone)
            throws EntidadeNaoLocalizada {
        final String sql = """
            UPDATE T_HC_UNIDADE_DE_SAUDE
               SET NOME = ?, ENDERECO = ?, TELEFONE = ?
             WHERE NOME = ?
        """;
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, novoNome);
            if (novoEndereco == null) ps.setNull(2, Types.CLOB); else ps.setString(2, novoEndereco);
            ps.setString(3, novoTelefone);
            ps.setString(4, nomeChave);

            if (ps.executeUpdate() == 0) throw new EntidadeNaoLocalizada("Hospital não encontrado para atualizar");
        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao atualizar por nome", e);
        }
    }

    @Override
    public void deletarPorNome(String nome) throws EntidadeNaoLocalizada {
        final String sql = "DELETE FROM T_HC_UNIDADE_DE_SAUDE WHERE NOME = ?";
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            if (ps.executeUpdate() == 0) throw new EntidadeNaoLocalizada("Hospital não encontrado para deletar");
        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao deletar por nome", e);
        }
    }
}
