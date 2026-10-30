
package br.com.roleta;

import java.sql.SQLException;
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
import javafx.stage.Stage;
import javafx.util.Duration;

public class RoletaController {

    private final Random random = new Random();

    private final List<Historia> historias = new ArrayList<>();
    private final List<Historia> disponiveis = new ArrayList<>();

    private Historia historiaAtual;

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
    private Button btnNovaRodada;

    @FXML
    private void initialize() {
        
    try {
        BancoDeDados.inicializar();
        BancoDeDados.cadastrarLivrosIniciais();

        historias.clear();
        historias.addAll(BancoDeDados.listarHistorias());

        disponiveis.clear();
        disponiveis.addAll(historias);

        System.out.println(
            "Livros carregados do banco: " + historias.size()
        );

    } catch (SQLException erro) {
        erro.printStackTrace();
        lblMensagem.setText(
            "Erro ao carregar os livros do banco de dados."
        );
        btnGirar.setDisable(true);
        return;
    }
        
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
                animacaoRoleta.getStatus() == Animation.Status.RUNNING) {
            return;
        }

        // Não repete livros que já foram sorteados nesta rodada.
        if (disponiveis.isEmpty()) {
            if (relogio != null) {
                relogio.stop();
            }

            historiaAtual = null;
            lblObjeto.setText("Todas as histórias foram sorteadas!");
            lblPista.setText("");
            lblResposta.setText("");
            lblCronometro.setText("00:00");
            lblMensagem.setText(
                "Parabéns! Você completou a rodada."
            );

            btnGirar.setDisable(true);
            btnRevelar.setDisable(true);
            btnProxima.setDisable(true);
            return;
        }

        // Escolhe um livro diretamente da lista carregada do banco.
        int posicao = random.nextInt(disponiveis.size());
        historiaAtual = disponiveis.remove(posicao);

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

        animacaoRoleta.setByAngle(
            1440 + random.nextDouble() * 360
        );

        animacaoRoleta.setOnFinished(event -> {
            lblObjeto.setText(historiaAtual.getNome());

            lblPista.setText(
                "Pista: " + historiaAtual.getPista()
            );

            lblResposta.setText("Resposta oculta");

            lblMensagem.setText(
                "Interprete a história usando gestos, sem falar!"
            );

            btnGirar.setDisable(disponiveis.isEmpty());
            btnRevelar.setDisable(false);
            btnProxima.setDisable(false);

            if (disponiveis.isEmpty()) {
                lblMensagem.setText(
                    "Ultima historia da rodada! Revele a resposta e conclua a atividade."
                );
            }

            iniciarCronometro();
        });

        animacaoRoleta.playFromStart();
    }

    public void selecionarFaixaEtaria(String faixaEtaria) {
        try {
            List<Historia> filtradas =
                    BancoDeDados.listarHistoriasPorFaixaEtaria(faixaEtaria);

            historias.clear();
            historias.addAll(filtradas);

            disponiveis.clear();
            disponiveis.addAll(filtradas);

            if (filtradas.isEmpty()) {
                lblMensagem.setText(
                        "Não há livros cadastrados para " + faixaEtaria
                );
                btnGirar.setDisable(true);
            } else {
                lblMensagem.setText(
                        "Turma selecionada: " + faixaEtaria
                );
                btnGirar.setDisable(false);
            }

        } catch (SQLException erro) {
            erro.printStackTrace();
            lblMensagem.setText("Erro ao carregar livros da turma.");
            btnGirar.setDisable(true);
        }
    }

    @FXML
    private void revelarResposta() {
        if (historiaAtual == null) {
            return;
        }

        lblResposta.setText(
            "" + historiaAtual.getLivro()
            + "\nPersonagem: " + historiaAtual.getPersonagem()
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

    @FXML
    private Button btnVoltarTurmas;

    @FXML
    private void voltarTurmas() {
        Stage palco = (Stage) btnVoltarTurmas
                .getScene()
                .getWindow();

        App app = new App();
        app.carregarHistorias();
        app.abrirTelaTurmas(palco);
    }
    
    @FXML
    private void novaRodada() {
        if (historias.isEmpty()) {
            lblMensagem.setText("Não há histórias para iniciar uma nova rodada.");
            return;
        }

        disponiveis.clear();
        disponiveis.addAll(historias);

        historiaAtual = null;
        segundosRestantes = 5 * 60;

        if (relogio != null) {
            relogio.stop();
        }

        lblCronometro.setText("05:00");
        lblObjeto.setText("🎁 OBJETO SORTEADO");
        lblPista.setText("A PISTA APARECERÁ AQUI");
        lblResposta.setText("🏆 RESPOSTA:");
        lblMensagem.setText(
                "Nova rodada iniciada! Clique em GIRAR para sortear."
        );

        btnGirar.setDisable(false);
        btnRevelar.setDisable(true);
        btnProxima.setDisable(true);
    }

}
