package WarSrv;

import javax.swing.JOptionPane;

public class SrvMain {

    public static void main(String[] args) {

        int numJog = Integer.parseInt(JOptionPane.showInputDialog("Digite o número de jogadores."));
        int numJogIA = Integer.parseInt(JOptionPane.showInputDialog("Digite o número de jogadoresIA."));

        // Inicia o Servidor do Jogo na porta 11000.
        SrvJogo srvJogo = new SrvJogo(11000, numJog);

        // ------------ TESTE --------------
        System.out.println("Adicionando IA...");
        // ------------ TESTE --------------

        // Distribui os Territórios preparando o jogo.
        srvJogo.adicionarIA(numJogIA);

        // ------------ TESTE --------------
        System.out.println("IA Adicionada!");
        // ------------ TESTE --------------

        // ------------ TESTE --------------
        System.out.println("Distribuindo Territórios...");
        // ------------ TESTE --------------

        // Distribui os Territórios preparando o jogo.
        srvJogo.distribuirTerritorios();

        // ------------ TESTE --------------
        System.out.println("Territórios Distribuídos!");
        // ------------ TESTE --------------

        srvJogo.distribuirObjetivos();

        // ------------ TESTE --------------
        System.out.println("Enviando Mapa...");
        // ------------ TESTE --------------

        // Envia o 'mapa' do jogo pronto, para todos os jogadores.
        srvJogo.enviarMapa();

        // ------------ TESTE --------------
        System.out.println("Mapa Enviado!");
        // ------------ TESTE --------------

        // ------------ TESTE --------------
        System.out.println("Enviando baralho...");
        // ------------ TESTE --------------

        srvJogo.enviarBaralho();

        // ------------ TESTE --------------
        System.out.println("Baralho Enviado!");
        // ------------ TESTE --------------

        // ------------ TESTE --------------
        System.out.println("Enviando Jogador...");
        // ------------ TESTE --------------

        // Envia os objetos 'Jogador' aos seus devidos jogadores
        srvJogo.enviarJogadores();

        // ------------ TESTE --------------
        System.out.println("Jogador Enviado!");
        // ------------ TESTE --------------

        JOptionPane.showMessageDialog(null, "Inicio de Jogo!");

        // Envia uma mensagem para o próximo jogador da vez.
        srvJogo.iniciarJogo();

        JOptionPane.showMessageDialog(null, "Servidor rodando...");
        System.exit(0);
    }
}
