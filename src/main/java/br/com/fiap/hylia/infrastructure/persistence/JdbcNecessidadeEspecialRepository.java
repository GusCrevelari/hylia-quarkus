package br.com.fiap.hylia.infrastructure.persistence;

import br.com.fiap.hylia.domain.model.NecessidadeEspecial;
import br.com.fiap.hylia.domain.repository.NecessidadeEspecialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JdbcNecessidadeEspecialRepository implements NecessidadeEspecialRepository {

    private final DatabaseConnection databaseConnection;

    @Inject
    public JdbcNecessidadeEspecialRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public NecessidadeEspecial criar(Long idUsuario, String descricao) {
        final String sql = """
            INSERT INTO T_HC_NECESSIDADES_ESPECIAIS (ID_USUARIO, DESCRICAO)
            VALUES (?, ?)
            """;
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_NECESSIDADE"})) {

            ps.setLong(1, idUsuario);
            ps.setString(2, descricao);
            if (ps.executeUpdate() == 0) throw new RuntimeException("Nenhuma linha inserida (NECESSIDADES)");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new NecessidadeEspecial(keys.getLong(1), idUsuario, descricao);
                }
            }
            throw new RuntimeException("ID_NECESSIDADE não retornado");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar necessidade especial", e);
        }
    }

    @Override
    public List<NecessidadeEspecial> listarPorUsuario(Long idUsuario) {
        final String sql = """
            SELECT ID_NECESSIDADE, ID_USUARIO, DESCRICAO
              FROM T_HC_NECESSIDADES_ESPECIAIS
             WHERE ID_USUARIO = ?
             ORDER BY ID_NECESSIDADE
            """;
        List<NecessidadeEspecial> out = new ArrayList<>();
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new NecessidadeEspecial(
                            rs.getLong("ID_NECESSIDADE"),
                            rs.getLong("ID_USUARIO"),
                            rs.getString("DESCRICAO")
                    ));
                }
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar necessidades especiais", e);
        }
    }

    @Override
    public void deletar(Long idNecessidade) {
        final String sql = "DELETE FROM T_HC_NECESSIDADES_ESPECIAIS WHERE ID_NECESSIDADE = ?";
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idNecessidade);
            ps.executeUpdate(); // idempotente
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar necessidade especial", e);
        }
    }
}
