package War;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Representa um Objetivo de conquistar 18 territórios
 * com duas peças em cada, pelo menos.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class Objetivo18Territorios extends Objetivo implements Serializable {

    /**
     * Cria um novo objetivo do tipo "Conquistar 18 territórios com 2 peças".
     * @param descricao
     */
    public Objetivo18Territorios(String descricao){
        super(descricao);
    }

    /**
     * @return true se o objetivo foi concluido, false caso contrário
     * @override
     */
    public boolean concluido(Jogador jogador){
        if(jogador.getTerritorios().size() >= 18){
            int num = 0;
            for (Territorio T : jogador.getTerritorios()) {
                // Se o número de peças for maior ou igual a 2,
                // este é um território válido.
                if (T.getPecas() >= 2) {
                    num++;
                }
            }

            if (num >= 18) {
                return true;
            }
        }
        return false;
    }


}
