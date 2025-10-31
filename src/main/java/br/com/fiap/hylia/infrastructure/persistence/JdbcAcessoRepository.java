package br.com.fiap.hylia.infrastructure.persistence;

import br.com.fiap.hylia.domain.repository.AcessoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ApplicationScoped
public class JdbcAcessoRepository implements AcessoRepository {

    private final DatabaseConnection databaseConnection;

    @Inject
    public JdbcAcessoRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Long inserir(String canal, boolean sucesso) {
        final String sql = """
            INSERT INTO T_HC_ACESSO (CANAL, SUCESSO)
            VALUES (?, ?)
            """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[] { "ID_ACESSO" })) {

            // CANAL tem CHECK constraint: APP/SMS/WHATSAPP/EMAIL/TELEFONE/WEB
            String canalEfetivo = (canal == null || canal.isBlank()) ? "WEB" : canal.toUpperCase();
            ps.setString(1, canalEfetivo);
            ps.setInt(2, sucesso ? 1 : 0);

            if (ps.executeUpdate() == 0) {
                throw new RuntimeException("Nenhuma linha inserida em T_HC_ACESSO");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new RuntimeException("ID_ACESSO não retornado por getGeneratedKeys()");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir em T_HC_ACESSO", e);
        }
    }
}
