package WarRede;

import War.Jogador;
import WarCli.CliJogo;

/**
 * Mensagem para dizer de quem é a vez.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class MsgVez extends Mensagem {

    // Jogador da vez.
    private Jogador jogador;

    public MsgVez(String msg, Jogador jogador) {
        super(msg);
        this.jogador = jogador;
    }

    @Override
    public void analisar(CliJogo cJogo) {
        if (jogador == cJogo.getJogador()) {
            cJogo.iniciarTurno();
        }
    }

    public Jogador getJogador() {
        return jogador;
    }
}
