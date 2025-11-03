package br.com.fiap.hylia.infrastructure.persistence;

import br.com.fiap.hylia.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.hylia.domain.model.Professional;
import br.com.fiap.hylia.domain.repository.ProfessionalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JdbcProfessionalRepository implements ProfessionalRepository {

    private final DatabaseConnection databaseConnection;

    @Inject
    public JdbcProfessionalRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    private static int idadeFrom(LocalDate birth) {
        if (birth == null) return 0;
        return Period.between(birth, LocalDate.now()).getYears();
    }

    private static LocalDate birthFromIdade(int idade) {
        if (idade <= 0) idade = 18;
        return LocalDate.now().minusYears(idade);
    }

    /** Maps a DB row to the domain model (now includes especialidade). */
    private Professional mapToProfessional(
            long idProfissional, String cpf, String nome, String email,
            String crm, Long idUnidade, LocalDate dtNascimento, String especialidade) {

        int idade = idadeFrom(dtNascimento);
        return new Professional(
                idProfissional,   // domain id = ID_PROFISSIONAL
                cpf,
                nome,
                idade,
                email,
                especialidade,    // <- pass it through
                crm
        );
    }

    @Override
    public Professional salvar(Professional p) {
        final String insertUsuario = """
            INSERT INTO T_HC_USUARIO
              (NOME, CPF, TELEFONE, EMAIL, TP_USUARIO, DT_NASCIMENTO, CRIACAO)
            VALUES
              (?, ?, NULL, ?, 'PROFISSIONAL', ?, SYSTIMESTAMP)
            """;

        // now persists ESPECIALIDADE too
        final String insertProf = """
            INSERT INTO T_HC_PROFISSIONAIS
              (NOME, CRM, ID_UNIDADE, ESPECIALIDADE)
            VALUES
              (?, ?, ?, ?)
            """;

        try (Connection conn = databaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1) Insert usuario (for personal info)
            try (PreparedStatement st = conn.prepareStatement(insertUsuario)) {
                st.setString(1, p.getNome());
                st.setString(2, p.getCpf());
                st.setString(3, p.getEmail());
                st.setDate(4, Date.valueOf(birthFromIdade(p.getIdade())));
                if (st.executeUpdate() == 0) {
                    throw new RuntimeException("Nenhuma linha inserida em T_HC_USUARIO");
                }
            }

            // 2) Insert profissional and capture ID_PROFISSIONAL
            long idProfissional;
            try (PreparedStatement st = conn.prepareStatement(insertProf, new String[]{"ID_PROFISSIONAL"})) {
                st.setString(1, p.getNome());
                st.setString(2, p.getCrm());
                st.setNull(3, Types.NUMERIC); // optional ID_UNIDADE
                st.setString(4, p.getEspecialidade()); // <- NEW
                if (st.executeUpdate() == 0) {
                    throw new RuntimeException("Nenhuma linha inserida em T_HC_PROFISSIONAIS");
                }
                try (ResultSet keys = st.getGeneratedKeys()) {
                    if (!keys.next()) throw new RuntimeException("Sem ID_PROFISSIONAL gerado");
                    idProfissional = keys.getLong(1);
                }
            }

            conn.commit();
            return p.withId(idProfissional);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar PROFESSIONAL (USUARIO + PROFISSIONAIS)", e);
        }
    }

    @Override
    public Professional buscarPorCrm(String crm) throws EntidadeNaoLocalizada {
        final String sql = """
        SELECT
            p.ID_PROFISSIONAL,
            p.NOME             AS P_NOME,
            p.CRM,
            p.ID_UNIDADE,
            p.ESPECIALIDADE,          -- NEW
            u.CPF,
            u.EMAIL,
            u.DT_NASCIMENTO
        FROM T_HC_PROFISSIONAIS p
        LEFT JOIN T_HC_USUARIO u
               ON UPPER(u.NOME) = UPPER(p.NOME)
              AND u.TP_USUARIO = 'PROFISSIONAL'
        WHERE p.CRM = ?
        """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, crm);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    long idProf = rs.getLong("ID_PROFISSIONAL");
                    String nome = rs.getString("P_NOME");
                    String cpf = rs.getString("CPF");
                    String email = rs.getString("EMAIL");
                    Date dt = rs.getDate("DT_NASCIMENTO");
                    LocalDate birth = (dt == null) ? null : dt.toLocalDate();
                    Long idUnidade = rs.getObject("ID_UNIDADE") == null ? null : rs.getLong("ID_UNIDADE");
                    String especialidade = rs.getString("ESPECIALIDADE"); // NEW

                    return mapToProfessional(idProf, cpf, nome, email, crm, idUnidade, birth, especialidade);
                }
            }
        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao buscar PROFESSIONAL por CRM", e);
        }
        throw new EntidadeNaoLocalizada("Professional não encontrado");
    }

    @Override
    public List<Professional> listar() {
        final String sql = """
        SELECT
            p.ID_PROFISSIONAL,
            p.NOME             AS P_NOME,
            p.CRM,
            p.ID_UNIDADE,
            p.ESPECIALIDADE,         -- NEW
            u.CPF,
            u.EMAIL,
            u.DT_NASCIMENTO
        FROM T_HC_PROFISSIONAIS p
        LEFT JOIN T_HC_USUARIO u
               ON UPPER(u.NOME) = UPPER(p.NOME)
              AND u.TP_USUARIO = 'PROFISSIONAL'
        ORDER BY p.CRM
        """;

        List<Professional> out = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                long idProf = rs.getLong("ID_PROFISSIONAL");
                String nome = rs.getString("P_NOME");
                String cpf = rs.getString("CPF");
                String email = rs.getString("EMAIL");
                Date dt = rs.getDate("DT_NASCIMENTO");
                LocalDate birth = (dt == null) ? null : dt.toLocalDate();
                String crmDb = rs.getString("CRM");
                Long idUnidade = rs.getObject("ID_UNIDADE") == null ? null : rs.getLong("ID_UNIDADE");
                String especialidade = rs.getString("ESPECIALIDADE"); // NEW

                out.add(mapToProfessional(idProf, cpf, nome, email, crmDb, idUnidade, birth, especialidade));
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar PROFESSIONAL", e);
        }
    }

    @Override
    public void atualizarPorCrm(String crmChave,
                                String novoNome,
                                int novaIdade,
                                String novoEmail,
                                String novaEspecialidade) throws EntidadeNaoLocalizada {

        final String findUserSql = """
            SELECT u.ID_USUARIO
              FROM T_HC_PROFISSIONAIS p
              JOIN T_HC_USUARIO u
                ON UPPER(u.NOME) = UPPER(p.NOME)
               AND u.TP_USUARIO = 'PROFISSIONAL'
             WHERE p.CRM = ?
            """;

        final String updUsuario = """
            UPDATE T_HC_USUARIO
               SET NOME = ?, EMAIL = ?, DT_NASCIMENTO = ?
             WHERE ID_USUARIO = ?
            """;

        // now updates ESPECIALIDADE as well
        final String updProf = """
            UPDATE T_HC_PROFISSIONAIS
               SET NOME = ?, ESPECIALIDADE = ?
             WHERE CRM = ?
            """;

        try (Connection conn = databaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Long idUsuario = null;
            try (PreparedStatement st = conn.prepareStatement(findUserSql)) {
                st.setString(1, crmChave);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        idUsuario = rs.getLong("ID_USUARIO");
                    }
                }
            }
            if (idUsuario == null) {
                conn.rollback();
                throw new EntidadeNaoLocalizada("Professional não encontrado para atualizar");
            }

            try (PreparedStatement st = conn.prepareStatement(updUsuario)) {
                st.setString(1, novoNome);
                st.setString(2, novoEmail);
                st.setDate(3, Date.valueOf(birthFromIdade(novaIdade)));
                st.setLong(4, idUsuario);
                st.executeUpdate();
            }

            try (PreparedStatement st = conn.prepareStatement(updProf)) {
                st.setString(1, novoNome);
                st.setString(2, novaEspecialidade); // NEW
                st.setString(3, crmChave);
                st.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao atualizar PROFESSIONAL", e);
        }
    }

    @Override
    public void deletarPorCrm(String crm) throws EntidadeNaoLocalizada {
        final String sql = "DELETE FROM T_HC_PROFISSIONAIS WHERE CRM = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, crm);
            if (st.executeUpdate() == 0) {
                throw new EntidadeNaoLocalizada("Professional não encontrado para deletar");
            }
        } catch (SQLException e) {
            throw new EntidadeNaoLocalizada("Erro ao deletar PROFESSIONAL por CRM", e);
        }
    }
}
