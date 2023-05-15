/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package War;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Vinicius
 */
public class Territorio implements Serializable {

    private String nome;
    private int pecas;
    private ArrayList<Territorio> fronteiras;
    private Jogador dono;
    private Continente continente;

    /**
     * Cria um novo território
     * @param nome Nome do território
     */
    public Territorio(String nome) {
        this.nome = nome;
        this.fronteiras = new ArrayList<Territorio>();
    }

    /**
     * 
     * @return Nome do território
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Recebe um território e o adiciona em sua fronteira. Caso o território a ser
     * adicionado já contivesse este território em suas fronteiras, simplesmente adiciona
     * a fronteira correspondente às fronteiras deste território.
     * @param t - Território a ser adicionado em sua fronteira
     * @return Se o território já continha essa fronteira
     */
    public boolean criaFronteira(Territorio t) {

        //Se o território t já fazia fronteira com este território
        if (t.fazFronteira(this)) {
            this.fronteiras.add(t);
            return true;
        }

        //Se o território t não fazia fronteira com este território
        this.fronteiras.add(t);
        //t.getFronteiras().add(t);
        return false;
    }

    /**
     * Recebe um território e verifica se faz fronteira com este território
     * @param t - Territorio a ser testado
     * @return true caso o território faça fronteira, falso caso não
     */
    public boolean fazFronteira(Territorio t) {
        if (this.fronteiras.contains(t)) {
            return true;
        }
        return false;
    }

    /**
     * 
     * @return ArrayList com as fronteiras deste território
     */
    public ArrayList<Territorio> getFronteiras() {
        return this.fronteiras;
    }

    /**
     *
     * @return String com uma lista das fronteiras
     */
    public String getStringFronteiras() {
        String strFronteiras = "";

        for (Territorio t : this.fronteiras) {
            strFronteiras += t.getNome() + "\n";
        }
        return strFronteiras;
    }

    /**
     *
     * @param p - Peça a ser adicionada a este território
     */
    public void adicionaPeca() {
        this.pecas++;
    }

    /**
     * Provisoriamente, simplesmente remove a última peça do território
     */
    public void removePeca() {
        this.pecas--;
    }

    /**
     * Remove um certo número de peças deste território
     * @param n Número de peças a serem removidas
     */
    public void removePecas(int n) {
        pecas -= n;
    }

    /**
     * Adiciona um certo número de peças a este território
     * @param n Número de pecas a serem adicionadas
     */
    public void adicionaPecas(int n) {
        pecas += n;
    }

    /**
     *
     * @return int com o número de peças contidas no território
     */
    public int getPecas() {
        return pecas;
    }

    public void setPecas(int pecas) {
        this.pecas = pecas;
    }

    /**
     * Recebe um jogador e o define como dono deste território
     * @param j - Novo dono deste território
     */
    public void setDono(Jogador j) {
        // Remove o Território do Jogador atual
        if (dono != null) {
            dono.removeTerritorio(this);
        }
        // Define o novo dono
        this.dono = j;
        // Adiciona o Território ao novo dono
        dono.adicionaTerritorio(this);
    }

    /**
     *
     * @return O jogador que possui este território
     */
    public Jogador getDono() {
        return dono;
    }

    /**
     * Recebe um continente e declara este território como pertencente a ele
     * @param c Continente
     */
    public void setContinente(Continente c) {
        this.continente = c;
    }

    public Continente getContinente(){
        return continente;
    }


    public ArrayList<Territorio> getFronteirasInimigas(){
        ArrayList<Territorio> inimigos = new ArrayList<Territorio>();
        for(Territorio t: fronteiras){
            if(!Territorio.mesmoDono(t, this)){
                inimigos.add(t);
            }
        }
        return inimigos;
    }

    public ArrayList<Territorio> getFronteirasAmigas(){
        ArrayList<Territorio> amigos = new ArrayList<Territorio>();
        for(Territorio t: fronteiras){
            if(Territorio.mesmoDono(t, this)){
                amigos.add(t);
            }
        }
        return amigos;
    }

    public static boolean mesmoDono(Territorio t1, Territorio t2){
        return t1.getDono().equals(t2.getDono());
    }
}
