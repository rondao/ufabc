package WarRede;

import War.Carta;
import WarCli.CliJogo;

/**
 * Mensagem para atualizar que uma 'Carta' foi retirada.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class MsgCarta extends Mensagem {

    /** A carta retirada. */
    private Carta carta;
    /** Se a carta foi trocada (deverá ir para o lixo) */
    private boolean troca;

    public MsgCarta (String msg, Carta carta, boolean troca) {
        super(msg);
        this.carta = carta;
        this.troca = troca;
    }

    @Override
    public void analisar(CliJogo cJogo) {
        // Remove a carta do baralho
            //baralho.remove(((MsgCarta) obj).getCarta());
            // Sempre a primeira.
            cJogo.getBaralho().remove(0);

            // Se a carta foi trocada, deve ir para o 'baralhoDescarte'
            if (troca) {
                // E a coloca no baralho de Descartes
                cJogo.getBaralhoDescarte().add(carta);
            }
    }

    public Carta getCarta() {
        return carta;
    }

    public boolean getBoolean() {
        return troca;
    }
}