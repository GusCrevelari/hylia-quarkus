package br.com.fiap.hylia.infrastructure.persistence;

import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.hylia.domain.model.Consulta;
import br.com.fiap.hylia.domain.repository.ConsultaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JdbcConsultaRepository implements ConsultaRepository {

    private final DatabaseConnection db;

    @Inject
    public JdbcConsultaRepository(DatabaseConnection db) {
        this.db = db;
    }

    private static Consulta map(ResultSet rs) throws SQLException {
        return new Consulta(
                rs.getLong("ID_CONSULTA"),
                rs.getLong("ID_PACIENTE"),
                rs.getLong("ID_PROFISSIONAL"),
                rs.getTimestamp("DT_HORA").toLocalDateTime(),
                rs.getString("STATUS"),
                rs.getString("LOCAL")
        );
    }

    @Override
    public Consulta criar(Long idPaciente, Long idProfissional, LocalDateTime dtHora, String local) {
        final String sql = """
            INSERT INTO T_HC_CONSULTA
              (ID_PACIENTE, ID_PROFISSIONAL, DT_HORA, STATUS, LOCAL, ID_CANCELAMENTO, ID_USUARIO)
            VALUES
              (?, ?, ?, 'AGENDADA', ?, NULL, NULL)
        """;
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_CONSULTA"})) {
            ps.setLong(1, idPaciente);
            ps.setLong(2, idProfissional);
            ps.setTimestamp(3, Timestamp.valueOf(dtHora));
            if (local == null) ps.setNull(4, Types.VARCHAR); else ps.setString(4, local);

            if (ps.executeUpdate() == 0) throw new RuntimeException("Nenhuma linha afetada (T_HC_CONSULTA)");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Consulta(keys.getLong(1), idPaciente, idProfissional, dtHora, "AGENDADA", local);
                }
            }
            throw new RuntimeException("Sem ID_CONSULTA gerado");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar consulta", e);
        }
    }

    @Override
    public void reagendar(Long idConsulta, LocalDateTime novoHorario, String novoLocal) throws EntidadeNaoLocalizada {
        // Build dynamic SQL depending on which fields are provided
        StringBuilder sql = new StringBuilder("UPDATE T_HC_CONSULTA SET ");
        List<Object> params = new ArrayList<>();
        List<Integer> types  = new ArrayList<>();

        if (novoHorario != null) {
            sql.append("DT_HORA = ?");
            params.add(Timestamp.valueOf(novoHorario));
            types.add(Types.TIMESTAMP);
        }
        if (novoLocal != null) {
            if (!params.isEmpty()) sql.append(", ");
            sql.append("LOCAL = ?");
            params.add(novoLocal);
            types.add(Types.VARCHAR);
        }

        if (params.isEmpty()) {
            throw new IllegalArgumentException("Nothing to update");
        }

        sql.append(" WHERE ID_CONSULTA = ?");
        params.add(idConsulta);
        types.add(Types.NUMERIC);

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object val = params.get(i);
                int type = types.get(i);
                if (val == null) ps.setNull(i + 1, type);
                else {
                    if (type == Types.TIMESTAMP)      ps.setTimestamp(i + 1, (Timestamp) val);
                    else if (type == Types.VARCHAR)   ps.setString(i + 1, (String) val);
                    else if (type == Types.NUMERIC)   ps.setLong(i + 1, (Long) val);
                    else                               ps.setObject(i + 1, val, type);
                }
            }

            if (ps.executeUpdate() == 0) {
                throw new EntidadeNaoLocalizada("Consulta não encontrada para reagendar");
            }
        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao reagendar consulta", e);
        }
    }

    @Override
    public void deletar(Long idConsulta) throws EntidadeNaoLocalizada {
        final String sql = "DELETE FROM T_HC_CONSULTA WHERE ID_CONSULTA = ?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idConsulta);
            if (ps.executeUpdate() == 0) throw new EntidadeNaoLocalizada("Consulta não encontrada para deletar");
        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao deletar consulta", e);
        }
    }

    @Override
    public Consulta buscarPorId(Long idConsulta) throws EntidadeNaoLocalizada {
        final String sql = "SELECT * FROM T_HC_CONSULTA WHERE ID_CONSULTA = ?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idConsulta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao buscar consulta por id", e);
        }
        throw new EntidadeNaoLocalizada("Consulta não encontrada");
    }

    @Override
    public List<Consulta> listarPorProfissional(Long idProfissional) {
        final String sql = "SELECT * FROM T_HC_CONSULTA WHERE ID_PROFISSIONAL = ? ORDER BY DT_HORA DESC";
        List<Consulta> out = new ArrayList<>();
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idProfissional);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar consultas por profissional", e);
        }
    }

    @Override
    public List<Consulta> listarPorPaciente(Long idPaciente) {
        final String sql = "SELECT * FROM T_HC_CONSULTA WHERE ID_PACIENTE = ? ORDER BY DT_HORA DESC";
        List<Consulta> out = new ArrayList<>();
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar consultas por paciente", e);
        }
    }

    @Override
    public void marcarCancelamento(Long idConsulta, Long idCancelamento) throws EntidadeNaoLocalizada {
        final String sql = "UPDATE T_HC_CONSULTA SET STATUS='CANCELADA', ID_CANCELAMENTO=? WHERE ID_CONSULTA=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, idCancelamento == null ? null : idCancelamento, Types.NUMERIC);
            ps.setLong(2, idConsulta);
            if (ps.executeUpdate() == 0) throw new EntidadeNaoLocalizada("Consulta não encontrada para cancelar");
        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao cancelar consulta", e);
        }
    }

    @Override
    public void marcarConfirmacao(Long idConsulta) throws EntidadeNaoLocalizada {
        final String sql = "UPDATE T_HC_CONSULTA SET STATUS='CONFIRMADA' WHERE ID_CONSULTA=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idConsulta);
            if (ps.executeUpdate() == 0) throw new EntidadeNaoLocalizada("Consulta não encontrada para confirmar");
        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao confirmar consulta", e);
        }
    }
}
