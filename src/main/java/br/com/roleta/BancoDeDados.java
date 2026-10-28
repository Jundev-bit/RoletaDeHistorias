package br.com.roleta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BancoDeDados {

    private static final String URL
            = "jdbc:sqlite:historias.db";

    public static Connection conectar()
            throws SQLException {

        return DriverManager.getConnection(URL);
    }

    public static void inicializar()
            throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS historias (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    pista TEXT NOT NULL,
                    livro TEXT NOT NULL,
                    personagem TEXT NOT NULL,
                    faixa_etaria TEXT NOT NULL
                )
                """;

        try (Connection conexao = conectar(); Statement comando = conexao.createStatement()) {

            comando.execute(sql);
        }
    }

    
    public static void salvarHistoria(Historia historia,
            String faixaEtaria) throws SQLException {

        String sql = """
            INSERT INTO historias
                (nome, pista, livro, personagem, faixa_etaria)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conexao = conectar();
             PreparedStatement comando =
                 conexao.prepareStatement(sql)) {

            comando.setString(1, historia.getNome());
            comando.setString(2, historia.getPista());
            comando.setString(3, historia.getLivro());
            comando.setString(4, historia.getPersonagem());
            comando.setString(5, faixaEtaria);

            comando.executeUpdate();
        }
    }

    public static List<Historia> listarHistorias()
            throws SQLException {

        List<Historia> historias = new ArrayList<>();

        String sql = """
            SELECT nome, pista, livro, personagem
            FROM historias
            ORDER BY id
            """;

        try (Connection conexao = conectar();
             PreparedStatement comando =
                 conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                Historia historia = new Historia(
                    resultado.getString("nome"),
                    resultado.getString("pista"),
                    resultado.getString("livro"),
                    resultado.getString("personagem")
                );

                historias.add(historia);
            }
        }

        return historias;
    }

    
    public static List<Historia> listarHistoriasPorFaixaEtaria(
            String faixaEtaria) throws SQLException {

        List<Historia> historias = new ArrayList<>();

        String sql = """
            SELECT nome, pista, livro, personagem
            FROM historias
            WHERE faixa_etaria = ?
            ORDER BY id
            """;

        try (Connection conexao = conectar();
             PreparedStatement comando =
                 conexao.prepareStatement(sql)) {

            comando.setString(1, faixaEtaria);

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    historias.add(new Historia(
                        resultado.getString("nome"),
                        resultado.getString("pista"),
                        resultado.getString("livro"),
                        resultado.getString("personagem")
                    ));
                }
            }
        }

        return historias;
    }


    
}
