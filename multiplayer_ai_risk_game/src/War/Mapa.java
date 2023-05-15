package War;

import java.io.Serializable;
import java.util.ArrayList;

public class Mapa implements Serializable {

    private ArrayList<Territorio> territorios;
    private ArrayList<Continente> continentes;

    public Mapa() {
        this.territorios = new ArrayList<Territorio>();
        this.continentes = new ArrayList<Continente>();
    }

    /**
     * Adiciona um território a este mapa
     * @param t Territorio a ser adicionado no mapa
     */
    public void adicionaTerritorio(Territorio t) {
        this.territorios.add(t);
    }

    /**
     *
     * @return ArrayList com os territórios contidos neste mapa
     */
    public ArrayList<Territorio> getTerritorios() {
        return this.territorios;
    }

    /**
     * Recebe um território e um continente e cria uma relação entre eles
     * @param t Território a ser adicionado no continente
     * @param c Continente a receber território
     */
    public void defineContinente(Territorio t, Continente c) {
        c.adicionaTerritorio(t);

    }

    /**
     * Dado um 'Território' este é buscado no 'Mapa'
     * Se for encontrado este é retornado.
     * @param nome Nome do 'Território'.
     * @return A referência para o 'Território' do Mapa'
     */
    public Territorio buscarTerritorio(String nome) {
        // Buscando em cada 'Territorio' qual possui o nome.
        for (Territorio t : territorios) {
            if (t.getNome().equals(nome)) {
                return t;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        String lista = "Lista de territórios e suas fronteiras:\n\n";

        for (Territorio t : territorios) {
            lista += String.format("Território: %s \n\tFronteiras: \n%s\n\n", t.getNome(), t.getStringFronteiras());
        }

        return lista;
    }

    /**
     *
     * @return Os continentes do mapa
     */
    public ArrayList<Continente> getContinentes(){
        return this.continentes;
    }
}

