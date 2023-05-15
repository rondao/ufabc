package WarSrv;

import War.*;
import WarRede.MsgVez;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Central do jogo.
 * Cria o estado inicial do jogo. Ordem dos jogadores e territórios.
 * Recebe e Distribui os eventos ocorridos no jogo para todos os jogadores.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class SrvJogo {

    /** Servidor que irá ouvir as conexões e gerenciá-las. */
    private Servidor srv;
    /** Mapa com todos os territórios e continentes do jogo */
    private Mapa mapa;
    /** Contém as cartas do jogo */
    private ArrayList<Carta> baralho;
    /** Jogadores conectados no jogo */
    private ArrayList<Jogador> jogadores = new ArrayList<Jogador>();
    /** Jogador que está efetuando o seu turno */
    private Jogador jogadorAtual;
    /** Contador para controlar a ordem dos jogadores */
    private int contJogador;

    /**
     * Construtor.
     * @param porta Número da porta que 'srv' ouvirá por conexões.
     */
    public SrvJogo(int porta, int numJog) {
        this.srv = new Servidor(porta, numJog, this);

        // ------------ TESTE --------------
        System.out.println("Carregando Mapa...");
        // ------------ TESTE --------------

        try {
            this.mapa = carregarMapa();
        } catch (IOException ex) {
            System.err.println(ex.toString());
        } catch (ClassNotFoundException ex) {
            System.err.println(ex.toString());
        }

        // ------------ TESTE --------------
        System.out.println(mapa.toString());
        System.out.println("Mapa Carregado!");
        // ------------ TESTE --------------

        // ------------ TESTE --------------
        System.out.println("Criando Cartas...");
        // ------------ TESTE --------------

        this.baralho = criarCartas();

        // ------------ TESTE --------------
        System.out.println("Cartas Criadas!");
        // ------------ TESTE --------------

        // Servidor esperará até todos os jogadores se conectarem.
        jogadores = srv.AceitarConexoes();

        contJogador = 0;
    }

    /**
     * Carrega o mapa de jogo do ficheiro 'mapa.wmp'.
     * @throws IOException Falha no ficheiro.
     * @throws ClassNotFoundException Falha no readObject() do ficheiro.
     */
    private Mapa carregarMapa() throws IOException, ClassNotFoundException {

        // Abrindo o ficheiro "mapa.wm" em 'FIS'.
        FileInputStream FIS;
        FIS = new FileInputStream("mapa.wmp");

        // Pegando o InputStream do ficheiro.
        ObjectInputStream ois = new ObjectInputStream(FIS);

        // Lendo o único objeto do ficheiro. O mapa.
        Mapa map = (Mapa) ois.readObject();

        // Fechando o ficheiro.
        ois.close();

        return map;
    }

    /**
     * Cria o conjunto com as 'Cartas' do jogo.
     * Atribui um 'Território' e um símbolo a cada 'Carta'.
     * @return O conjunto com todas as 'Cartas' em ordem aleatória.
     */
    private ArrayList<Carta> criarCartas() {

        // Símbolo a ser atribuído a cada 'Carta'.
        int simbolo = Carta.CIRCULO;

        ArrayList<Carta> cartas = new ArrayList<Carta>();

        // Cada 'Território' possuirá uma 'Carta' relacionada.
        for (Territorio t : mapa.getTerritorios()) {

            cartas.add(new Carta(t, simbolo));

            // Passa para o próximo símbolo possível
            simbolo++;

            // Se o símbolo chegar no último possível (CURINGA)
            // volta ao primeiro (CIRCULO).
            if (simbolo == Carta.CURINGA) {
                simbolo = Carta.CIRCULO;
            }
        }

        // Adiciona as duas cartas curinga
        cartas.add(new Carta(null, Carta.CURINGA));
        cartas.add(new Carta(null, Carta.CURINGA));

        Collections.shuffle(cartas, new Random());

        return cartas;
    }

    /**
     * Distribui os possíveis objetivos para os jogadores.
     */
    public void distribuirObjetivos() {
        ArrayList<Objetivo> objetivos = new ArrayList<Objetivo>();
        criarObjetivos(objetivos);

        Collections.shuffle(objetivos);

        int i = 0;
        for (Jogador j : jogadores) {
            j.setObjetivo(objetivos.get(i));
            i++;
        }
    }

    /** METODO TEMPORARIO. SERIALIZAR O ARRAYLIST FINAL */
    private void criarObjetivos(ArrayList<Objetivo> objetivos) {

        ArrayList<Continente> AmNorte_Afr = new ArrayList<Continente>();
        AmNorte_Afr.add(mapa.getContinentes().get(5));
        AmNorte_Afr.add(mapa.getContinentes().get(1));

        ArrayList<Continente> Asia_Afr = new ArrayList<Continente>();
        Asia_Afr.add(mapa.getContinentes().get(4));
        Asia_Afr.add(mapa.getContinentes().get(1));

        ArrayList<Continente> AmNorte_Oce = new ArrayList<Continente>();
        AmNorte_Oce.add(mapa.getContinentes().get(5));
        AmNorte_Oce.add(mapa.getContinentes().get(0));

        ArrayList<Continente> Asia_AmSul = new ArrayList<Continente>();
        Asia_AmSul.add(mapa.getContinentes().get(4));
        Asia_AmSul.add(mapa.getContinentes().get(3));

        objetivos.add(new ObjetivoContinente("Conquistar a America do Norte e a Africa", AmNorte_Afr));
        objetivos.add(new ObjetivoContinente("Conquistar a Asia e a Africa", Asia_Afr));
        objetivos.add(new ObjetivoContinente("Conquistar a America do Norte e a Oceania", AmNorte_Oce));
        objetivos.add(new ObjetivoContinente("Conquistar a Asia e a America do Sul", Asia_AmSul));

        objetivos.add(new Objetivo24Territorios("Conquistar 24 territorios"));
        objetivos.add(new Objetivo18Territorios("Conquistar 18 territorios e ocupa-los com duas pecas"));
    }

    /**
     * Primeiro passo do jogo.
     * Distribui os 'Territórios' para os 'jogadores'.
     */
    public void distribuirTerritorios() {

        // Ordem aleatória dos jogadores.
        Collections.shuffle(jogadores);

        for (Carta c : baralho) {

            // Se a carta for um Curinga, ignorar.
            if (c.getSimbolo() == Carta.CURINGA) {
                continue;
            }

            // Pega o jogador atual.
            jogadorAtual = jogadores.get(contJogador);

            // E lhe atribui um 'Território'.
            c.getPais().setDono(jogadorAtual);
            // Com uma pedra.
            c.getPais().adicionaPeca();

            // Avançando para o próximo jogador.
            contJogador++;
            // Se for o último jogador, retornar ao primeiro.
            if (contJogador == jogadores.size()) {
                contJogador = 0;
            }

        }

        // Preparando para o primeiro jogador.
        contJogador = 0;
        jogadorAtual = jogadores.get(contJogador);
    }

    /** Envia uma mensagem avisando ao jogador que é o seu turno */
    public void passarVez() {
        // Avançando para o próximo jogador.
        contJogador++;
        // Se for o último jogador, retornar ao primeiro.
        if (contJogador >= jogadores.size()) {
            contJogador = 0;
        }
        jogadorAtual = jogadores.get(contJogador);

        // Antes de enviar a 'msgVez', se o jogador da vez for IA,
        // será tratado aqui mesmo.
        if (jogadores.get(contJogador) instanceof JogadorIA) {
            ((JogadorIA) jogadores.get(contJogador)).executarIA();
            passarVez();
            return;
            // A vez foi do JogadorIA e nao sera enviado msgVez dessa vez.
        }
        
        MsgVez msgVez = new MsgVez("O turno é do jogador: " +
                jogadorAtual.getNome(), jogadorAtual);

        try {
            srv.enviarObjetoTodos(msgVez);
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
    }

    /** Inicia o jogo avisando o jogador para começar */
    public void iniciarJogo() {

        // Antes de enviar a 'msgVez', se o jogador da vez for IA,
        // será tratado aqui mesmo.
        if (jogadores.get(contJogador) instanceof JogadorIA) {
            ((JogadorIA) jogadores.get(contJogador)).executarIA();
            passarVez();
            return;
            // msgVez sera enviada no passarVez(); Esta rodada foi de IA.
        }

        MsgVez msgVez = new MsgVez("O turno é do jogador: " +
                jogadorAtual.getNome(), jogadorAtual);

        try {
            srv.enviarObjetoTodos(msgVez);
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
    }

    public void adicionarIA(int numJogIA) {
        if (numJogIA == 0) return;
        
        /** Cores distribuidas para os jogadoresIA. */
        Color[] cores = {Color.BLACK, Color.WHITE, Color.BLUE,
                         Color.RED, Color.GREEN};

        // Eliminando as cores ja utilizadas.
        for (int cor = 0; cor < 5; cor++) {
            for (Jogador j : jogadores) {
                if (j.getCor() == cores[cor])
                    cores[cor] = null;
            }
        }

        // Adicionando os jogadoresIA.
        for (int ia = 0; ia < numJogIA; ia++) {
            for (int cor = 0; cor < 5; cor++) {
                if (cores[cor] != null) {
                    jogadores.add(new JogadorIA("IA-0" + ia, cores[cor], this));
                    cores[cor] = null;
                    break;
                }
            }
        }

        // Randomizando a ordem.
        Collections.shuffle(jogadores, new Random());
    }

    /**
     * Segundo passo do jogo.
     * Envia o estado atual do 'mapa' para todos os 'jogadores'.
     */
    public void enviarMapa() {
        try {
            srv.enviarObjetoTodos(mapa);
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
    }

    /** Envia as cartas criadas pro jogo */
    public void enviarBaralho() {
        try {
            srv.enviarObjetoTodos(baralho);
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
    }

    /** Envia os Objetos 'Jogadores' aos seus devidos jogadores. */
    public void enviarJogadores() {
        try {
            for (Jogador j : jogadores) {
                // Se for JogadorIA ignora.
                if (j instanceof JogadorIA) continue;

                srv.enviarObjeto(j, j);
            }
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
    }

    /** Retorna os jogadores conectados */
    public ArrayList<Jogador> getJogadores() {
        return jogadores;
    }

    /** Retorna o mapa */
    public Mapa getMapa() {
        return mapa;
    }

    /** Retorna o Servidor com as Conexoes */
    public Servidor getServidor() {
        return srv;
    }
}
