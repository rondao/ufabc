package War;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class Jogador implements Serializable {

    protected ArrayList<Territorio> territorios;
    protected String nome;
    protected Color cor;
    protected ArrayList<Continente> totalidades;
    protected ArrayList<Carta> cartas;
    protected ArrayList<Jogador> jogadoresEliminados;
    protected Objetivo objetivo;
    protected double pontuacao;

    /**
     * Cria um novo jogador, recebendo uma String para o seu nome e uma cor
     * @param nome Nome do jogador
     */
    public Jogador(String nome, Color c){
        this.nome = nome;
        this.cor = c;
        territorios = new ArrayList<Territorio>();
        this.totalidades = new ArrayList<Continente>();
        jogadoresEliminados = new ArrayList<Jogador>();
        cartas = new ArrayList<Carta>();
    }

    /**
     *
     * @return String com o nome do jogador
     */
    public String getNome(){
        return this.nome;
    }

    public Objetivo getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(Objetivo objetivo) {
        this.objetivo = objetivo;
    }

    /**
     * @param t Território a ser adicionado às posses deste jogador
     */
    public void adicionaTerritorio(Territorio t){
        this.territorios.add(t);
    }

    /**
     * @param t Território a ser removido dos territórios deste jogador
     */
    public void removeTerritorio(Territorio t){
        this.territorios.remove(t);
    }

    /**
     * Verifica quais Continentes este jogador possui.
     * @param todosContinentes Os continentes do Mapa.
     * @return As totalidades.
     */
    public ArrayList<Continente> verificaTotalidades(ArrayList<Continente> todosContinentes){
        for(Continente c: todosContinentes){
            if(this.territorios.containsAll(c.getTerritorios())){
                // Se já tiver a totalidade, ignore
                if (this.totalidades.contains(c)) {
                    continue;
                }
                this.totalidades.add(c);
            }else{
                this.totalidades.remove(c);
            }
        }
        return this.totalidades;
    }

public double pontuacao(Mapa m) {

        /**
         * Regras de pontuação:
         *
         * Para cada território controlado, 2 pontos
         *
         * Para cada peça no jogo, 1 ponto
         *
         * Para controle de continente:
         * até 25% - 0 pts
         * de 25% a 49% - 3 pts
         * de 50% a 74% - 7 pts
         * de 75% a 99% - 9 pts
         * 100% - 15pts
         * isso para cada continente controlado/parcialmente controlado
         *
         *
         */
        pontuacao = 0;

        //pra cada território controlado, 2 pontos
        pontuacao += 2 * territorios.size();

        //pra cada peça no jogo, 1 ponto
        for (Territorio t : territorios) {
            pontuacao += t.getPecas();
        }

        for (Continente c : m.getContinentes()) {


            if (getPctControlada(c) >= 0.25 && getPctControlada(c) < 0.5) {
                //Se for meu objetivo possuir aquele continente, a pontuação é maior
                if (objetivo instanceof ObjetivoContinente) {
                    if (((ObjetivoContinente) objetivo).contem(c)) {
                        pontuacao += 10;
                    }
                } else {
                    pontuacao += 3;
                }

            } else if (getPctControlada(c) < 0.75) {
                if (objetivo instanceof ObjetivoContinente) {
                    if (((ObjetivoContinente) objetivo).contem(c)) {
                        pontuacao += 15;
                    }
                } else {
                    pontuacao += 7;
                }


            } else if (getPctControlada(c) < 1.0) {
                if (objetivo instanceof ObjetivoContinente) {
                    if (((ObjetivoContinente) objetivo).contem(c)) {
                        pontuacao += 25;
                    }
                } else {
                    pontuacao += 10;
                }
            } else {

                if (objetivo instanceof ObjetivoContinente) {
                    if (((ObjetivoContinente) objetivo).contem(c)) {
                        pontuacao += 35;
                    }
                } else {
                    pontuacao += 15;
                }
            }

        }

        return pontuacao;
    }

    public double ptsUtilidadeTerritorioObjetivo(Territorio t) {
        if (objetivo instanceof ObjetivoContinente) {
            if (((ObjetivoContinente) objetivo).contem(t)) {
                return 1;
            }
        }
        return 0;
    }

    public double ptsUtilidadeAmigosFronteira(Territorio t) {
        ArrayList<Territorio> amigos = new ArrayList<Territorio>();
        ArrayList<Territorio> inimigos = new ArrayList<Territorio>();

        for (Territorio front : t.getFronteiras()) {
            for (Territorio front2 : front.getFronteiras()) {
                // Ve se a fronteira da fronteira e amigo ou inimigo.
                if (Territorio.mesmoDono(front2, t)) {
                    if (( ! amigos.contains(front2)) && (front2 != t))
                        amigos.add(front2);
                } else {
                    if (( ! inimigos.contains(front2)) && (front2 != t)) {
                        inimigos.add(front2);
                    }
                }
            }
        }

        return ((double) amigos.size()) / ((double) (amigos.size() + inimigos.size()));
    }

    /**
     * Quanto mais pecas ja tiver no territorio, menos eu preciso por.
     * @param territorio
     * @return Valor entre [0,1] considerando 8 o numero max.
     */
    public double ptsUtilidadeNumeroDePecas(Territorio t) {
        return ((float) t.getPecas()) / 8.0f;
    }

    /** Retorna as totalidades deste jogador */
    public ArrayList<Continente> getTotalidades() {
        return totalidades;
    }

    /**Retorna os Territórios deste jogador */
    public ArrayList<Territorio> getTerritorios() {
        return territorios;
    }

    /**
     * Verifica se o jogador contem um dado Terrotorio.
     * @param T O Territorio a ser avaliado.
     * @return Se contem ou não.
     */
    public boolean contemTerritorio(Territorio T) {
        return territorios.contains(T);
    }

    /**
     * Remove a carta do seu grupo de cartas
     * @param c Carta a ser removida
     */
    public void removerCarta(Carta c) {
        cartas.remove(c);
    }

    /**
     * Adiciona uma carta à mão deste jogador. Se ele já tiver 5 cartas, deverá
     * descartar uma.
     * @param c
     */
    public void adicionaCarta(Carta c){
        if(this.cartas.size() >=5){
          cartas.remove(0);
        }
        cartas.add(c);
    }

    /** Retorna a sua cor */
    public Color getCor() {
        return cor;
    }

    /** Retorna suas Cartas */
    public ArrayList<Carta> getCartas() {
        return cartas;
    }

    public double getPctControlada(Continente c){
        double contTerritorios = 0;
        double totalTerritorios = 0;

        for(Territorio t: c.getTerritorios()){
            if(this.territorios.contains(t)){
                contTerritorios++;
            }
            totalTerritorios++;
        }
        return contTerritorios / totalTerritorios;
    }

    public boolean equals(Jogador j){
        return this.nome.equals(j.getNome());
    }
}
