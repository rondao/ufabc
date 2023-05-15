package WarRede;

import WarCli.CliJogo;
import java.io.Serializable;
import javax.swing.JOptionPane;

/**
 * Objeto de comunicação entre o Servidor e o Cliente.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public abstract class Mensagem implements Serializable {
    
    // Descrição da mensagem.
    String msg;

    public Mensagem (String msg) {
        this.msg = msg;
        // JOptionPane.showMessageDialog(null, msg);
    }

    public abstract void analisar(CliJogo cJogo);
}
