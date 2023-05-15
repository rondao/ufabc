package WarRede;

import War.Carta;
import WarCli.CliJogo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Mensagem para embaralhar as cartas do jogo.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class MsgEmbaralhar extends Mensagem {

    /** Seed para randomizar a ordem do baralho. */
    private long seed;

    public MsgEmbaralhar(String msg, long seed) {
        super(msg);
        this.seed = seed;
    }

    @Override
    public void analisar(CliJogo cJogo) {
        // 'baralhoDescarte' conterá todas as cartas.
        cJogo.setBaralho(cJogo.getBaralhoDescarte());
        // Limpando 'baralhoDescarte'
        cJogo.setBaralhoDescarte( new ArrayList<Carta>());

        // Embaralhando as cartas do Seed dado.
        Collections.shuffle(cJogo.getBaralho(), new Random(
                seed));
    }

    public long getSeed() {
        return seed;
    }
}