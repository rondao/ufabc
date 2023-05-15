package WarCli;

import War.*;
import WarGUI.TerImageSprite;
import WarGUI.TerLabel;
import WarRede.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;
import pulpcore.sprite.Label;

public class CliJogo {

    /** Possíveis estados para o jogo */
    public enum Estado {

        TrocaDeCartas,
        AdicionarExercitos,
        Atacar,
        MovimentarExercitos,
        Aguardar;
    }
    /** Servidor que irá ouvir as conexões e gerenciá-las. */
    private Cliente cli;
    /** Mapa com todos os territórios e continentes do jogo */
    private Mapa mapa;
    /** O baralho do jogo */
    private ArrayList<Carta> baralho;
    /** As Cartas que já foram trocadas */
    private ArrayList<Carta> baralhoDescarte;
    /** O objeto deste jogador */
    private Jogador jogador;
    /** Estado atual do jogo */
    private Estado estadoJogo;
    /** Receberá carta ao final de seu turno? */
    private boolean receberCarta = false;
    /** Controla se o 'Jogador' está jogando. */
    private boolean jogando = false;
    /** Número de exércitos disponíveis para serem adicionados */
    private int pecasLivres = 0;
    /** Número de exércitos ganhos pela troca */
    private int exercitosTroca = 4;
    /** Território selecionado */
    private Territorio tSelecionado;
    private ArrayList<TerImageSprite> iPecas;
    private ArrayList<TerLabel> lPecas;
    private Label lPecasLivres;
    private Label lEstado;

    // RNAs para treinar os eventos de jogo
    RNA rnaAddEx;
    RNA rnaAtaque;
    RNA rnaMovimento;

    /**
     * Repassa os parâmetros para o Objeto 'cli'.
     * Conecta o Cliente, e carraga o mapa do jogo.
     * Deixa o Cliente pronto para iniciar o jogo.
     *
     * @param host Endereço do Servidor
     * @param porta Porta que o Servidor está ouvindo
     * @param nome Nome deste Jogador
     * @param guiLabels Labels da GUI para criar o HashMap com os Territórios
     */
    public CliJogo(String host, int porta, String nome)
            throws UnknownHostException, IOException {

        this.cli = new Cliente(host, porta, nome);

        // ------------ TESTE --------------
        System.out.println("Lendo Mapa, Baralho e Jogador...");
        // ------------ TESTE --------------

        cli.iniciarProdutor();
        

        // Carregando os recursos necessários para o jogo iniciar.
        // O Mapa completo, O baralho com todas as cartas e o jogador.
        while (jogador == null || mapa == null || baralho == null) {
            Object obj = cli.lerObjeto();
            analisarMsg(obj);
        }

        // Verificando quais territorios este jogador possui.
        for (Territorio T : mapa.getTerritorios()) {
            if (T.getDono() == jogador) {
                T.setDono(jogador);
            }
        }

        // ------------ TESTE --------------
        System.out.println("Mapa, Baralho e Jogador Lido!");
        // ------------ TESTE --------------

        // Após ter carregado o início do jogo.
        // Iniciamos as Threads Produtor/Consumidor
        cli.iniciarConsumidor();

        //Thread responsável por ficar analizando as mensagens vindas do servidor
        Thread analisadora = new Thread(new Runnable() {

            public void run() {
                while (true) {
                    try {
                        Object o = cli.getFilaIn().retira();
                        analisarMsg(o);
                    } catch (InterruptedException ex) {
                        System.err.println(ex.toString());
                    }
                }
            }
        });
        analisadora.start();

        // Contruindo as RNA.
        double neta = 0.5;
        
        double pesosAddEx[] = {0.1, 0.1, 0.1, 0.1};
        rnaAddEx = new RNA(pesosAddEx.clone() , neta, 0.1);
        double pesosAtaque[] = {0.1, 0.1, 0.1};
        rnaAtaque = new RNA(pesosAtaque.clone(), neta, 0.1);
        double pesosMovimento[] = {0.1, 0.1};
        rnaMovimento = new RNA(pesosMovimento.clone(), neta, 0.1);
    }

    /**
     * Recebe o 'Território' selecionado na GUI.
     * Este é trabalhado para realizar os eventos de jogo.
     * @param tSel
     * @return Sinalização para a GUI.
     *   True: Desselecionar os botões da GUI.
     *   False: Não tirar a Seleção.
     */
    public boolean selecionarTerritorio(Territorio tSel) {

        // Se o estado do jogo for para adicionar exércitos.
        // Apenas um 'Território' selecionado já basta.
        // Ele precisa ter 'pecasLivres'.
        // E 'Território' deve ser deste jogador.
        if (getEstadoJogo() == Estado.AdicionarExercitos &&
                tSel.getDono().getCor().getRGB() == jogador.getCor().getRGB()) {

            // Se não tiver pecas para por, cancelar.
            if (pecasLivres <= 0) {
                return false;
            }

            atualizarPeca(tSel, tSel.getPecas() + 1);
            setPecasLivres(getPecasLivres() - 1);

            lPecasLivres.setText(String.format("%2d", getPecasLivres()));

            // Guardar Evento de por Exercito na RNA.
            double dado[] = {jogador.ptsUtilidadeTerritorioObjetivo(tSel),
                             jogador.getPctControlada(tSel.getContinente()),
                             jogador.ptsUtilidadeNumeroDePecas(tSel),
                             jogador.ptsUtilidadeAmigosFronteira(tSel)};
            rnaAddEx.addDado(dado.clone());
            // Sucesso. Não é preciso manter o 'Território' selecionado.
            return false;
        }

        // Se um primeiro 'Território' não tiver sido selecionado.
        if (tSelecionado == null) {
            // Se o dono for este jogador.////////////DEFORMACAO OBJ JOGADOR!!!
            if (tSel.getDono().getCor().getRGB() == jogador.getCor().getRGB()) {
                // Este será a seleção1.
                tSelecionado = tSel;
                // Para manter a seleção.
                return true;
            } else {
                // Se não for meu, nada a fazer.
                return false;
            }
        }

        // Se o segunto 'Território' selecionado for o mesmo que o primeiro,
        // desselecionar o primeiro 'Território'.
        if (tSelecionado == tSel) {
            tSelecionado = null;

            // Desselecionar
            return false;
        }

        // Agora se um 'Território' já estiver selecionado,
        // selecionar um segundo irá executar um evento de jogo.

        // Para um evento ser possível, 'tSelecionado' e 'tSel'
        // devem fazer fronteira.
        // Se isto não ocorrer, cancelar a operação.
        if (tSelecionado != null) {
            if (!tSelecionado.fazFronteira(tSel)) {

                return false;
            }
        }

        // Agora dependendo do estado do jogo teremos um evento diferente.

        // Movimentação.
        // O segundo 'Território' deve ser deste jogador
        if (getEstadoJogo() == Estado.MovimentarExercitos &&
                tSel.getDono().getCor().getRGB() == jogador.getCor().getRGB()) {
            // Se tiver apenas o exército de ocupação, não pode mover
            if (tSelecionado.getPecas() <= 1) {
                return false;
            }
            // Primeiro 'Território' perde uma peça.
            atualizarPeca(tSelecionado, tSelecionado.getPecas() - 1);
            // Segundo 'Território' ganha uma peça.
            atualizarPeca(tSel, tSel.getPecas() + 1);

            // Guardar evento de Movimentacao na RNA.
            double dado[] = {tSel.getFronteirasInimigas().size() / tSel.getFronteiras().size(),
                             tSel.getPecas()/ 6.0f};
            rnaMovimento.addDado(dado);
            // Desselecionar 'Territórios'.
            return false;
        }

        // Ataque
        // Para ocorrer um ataque o dono do 'Território'
        // deve ser inimigo.
        if (getEstadoJogo() == Estado.Atacar &&
                tSel.getDono().getCor().getRGB() != jogador.getCor().getRGB()) {
            // Se o 'Território' tiver apenas o exército de ocupação
            // não pode efetuar um ataque
            if (tSelecionado.getPecas() == 1) {
                return false;
            }

            // Realiza um ataque. (Ter Ataque, Ter Defesa)
            Ataque ataque = new Ataque(tSelecionado, tSel);
            ataque.realizaAtaque();

            // Se a defesa perdeu todos as peças
            // Este 'Território' foi conquistado
            if (tSel.getPecas() - ataque.getPerdidosD() == 0) {
                // O Ataque ficara com as peças que tinha
                // subtraido das que usou para atacar.
                atualizarPeca(tSelecionado,
                        tSelecionado.getPecas() - ataque.getExA());

                // A Defesa ficara com as peças que foram usadas no ataque
                // subtraidas das que foram perdidas.
                atualizarPeca(tSel, ataque.getExA() - ataque.getPerdidosA());

                // Agora o 'Território' da Defesa foi conquistado
                // por este jogador
                atualizarDono(tSel);

                // Como o 'Território' foi conquistado
                // O jogador poderá comprar uma carta
                // receberCarta = true;
            } else {
                // Caso a Defesa sobreviva, basta atualizar
                // o resultado das peças.
                // Atualizando o Ataque
                atualizarPeca(tSelecionado,
                        tSelecionado.getPecas() - ataque.getPerdidosA());

                // Atualizando a Defesa
                atualizarPeca(tSel,
                        tSel.getPecas() - ataque.getPerdidosD());
            }
            // Guardando evento de ataque na RNA.
            double dado[] = {ataque.ptsPorPctControlada(),
                             ataque.ptsPorConquistaDeTerritorioDoObjetivo(),
                             (ataque.getExA() - 1.5*ataque.getExsD()) / (ataque.getExA() - 1.5f)};
            rnaAtaque.addDado(dado);
        }

        // Desselecionando
        return false;
    }

    /** Analisa a 'Mensagem' recebida do Servidor */
    private void analisarMsg(Object obj) {

        //----
        //MAPA
        //----
        //Se a mensagem for um objeto mapa, o servidor quer que todos atualizem seus mapas
        if (obj instanceof Mapa) {
            this.mapa = (Mapa) obj;
        } else //----
        //JOGADOR
        //----
        // Se a mensagem for um objeto Jogador, o servidor
        // quer informar ao cliente quem é seu jogador.
        if (obj instanceof Jogador) {
            this.jogador = (Jogador) obj;
        } else //
        //----
        //BARALHO
        //----
        //Se for um ArrayList, é o baralho.
        if (obj instanceof ArrayList) {
            this.setBaralho((ArrayList<Carta>) obj);
        } else //----
        //MENSAGEM
        //----
        {
            ((Mensagem) obj).analisar(this);
        }


    }

    /**
     * Verifica se 'cartas' satisfazem uma troca.
     * @param cartas As três cartas as serem trocadas.
     */
    private boolean verificarCartas(ArrayList<Carta> cartas) {

        // Simbolo das cartas do jogador
        ArrayList<Integer> simbolos = new ArrayList<Integer>();

        // Pegando os Simbolos das cartas
        for (Carta c : cartas) {
            int s = c.getSimbolo();
            // O Curinga sempre faz combinação, então ignore.
            if (s == Carta.CURINGA) {
                continue;
            } else {
                simbolos.add(new Integer(s));
            }
        }

        /* Checando uma combinação com os três simbolos diferentes */
        try {
            // Espaço auxiliar para poder comparar os elementos de 'simbolos'.
            ArrayList<Integer> auxList = new ArrayList<Integer>();

            // Comparando os elementos de 'simbolos', com todos os de 'auxList'.
            for (Integer s : simbolos) {
                for (Integer aux : auxList) {
                    // Se for igual, não é uma combinação válida.
                    if (s == aux) {
                        throw new Exception();
                    }
                }
                // Se não for encontrado, adicionar este na lista.
                auxList.add(s);
            }
            // Se chegar aqui, a troca é válida
            return true;
        } catch (Exception ex) {
            // Apenas para a comparação.
        }

        /* Checando uma combinação com os três simbolos iguais. */
        try {
            // Espaço auxiliar para poder comparar os elementos de 'simbolos'.
            ArrayList<Integer> auxList = new ArrayList<Integer>();

            // Comparando os elementos de 'simbolos', com todos os de 'auxList'.
            for (Integer s : simbolos) {
                for (Integer aux : auxList) {
                    // Se for diferente, não é uma combinação válida.
                    if (s != aux) {
                        throw new Exception();
                    }
                }
                // Se não for encontrado, adicionar este na lista.
                auxList.add(s);
            }
            // Se chegar aqui, a troca é válida
            return true;
        } catch (Exception ex) {
            // Apenas para a comparação
        }

        // Se chegar aqui, a troca é inválida
        return false;
    }

    /**
     * Calcula as totalidades do jogador.
     * Calcula o número de exércitos a serem adicionados.
     */
    public void iniciarTurno() {
        // Antes de tudo, treinar a RNA.
        rnaAddEx.treinar(jogador.pontuacao(mapa));
        rnaAtaque.treinar(jogador.pontuacao(mapa));
        rnaMovimento.treinar(jogador.pontuacao(mapa));

        JOptionPane.showMessageDialog(null, "pontuacao: " + rnaAddEx.getPontuacao() + "\n" +
                                    "pontuacaoFinal: " + jogador.pontuacao(mapa) + "\n" +
                                    "yd: " + ((jogador.pontuacao(mapa) > rnaAddEx.getPontuacao()) ? 1 : -1));

        // Pegando a pontuacao para as jogadas atuais.
        rnaAddEx.setPontuacao(jogador.pontuacao(mapa));
        rnaAtaque.setPontuacao(jogador.pontuacao(mapa));
        rnaMovimento.setPontuacao(jogador.pontuacao(mapa));

        // Mostrando os pesos das RNA.
        rnaAddEx.showPesos("RNA Adicionar Exercitos\n");
        rnaAtaque.showPesos("RNA Ataque\n");
        rnaMovimento.showPesos("RNA Movimento\n");

        // Aviso Inicio do Turno
        JOptionPane.showMessageDialog(null, "Seu turno.", "Aviso", JOptionPane.INFORMATION_MESSAGE);

        // Recebe quantos continentes o jogador tem conquistado.
        ArrayList<Continente> totalidades = jogador.verificaTotalidades(mapa.getContinentes());
        
        // Número de peças livres é metade dos seus territórios.
        pecasLivres = jogador.getTerritorios().size() / 2;

        for (Continente c : totalidades) {
            pecasLivres += c.getPecasPorRodada();
        }

        // Setando o texto na GUI.
        lPecasLivres.setText(String.valueOf(pecasLivres));

        jogando = true;
        definirEstado(Estado.AdicionarExercitos);
    }

    /**
     * Finaliza o turno do jogador.
     * Distribui uma carta a este jogador, se fizer jus a ela.
     * Recalcula as totalidades do jogador.
     * Verifica se este jogador cumpriu seu objetivo.
     */
    public void finalizarTurno() {
        // Se merecer receber uma 'Carta'.
        if (receberCarta) {
            // Pegando a primeira carta do baralho.
            jogador.adicionaCarta(getBaralho().get(0));
            JOptionPane.showMessageDialog(null, "Recebeu a carta: " + getBaralho().get(0).getPais().getNome());////
            // false = não é uma troca.
            atualizarCarta(getBaralho().get(0), false);
            // Ja recebeu a carta.
            receberCarta = false;
        }

        jogador.verificaTotalidades(mapa.getContinentes());

        if (jogador.getObjetivo().concluido(jogador)) {
            JOptionPane.showMessageDialog(null, "Venceu!");
        }

        // Passando a vez.
        MsgVez mensagem = new MsgVez("O Jogador " + jogador.getNome() +
                " terminou sua rodada.", jogador);
        try {
            // Enviando mensagem para o Servidor.
            cli.getFilaOut().insere(mensagem);
        } catch (InterruptedException ex) {
            System.err.println(ex.toString());
        }

        // Não é mais o turno deste jogador
        jogando = false;
        definirEstado(Estado.Aguardar);
    }

    /**
     * Realiza a troca das 'cartas' por exércitos.
     * @param cartas As três cartas a serem trocadas
     */
    public void trocarCartas(ArrayList<Carta> cartas) {

        // Se a troca for inválida (false), já encerra o método.
        if (!verificarCartas(cartas)) {
            return;
        }

        for (Carta c : cartas) {
            // Se o 'jogador' conter o território da 'carta'.
            if (jogador.contemTerritorio(c.getPais())) {
                // Ganhará 2 exércitos neste.
                atualizarPeca(c.getPais(), c.getPais().getPecas() + 2);
            }
            // Remove a carta deste jogador
            jogador.removerCarta(c);

            // Atualizando a troca da Carta para todos.
            atualizarCarta(c, true);
        }

        setPecasLivres(getPecasLivres() + exercitosTroca);

        // Aumentando o número de peças na troca.
        if (exercitosTroca >= 12) {
            setExercitosTroca(exercitosTroca + 5);
        } else {
            setExercitosTroca(exercitosTroca + 2);
        }
        atualizarTroca(exercitosTroca);

        // Será preciso re-embaralhar o baralho.
        if (getBaralho().isEmpty()) {
            // Gera um Seed para que todos os jogadores embaralhem igual.
            long seed = new Random().nextLong();

            atualizarBaralho(seed);
        }

    }

    /**
     * Cria uma mensagem para o Servidor sobre o número de pecas
     * de um certo 'Território' que foi alterado.
     * @param territorio 'Território' a ser atualizado.
     * @param pecas Novo número de peças deste 'Território'.
     */
    private void atualizarPeca(Territorio territorio, int pecas) {

        MsgPeca mensagem = new MsgPeca("Territorio: " + territorio.getNome() +
                " possui: " + pecas + " pecas.", territorio, pecas);
        try {
            // Enviando mensagem para o Servidor.
            cli.getFilaOut().insere(mensagem);
        } catch (InterruptedException ex) {
            System.err.println(ex.toString());
        }

    }

    /**
     * Cria uma mensagem para o Servidor sobre o novo dono
     * de um certo 'Território' que foi alterado.
     * @param territorio 'Território' a ser atualizado.
     * @param jogador Novo dono deste 'Território'.
     */
    private void atualizarDono(Territorio territorio) {

        MsgDono mensagem = new MsgDono("Territorio: " + territorio.getNome() +
                " foi conquistado por: " + jogador.getNome(),
                territorio, jogador);

        // Enviando mensagem para o Servidor.
        try {
            cli.getFilaOut().insere(mensagem);
        } catch (InterruptedException ex) {
            System.err.println(ex.toString());
        }
    }

    /**
     * Cria uma mensagem para o Servidor sobre o novo número
     * de exércitos em uma troca.
     * @param troca Novo número de peças na troca.
     */
    private void atualizarTroca(int troca) {

        MsgTroca mensagem = new MsgTroca("Novo numero de pecas" +
                "recebidos na troca e: " + troca, troca);

        // Enviando mensagem para o Servidor.
        try {
            cli.getFilaOut().insere(mensagem);
        } catch (InterruptedException ex) {
            System.err.println(ex.toString());
        }
    }

    /**
     * Cria uma mensagem para o Servidor pois uma carta for retirada
     * e está fora do baralho.
     * @param carta A 'carta' retirada.
     * @param troca Se a 'carta' foi trocada.
     */
    private void atualizarCarta(Carta carta, boolean troca) {

        MsgCarta mensagem = new MsgCarta("A carta de pais: " +
                carta.getPais().getNome() + "foi trocada.", carta, troca);

        // Enviando mensagem para o Servidor.
        try {
            cli.getFilaOut().insere(mensagem);
        } catch (InterruptedException ex) {
            System.err.println(ex.toString());
        }
    }

    /**
     * Cria uma mensagem para o Servidor para embaralhar as
     * cartas do jogo no 'seed' dado.
     * @param seed Em que ordem embaralhar as cartas.
     */
    private void atualizarBaralho(long seed) {

        MsgEmbaralhar mensagem = new MsgEmbaralhar("O baralho foi reordenado.",
                seed);

        // Enviando mensagem para o Servidor.
        try {
            cli.getFilaOut().insere(mensagem);
        } catch (InterruptedException ex) {
            System.err.println(ex.toString());
        }
    }

    /**
     * Jogador perdeu a partida e será retirado.
     */
    public void sairJogo() {
        MsgJogadorMorto mensagem = new MsgJogadorMorto("O Jogador " +
                jogador.getNome() + " saiu do jogo", jogador);

        // Enviando mensagem para o Servidor.
        try {
            cli.getFilaOut().insere(mensagem);
        } catch (InterruptedException ex) {
            System.err.println(ex.toString());
        }
    }

    public void setIPecas(ArrayList<TerImageSprite> iPecas) {
        this.iPecas = iPecas;
    }

    public void setLPecas(ArrayList<TerLabel> lPecas) {
        this.lPecas = lPecas;
    }

    public void setLPecasLivres(Label lPecasLivres) {
        this.lPecasLivres = lPecasLivres;
    }

    /** Altera o estado atual do jogo */
    public void definirEstado(Estado novoEstado) {
        estadoJogo = novoEstado;
        // Se não estiver jogando deve mostrar para esperar
        if (jogando) {
            lEstado.setText("Estado: " + novoEstado.toString());
        } else {
            lEstado.setText("Estado: Aguarde sua vez...");
        }
    }

    public Mapa getMapa() {
        return mapa;
    }

    public void setJogando(boolean jogando) {
        this.jogando = jogando;
    }

    public boolean getJogando() {
        return jogando;
    }

    public Jogador getJogador() {
        return jogador;
    }

    /**
     * @return the estadoJogo
     */
    public Estado getEstadoJogo() {
        return estadoJogo;
    }

    /**
     * @return the pecasLivres
     */
    public int getPecasLivres() {
        return pecasLivres;
    }

    /**
     * @param pecasLivres the pecasLivres to set
     */
    public void setPecasLivres(int pecasLivres) {
        this.pecasLivres = pecasLivres;
    }

    public void setLEstado(Label lEstado) {
        this.lEstado = lEstado;
    }

    /**
     * @return the lPecas
     */
    public ArrayList<TerLabel> getLPecas() {
        return lPecas;
    }

    /**
     * @return the baralho
     */
    public ArrayList<Carta> getBaralho() {
        return baralho;
    }

    /**
     * @return the baralhoDescarte
     */
    public ArrayList<Carta> getBaralhoDescarte() {
        return baralhoDescarte;
    }

    /**
     * @return the iPecas
     */
    public ArrayList<TerImageSprite> getIPecas() {
        return iPecas;
    }

    /**
     * @param exercitosTroca the exercitosTroca to set
     */
    public void setExercitosTroca(int exercitosTroca) {
        this.exercitosTroca = exercitosTroca;
    }

    /**
     * @param baralho the baralho to set
     */
    public void setBaralho(ArrayList<Carta> baralho) {
        this.baralho = baralho;
    }

    /**
     * @param baralhoDescarte the baralhoDescarte to set
     */
    public void setBaralhoDescarte(ArrayList<Carta> baralhoDescarte) {
        this.baralhoDescarte = baralhoDescarte;
    }
}
