/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package WarRede;

import War.Jogador;
import War.Territorio;
import WarCli.CliJogo;
import WarGUI.TerImageSprite;
import WarSrv.SrvJogo;

/**
 * Mensagem para atualizar o Dono do Território dado.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class MsgDono extends Mensagem {

    // Nova quantidade de peças.
    Jogador jogador;
    // 'Território' atualizado.
    Territorio territorio;

    public MsgDono(String msg, Territorio territorio, Jogador jogador) {
        super(msg);
        this.jogador = jogador;
        this.territorio = territorio;
    }

    @Override
    public void analisar(CliJogo cJogo) {
        // Buscando pela referência deste 'Território' em 'Mapa'.
        Territorio t = cJogo.getMapa().buscarTerritorio(
                territorio.getNome());

        // Alterando o dono do território.
        t.setDono(jogador);
        // Adiciona Territorio. ///////Deformaçao do objeto jogador!!!!!
        if (jogador.getCor().getRGB() == cJogo.getJogador().getCor().getRGB()) {
            t.setDono(cJogo.getJogador());
        } else {
            cJogo.getJogador().getTerritorios().remove(t);
        }

        // Se o jogador perdeu todos os Territórios.
        if (cJogo.getJogador().getTerritorios().isEmpty()) {
            cJogo.sairJogo();
        }

        for (TerImageSprite tis : cJogo.getIPecas()) {
            if (tis.territorio == t) {
                tis.setImage(TerImageSprite.pegarAsset(t.getDono().getCor()));
            }
        }
    }

    public void analisar(SrvJogo sJogo) {
        // Buscando pela referência deste 'Território' em 'Mapa'.
        Territorio t = sJogo.getMapa().buscarTerritorio(territorio.getNome());

        // Alterando o dono do território.
        t.setDono(jogador);
        // Se o dono for eu atualizar corretamente.
        for (Jogador j : sJogo.getJogadores()) {
            if (jogador.getCor().getRGB() == j.getCor().getRGB()) {
                t.setDono(j);
            }
        }
    }

    public Territorio getTerritorio() {
        return territorio;
    }

    public Jogador getJogador() {
        return jogador;
    }
}
