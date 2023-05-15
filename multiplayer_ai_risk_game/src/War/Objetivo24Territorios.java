package War;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Representa um Objetivo de conquistar 24 territórios.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class Objetivo24Territorios extends Objetivo implements Serializable {
    
    /**
     * Cria um novo objetivo do tipo "Conquistar 24 territórios"
     * @param descricao
     */
    public Objetivo24Territorios(String descricao){
        super(descricao);
    }

    /**
     * @return true se o objetivo foi concluido, false caso contrário
     * @override
     */
    public boolean concluido(Jogador jogador){
        if(jogador.getTerritorios().size() >= 24){
            return true;
        }
        return false;
    }


}
