package WarCli;

import WarRede.Fila;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {

    /** 'Socket' para a conexão. */
    private Socket socket;
    /** ObjectInput do 'socket'. */
    private ObjectInputStream OIS;
    /** ObjectOutput do 'socket'. */
    private ObjectOutputStream OOS;
    /** Fila de entrada Mensagens para atualização*/
    private Fila filaIn;
    /** Fila de saída de mensagens para atualização*/
    private Fila filaOut;

    /**
     * Construtor.
     * Se conecta ao 'ServerSocket' na 'porta' e 'host' passados.
     * @param host String com o host.
     * @param porta Número da porta para se conectar.
     * @param nome Nome do jogador.
     */
    public Cliente(String host, int porta, String nome)
            throws UnknownHostException, IOException {

        filaIn = new Fila(10);
        filaOut = new Fila(10);

        // ------------ TESTE --------------
        System.out.println("Conectando-se ao Servidor...");
        // ------------ TESTE --------------

        // Conectando-se ao Servidor.
        this.socket = new Socket(host, porta);

        // ------------ TESTE --------------
        System.out.println("Conectado!");
        // ------------ TESTE --------------

        // ------------ TESTE --------------
        System.out.println("Carregando I/O...");
        // ------------ TESTE --------------

        // Pegando os Input e Output da conexão.
        OOS = new ObjectOutputStream(socket.getOutputStream());
        OIS = new ObjectInputStream(socket.getInputStream());

        // ------------ TESTE --------------
        System.out.println("I/O Carregado!");
        // ------------ TESTE --------------

        // Enviando o nome ao Servidor.
        OOS.writeObject(nome);
    }

    /** Inicia uma Thread para enviar as mensagens da fila para o Servidor */
    public void iniciarConsumidor() {
        Thread consumidor = new Thread(new Runnable() {

            public void run() {
                while (true) {
                    try {
                        Object o = filaOut.retira();
                        getOOS().writeObject(o);
                    } catch (IOException ex) {
                        System.err.println(ex.toString());
                    } catch (InterruptedException ex) {
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
                        Object o = getOIS().readObject();
                        filaIn.insere(o);
                    } catch (InterruptedException ex) {
                        System.err.println(ex.toString());
                    } catch (IOException ex) {
                        System.err.println(ex.toString());
                    } catch (ClassNotFoundException ex) {
                        System.err.println(ex.toString());
                    }
                }
            }
        });
        produtor.start();
    }

    /** Envia um objeto a todos os jogadores conectados */
    public void enviarObjeto(Object obj) throws IOException {
        try {
            filaOut.insere(obj);
        } catch (InterruptedException ex) {
            System.err.println(ex.toString());
        }
    }

    public Object lerObjeto() {
        try {
            return filaIn.retira();
        } catch (InterruptedException ex) {
            System.err.println(ex.toString());
        }
        return null;
    }

    public ObjectOutputStream getOOS() {
        return this.OOS;
    }

    public ObjectInputStream getOIS() {
        return this.OIS;
    }

    public Fila getFilaIn() {
        return this.filaIn;
    }

    public Fila getFilaOut() {
        return this.filaOut;
    }
}
