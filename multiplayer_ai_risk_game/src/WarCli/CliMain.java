package WarCli;

import java.io.IOException;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

/**
 * Classe temporária a ser substituida pela GUI.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class CliMain {

    public static void main(String[] args) throws InterruptedException {

        String nome = JOptionPane.showInputDialog("Digite seu nome");

        try {
            CliJogo Client = new CliJogo("127.0.0.1", 11000, nome);
        } catch (UnknownHostException ex) {
            System.err.println(ex.toString());
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
    }
}
