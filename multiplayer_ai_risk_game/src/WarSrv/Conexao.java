package WarSrv;

import War.Jogador;
import WarRede.Fila;
import WarRede.MsgJogadorMorto;
import WarRede.MsgVez;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * É o interpolador entre o Servidor e um Cliente.
 * Possui o 'Socket' da conexão estabelecida.
 * É uma 'Thread' para que possa ficar ouvindo
 * as 'Mensagens' enviadas pelo Cliente.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class Conexao {

    /** Esta conexão (Para ser visível as Threads) */
    private Conexao conexao = this;
    /** 'Servidor' a qual está conectado */
    private Servidor srv;
    /** 'Socket' com a conexão estabelecida */
    private Socket sckConexao;
    /** ObjectInput da conexão */
    private ObjectInputStream OIS;
    /** ObjectOutput da conexão */
    private ObjectOutputStream OOS;
    /** Fila de mensagens a serem enviadas */
    private Fila filaMsg;
    /**  Jogador com o qual está conexão se conecta */
    private Jogador jogador;

    public Conexao(Servidor srv, Socket novaConexao) {

        this.srv = srv;
        this.sckConexao = novaConexao;

        filaMsg = new Fila(50);

        // ------------ TESTE --------------
        System.out.println("Carregando I/O...");
        // ------------ TESTE --------------

        // Pegando os Streams da nova conexão
        try {
            this.OIS = new ObjectInputStream(sckConexao.getInputStream());
            this.OOS = new ObjectOutputStream(sckConexao.getOutputStream());
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }

        // ------------ TESTE --------------
        System.out.println("I/O Carregado!");
        // ------------ TESTE --------------
    }

    /** Retorna o ObjectOutputStream da 'Conexao' */
    public ObjectOutputStream getOOS() {
        return OOS;
    }

    public Fila getFilaMsg() {
        return filaMsg;
    }

    /** Inicia uma Thread para enviar as mensagens da fila para o Servidor. */
    public void iniciarConsumidor() {
        Thread consumidor = new Thread(new Runnable() {

            public void run() {
                while (true) {
                    try {
                        Object obj = filaMsg.retira();

                        // Se for uma 'MsgJogadorMorto' o jogador.
                        // saiu do jogo.
                        if (obj instanceof MsgJogadorMorto) {
                            // Terminando esta conexão.
                            srv.getConexaoJog().remove(conexao);
                            // Removendo este jogador dos existentes.
                            srv.getSJogo().getJogadores().remove(jogador);
                            // Finalizando a Thread.
                            break;
                        }

                        OOS.writeObject(obj);
                    } catch (InterruptedException ex) {
                        System.err.println(ex.toString());
                    } catch (IOException ex) {
                        System.err.println(ex.toString());
                    }
                }
            }
        });
        consumidor.start();
    }

    /** Inicia uma Thread para armazenar na fila as mensagens do Servidor */
    public void iniciarProdutor() {
        Thread produtor = new Thread(new Runnable() {

            public void run() {
                while (true) {
                    try {
                        Object obj = OIS.readObject();

                        // Se for String, é o nome do jogador, enviado só uma vez.
                        if (obj instanceof String) {
                            filaMsg.insere(obj);
                            continue;
                        }

                        // Se a mensagem for de passar vez
                        // Esta será analisada pelo SrvJogo diretamente
                        if (obj instanceof MsgVez) {
                            srv.getSJogo().passarVez();
                            // Não precisa inserir na fila
                            // SrvJogo a tratara agora.
                            continue;
                        }

                        // Se for uma 'MsgJogadorMorto' o jogador
                        // saiu do jogo.
                        if (obj instanceof MsgJogadorMorto) {
                            // Pondo na Fila para chegar na Thread Consumidora
                            filaMsg.insere(obj);
                            // Finalizando a Thread
                            break;
                        }

                        srv.enviarObjetoTodos(obj);
                    } catch (IOException ex) {
                        System.err.println(ex.toString());
                    } catch (ClassNotFoundException ex) {
                        System.err.println(ex.toString());
                    } catch (InterruptedException ex) {
                        System.err.println(ex.toString());
                    }
                }
            }
        });
        produtor.start();
    }

    public void enviarObjeto(Object obj) {
        try {
            filaMsg.insere(obj);
        } catch (InterruptedException ex) {
            System.err.println(ex.toString());
        }

    }

    /**
     * @return the jogador
     */
    public Jogador getJogador() {
        return jogador;
    }

    /**
     * @param jogador the jogador to set
     */
    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
    }
}
