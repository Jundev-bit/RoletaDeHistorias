package br.com.roleta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private final List<Historia> historias = new ArrayList<>();
    private final List<Historia> disponiveis = new ArrayList<>();
    private final Random random = new Random();

    private Historia historiaAtual;
    private int segundosRestantes = 5 * 60;

    private Label nomeHistoria;
    private Label textoPista;
    private Label descricaoLivro;
    private Label resposta;
    private Label timerLabel;
    private Label status;
    private Button botaoGirar;
    private Button botaoRevelar;
    private Button botaoProxima;
    private Timeline relogio;
    private RotateTransition animacaoRoleta;
    private StackPane roleta;

    
    @Override
    public void start(Stage palco) throws Exception {
        BancoDeDados.inicializar();
        BancoDeDados.cadastrarLivrosIniciais();
        carregarHistorias();

        abrirTelaTurmas(palco);
    }

    private void abrirRoleta(Stage palco, String faixaEtaria) {
        try {
            FXMLLoader carregador = new FXMLLoader(
                getClass().getResource("/br/com/roleta/Roleta.fxml")
            );

            javafx.scene.Parent raiz = carregador.load();

            RoletaController controlador = carregador.getController();
            controlador.selecionarFaixaEtaria(faixaEtaria);

            Scene cena = new Scene(raiz, 1000, 900);

            palco.setTitle(
                "Roleta de Histórias - " + faixaEtaria
            );
            palco.setScene(cena);
            palco.show();

        } catch (Exception erro) {
            erro.printStackTrace();
        }
    }


    private Button criarBotao(String texto, String fundo, String corTexto, double largura, double altura) {
        Button botao = new Button(texto);
        botao.setMinSize(largura, altura);
        botao.setPrefSize(largura, altura);
        botao.setStyle(
            "-fx-background-color: " + fundo + "; -fx-text-fill: " + corTexto + ";"
            + "-fx-background-radius: 18; -fx-border-radius: 18;"
            + "-fx-font-size: 15px; -fx-font-weight: bold;"
            + "-fx-padding: 10 16 10 16;"
        );
        return botao;
    }

    public void carregarHistorias() {
        try {
            historias.clear();
            disponiveis.clear();

            historias.addAll(BancoDeDados.listarHistorias());
            disponiveis.addAll(historias);

            System.out.println(
                "Histórias carregadas do banco: " + historias.size()
            );

        } catch (java.sql.SQLException erro) {
            erro.printStackTrace();
            throw new RuntimeException(
                "Não foi possível carregar as histórias do banco de dados.",
                erro
            );
        }
    }

    private void sortear() {
        if (disponiveis.isEmpty()) {
            relogio.stop();
            nomeHistoria.setText("Todas as pistas acabaram!");
            textoPista.setText("Você completou todas as rodadas.");
            descricaoLivro.setText("Feche e abra o programa para começar uma nova sessão.");
            resposta.setText("Parabéns, detetive das histórias! 🎉");
            timerLabel.setText("⏰ FIM");
            status.setText("Nenhuma pista foi repetida nesta sessão.");
            botaoGirar.setDisable(true);
            botaoRevelar.setDisable(true);
            botaoProxima.setDisable(true);
            return;
        }

        relogio.stop();
        botaoGirar.setDisable(true);
        botaoRevelar.setDisable(true);
        botaoProxima.setDisable(true);

        historiaAtual = disponiveis.remove(random.nextInt(disponiveis.size()));
        nomeHistoria.setText(historiaAtual.getNome());
        textoPista.setText(historiaAtual.getPista());
        descricaoLivro.setText("Pense bem: qual história combina com essa pista?");
        resposta.setText("O livro e o personagem vão aparecer aqui!");
        status.setText("🕵️ Leia a pista e tente adivinhar!");
        segundosRestantes = 5 * 60;
        atualizarTimer();

        animacaoRoleta.stop();
        roleta.setRotate(0);
        animacaoRoleta.playFromStart();
    }

    private void revelar() {
        if (historiaAtual == null) {
            status.setText("Clique em Girar para sortear uma pista primeiro.");
            return;
        }
        relogio.stop();
        resposta.setText("Livro: " + historiaAtual.getLivro()
            + "\nPersonagem: " + historiaAtual.getPersonagem());
        descricaoLivro.setText("Resposta revelada! Muito bem por tentar! ⭐");
        status.setText("Rodada concluída! Pronto para a próxima aventura?");
        botaoGirar.setDisable(true);
        botaoRevelar.setDisable(true);
        botaoProxima.setDisable(false);
    }

    private void atualizarTimer() {
        int minutos = segundosRestantes / 60;
        int segundos = segundosRestantes % 60;
        timerLabel.setText(String.format("⏰ %02d:%02d", minutos, segundos));
    }

    @Override
    public void stop() {
        if (relogio != null) relogio.stop();
        if (animacaoRoleta != null) animacaoRoleta.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void abrirTelaTurmas(Stage palco) {
        Label titulo = new Label("📚 ROLETA DE HISTÓRIAS");
        titulo.setStyle(
            "-fx-font-size: 28px;"
            + "-fx-font-weight: bold;"
            + "-fx-text-fill: #24345C;"
        );
        titulo.setWrapText(true);
        titulo.setAlignment(Pos.CENTER);

        Label subtitulo = new Label(
            "Escolha a faixa etária para começar a aventura!"
        );
        subtitulo.setStyle(
            "-fx-font-size: 17px;"
            + "-fx-text-fill: #52617A;"
        );
        subtitulo.setWrapText(true);
        subtitulo.setAlignment(Pos.CENTER);

        Button turmaCreche = criarBotao("👶  CRECHE", "#7357E8", "#FFFFFF", 300, 70);

        Button turmaOficina = criarBotao("🎨  OFICINA DO SABER", "#FFB84D", "#553600", 300, 70);

        Button turmaRever = criarBotao("🔎  REVER", "#FF8A65", "#FFFFFF", 300, 70);

        Button btnAdicionarHistoria = criarBotao(
            "➕  ADICIONAR HISTÓRIA",
            "#16A34A", "#FFFFFF", 300, 60
        );

        Label mensagem = new Label(
            "Selecione uma turma para continuar."
        );
        mensagem.setWrapText(true);
        mensagem.setAlignment(Pos.CENTER);
        mensagem.setStyle(
            "-fx-font-size: 14px;"
            + "-fx-text-fill: #52617A;"
        );

        
        turmaCreche.setOnAction(e -> selecionarTurma("CRECHE", palco));
        turmaOficina.setOnAction(e -> selecionarTurma("OFICINA DO SABER", palco));
        turmaRever.setOnAction(e -> selecionarTurma("REVER", palco));

        btnAdicionarHistoria.setOnAction(
            e -> abrirFormularioHistoria(palco)
        );

        Button btnGerenciarHistorias = criarBotao(
            "🗑 GERENCIAR HISTÓRIAS",
            "#DC2626", "#FFFFFF", 300, 60
        );

        btnGerenciarHistorias.setOnAction(
            e -> abrirTelaExcluirHistorias(palco)
        );


        VBox layout = new VBox(
            22,
            titulo,
            subtitulo,
            turmaCreche,
            turmaOficina,
            turmaRever,
            btnAdicionarHistoria,
            btnGerenciarHistorias,
            mensagem
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(35));
        layout.setStyle(
            "-fx-background-color: linear-gradient("
            + "to bottom right, #FFF9F0, #F0FAFF);"
        );

        Scene cena = new Scene(layout, 650, 700);

        palco.setTitle("Roleta de Histórias - Seleção de Turma");
        palco.setScene(cena);
        palco.setMinWidth(600);
        palco.setMinHeight(650);
        palco.show();
    }

    
    private void selecionarTurma(String faixaEtaria, Stage palco) {
        try {
            disponiveis.clear();

            for (Historia historia : historias) {
                if (historia.getTurma().equalsIgnoreCase(faixaEtaria)) {
                    disponiveis.add(historia);
                }
            }

            if (disponiveis.isEmpty()) {
                Label aviso = new Label(
                    "Não existem livros cadastrados para esta faixa etária."
                );
                aviso.setWrapText(true);

                Button voltar = criarBotao(
                    "VOLTAR ÀS TURMAS",
                    "#7357E8", "#FFFFFF", 220, 50
                );

                voltar.setOnAction(e -> abrirTelaTurmas(palco));

                VBox layout = new VBox(20, aviso, voltar);
                layout.setAlignment(Pos.CENTER);
                layout.setPadding(new Insets(30));

                palco.setScene(new Scene(layout, 500, 300));
                return;
            }

            abrirRoleta(palco, faixaEtaria);

        } catch (Exception erro) {
            erro.printStackTrace();
        }
    }
    
    private void abrirFormularioHistoria(Stage palco) {
        Stage janela = new Stage();

        janela.initOwner(palco);
        janela.initModality(javafx.stage.Modality.WINDOW_MODAL);
        janela.setTitle("Adicionar História");

        Label titulo = new Label("📚 Cadastrar Nova História");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        javafx.scene.control.TextField campoNome =
                new javafx.scene.control.TextField();
        campoNome.setPromptText("Nome da história ou objeto");

        javafx.scene.control.TextArea campoPista =
                new javafx.scene.control.TextArea();
        campoPista.setPromptText("Pista para a criança");
        campoPista.setPrefRowCount(3);
        campoPista.setWrapText(true);

        javafx.scene.control.TextField campoLivro =
                new javafx.scene.control.TextField();
        campoLivro.setPromptText("Título do livro");

        javafx.scene.control.TextField campoPersonagem =
                new javafx.scene.control.TextField();
        campoPersonagem.setPromptText("Personagem principal");

        
        javafx.scene.control.ComboBox<String> campoFaixa =
                new javafx.scene.control.ComboBox<>();

        campoFaixa.getItems().addAll(
                "2 e 3 anos",
                "4 a 5 anos",
                "6 a 8 anos"
        );

        campoFaixa.setValue("2 e 3 anos");
        campoFaixa.setMaxWidth(Double.MAX_VALUE);

        javafx.scene.control.ComboBox<String> campoTurma =
                new javafx.scene.control.ComboBox<>();

        campoTurma.getItems().addAll(
                "CRECHE",
                "OFICINA DO SABER",
                "REVER"
        );

        campoTurma.setValue("CRECHE");
        campoTurma.setMaxWidth(Double.MAX_VALUE);

        campoFaixa.setOnAction(e -> {
            String faixa = campoFaixa.getValue();

            if ("2 e 3 anos".equals(faixa)) {
                campoTurma.setValue("CRECHE");
            } else if ("4 a 5 anos".equals(faixa)) {
                campoTurma.setValue("OFICINA DO SABER");
            } else if ("6 a 8 anos".equals(faixa)) {
                campoTurma.setValue("REVER");
            }
        });


        Button btnSalvar = new Button("SALVAR HISTÓRIA");
        btnSalvar.setMaxWidth(Double.MAX_VALUE);
        btnSalvar.setStyle(
                "-fx-background-color: #16A34A;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 12;"
        );

        Label mensagem = new Label();
        mensagem.setWrapText(true);

        btnSalvar.setOnAction(e -> {
            String nome = campoNome.getText().trim();
            String pista = campoPista.getText().trim();
            String livro = campoLivro.getText().trim();
            String personagem = campoPersonagem.getText().trim();
            String faixa = campoFaixa.getValue();
            String turma = campoTurma.getValue();

            if (nome.isEmpty() || pista.isEmpty()
                    || livro.isEmpty() || personagem.isEmpty()
                    || faixa == null || turma == null) {
                mensagem.setText("Preencha todos os campos.");
                return;
            }

            try {
                Historia historia = new Historia(
                        nome, pista, livro, personagem, turma
                );

                BancoDeDados.salvarHistoria(historia, faixa);

                mensagem.setText("História salva com sucesso!");
                btnSalvar.setDisable(true);

                carregarHistorias();

            } catch (java.sql.SQLException erro) {
                erro.printStackTrace();
                mensagem.setText("Erro ao salvar a história no banco.");
            }
        });

        VBox formulario = new VBox(
                12,
                titulo,
                new Label("Nome:"), campoNome,
                new Label("Pista:"), campoPista,
                new Label("Livro:"), campoLivro,
                new Label("Personagem:"), campoPersonagem,
                new Label("Faixa etária:"), campoFaixa,
                new Label("Turma:"), campoTurma,
                btnSalvar, mensagem
        );

        formulario.setPadding(new Insets(20));
        formulario.setStyle("-fx-background-color: #FFF9F0;");

        javafx.scene.control.ScrollPane rolagem =
                new javafx.scene.control.ScrollPane(formulario);
        rolagem.setFitToWidth(true);

        janela.setScene(new Scene(rolagem, 450, 700));
        janela.show();
    }


    private void abrirTelaExcluirHistorias(Stage palco) {
        Stage janela = new Stage();

        janela.initOwner(palco);
        janela.initModality(javafx.stage.Modality.WINDOW_MODAL);
        janela.setTitle("Gerenciar Histórias");

        Label titulo = new Label("📚 HISTÓRIAS CADASTRADAS");
        titulo.setStyle(
            "-fx-font-size: 22px;"
            + "-fx-font-weight: bold;"
            + "-fx-text-fill: #24345C;"
        );

        javafx.scene.control.ListView<String> lista =
            new javafx.scene.control.ListView<>();

        java.util.List<String[]> historias;

        try {
            historias = BancoDeDados.listarHistoriasParaExcluir();

            for (String[] historia : historias) {
                lista.getItems().add(
                    historia[1]
                    + " | Livro: " + historia[2]
                    + " | Personagem: " + historia[3]
                    + " | Faixa: " + historia[4]
                );
            }

        } catch (java.sql.SQLException erro) {
            erro.printStackTrace();
            lista.getItems().add("Erro ao carregar histórias.");
            historias = new java.util.ArrayList<>();
        }

        Label mensagem = new Label("Selecione uma história para excluir.");
        mensagem.setWrapText(true);

        Button btnExcluir = criarBotao(
            "🗑 EXCLUIR HISTÓRIA",
            "#DC2626", "#FFFFFF", 250, 50
        );

        Button btnVoltar = criarBotao(
            "VOLTAR",
            "#839CB5", "#FFFFFF", 250, 50
        );

        final java.util.List<String[]> listaHistorias = historias;

        btnExcluir.setOnAction(e -> {
            int indice = lista.getSelectionModel().getSelectedIndex();

            if (indice < 0) {
                mensagem.setText("Selecione uma história primeiro.");
                return;
            }

            String[] selecionada = listaHistorias.get(indice);

            javafx.scene.control.Alert confirmacao =
                new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION
                );

            confirmacao.setTitle("Confirmar exclusão");
            confirmacao.setHeaderText("Excluir esta história?");
            confirmacao.setContentText(
                selecionada[1] + " - " + selecionada[2]
            );

            java.util.Optional<javafx.scene.control.ButtonType> resposta =
                confirmacao.showAndWait();

            if (resposta.isPresent()
                    && resposta.get() == javafx.scene.control.ButtonType.OK) {

                try {
                    int id = Integer.parseInt(selecionada[0]);

                    boolean excluida =
                        BancoDeDados.excluirHistoriaPorId(id);

                    if (excluida) {
                        listaHistorias.remove(indice);
                        lista.getItems().remove(indice);

                        mensagem.setText("História excluída com sucesso!");
                    } else {
                        mensagem.setText("História não encontrada no banco.");
                    }

                } catch (java.sql.SQLException erro) {
                    erro.printStackTrace();
                    mensagem.setText("Erro ao excluir a história.");
                }
            }
        });

        btnVoltar.setOnAction(e -> janela.close());

        

        VBox layout = new VBox(
            15,
            titulo,
            lista,
            mensagem,
            btnExcluir,
            btnVoltar
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle(
            "-fx-background-color: linear-gradient("
            + "to bottom right, #FFF9F0, #F0FAFF);"
        );

        Scene cena = new Scene(layout, 750, 600);

        janela.setScene(cena);
        janela.show();
    }
}
