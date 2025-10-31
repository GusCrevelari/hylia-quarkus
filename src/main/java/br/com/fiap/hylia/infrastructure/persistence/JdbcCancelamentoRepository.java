package br.com.fiap.hylia.infrastructure.persistence;

import br.com.fiap.hylia.domain.repository.CancelamentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.*;

@ApplicationScoped
public class JdbcCancelamentoRepository implements CancelamentoRepository {
    private final DatabaseConnection db;
    @Inject public JdbcCancelamentoRepository(DatabaseConnection db) { this.db = db; }

    @Override
    public Long criar(String motivo, String canceladoPor) {
        final String sql = """
            INSERT INTO T_HC_CANCELAMENTO (MOTIVO, CANCELADO_POR, DT_CANCELAMENTO)
            VALUES (?, ?, SYSTIMESTAMP)
        """;
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_CANCELAMENTO"})) {
            if (motivo == null) ps.setNull(1, Types.VARCHAR); else ps.setString(1, motivo);
            if (canceladoPor == null) ps.setNull(2, Types.VARCHAR); else ps.setString(2, canceladoPor);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar cancelamento", e);
        }
    }
}
