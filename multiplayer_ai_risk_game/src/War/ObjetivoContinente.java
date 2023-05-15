package War;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Representa um Objetivo de conquistar um certo número de Continentes.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class ObjetivoContinente extends Objetivo implements Serializable {

    private ArrayList<Continente> continentes;
    
    /**
     * Cria um novo objetivo do tipo "Conquistar um ou mais continentes"
     * @param descricao
     */
    public ObjetivoContinente(String descricao, ArrayList<Continente> continentes){
        super(descricao);
        this.continentes = continentes;
    }

    /**
     * Adiciona um continente à lista de continentes necessária para cumprir-se
     * este objetivo
     * @param c Continente à ser adicionado
     */
    public void adicionaContinente(Continente c){
        this.continentes.add(c);
    }

    /**
     * @return true se o objetivo foi concluido, false caso contrário
     * @override
     */
    public boolean concluido(Jogador jogador) {
        if(jogador.getTotalidades().containsAll(this.continentes)){
            return true;
        }
        return false;
    }

    public boolean contem(Territorio t){
        for(Continente c: continentes){
            if(c.contem(t))
                return true;
        }
        return false;
    }

    public boolean contem(Continente c){
        return continentes.contains(c) ;
    }
}
