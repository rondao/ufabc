package WarGUI;

import War.Territorio;
import java.awt.Color;
import java.io.Serializable;
import pulpcore.sprite.ImageSprite;

/** Adiciona o atributo Territorio ao ImageSprite */
public final class TerImageSprite extends ImageSprite implements Serializable {

    // O Caminho no arquivo para os ícones de peças
    private static final String ASSET_PRETA = "pPreta.png";
    private static final String ASSET_BRANCA = "pBranco.png";
    private static final String ASSET_VERMELHA = "pVermelho.png";
    private static final String ASSET_AZUL = "pAzul.png";
    private static final String ASSET_VERDE = "pVerde.png";
    private static final String ASSET_AMARELA = "pAmarelo.png";

    public Territorio territorio;

    public TerImageSprite(String imageAsset, int x, int y) {
        super(imageAsset, x, y);
        this.territorio = null;
    }

    /** Analisa a cor do jogador para buscar o asset associado */
    public static String pegarAsset(Color cor) {
        if (cor.equals(Color.BLACK)) {
            return ASSET_PRETA;
        }
        if (cor.equals(Color.WHITE)) {
            return ASSET_BRANCA;
        }
        if (cor.equals(Color.RED)) {
            return ASSET_VERMELHA;
        }
        if (cor.equals(Color.BLUE)) {
            return ASSET_AZUL;
        }
        if (cor.equals(Color.GREEN)) {
            return ASSET_VERDE;
        } // Caso contrário
        return ASSET_AMARELA;
    }

    public void setTerritorio(Territorio territorio) {
        this.territorio = territorio;
    }
}
