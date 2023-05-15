package WarGUI;

import War.Territorio;
import java.io.Serializable;
import pulpcore.image.CoreFont;
import pulpcore.sprite.Label;

/** Adiciona o atributo Territorio ao Label */
public final class TerLabel extends Label  implements Serializable {

    public Territorio territorio;

    public TerLabel(CoreFont font, String text, double x, double y,
            Territorio territorio) {
        super(font, text, x, y);
        this.territorio = territorio;
    }
}