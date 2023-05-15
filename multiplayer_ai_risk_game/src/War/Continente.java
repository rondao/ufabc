package War;

import java.io.Serializable;
import java.util.ArrayList;

public class Continente implements Serializable {

    private ArrayList<Territorio> territorios;
    private int pecasPorRodada=0;
    private String nome;

    /**
     * Cria um novo continente
     * @param nome Nome do continente (deve ser corretamente acentuado)
     * @param pecasPorRodada Número de pecas obtidas por rodada por possuir
     * este território
     */
    public Continente(String nome, int pecasPorRodada){
        this.pecasPorRodada = pecasPorRodada;
        territorios = new ArrayList<Territorio>();
        this.nome = nome;
    }

    /**
     * Adiciona um território à lista de territórios deste continente
     * @param t Território a ser adicionado
     */
    public void adicionaTerritorio(Territorio t){
        this.territorios.add(t);
        t.setContinente(this);
    }

    /**
     * @return ArrayList com territórios
     */
    public ArrayList<Territorio> getTerritorios(){
        return this.territorios;
    }

    /**
     * @return String com nome do território
     */
    public String getNome(){
        return this.nome;
    }

    /**
     * Retorna quantas peças esse território confere ao jogador no início do turno
     * @return int com a quantidade de peças
     */
    public int getPecasPorRodada(){
        return pecasPorRodada;
    }

    public boolean contem(Territorio t){
        for(Territorio ter: territorios){
            if(ter.equals(t)){
                return true;
            }
        }
        return false;
    }
}
