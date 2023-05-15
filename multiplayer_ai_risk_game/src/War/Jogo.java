/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package War;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author Vinicius / Rafael
 */
public class Jogo {

    private static final int PRIMEIRA_RODADA = 0;
    private static final int TROCANDO_CARTAS = 1;
    private static final int ADICIONANDO_EXERCITOS = 2;
    private static final int ATACANDO = 3;
    private static final int MOVENDO_EXERCITOS = 4;
    private ArrayList<Jogador> jogadores;
    private ArrayList<Carta> baralho;
    private Mapa mapa;
    private Jogador jogadorAtual;
    private int estadoDoJogo;
    private Random rnd;

    /**Cria um novo jogo com um número definido de jogadores
     *
     * @param noJogadores  Número de jogadores que vão participar do jogo
     * @param nomesDosJogadores  vetor de Strings com os nomes dos jogadores
     * @param cores Cores dos jogadores
     */
    public Jogo(String[] nomesDosJogadores, Color[] cores) {
        //this.criaJogadores(nomesDosJogadores, cores);
        if (!this.carregarMapa()) {
            this.criarMapa();
        }
        rnd = new Random();
    }

    /**
     * @param nomesDosJogadores  vetor de Strings com os nomes dos jogadores
     * @param cores Cores dos jogadores
     */
    public void criaJogadores(String[] nomesDosJogadores, Color[] cores) {
        //Cria jogadores
        jogadores = new ArrayList<Jogador>();
        for (int i = 0; i < nomesDosJogadores.length; i++) {
            Jogador j = new Jogador(nomesDosJogadores[i], cores[i]);
            jogadores.add(j);
        }
    }

    /**
     * Cria os territórios e fronteiras e os adiciona no mapa
     */
    public void criarMapa() {

        this.mapa = new Mapa();

        //adicionar territórios
        while (true) {
            String nome = JOptionPane.showInputDialog(null,
                    "Digite o nome do território (/sair quando tiver terminado)",
                    "Inserindo Território",
                    JOptionPane.OK_OPTION);
            if (nome.equals("/sair")) {
                break;
            }
            Territorio t = new Territorio(nome);
            mapa.adicionaTerritorio(t);
        }

        String menu = "";
        String adicionadas = "";
        int opc;

        for (int i = 0; i < mapa.getTerritorios().size(); i++) {
            menu += String.format("%d - %s\n", i, mapa.getTerritorios().get(i).getNome());
        }

        for (Territorio t : mapa.getTerritorios()) {
            adicionadas = "" + t.getStringFronteiras();

            while (true) {
                String opcao = JOptionPane.showInputDialog(null,
                        "Já adicionadas:\n " + adicionadas +
                        "Digite o índice correspondente" +
                        " à fronteira(/sair quando tiver terminado)\n\n" + menu,
                        "Adicionando fronteiras a " + t.getNome(),
                        JOptionPane.OK_OPTION);
                if (opcao.equals("/sair")) {
                    break;
                }
                opc = 0;

                try {
                    opc = Integer.parseInt(opcao);
                    if (opc > mapa.getTerritorios().size() - 1 || opc < 0) {
                        throw new NumberFormatException();
                    }
                    t.criaFronteira(mapa.getTerritorios().get(opc));
                    adicionadas += mapa.getTerritorios().get(opc).getNome() + "\n";
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Opção inválida");
                }
            }
        }
        salvarMapa(this.mapa);
    }

    /**
     * Salva os territórios e fronteiras de um mapa em uma arquivo mapa.m
     * @param mapa O mapa a ser salvo
     * @return true se o mapa pôde ser salvo, false se não pôde
     */
    public boolean salvarMapa(Mapa mapa) {
        try {
            FileOutputStream fos = new FileOutputStream("mapa.wmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mapa);
            oos.close();

            System.out.println("Mapa salvo com sucesso!");
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    /**
     * Tenta carregar o mapa de um arquivo mapa.m e retorna false caso o arquivo não
     * exista
     * @return false caso o arquivo mapa.m ainda não exista e true caso exista
     */
    public boolean carregarMapa() {

        FileInputStream fis;
        try {
            fis = new FileInputStream("mapa.m");
        } catch (FileNotFoundException e) {
            return false;
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.mapa = (Mapa) ois.readObject();
            ois.close();

            System.out.println("Mapa carregado com sucesso!");
            System.out.println(mapa.toString());
            return true;

        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
    }

    public void criarCartas() {
        int simbolo = Carta.CIRCULO;
        for (Territorio t : mapa.getTerritorios()) {
            baralho.add(new Carta(t, simbolo));
            simbolo++;
            if (simbolo == Carta.CURINGA) {
                simbolo = Carta.CIRCULO;
            }
        }

        baralho.add(new Carta(null, Carta.CURINGA));
        baralho.add(new Carta(null, Carta.CURINGA));

        Collections.shuffle(baralho);
    }

    /**
     * Inicia o jogo atual.
     */
    public void primeiraRodada() {
        Collections.shuffle(jogadores);
        int contJogador = 0;
        for (Carta c : baralho) {
            jogadorAtual = jogadores.get(contJogador);
            c.getPais().setDono(jogadorAtual);
            c.getPais().adicionaPeca();
            jogadorAtual.adicionaTerritorio(c.getPais());
            contJogador++;
            if (contJogador == jogadores.size()) {
                contJogador = 0;
            }

        }

        contJogador = 0;

        jogadorAtual = jogadores.get(contJogador);
    }

//    /**
//     *  Cria um objeto Ataque e executa o método realizaAtaque. (Nota: Ao executar
//     * este método, assegure que as condições de legalidade para o ataque já existem, i.e.:
//     * O território atacante tem mais de 1 exército, ambos não pertencem ao mesmo jogador, etc.)
//     * @param atacante Territorio que realiza o ataque
//     * @param defensor Território que se defende do ataque
//     * @return
//     */
//    public boolean atacar(Territorio atacante, Territorio defensor) {
//        Ataque a = new Ataque(atacante, defensor);
//        a.realizaAtaque();
//        //if (retorno) {//O ataque foi bem sucedido
//            defensor.getDono().removeTerritorio(defensor);
//
//            if (!defensor.getDono().temTerritorios()) {//se o jogador foi eliminado
//                atacante.getDono().adicionaJogadorEliminado(defensor.getDono());
//                this.jogadores.remove(defensor.getDono());
//            }
//
//            defensor.setDono(atacante.getDono());
//
//    return true;
//       // return retorno;
//    }
//
//    /**
//     * Verifica os objetivos dos jogadores e vê se alguém ganhou
//     * @return O jogador vitorioso, caso exista. Null, caso contrário.
//     */
//    public Jogador verificaObjetivos(){
//        for(Jogador j: jogadores){
//            if(j.getObjetivo().concluido()){
//                return j;
//            }
//        }
//        return null;
//    }

    /**
     * Move uma quantidade de peças de um território para outro
     * @param origem Território de onde saem as peças
     * @param destino Território para onde vão as peças
     * @param pecas número de peças a serem movidas
     */
    public void mover(Territorio origem, Territorio destino, int pecas) {
        if (!origem.fazFronteira(destino)) {
            //Isso já deveria ter sido tratado
            return;
        }
        if (!origem.getDono().equals(destino.getDono())) {
            //Isso já deveria ter sido tratado
            return;
        }
        if (origem.getPecas() <= 1) {
            //Isso já deveria ter sido tratado
            return;
        }
        origem.removePecas(pecas);
        origem.adicionaPecas(pecas);

    }

    public void adicionarPeca(Territorio t) {
        t.adicionaPeca();
    }

    public void adicionarPecas(Territorio t, int n) {
        t.adicionaPecas(n);
    }
}
