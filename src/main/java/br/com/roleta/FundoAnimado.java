package br.com.roleta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FundoAnimado {

    private static final Random random = new Random();

    private static class Particula {
        private final javafx.scene.Node elemento;
        private double x;
        private double y;
        private double velocidade;
        private double movimento;
        private double angulo;
        private double rotacao;

        Particula(
                javafx.scene.Node elemento,
                double x,
                double y,
                double velocidade,
                double movimento,
                double rotacao) {

            this.elemento = elemento;
            this.x = x;
            this.y = y;
            this.velocidade = velocidade;
            this.movimento = movimento;
            this.rotacao = rotacao;
        }
    }

    public static void aplicar(Scene cena) {

        Parent interfaceAtual = cena.getRoot();

        Pane fundo = new Pane();
        fundo.setMouseTransparent(true);
        fundo.setPickOnBounds(false);

        StackPane camada = new StackPane();

        // Deixa o fundo ocupar a janela inteira.
        fundo.prefWidthProperty().bind(camada.widthProperty());
        fundo.prefHeightProperty().bind(camada.heightProperty());

        // Cor de fundo suave.
        fundo.setStyle("-fx-background-color: #FFF7FC;");

        List<Particula> particulas = new ArrayList<>();

        Color[] coresConfete = {
            Color.web("#FF8FAB"),
            Color.web("#FFD166"),
            Color.web("#80ed99"),
            Color.web("#72DDF7"),
            Color.web("#CDB4DB"),
            Color.web("#FFAD69")
        };

        Color[] coresBalao = {
            Color.web( "#04f53c"),
            Color.web( "#1c04f5"),
            Color.web( "#d1f504")
        };

        // Cria estrelas, balões e confetes.
        for (int i = 0; i < 42; i++) {

            double x = random.nextDouble() * 1200;
            double y = random.nextDouble() * 900;

            double velocidade = 20 + random.nextDouble() * 45;
            double movimento = -12 + random.nextDouble() * 24;

            javafx.scene.Node elemento;

            int tipo = random.nextInt(3);

            if (tipo == 0) {

                // Estrelinha.
                Text estrela = new Text("★");
                estrela.setFont(Font.font(18 + random.nextInt(13)));
                estrela.setFill(coresConfete[
                    random.nextInt(coresConfete.length)
                ]);

                elemento = estrela;

            } else if (tipo == 1) {

                // Balão colorido.
                Text balao = new Text("🎈");
                balao.setFont(Font.font(25 + random.nextInt(12)));
                balao.setFill(coresBalao[
                    random.nextInt(coresBalao.length)

                ]);

                elemento = balao;

                // Balões sobem.
                velocidade = -(15 + random.nextDouble() * 25);

            } else {

                // Confete.
                Rectangle confete = new Rectangle(
                    5 + random.nextInt(5),
                    9 + random.nextInt(7)
                );

                confete.setArcWidth(3);
                confete.setArcHeight(3);

                confete.setFill(coresConfete[
                    random.nextInt(coresConfete.length)
                ]);

                elemento = confete;
            }

            elemento.setOpacity(
                0.35 + random.nextDouble() * 0.45
            );

            Particula particula = new Particula(
                elemento,
                x,
                y,
                velocidade,
                movimento,
                30 + random.nextDouble() * 120
            );

            particula.angulo = random.nextDouble() * 360;

            particulas.add(particula);
            fundo.getChildren().add(elemento);
        }

        // Pequenas bolinhas decorativas.
        for (int i = 0; i < 18; i++) {

            Circle bolinha = new Circle(
                3 + random.nextDouble() * 5,
                coresConfete[random.nextInt(coresConfete.length)]
            );

            bolinha.setOpacity(0.35);

            Particula particula = new Particula(
                bolinha,
                random.nextDouble() * 1200,
                random.nextDouble() * 900,
                15 + random.nextDouble() * 30,
                -10 + random.nextDouble() * 20,
                0
            );

            particulas.add(particula);
            fundo.getChildren().add(bolinha);
        }

        // O fundo fica atrás da interface existente.
        camada.getChildren().add(fundo);
        camada.getChildren().add(interfaceAtual);

        // Mantém o conteúdo atual centralizado.
        StackPane.setAlignment(
            interfaceAtual,
            javafx.geometry.Pos.TOP_CENTER
        );

        // Substitui a raiz da cena sem perder os componentes.
        cena.setRoot(camada);

        // Anima as partículas continuamente.
        AnimationTimer animacao = new AnimationTimer() {

            private long ultimoTempo = 0;

            @Override
            public void handle(long agora) {

                if (ultimoTempo == 0) {
                    ultimoTempo = agora;
                    return;
                }

                double delta = (agora - ultimoTempo) / 1_000_000_000.0;
                ultimoTempo = agora;

                double largura = fundo.getWidth();
                double altura = fundo.getHeight();

                if (largura <= 0 || altura <= 0) {
                    return;
                }

                for (Particula p : particulas) {

                    p.y += p.velocidade * delta;
                    p.x += Math.sin(p.y / 45.0) * p.movimento * delta;
                    p.angulo += p.rotacao * delta;

                    if (p.velocidade >= 0 && p.y > altura + 40) {
                        p.y = -40;
                        p.x = random.nextDouble() * largura;
                    }

                    if (p.velocidade < 0 && p.y < -40) {
                        p.y = altura + 40;
                        p.x = random.nextDouble() * largura;
                    }

                    if (p.x < -40) {
                        p.x = largura + 20;
                    }

                    if (p.x > largura + 40) {
                        p.x = -20;
                    }

                    p.elemento.setTranslateX(p.x);
                    p.elemento.setTranslateY(p.y);
                    p.elemento.setRotate(p.angulo);
                }
            }
        };

        animacao.start();
    }
}
