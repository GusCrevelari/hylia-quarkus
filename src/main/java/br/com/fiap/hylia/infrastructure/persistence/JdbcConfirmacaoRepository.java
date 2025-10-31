package br.com.fiap.hylia.infrastructure.persistence;

import br.com.fiap.hylia.domain.repository.ConfirmacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;

@ApplicationScoped
public class JdbcConfirmacaoRepository implements ConfirmacaoRepository {
    private final DatabaseConnection db;
    @Inject public JdbcConfirmacaoRepository(DatabaseConnection db) { this.db = db; }

    @Override
    public Long criar(Long idConsulta, String canal) {
        final String sql = """
            INSERT INTO T_HC_CONFIRMACAO (ID_CONSULTA, DT_CONFIRMACAO, CANAL)
            VALUES (?, SYSTIMESTAMP, ?)
        """;
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_CONFIRMACAO"})) {
            ps.setLong(1, idConsulta);
            if (canal == null) ps.setNull(2, Types.VARCHAR); else ps.setString(2, canal);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar confirmação", e);
        }
    }
}
