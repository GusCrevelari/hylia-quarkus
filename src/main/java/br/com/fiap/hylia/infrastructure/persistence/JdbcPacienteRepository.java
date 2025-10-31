package br.com.fiap.hylia.infrastructure.persistence;

import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.hylia.domain.model.Paciente;
import br.com.fiap.hylia.domain.repository.PacienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JdbcPacienteRepository implements PacienteRepository {

    private final DatabaseConnection databaseConnection;

    @Inject
    public JdbcPacienteRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }


    private static int idadeFrom(LocalDate birth) {
        if (birth == null) return 0;
        return Period.between(birth, LocalDate.now()).getYears();
    }

    private static LocalDate birthFromIdade(int idade) {
        if (idade < 0) idade = 0;
        if (idade > 120) idade = 120;
        return LocalDate.now().minusYears(idade);
    }


    @Override
    public Paciente salvar(Paciente p) {
        final String sql = """
            INSERT INTO T_HC_USUARIO
              (NOME, CPF, TELEFONE, EMAIL, TP_USUARIO, DT_NASCIMENTO, CRIACAO)
            VALUES
              (?, ?, NULL, NULL, 'PACIENTE', ?, SYSTIMESTAMP)
            """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[] {"ID_USUARIO"})) {

            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCpf());
            stmt.setDate(3, Date.valueOf(birthFromIdade(p.getIdade())));

            if (stmt.executeUpdate() == 0)
                throw new RuntimeException("Nenhuma linha afetada (T_HC_USUARIO PACIENTE)");

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return p.withId(keys.getLong(1));
            }
            // Fallback: read back by CPF (should be unique)
            return buscarPorCpf(p.getCpf());

        } catch (EntidadeNaoLocalizada e) {
            throw new RuntimeException("Paciente inserido mas não encontrado por CPF (inconsistência)", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar paciente (T_HC_USUARIO)", e);
        }
    }

    @Override
    public Paciente buscarPorCpf(String cpf) throws EntidadeNaoLocalizada {
        final String sql = """
            SELECT
                u.ID_USUARIO,
                u.NOME,
                u.CPF,
                u.DT_NASCIMENTO
            FROM T_HC_USUARIO u
            WHERE u.CPF = ?
              AND u.TP_USUARIO = 'PACIENTE'
            """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong("ID_USUARIO");
                    String nome = rs.getString("NOME");
                    Date dt = rs.getDate("DT_NASCIMENTO");
                    LocalDate birth = (dt == null) ? null : dt.toLocalDate();
                    int idade = idadeFrom(birth);
                    return new Paciente(id, cpf, nome, idade);
                }
            }
        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao buscar paciente por CPF", e);
        }
        throw new EntidadeNaoLocalizada("Paciente não encontrado");
    }

    @Override
    public List<Paciente> listar() {
        final String sql = """
            SELECT
                u.ID_USUARIO,
                u.NOME,
                u.CPF,
                u.DT_NASCIMENTO
            FROM T_HC_USUARIO u
            WHERE u.TP_USUARIO = 'PACIENTE'
            ORDER BY u.NOME
            """;

        List<Paciente> out = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("ID_USUARIO");
                String nome = rs.getString("NOME");
                String cpf = rs.getString("CPF");
                Date dt = rs.getDate("DT_NASCIMENTO");
                LocalDate birth = (dt == null) ? null : dt.toLocalDate();
                int idade = idadeFrom(birth);

                out.add(new Paciente(id, cpf, nome, idade));
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pacientes", e);
        }
    }

    @Override
    public void deletarPorCpf(String cpf) throws EntidadeNaoLocalizada {
        final String sql = """
            DELETE FROM T_HC_USUARIO
             WHERE CPF = ?
               AND TP_USUARIO = 'PACIENTE'
            """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            int n = stmt.executeUpdate();
            if (n == 0)
                throw new EntidadeNaoLocalizada("Paciente não encontrado para deletar");
        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao deletar paciente por CPF", e);
        }
    }

    @Override
    public void atualizarNomeEIdadePorCpf(String cpf, String novoNome, int novaIdade) throws EntidadeNaoLocalizada {
        final String sql = """
            UPDATE T_HC_USUARIO
               SET NOME = ?,
                   DT_NASCIMENTO = ?
             WHERE CPF = ?
               AND TP_USUARIO = 'PACIENTE'
            """;
        try (Connection c = databaseConnection.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setString(1, novoNome);
            stmt.setDate(2, Date.valueOf(birthFromIdade(novaIdade)));
            stmt.setString(3, cpf);

            if (stmt.executeUpdate() == 0)
                throw new EntidadeNaoLocalizada("Paciente não encontrado para atualizar");

        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao atualizar paciente", e);
        }
    }
}
