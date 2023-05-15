package WarRede;

import War.Jogador;
import WarCli.CliJogo;

/**
 * Mensagem para atualizar que um jogador está fora do jogo.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class MsgJogadorMorto extends Mensagem {

    private Jogador jogador;

    public MsgJogadorMorto (String msg, Jogador jogador) {
        super(msg);
        this.jogador = jogador;
    }

    public Jogador getJogador() {
        return jogador;
    }

    @Override
    public void analisar(CliJogo cJogo) {}
}