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

    public static void inicializar() throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS historias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                pista TEXT NOT NULL,
                livro TEXT NOT NULL,
                personagem TEXT NOT NULL,
                faixa_etaria TEXT NOT NULL DEFAULT '',
                turma TEXT NOT NULL DEFAULT ''
            )
            """;

        try (Connection conexao = conectar();
            Statement comando = conexao.createStatement()) {

            comando.execute(sql);

            // Verifica se a coluna turma já existe
            boolean temTurma = false;
            boolean temFaixaEtaria = false;

            try (ResultSet colunas = comando.executeQuery(
                    "PRAGMA table_info(historias)")) {

                while (colunas.next()) {
                    String nomeColuna = colunas.getString("name");

                    if ("turma".equals(nomeColuna)) {
                        temTurma = true;
                    }

                    if ("faixa_etaria".equals(nomeColuna)) {
                        temFaixaEtaria = true;
                    }
                }
            }

            if (!temTurma) {
                comando.execute(
                    "ALTER TABLE historias ADD COLUMN turma TEXT NOT NULL DEFAULT ''"
                );
            }

            if (!temFaixaEtaria) {
                comando.execute(
                    "ALTER TABLE historias ADD COLUMN faixa_etaria TEXT NOT NULL DEFAULT ''"
                );
            }
        }
    }

    
    public static void salvarHistoria(Historia historia,
            String faixaEtaria) throws SQLException {

        String sql = """
            INSERT INTO historias
                (nome, pista, livro, personagem, faixa_etaria, turma)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conexao = conectar();
             PreparedStatement comando =
                 conexao.prepareStatement(sql)) {

            comando.setString(1, historia.getNome());
            comando.setString(2, historia.getPista());
            comando.setString(3, historia.getLivro());
            comando.setString(4, historia.getPersonagem());
            comando.setString(5, faixaEtaria);
            comando.setString(6, historia.getTurma());

            comando.executeUpdate();
        }
    }

        public static boolean excluirHistoria(String nomeLivro)
            throws SQLException {

        String sql = "DELETE FROM historias WHERE livro = ?";

        try (Connection conexao = conectar();
            PreparedStatement comando =
                    conexao.prepareStatement(sql)) {

            comando.setString(1, nomeLivro);

            int linhasExcluidas = comando.executeUpdate();

            return linhasExcluidas > 0;
        }
    }

    public static List<Historia> listarHistorias()
            throws SQLException {

        List<Historia> historias = new ArrayList<>();

        String sql = """
            SELECT nome, pista, livro, personagem, turma
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
                    resultado.getString("personagem"),
                    resultado.getString("turma")
                );

                historias.add(historia);
            }
        }

        return historias;
    }

    public static List<String[]> listarHistoriasParaExcluir()
        throws SQLException {

        List<String[]> historias = new ArrayList<>();

        String sql = """
            SELECT id, nome, livro, personagem, faixa_etaria
            FROM historias
            ORDER BY id
            """;

        try (Connection conexao = conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql);
            ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                String[] historia = {
                    resultado.getString("id"),
                    resultado.getString("nome"),
                    resultado.getString("livro"),
                    resultado.getString("personagem"),
                    resultado.getString("faixa_etaria")
                };

                historias.add(historia);
            }
        }

        return historias;
    }

    public static boolean excluirHistoriaPorId(int id)
        throws SQLException {

        String sql = "DELETE FROM historias WHERE id = ?";

        try (Connection conexao = conectar();
            PreparedStatement comando =
                    conexao.prepareStatement(sql)) {

            comando.setInt(1, id);

            int registrosExcluidos = comando.executeUpdate();

            return registrosExcluidos > 0;
        }
    }

    
    public static List<Historia> listarHistoriasPorFaixaEtaria(
            String faixaEtaria) throws SQLException {

        List<Historia> historias = new ArrayList<>();

        String sql = """
            SELECT nome, pista, livro, personagem, turma
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
                        resultado.getString("personagem"),
                        resultado.getString("turma")
                    ));
                }
            }
        }

        return historias;
    }

    public static void cadastrarLivrosIniciais() throws SQLException {

        String sql = """
            INSERT INTO historias
                (nome, pista, livro, personagem, faixa_etaria, turma)
            SELECT ?, ?, ?, ?, ?, ?
            WHERE NOT EXISTS (
                SELECT 1 FROM historias WHERE livro = ?
            )
            """;

        String[][] livros = {
            {
                "O que será que é?",
                "Sou um bichinho que encontra algo estranho e tenta descobrir o que é.",
                "Perigoso", "Toupeira", "2 anos", "2 anos"
            },
            {
                "Bolinhas coloridas",
                "Sou um livro com bolinhas coloridas. Você aperta uma bolinha e a brincadeira começa.",
                "Aperte Aqui", "Bolinhas coloridas", "2 anos", "2 anos"
            },
            {
                "A caixa de brincar",
                "Sou um coelhinho que usa a imaginação para transformar uma caixa em muitas coisas.",
                "Não é uma Caixa", "Coelho", "2 anos", "2 anos"
            },
            {
                "A lagarta comilona",
                "Sou pequenininha, adoro comer e depois me transformo em uma linda borboleta.",
                "Uma Lagarta Muito Comilona", "Lagarta", "2 anos", "2 anos"
            },
            {
                "O monstrinho das emoções",
                "Sou um monstrinho que precisa organizar suas emoções e descobrir o que está sentindo.",
                "O Monstro das Cores", "Monstro das Cores", "3 anos", "3 anos"
            },
            {
                "Caixas e carinho",
                "Sou um pai que adora caixas e usa a imaginação para demonstrar carinho pelo filho.",
                "O Homem que Amava Caixas", "Pai e filho", "3 anos", "3 anos"
            },
            {
                "O ratinho esperto",
                "Sou um ratinho esperto que encontra animais na floresta e inventa uma criatura chamada Grúfalo.",
                "O Grúfalo", "Ratinho", "3 anos", "3 anos"
            },
            {
                "O elefante colorido",
                "Sou um elefante muito especial, coberto de várias cores, como um arco-íris.",
                "Elmer", "Elmer", "3 anos", "3 anos"
            },
            {
                "Muitos resmungos",
                "Sou um menino que carrega muitos resmungos e precisa descobrir como seguir por um caminho diferente.",
                "Edmundo & o Resmungo", "Edmundo", "4 a 5 anos", "4 a 5 anos"
            },
            {
                "O menino da panela",
                "Sou um menino muito brincalhão, cheio de energia e conhecido por usar uma panela na cabeça.",
                "O Menino Maluquinho", "Menino Maluquinho", "4 a 5 anos", "4 a 5 anos"
            },
            {
                "Aprendendo a ver",
                "Sou um menino que começa a reconhecer as letras nas placas e nas coisas que vê pelas ruas.",
                "O Menino que Aprendeu a Ver", "Joãozinho", "4 a 5 anos", "4 a 5 anos"
            },
            {
                "Desafios matemáticos",
                "Um viajante resolve problemas aparentemente impossíveis usando raciocínio matemático e estratégias engenhosas. Qual é o livro?",
                "O Homem que Calculava", "Beremiz Samir", "6 a 8 anos", "6 a 8 anos"
            },
            {
                "Um diário da realidade",
                "Uma escritora registra seu cotidiano e revela as dificuldades da pobreza e as desigualdades sociais. Qual é o livro?",
                "Quarto de Despejo", "Carolina Maria de Jesus", "6 a 8 anos", "6 a 8 anos"
            },
            {
                "O detetive da dedução",
                "Um detetive analisa pistas, examina depoimentos e usa a lógica para desvendar mistérios. Qual é o livro?",
                "Poirot Investiga", "Hercule Poirot", "6 a 8 anos", "6 a 8 anos"
            }
        };

        try (Connection conexao = conectar();
            PreparedStatement comando = conexao.prepareStatement(sql)) {

            for (String[] livro : livros) {
                for (int i = 0; i < 6; i++) {
                    comando.setString(i + 1, livro[i]);
                }

                comando.setString(7, livro[2]);
                comando.executeUpdate();
            }
        }
    }


    
}
