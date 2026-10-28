
package br.com.roleta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class RoletaController {

    private final Random random = new Random();

    private final String[] nomesHistorias = {
        "Chapeuzinho Vermelho",
        "Os Três Porquinhos",
        "O Patinho Feio",
        "João e Maria",
        "A Cigarra e a Formiga",
        "O Leão e o Ratinho",
        "Pinóquio",
        "A Lebre e a Tartaruga"
    };

    private final String[] pistasHistorias = {
        "Uma menina leva uma cesta para visitar a avó.",
        "Três irmãos constroem casas diferentes.",
        "Uma ave se sente diferente das outras.",
        "Dois irmãos encontram uma casa feita de doces.",
        "Um inseto canta enquanto outro trabalha.",
        "Um pequeno animal ajuda um grande rei.",
        "Um boneco de madeira sonha em ser menino.",
        "Uma corrida coloca um animal rápido contra um lento."
    };

    private final String[] respostasHistorias = {
        "Chapeuzinho Vermelho",
        "Os Três Porquinhos",
        "O Patinho Feio",
        "João e Maria",
        "A Cigarra e a Formiga",
        "O Leão e o Ratinho",
        "Pinóquio",
        "A Lebre e a Tartaruga"
    };

    private final List<Integer> indicesDisponiveis =
        new ArrayList<>();

    private int indiceHistoriaAtual = -1;
    private int segundosRestantes = 5 * 60;

    private Timeline relogio;
    private RotateTransition animacaoRoleta;

    @FXML
    private Label lblObjeto;

    @FXML
    private Label lblPista;

    @FXML
    private Label lblResposta;

    @FXML
    private Label lblCronometro;

    @FXML
    private Label lblMensagem;

    @FXML
    private Button btnGirar;

    @FXML
    private Button btnRevelar;

    @FXML
    private Button btnProxima;

    @FXML
    private StackPane roleta;

    @FXML
    private void initialize() {
        Group desenhoRoleta = criarRoleta();

        roleta.getChildren().clear();
        roleta.getChildren().add(desenhoRoleta);

        animacaoRoleta = new RotateTransition(
            Duration.seconds(2),
            desenhoRoleta
        );

        animacaoRoleta.setByAngle(1440);

        // Estado inicial da interface.
        lblObjeto.setText("Clique em GIRAR!");
        lblPista.setText("");
        lblResposta.setText("");
        lblCronometro.setText("05:00");
        lblMensagem.setText(
            "Gire a roleta para sortear uma história."
        );

        btnRevelar.setDisable(true);
        btnProxima.setDisable(true);

        Platform.runLater(() -> {
            FundoAnimado.aplicar(roleta.getScene());
        });
    }

    @FXML
    private void sortear() {
        if (animacaoRoleta == null ||
                animacaoRoleta.getStatus()
                    == Animation.Status.RUNNING) {
            return;
        }

        // Prepara uma nova rodada quando todas as histórias
        // já tiverem sido sorteadas.
        if (indicesDisponiveis.isEmpty()) {
            for (int i = 0; i < nomesHistorias.length; i++) {
                indicesDisponiveis.add(i);
            }
        }

        // Escolhe uma história sem repetir na rodada.
        int posicao = random.nextInt(indicesDisponiveis.size());

        indiceHistoriaAtual =
            indicesDisponiveis.remove(posicao);

        lblObjeto.setText("Girando...");
        lblPista.setText("");
        lblResposta.setText("");
        lblCronometro.setText("05:00");
        lblMensagem.setText(
            "Vamos descobrir a próxima história!"
        );

        btnGirar.setDisable(true);
        btnRevelar.setDisable(true);
        btnProxima.setDisable(true);

        // Gira várias voltas e acrescenta um ângulo aleatório.
        animacaoRoleta.setByAngle(
            1440 + random.nextDouble() * 360
        );

        animacaoRoleta.setOnFinished(event -> {
            lblObjeto.setText(
                nomesHistorias[indiceHistoriaAtual]
            );

            lblPista.setText(
                "Pista: "
                + pistasHistorias[indiceHistoriaAtual]
            );

            lblResposta.setText("Resposta oculta");

            lblMensagem.setText(
                "Interprete a história usando gestos, sem falar!"
            );

            btnGirar.setDisable(false);
            btnRevelar.setDisable(false);
            btnProxima.setDisable(false);

            // Começa a contagem somente quando a roleta parar.
            iniciarCronometro();
        });

        animacaoRoleta.playFromStart();
    }

    @FXML
    private void revelarResposta() {
        if (indiceHistoriaAtual < 0) {
            return;
        }

        lblResposta.setText(
            ""
            + respostasHistorias[indiceHistoriaAtual]
        );
    }

    @FXML
    private void proximaHistoria() {
        sortear();
    }

    private void iniciarCronometro() {
        if (relogio != null) {
            relogio.stop();
        }

        segundosRestantes = 5 * 60;
        atualizarCronometro();

        relogio = new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                evento -> {
                    if (segundosRestantes > 0) {
                        segundosRestantes--;
                        atualizarCronometro();
                    }

                    if (segundosRestantes <= 0) {
                        relogio.stop();

                        lblMensagem.setText(
                            "Tempo encerrado! Muito bem!"
                        );

                        btnGirar.setDisable(false);
                        btnProxima.setDisable(false);
                    }
                }
            )
        );

        relogio.setCycleCount(Animation.INDEFINITE);
        relogio.play();
    }

    private void atualizarCronometro() {
        int minutos = segundosRestantes / 60;
        int segundos = segundosRestantes % 60;

        lblCronometro.setText(
            String.format("%02d:%02d", minutos, segundos)
        );
    }

    private Group criarRoleta() {
        Group grupo = new Group();

        Color[] cores = {
            Color.web("#96D7EB"),
            Color.web("#F2E4F9"),
            Color.web("#B1E1E9"),
            Color.web("#839CB5"),
            Color.web("#96D7EB"),
            Color.web("#F2E4F9"),
            Color.web("#B1E1E9"),
            Color.web("#839CB5")
        };

        for (int i = 0; i < 8; i++) {
            Arc setor = new Arc(
                0, 0,
                120, 120,
                i * 45,
                45
            );

            setor.setType(ArcType.ROUND);
            setor.setFill(cores[i]);
            setor.setStroke(Color.WHITE);
            setor.setStrokeWidth(2);

            grupo.getChildren().add(setor);
        }

        Circle centro = new Circle(18, Color.WHITE);
        centro.setStroke(Color.web("#839CB5"));

        grupo.getChildren().add(centro);

        return grupo;
    }
}
