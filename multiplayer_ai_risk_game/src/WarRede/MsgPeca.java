package WarRede;

import War.Territorio;
import WarCli.CliJogo;
import WarGUI.TerLabel;
import WarSrv.SrvJogo;
/**
 * Mensagem para atualizar o número de peças no Território dado
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class MsgPeca extends Mensagem {

    // Nova quantidade de peças.
    private int pecas;
    // 'Território' atualizado.
    Territorio territorio;

    public MsgPeca (String msg, Territorio territorio, int pecas) {
        super(msg);
        this.pecas = pecas;
        this.territorio = territorio;
    }

    @Override
    public void analisar(CliJogo cJogo) {
            // Buscando pela referência deste 'Território' em 'Mapa'
            Territorio t = cJogo.getMapa().buscarTerritorio(
                    territorio.getNome());

            // Atribuindo o valor de Pecas
            t.setPecas(pecas);

            for (TerLabel tl : cJogo.getLPecas()) {
                if (tl.territorio == t) {
                    tl.setText(String.format("%2d", t.getPecas()));
                }
            }
    }

    public void analisar(SrvJogo sJogo) {
        // Buscando pela referência deste 'Território' em 'Mapa'
            Territorio t = sJogo.getMapa().buscarTerritorio(
                    territorio.getNome());

            // Atribuindo o valor de Pecas
            t.setPecas(pecas);
    }

    public Territorio getTerritorio() {
        return territorio;
    }

    public int getPecas() {
        return pecas;
    }
}
