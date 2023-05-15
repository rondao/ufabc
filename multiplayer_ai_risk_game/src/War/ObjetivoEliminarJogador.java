package War;

/**
 * Representa um Objetivo de Eliminar um jogador.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class ObjetivoEliminarJogador extends Objetivo {

    private Jogador aEliminar;

    public ObjetivoEliminarJogador(String descricao, Jogador aEliminar) {
        super(descricao);
        this.aEliminar = aEliminar;
    }

    @Override
    public boolean concluido(Jogador jogador) {
        return false; //jogador.getJogadoresEliminados().contains(aEliminar);
    }

    /**
     *
     * @return O jogador que deve ser eliminado
     */
    public Jogador getAEliminar(){
        return aEliminar;
    }
}
