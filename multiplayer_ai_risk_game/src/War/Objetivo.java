package War;

import java.io.Serializable;

/**
 * Representa um Objetivo a ser cumprido pelo jogador.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public abstract class Objetivo implements Serializable {

    private String descricao;

    public Objetivo(String descricao){
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    /**
     * Verifica se este objetivo foi concluido
     * @return true se foi concluído, false caso contrário
     */
    public abstract boolean concluido(Jogador jogador);
}
