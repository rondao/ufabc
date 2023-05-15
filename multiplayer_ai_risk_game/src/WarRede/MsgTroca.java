package WarRede;

import WarCli.CliJogo;

/**
 * Mensagem para atualizar o número de exércitos numa troca
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class MsgTroca extends Mensagem {

    // Nova quantidade de peças para a troca.
    private int troca;

    public MsgTroca (String msg, int troca) {
        super(msg);
        this.troca = troca;
    }

    @Override
    public void analisar(CliJogo cJogo) {
        cJogo.setExercitosTroca(troca);
    }

    public int getTroca() {
        return troca;
    }
}