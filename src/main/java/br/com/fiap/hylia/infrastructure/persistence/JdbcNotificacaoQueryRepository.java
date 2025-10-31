package br.com.fiap.hylia.infrastructure.persistence;

import br.com.fiap.hylia.domain.model.Notificacao;
import br.com.fiap.hylia.domain.repository.NotificacaoQueryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JdbcNotificacaoQueryRepository implements NotificacaoQueryRepository {

    private final DatabaseConnection databaseConnection;

    @Inject
    public JdbcNotificacaoQueryRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    private static OffsetDateTime toOffsetDateTime(Timestamp ts) {
        if (ts == null) return null;
        return ts.toInstant().atOffset(ZoneOffset.UTC);
    }

    @Override
    public List<Notificacao> listarPorUsuario(Long idUsuario) {
        final String sql = """
            SELECT ID_NOTIFICACAO, ID_USUARIO, ID_CONSULTA, TIPO, CANAL, DT_ENVIO, FOI_LIDA
              FROM T_HC_NOTIFICACAO
             WHERE ID_USUARIO = ?
             ORDER BY DT_ENVIO DESC, ID_NOTIFICACAO DESC
        """;
        List<Notificacao> out = new ArrayList<>();
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Notificacao(
                            rs.getLong("ID_NOTIFICACAO"),
                            rs.getLong("ID_USUARIO"),
                            (Long) rs.getObject("ID_CONSULTA"),
                            rs.getString("TIPO"),
                            rs.getString("CANAL"),
                            toOffsetDateTime(rs.getTimestamp("DT_ENVIO")),
                            rs.getInt("FOI_LIDA") == 1
                    ));
                }
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar notificações", e);
        }
    }

    @Override
    public void marcarComoLida(Long idNotificacao) {
        final String sql = "UPDATE T_HC_NOTIFICACAO SET FOI_LIDA = 1 WHERE ID_NOTIFICACAO = ?";
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idNotificacao);
            ps.executeUpdate(); // idempotente
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao marcar notificação como lida", e);
        }
    }
}
