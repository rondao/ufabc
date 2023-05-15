package WarSrv;

import War.Jogador;
import WarRede.Mensagem;
import WarRede.MsgPeca;
import WarRede.MsgDono;
import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Possui um 'ServerSocket' para criar a conexão
 * com todos os usuários conectados ao jogo.
 * É o intermediário entre o jogo central e os jogadores.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class Servidor {

    /** Número da porta que o 'ServerSocket' estará ouvindo. */
    private int porta;
    /** 'ServerSocket' para ouvir as conexões. */
    private ServerSocket srvSocket;
    /** Todas as conexões criadas com os jogadores. */
    private ArrayList<Conexao> conexaoJog;
    /** Engine do jogo do Servidor */
    private SrvJogo sJogo;
    /** Número de jogadores */
    private int numJog;

    /**
     * Construtor.
     * @param porta Número da porta a ouvir conexões.
     */
    public Servidor(int porta, int numJog, SrvJogo sJogo) {
        this.porta = porta;

        this.numJog = numJog;

        conexaoJog = new ArrayList<Conexao>();
        this.sJogo = sJogo;
    }

    /**
     * Começa a ouvir conexões na porta.
     * Cria novas 'Conexao' para estas e as inicia como 'Thread'.
     */
    public ArrayList<Jogador> AceitarConexoes() {

        /** Jogadores que iram se conectar ao jogo. */
        ArrayList<Jogador> jogadores = new ArrayList<Jogador>();
        /** Cores distribuidas para os jogadores. */
        Color[] cores = {Color.BLACK, Color.WHITE, Color.BLUE,
            Color.RED, Color.GREEN};

        Collections.shuffle(Arrays.asList(cores), new Random());

        try {
            srvSocket = new ServerSocket(porta, 1000);
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }

        // Ouve até quatro conexões e depois iniciará o jogo.
        while (conexaoJog.size() != numJog) {
            try {
                // ------------ TESTE --------------
                System.out.println("Esperando Conexão...");
                // ------------ TESTE --------------

                // Ouvindo conexões. Retorna a nova conexão estabelecida.
                Socket socketConexao = srvSocket.accept();

                // ------------ TESTE --------------
                System.out.println("Conexão Estabelecida!");
                // ------------ TESTE --------------


                // Cria uma 'Conexao' para o 'Socket' criado.
                // Sendo este o Servidor.
                Conexao novaConexao = new Conexao(this, socketConexao);

                // Um novo jogador se conectou.
                conexaoJog.add(novaConexao);

                // Inicia um leitor de mensagens do Servidor
                novaConexao.iniciarProdutor();

                // ------------ TESTE --------------
                System.out.println("Lendo Nome...");
                // ------------ TESTE --------------

                // Lendo o nome do jogador. Não é necessário o tratamento, pois
                //sabemos que a primeira informação que o jogador enviará será
                //seu nome.

                String nome = (String) novaConexao.getFilaMsg().retira();

                // A cada jogador inserido, a próxima cor é selecionada.
                Color cor = cores[conexaoJog.size() - 1];

                // ------------ TESTE --------------
                System.out.println("Nome Lido...");
                // ------------ TESTE --------------

                //Criando o objeto Jogador e relacionando-o à conexão
                Jogador j = new Jogador(nome, cor);
                novaConexao.setJogador(j);

                // Adicionando o novo jogador na lista de retorno.
                jogadores.add(j);

                // Inicia um leitor de mensagens da Fila para enviar
                novaConexao.iniciarConsumidor();
            } catch (IOException ex) {
                System.err.println(ex.toString());
            } catch (InterruptedException ex) {
                System.err.println(ex.toString());
            }
        }

        return jogadores;
    }

    /** Retorna a conexão dos jogadores */
    public ArrayList<Conexao> getConexaoJog() {
        return conexaoJog;
    }

    /** Envia um objeto a um dado jogador */
    public void enviarObjeto(Object obj, Jogador j) throws IOException {

        for (Conexao cj : conexaoJog) {
            if (cj.getJogador().equals(j)) {
                System.out.println("JOG: " + j.getNome());
                cj.enviarObjeto(obj);
            }
        }
    }

    /** Envia um objeto a todos os jogadores conectados */
    public void enviarObjetoTodos(Object obj) throws IOException {
        if (obj instanceof MsgPeca) {
            ((MsgPeca) obj).analisar(sJogo);
        } else if (obj instanceof MsgDono) {
            ((MsgDono) obj).analisar(sJogo);
        }

        for (Conexao cj : conexaoJog) {
            cj.enviarObjeto(obj);
        }
    }

    /** Retorna o SrvJogo */
    public SrvJogo getSJogo() {
        return sJogo;
    }
}
