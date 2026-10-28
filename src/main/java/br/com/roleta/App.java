package br.com.roleta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

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
        carregarHistorias();

        FXMLLoader carregador = new FXMLLoader(
            getClass().getResource("/br/com/roleta/Roleta.fxml")
        );

        javafx.scene.Parent raiz = carregador.load();

        System.out.println(getClass().getResource("/br/com/roleta/Roleta.fxml"));

        Label saudacao = new Label("✨ HORA DE BRINCAR COM AS HISTÓRIAS! ✨");
        saudacao.setStyle("-fx-font-size: 23px; -fx-font-weight: bold; -fx-text-fill: #24345C;");
        saudacao.setWrapText(true);
        saudacao.setAlignment(Pos.CENTER);

        Label subtitulo = new Label("Gire a roleta, leia a pista e descubra o conto!");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #52617A;");
        subtitulo.setWrapText(true);
        subtitulo.setAlignment(Pos.CENTER);

        Label rotuloRoleta = new Label("🎡 ROLETA MÁGICA");
        rotuloRoleta.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #24345C;");

        Circle aro = new Circle(145);
        aro.setFill(Color.web("#FFFFFF"));
        aro.setStroke(Color.web("#7C5CFC"));
        aro.setStrokeWidth(8);

        Circle interior = new Circle(126);
        interior.setFill(Color.web("#E9E5FF"));
        interior.setStroke(Color.web("#FFB84D"));
        interior.setStrokeWidth(5);

        Label textoCentro = new Label("✨\nGIRE!\n✨");
        textoCentro.setAlignment(Pos.CENTER);
        textoCentro.setStyle("-fx-font-size: 27px; -fx-font-weight: bold; -fx-text-fill: #5A42C7;");

        roleta = new StackPane(aro, interior, textoCentro);
        roleta.setMinSize(300, 300);
        roleta.setPrefSize(300, 300);
        roleta.setMaxSize(300, 300);

        botaoGirar = criarBotao("🎡  GIRAR A ROLETA", "#7357E8", "#FFFFFF", 235, 54);
        botaoGirar.setStyle(botaoGirar.getStyle() + "-fx-font-size: 18px;");

        timerLabel = new Label("⏰ 05:00");
        timerLabel.setAlignment(Pos.CENTER);
        timerLabel.setMinWidth(180);
        timerLabel.setPadding(new Insets(12, 20, 12, 20));
        timerLabel.setStyle(
            "-fx-background-color: #FFF1C9; -fx-background-radius: 18;"
            + "-fx-text-fill: #7A4C00; -fx-font-size: 21px; -fx-font-weight: bold;"
            + "-fx-border-color: #FFC857; -fx-border-width: 2; -fx-border-radius: 18;"
        );

        Label tituloItem = new Label("🎁  O QUE SAIU?");
        tituloItem.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        nomeHistoria = new Label("Sua surpresa aparece aqui!");
        nomeHistoria.setWrapText(true);
        nomeHistoria.setAlignment(Pos.CENTER);
        nomeHistoria.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        VBox caixaItem = new VBox(12, tituloItem, nomeHistoria);
        caixaItem.setAlignment(Pos.CENTER);
        caixaItem.setPadding(new Insets(18));
        caixaItem.setMinHeight(125);
        caixaItem.setStyle("-fx-background-color: #FF8A65; -fx-background-radius: 24;");

        Label tituloPista = new Label("🔎  SUA PISTA");
        tituloPista.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #315C47;");

        textoPista = new Label("Leia a pista e tente adivinhar a história.");
        textoPista.setWrapText(true);
        textoPista.setAlignment(Pos.CENTER);
        textoPista.setStyle("-fx-font-size: 17px; -fx-text-fill: #263B35;");

        VBox caixaPista = new VBox(10, tituloPista, textoPista);
        caixaPista.setAlignment(Pos.CENTER);
        caixaPista.setPadding(new Insets(18));
        caixaPista.setMinHeight(135);
        caixaPista.setStyle(
            "-fx-background-color: #DDF7E8; -fx-background-radius: 24;"
            + "-fx-border-color: #8ED9AE; -fx-border-width: 2; -fx-border-radius: 24;"
        );

        Label tituloDescricao = new Label("📚  MISSÃO DO DETETIVE");
        tituloDescricao.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #34517B;");

        descricaoLivro = new Label("Pense bem: qual história combina com essa pista?");
        descricaoLivro.setWrapText(true);
        descricaoLivro.setAlignment(Pos.CENTER);
        descricaoLivro.setStyle("-fx-font-size: 15px; -fx-text-fill: #34445F;");

        VBox caixaDescricao = new VBox(8, tituloDescricao, descricaoLivro);
        caixaDescricao.setAlignment(Pos.CENTER);
        caixaDescricao.setPadding(new Insets(16));
        caixaDescricao.setMinHeight(100);
        caixaDescricao.setStyle(
            "-fx-background-color: #DDEBFF; -fx-background-radius: 24;"
            + "-fx-border-color: #A8C9FF; -fx-border-width: 2; -fx-border-radius: 24;"
        );

        Label tituloResposta = new Label("🏆  RESPOSTA");
        tituloResposta.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #795015;");

        resposta = new Label("O livro e o personagem vão aparecer aqui!");
        resposta.setWrapText(true);
        resposta.setAlignment(Pos.CENTER);
        resposta.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #795015;");

        VBox caixaResposta = new VBox(8, tituloResposta, resposta);
        caixaResposta.setAlignment(Pos.CENTER);
        caixaResposta.setPadding(new Insets(16));
        caixaResposta.setMinHeight(110);
        caixaResposta.setStyle(
            "-fx-background-color: #FFF0C2; -fx-background-radius: 24;"
            + "-fx-border-color: #FFD166; -fx-border-width: 2; -fx-border-radius: 24;"
        );

        botaoRevelar = criarBotao("💡  REVELAR RESPOSTA", "#2BAE83", "#FFFFFF", 220, 46);
        botaoProxima = criarBotao("➡  PRÓXIMA RODADA", "#FFB84D", "#553600", 220, 46);
        botaoRevelar.setDisable(true);
        botaoProxima.setDisable(true);

        HBox linhaBotoes = new HBox(12, botaoRevelar, botaoProxima);
        linhaBotoes.setAlignment(Pos.CENTER);

        status = new Label("Vamos lá! Clique em Girar para começar.");
        status.setWrapText(true);
        status.setAlignment(Pos.CENTER);
        status.setStyle("-fx-font-size: 13px; -fx-text-fill: #52617A;");

        VBox ladoEsquerdo = new VBox(20, rotuloRoleta, roleta, botaoGirar, timerLabel);
        ladoEsquerdo.setAlignment(Pos.CENTER);
        ladoEsquerdo.setPadding(new Insets(20));
        ladoEsquerdo.setMinWidth(340);
        ladoEsquerdo.setStyle("-fx-background-color: #F1EEFF; -fx-background-radius: 28;");

        VBox ladoDireito = new VBox(14, caixaItem, caixaPista, caixaDescricao, caixaResposta, linhaBotoes, status);
        ladoDireito.setAlignment(Pos.CENTER);
        ladoDireito.setPadding(new Insets(8));
        ladoDireito.setMinWidth(390);
        ladoDireito.setPrefWidth(430);
        VBox.setVgrow(caixaPista, Priority.ALWAYS);

        HBox conteudo = new HBox(20, ladoEsquerdo, ladoDireito);
        conteudo.setAlignment(Pos.CENTER);
        conteudo.setPadding(new Insets(20));

        VBox layout = new VBox(8, saudacao, subtitulo, conteudo);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #FFF9F0, #F0FAFF);");

        relogio = new Timeline(new KeyFrame(Duration.seconds(1), evento -> {
            if (segundosRestantes > 0) {
                segundosRestantes--;
                atualizarTimer();
            }
            if (segundosRestantes <= 0) {
                relogio.stop();
                status.setText("⏳ O tempo acabou! Está na hora de revelar a resposta.");
                botaoRevelar.setDisable(false);
            }
        }));
        relogio.setCycleCount(Timeline.INDEFINITE);

        animacaoRoleta = new RotateTransition(Duration.seconds(1.25), roleta);
        animacaoRoleta.setByAngle(720);
        animacaoRoleta.setOnFinished(evento -> {
            relogio.playFromStart();
            botaoRevelar.setDisable(false);
        });

        botaoGirar.setOnAction(evento -> sortear());
        botaoRevelar.setOnAction(evento -> revelar());
        botaoProxima.setOnAction(evento -> sortear());

        Scene cena = new Scene(raiz, 1000, 900);
        palco.setTitle("Roleta de Histórias - Aventura de Adivinhação");
        palco.setScene(cena);
        palco.setMinWidth(950);
        palco.setMinHeight(800);
        palco.show();
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

    private void carregarHistorias() {
        // Cadastre novas pistas manualmente copiando o formato de uma linha abaixo.
        historias.add(new Historia("Capa Vermelha",
            "Quem usava uma capa vermelha ao visitar a avó e encontrou um lobo falante?",
            "Chapeuzinho Vermelho", "Chapeuzinho Vermelho"));
        historias.add(new Historia("Pão e migalhas",
            "Quais irmãos tentaram marcar o caminho com migalhas e se perderam na floresta?",
            "João e Maria", "João e Maria"));
        historias.add(new Historia("Maçã envenenada",
            "Quem mordeu uma maçã envenenada e caiu em um sono profundo?",
            "Branca de Neve e os Sete Anões", "Branca de Neve"));
        historias.add(new Historia("Feijões mágicos",
            "Quem subiu por um pé de feijão gigante e chegou à casa de um gigante?",
            "João e o Pé de Feijão", "João"));
        historias.add(new Historia("Tijolos e palha",
            "Quem construiu casas de materiais diferentes para se proteger do Lobo Mau?",
            "Os Três Porquinhos", "Os Três Porquinhos"));
        historias.add(new Historia("Coelho Branco",
            "Quem seguiu um Coelho Branco e entrou em um mundo cheio de coisas malucas?",
            "Alice no País das Maravilhas", "Alice"));
        historias.add(new Historia("Uma rosa",
            "Qual personagem cuidava de uma rosa especial em seu pequeno planeta?",
            "O Pequeno Príncipe", "O Pequeno Príncipe"));
        historias.add(new Historia("Chave antiga",
            "Quem encontrou uma chave que ajudou a abrir um jardim secreto?",
            "O Jardim Secreto", "Mary Lennox"));
        historias.add(new Historia("Árvore generosa",
            "Qual história mostra uma árvore que oferece partes de si para ajudar um menino?",
            "A Árvore Generosa", "O Menino"));
        historias.add(new Historia("Ciclo da vida",
            "Qual jovem leão aprende sobre o ciclo da vida e se torna rei?",
            "O Rei Leão", "Simba"));
        disponiveis.addAll(historias);
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
}
