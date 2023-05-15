package War;

import java.io.Serializable;

public class Carta implements Serializable {

    public static int CIRCULO = 0;
    public static int TRIANGULO = 1;
    public static int QUADRADO = 2;
    public static int CURINGA = 3;
    private Territorio territorio;
    private int simbolo;

    /**
     * Cria uma carta recebendo um nome e um país associados
     * @param territorio
     * @param simbolo
     */
    public Carta(Territorio pais, int simbolo) {
        this.territorio = pais;
        this.simbolo = simbolo;
    }


    public Territorio getPais() {
        return this.territorio;
    }

    public int getSimbolo() {
        return this.simbolo;
    }

    public String getStrSimbolo() {
        switch (simbolo) {
            case 0:
                return "Circulo";
            case 1:
                return "Triangulo";
            case 2:
                return "Quadrado";
            case 3:
                return "Curinga";
        }
        return "Curinga";
    }
}
