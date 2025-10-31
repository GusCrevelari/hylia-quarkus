package br.com.fiap.hylia.infrastructure.persistence;

import br.com.fiap.hylia.domain.repository.NotificacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;

@ApplicationScoped
public class JdbcNotificacaoRepository implements NotificacaoRepository {
    private final DatabaseConnection db;
    @Inject public JdbcNotificacaoRepository(DatabaseConnection db) { this.db = db; }

    @Override
    public Long criar(Long idUsuario, Long idConsulta, String tipo, String canal, boolean foiLida) {
        final String sql = """
            INSERT INTO T_HC_NOTIFICACAO
              (ID_USUARIO, ID_CONSULTA, TIPO, CANAL, DT_ENVIO, FOI_LIDA)
            VALUES (?, ?, ?, ?, SYSTIMESTAMP, ?)
        """;
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_NOTIFICACAO"})) {
            ps.setLong(1, idUsuario);
            if (idConsulta == null) ps.setNull(2, Types.NUMERIC); else ps.setLong(2, idConsulta);
            if (tipo == null) ps.setNull(3, Types.VARCHAR); else ps.setString(3, tipo);
            if (canal == null) ps.setNull(4, Types.VARCHAR); else ps.setString(4, canal);
            ps.setInt(5, foiLida ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar notificação", e);
        }
    }
}
