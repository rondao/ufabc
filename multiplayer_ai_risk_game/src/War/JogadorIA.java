package War;

import WarRede.MsgDono;
import WarRede.MsgPeca;
import WarSrv.SrvJogo;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Extende a classe Jogador, permitindo sua participacao
 * no jogo sem alteracoes. Porem, quando for sua vez de jogar,
 * sera processado pelo servidor.
 * @author Rafael Rondão / Vinicius Aldeia
 */
public class JogadorIA extends Jogador {

    private int pecasLivres;
    private boolean primeiroTurno;
    transient private SrvJogo sJogo;

    public JogadorIA(String nome, Color c, SrvJogo sJogo) {
        super(nome, c);
        primeiroTurno = true;
        this.sJogo = sJogo;
    }

    public void executarIA() {
        JOptionPane.showMessageDialog(null, "Inicio turno da " + nome);
        iniciaTurno();

        colocarPecas();

        if (primeiroTurno) {
            JOptionPane.showMessageDialog(null, "Objetivo da " + nome + " e:\n" + objetivo.toString());
            JOptionPane.showMessageDialog(null, "Fim turno da " + nome);
            primeiroTurno = false;
            return;
        }

        JOptionPane.showMessageDialog(null, "Fase de Ataque da " + nome);

        atacar();

        JOptionPane.showMessageDialog(null, "Fase de Movimentacao da " + nome);

        movimentar();

        JOptionPane.showMessageDialog(null, "Fim turno da " + nome);
    }

    private void iniciaTurno() {
        // Recebe quantos continentes o jogador tem conquistado.
        verificaTotalidades(sJogo.getMapa().getContinentes());

        // Número de peças livres é metade dos seus territórios.
        pecasLivres = territorios.size() / 2;

        for (Continente c : totalidades) {
            pecasLivres += c.getPecasPorRodada();
        }
    }

    private void colocarPecas() {
        //Se esse território é parte do meu objetivo
        double pesoUtilidadeTerritorioObjetivo = 0.23; //0.86
        double pesoUtilidadeTerritorioContinente = 0.3; //0.921
        double pesoUtilidadeNumeroDePecas = -0.475; // 0.475
        double pesoUtilidadeAmigosFronteiras = 1.3; // 0.75
        double bias = 0.4; //0.4
        boolean posExercito;

        while (0 != pecasLivres) {
            double pontuacaoTerritorio = 0;
            double melhorPontuacao = 0;
            Territorio menosPior = null;

            posExercito = false;
            for (Territorio t : territorios) {

                //So colocara se fizer fronteira com inimigo
                if (t.getFronteirasInimigas().size() > 0) {
                    pontuacaoTerritorio += pesoUtilidadeTerritorioObjetivo * ptsUtilidadeTerritorioObjetivo(t);
                    pontuacaoTerritorio += pesoUtilidadeTerritorioContinente * getPctControlada(t.getContinente());
                    pontuacaoTerritorio += pesoUtilidadeNumeroDePecas * ptsUtilidadeNumeroDePecas(t);
                    pontuacaoTerritorio += pesoUtilidadeAmigosFronteiras * ptsUtilidadeAmigosFronteira(t);

                    //Se for o menosPior território até agora, guardar ele
                    if (pontuacaoTerritorio >= melhorPontuacao) {
                        menosPior = t;
                        melhorPontuacao = pontuacaoTerritorio;
                    }

                    if (pontuacaoTerritorio > bias) {
                        atualizarPeca(t, t.getPecas() + 1);
                        pecasLivres--;
                        if (pecasLivres == 0) {
                            return;
                        }
                        posExercito = true;
                    }
                }
                //zera para a próxima iteração
                pontuacaoTerritorio = 0;
            }
            if (!posExercito) {
                if (menosPior == null) {
                    Random r = new Random();
                    Territorio t = territorios.get(r.nextInt(territorios.size()));
                    atualizarPeca(t, t.getPecas() + 1);
                } else {
                    atualizarPeca(menosPior, menosPior.getPecas() + 1);
                }
                pecasLivres--;
            }
        }

    }

    private void atacar() {
        ArrayList<Ataque> ataquesPossiveis = new ArrayList<Ataque>();
        ArrayList<Territorio> territoriosGanhos = new ArrayList<Territorio>();
        double bias = 0.4; // 0.4

        //Para cada território, se houver algum front onde ele pode atacar, cria um ataque
        //se a pontuação desse ataque for maior que o limiar, cria esse ataque
        for (Territorio atq : territorios) {
            // Menos de duas pecas o ataque eh invalido.
            if (atq.getPecas() < 2) {
                continue;
            }

            for (Territorio def : atq.getFronteiras()) {

                if (!atq.getDono().equals(def.getDono())) {
                    Ataque a = new Ataque(atq, def);
                    if (a.atribuiPontuacao() > bias) {
                        ataquesPossiveis.add(a);
                    }
                }
            }
        }

        while (ataquesPossiveis.size() != 0) {
            Collections.sort(ataquesPossiveis);
            Collections.reverse(ataquesPossiveis);

            Ataque melhor = ataquesPossiveis.remove(0);

            //remove os ataques possiveis que usam o territorio de ataque
            //vou calcular de novo e ver se ainda é possível atacar deles
            for (int i = 0; i < ataquesPossiveis.size();) {
                if (ataquesPossiveis.get(i).atacante == melhor.atacante) {
                    ataquesPossiveis.remove(i);
                    continue;
                }
                i++;
            }

            //ataco.. se foi bem sucedido, o exército atacado agora pode ser usado em outro ataque
            if (calcularAtaque(melhor.atacante, melhor.defensor)) {
                // Se tiver pelo menos duas pecas ai podera atacar.
                if (melhor.defensor.getPecas() > 1) {
                    territoriosGanhos.add(melhor.defensor);
                }
            }

            //Se algum que já atacou ainda pode atacar, colocar nos atqs possíveis de novo
            if (melhor.atacante.getPecas() > 1) {
                for (Territorio front : melhor.atacante.getFronteiras()) {
                    if (!melhor.atacante.getDono().equals(front.getDono())) {
                        Ataque a = new Ataque(melhor.atacante, front);
                        if (a.atribuiPontuacao() > bias) {
                            ataquesPossiveis.add(a);
                        }
                    }
                }
            }


            //verifica a partir de qual território ganho posso atacar e coloca nos ataques possíveis
            if (melhor.atacante.getPecas() > 1) {
                for (Territorio t : territoriosGanhos) {
                    for (Territorio def : t.getFronteiras()) {
                        if (!t.getDono().equals(def.getDono())) {
                            Ataque a = new Ataque(t, def);
                            if (a.atribuiPontuacao() > bias) {
                                ataquesPossiveis.add(a);
                            }
                        }
                    }
                }
            }
            //contina a rotina, pegando o menosPior dos ataques possíveis e atacando...

            //remove os ataques que atacavam um territorio conquistado
            for (int i = 0; i < ataquesPossiveis.size();) {
                if (territoriosGanhos.contains(ataquesPossiveis.get(i).defensor)) {
                    ataquesPossiveis.remove(i);
                    continue;
                }
                i++;
            }
        }
    }

    private void movimentar() {
        double frontPontuacao = 0;
        double pesoUtilidadeInimigos = 0.8; //0.1
        double pesoUtilidadePecas = -0.17; //-0.71
        double bias = 0.4; //0.4

        Territorio moverPara = null;
        boolean fezMovimento;

        int contMovimentos = 0;

        tudo:
        do {
            fezMovimento = false;
            //vejo com quantos inimigos faço fronteira
            for (Territorio t : territorios) {
                if (t.getPecas() <= 1) {
                    continue;
                }

                for (Territorio front : t.getFronteirasAmigas()) {
                    frontPontuacao = 0;
                    frontPontuacao += pesoUtilidadeInimigos * (double) front.getFronteirasInimigas().size();
                    frontPontuacao += pesoUtilidadePecas * (double) front.getPecas();

                    if (frontPontuacao > bias) {
                        moverPara = front;
                        fezMovimento = true;
                        break;
                    }
                }

                if (fezMovimento) {
                    atualizarPeca(t, t.getPecas() - 1);
                    atualizarPeca(moverPara, moverPara.getPecas() + 1);
                    contMovimentos++;
                    break;
                }
            }

        } while (fezMovimento && contMovimentos <= 30);
    }

    private boolean calcularAtaque(Territorio tAtq, Territorio tDef) {
        if (tAtq.getPecas() == 1) {
            return false;
        }

        // Realiza um ataque. (Ter Ataque, Ter Defesa)
        Ataque ataque = new Ataque(tAtq, tDef);
        ataque.realizaAtaque();

        // Se a defesa perdeu todos as peças
        // Este 'Território' foi conquistado
        if (tDef.getPecas() - ataque.getPerdidosD() == 0) {
            // O Ataque ficara com as peças que tinha
            // subtraido das que usou para atacar.
            atualizarPeca(tAtq,
                    tAtq.getPecas() - ataque.getExA());

            // A Defesa ficara com as peças que foram usadas no ataque
            // subtraidas das que foram perdidas.
            atualizarPeca(tDef, ataque.getExA() - ataque.getPerdidosA());

            // Agora o 'Território' da Defesa foi conquistado
            // por este jogador
            atualizarDono(tDef);

            // Como o 'Território' foi conquistado
            // O jogador poderá comprar uma carta
            // receberCarta = true;
            return true;
        } else {
            // Caso a Defesa sobreviva, basta atualizar
            // o resultado das peças.
            // Atualizando o Ataque
            atualizarPeca(tAtq,
                    tAtq.getPecas() - ataque.getPerdidosA());

            // Atualizando a Defesa
            atualizarPeca(tDef,
                    tDef.getPecas() - ataque.getPerdidosD());

            return false;
        }
    }

    /**
     * Cria uma mensagem para o Servidor sobre o número de pecas
     * de um certo 'Território' que foi alterado.
     * @param territorio 'Território' a ser atualizado.
     * @param pecas Novo número de peças deste 'Território'.
     */
    private void atualizarPeca(Territorio territorio, int pecas) {

        MsgPeca mensagem = new MsgPeca("Territorio: " + territorio.getNome() +
                " possui: " + pecas + " pecas.", territorio, pecas);
        try {
            // Enviando mensagem para todos.
            sJogo.getServidor().enviarObjetoTodos(mensagem);
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }

    }

    /**
     * Cria uma mensagem para o Servidor sobre o novo dono
     * de um certo 'Território' que foi alterado.
     * @param territorio 'Território' a ser atualizado.
     * @param jogador Novo dono deste 'Território'.
     */
    private void atualizarDono(Territorio territorio) {

        MsgDono mensagem = new MsgDono("Territorio: " + territorio.getNome() +
                " foi conquistado por: " + nome,
                territorio, this);

        // Enviando mensagem para o Servidor.
        try {
            // Enviando mensagem para todos.
            sJogo.getServidor().enviarObjetoTodos(mensagem);
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
    }

    /**
     * Quanto maior o número de retorno, mais amigos tenho nas minhas fronteiras
     * o valor pode ser de -1 (só inimigos) a 1 (só amigos)
     * @param territorio
     * @return
     */
    public double ptsFronteiras(Territorio territorio) {
        int contInimigos = 0;
        int contAmigos = 0;
        boolean fronteiraComInimigo = false;
        boolean fronteiraComAmigo = false;
        boolean atacoAPriori;
        for (Territorio t : territorio.getFronteiras()) {
            if (t.getDono().equals(territorio.getDono())) {
                fronteiraComAmigo = true;
                continue;
            }
            fronteiraComInimigo = true;
        }

        atacoAPriori = fronteiraComAmigo && fronteiraComInimigo;

        if (!atacoAPriori) {
            return 0;
        }



        for (Territorio t : territorio.getFronteiras()) {
            if (t.getDono().equals(territorio.getDono())) {
                contAmigos++;
            } else {
                contInimigos++;
            }

        }
        return (contAmigos - contInimigos) / territorio.getFronteiras().size();
    }
}
