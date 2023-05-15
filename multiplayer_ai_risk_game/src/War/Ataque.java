package War;

import java.util.Random;
import javax.swing.JOptionPane;

public class Ataque implements Comparable {

    Territorio atacante;
    Territorio defensor;
    Objetivo objAtacante;
    boolean defensorEliminado;
    int perdidosA, perdidosD;
    int exA, exD;
    double pontuacao;

    /**
     * Instancia um objeto Ataque
     * @param atacante - Território que realiza o ataque
     * @param defensor - Território que defende-se do ataque
     */
    public Ataque(Territorio atacante, Territorio defensor) {
        this.atacante = atacante;
        this.defensor = defensor;
        perdidosA = 0;
        perdidosD = 0;
        objAtacante = atacante.getDono().getObjetivo();
        exA = atacante.getPecas();
        exD = defensor.getPecas();
        pontuacao = -1;
    }

    /**
     * Efetua o ataque, calculando quantos exércitos
     * cada território irá perder
     */
    public void realizaAtaque() {

        //o Atacante não pode atacar com mais de 3 exércitos por vez, e não pode
        //atacar utilizando o exército de ocupação
        exA = atacante.getPecas() - 1;
        if (exA > 3) {
            exA = 3;
        }

        //o Defensor só se defende com 3 exércitos por vez
        exD = defensor.getPecas();
        if (exD > 3) {
            exD = 3;
        }

        double d = (new Random()).nextDouble();

        //Exércitos perdidos na batalha no território defensor e atacante

        switch (exA) {
            case 1: // Atacante com uma peça
                switch (exD) {
                    case 1: // Defensor com uma peça
                        if (d < 0.58) {
                            perdidosA = 1;
                        } else {
                            perdidosD = 1;
                        }
                        break;

                    case 2: // Defensor com duas peça
                        if (d < 0.75) {
                            perdidosA = 1;
                        } else {
                            perdidosD = 1;
                        }
                        break;

                    case 3: // Defensor com tres peça
                        if (d < 0.83) {
                            perdidosA = 1;
                        } else {
                            perdidosD = 1;
                        }
                        break;
                }
                break;

            case 2: // Atacante com duas peça
                switch (exD) {
                    case 1: // Defensor com uma peça
                        if (d < 0.42) {
                            perdidosA = 1;
                        } else {
                            perdidosD = 1;
                        }
                        break;

                    case 2: // Defensor com duas peça
                        if (d < 0.23) {//23%
                            perdidosD = 2;
                        } else if (d < 0.23 + 0.45) {//45%
                            perdidosA = 1;
                            perdidosD = 1;
                        } else {//32%
                            perdidosA = 2;
                        }
                        break;

                    case 3: // Defensor com tres peça
                        if (d < 0.13) {//13%
                            perdidosD = 2;
                        } else if (d < 0.13 + 0.25) {//25%
                            perdidosA = 1;
                            perdidosD = 1;
                        } else {//62%
                            perdidosA = 2;
                        }
                        break;
                }
                break;

            case 3: // Atacante com três peça
                switch (exD) {
                    case 1: // Defensor com uma peça
                        if (d < 0.34) {//34%
                            perdidosA = 1;
                        } else {//66%
                            perdidosD = 1;
                        }
                        break;

                    case 2: // Defensor com duas peça
                        if (d < 0.29) {//29%
                            perdidosA = 1;
                            perdidosD = 1;
                        } else if (d < 0.29 + 0.34) {//34%
                            perdidosA = 2;
                        } else {//37%
                            perdidosD = 2;
                        }
                        break;

                    case 3: // Defensor com tres peça
                        if (d < 0.14) {//14%
                            perdidosD = 3;
                        } else if (d < 0.14 + 0.22) {//22%
                            perdidosA = 1;
                            perdidosD = 2;
                        } else if (d < 0.14 + 0.22 + 0.26) {//26%
                            perdidosA = 2;
                            perdidosD = 1;
                        } else {//38%
                            perdidosA = 3;
                        }
                        break;
                }
                break;
        }
    }


    public double atribuiPontuacao() {

        double pesoPctControlada = 0.4; //0.62
        double pesoVantagemExercitos = 1.4; //1.1
        double pesoConquistarTerritorioDoObjetivo = 0.27; //0.9
        // double pesoEliminarJogadorObjetivo = 10;
        double pesoMaisInimigos = 1.5;



        /**
         * ENTRADAS:
         *
         * ->Porcentagem do continente atacado controlada
         * ->Diferença de exércitos ataque e exércitos defesa
         * ->Se meu objetivo é eliminar o dono do território defesa
         * ->Se meu objetivo é conquistar o continente onde está o território defesa
         *
         */

        //quanto maior, melhor o ataque (tenho mais do continente, quero dominá-lo completamente)
        //valor de 0 a pesoPctControlada
        pontuacao += pesoPctControlada * ptsPorPctControlada();

        //valor de 0 a pesoConquistarTerritorioDoObjetivo
        pontuacao += pesoConquistarTerritorioDoObjetivo * ptsPorConquistaDeTerritorioDoObjetivo();

        //valor de 0 a pesoEliminarJogadorObjetivo
        // pontuacao += pesoEliminarJogadorObjetivo * ptsPorAtaqueAJogadorAEliminar();

        //retorna um número relativo à proporção de exA para exB. Esse número não precisa ser limitado.
        //se tenho uma vantagem MUITO grande de exércitos em relação ao meu oponente, sempre vale a pena
        //atacar
        pontuacao += pesoVantagemExercitos * ((exA - pesoMaisInimigos*exD) / (exA - 1.5f));

        return pontuacao;
    }

    public double ptsPorAtaqueAJogadorAEliminar() {
        if (objAtacante instanceof ObjetivoEliminarJogador) {
            if (((ObjetivoEliminarJogador) objAtacante).getAEliminar().equals(defensor.getDono())) {
                return 1;
            }
        }
        return 0;
    }

    /**
     *
     * @return 1 se o território pertence ao objetivo, 0 caso contrário
     */
    public double ptsPorConquistaDeTerritorioDoObjetivo() {
        if (objAtacante instanceof ObjetivoContinente) {
            if (((ObjetivoContinente) objAtacante).contem(defensor)) {
                return 1;
            }
        }
        return 0;
    }

    public double ptsPorPctControlada() {
        return atacante.getDono().getPctControlada(defensor.getContinente());
    }

    public int getPerdidosD() {
        return perdidosD;
    }

    public int getExsD() {
        return exD;
    }

    public int getPerdidosA() {
        return perdidosA;
    }

    public int getExA() {
        return exA;
    }

    public int compareTo(Object o) {
        Ataque outro = (Ataque) o;

        //Ainda que pontuacao possa ter o valor -1, isso não é muito comum...
        //Neste caso, -1 é atribuído para sem pontuação, e caso esse valor seja encontrado
        //aqui, a pontuação será calculada novamente
        if (outro.pontuacao == -1) {
            outro.atribuiPontuacao();
        }

        if (this.pontuacao > outro.pontuacao) {
            return 1;
        } else if (this.pontuacao < outro.pontuacao) {
            return -1;
        }
        return 0;
    }
}
