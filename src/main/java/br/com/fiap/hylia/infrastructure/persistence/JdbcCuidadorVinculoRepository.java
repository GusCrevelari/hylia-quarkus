package br.com.fiap.hylia.infrastructure.persistence;

import br.com.fiap.hylia.domain.model.CuidadorVinculo;
import br.com.fiap.hylia.domain.repository.CuidadorVinculoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JdbcCuidadorVinculoRepository implements CuidadorVinculoRepository {

    private final DatabaseConnection databaseConnection;

    @Inject
    public JdbcCuidadorVinculoRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public CuidadorVinculo vincular(Long idPaciente, Long idUsuarioCuidador) {
        final String sql = """
            INSERT INTO T_HC_CUIDADORES (ID_PACIENTE, ID_USUARIO)
            VALUES (?, ?)
            """;
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[] { "ID_CUIDADOR" })) {

            ps.setLong(1, idPaciente);
            ps.setLong(2, idUsuarioCuidador);

            ps.executeUpdate(); // UK_T_HC_CUIDADORES_UNIQUE_PAIR protects duplicates

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new CuidadorVinculo(keys.getLong(1), idPaciente, idUsuarioCuidador);
                }
            }
            throw new RuntimeException("ID_CUIDADOR não retornado");
        } catch (SQLIntegrityConstraintViolationException dup) {
            // já existe o vínculo; devolve um “fake” com id null (ou você pode lançar 409)
            return new CuidadorVinculo(null, idPaciente, idUsuarioCuidador);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar vínculo cuidador", e);
        }
    }

    @Override
    public List<CuidadorVinculo> listarPorPaciente(Long idPaciente) {
        final String sql = """
            SELECT ID_CUIDADOR, ID_PACIENTE, ID_USUARIO
              FROM T_HC_CUIDADORES
             WHERE ID_PACIENTE = ?
             ORDER BY ID_CUIDADOR
            """;
        List<CuidadorVinculo> out = new ArrayList<>();
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new CuidadorVinculo(
                            rs.getLong("ID_CUIDADOR"),
                            rs.getLong("ID_PACIENTE"),
                            rs.getLong("ID_USUARIO")
                    ));
                }
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cuidadores por paciente", e);
        }
    }

    @Override
    public void desvincular(Long idPaciente, Long idUsuarioCuidador) {
        final String sql = """
            DELETE FROM T_HC_CUIDADORES
             WHERE ID_PACIENTE = ? AND ID_USUARIO = ?
            """;
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idPaciente);
            ps.setLong(2, idUsuarioCuidador);
            ps.executeUpdate(); // idempotente
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desfazer vínculo cuidador", e);
        }
    }
}
