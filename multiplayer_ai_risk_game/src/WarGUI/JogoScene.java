package WarGUI;

import War.Territorio;
import WarCli.CliJogo;
import java.util.ArrayList;
import pulpcore.image.Colors;
import pulpcore.image.CoreFont;
import pulpcore.image.CoreImage;
import pulpcore.scene.Scene2D;
import pulpcore.sprite.Button;
import pulpcore.sprite.Group;
import pulpcore.sprite.ImageSprite;
import pulpcore.sprite.Label;
import pulpcore.sprite.ScrollPane;

public class JogoScene extends Scene2D {

    /** Todos os botões de territorios */
    private ArrayList<TerButton> bTerritorios;
    /** Todos os icones de peças.
     * Serão alterados dependendo do dono do territorio */
    private ArrayList<TerImageSprite> iPecas;
    /** Todos os Labels que indicam quantas peças tem no Territorio */
    private ArrayList<TerLabel> lPecas;
    /** O Engine do jogo do cliente. Comunicação com o Servidor */
    private CliJogo cJogo;
    
    // Botões para alterar o estado do jogo
    private Button bTrocar;
    private Button bAtacar;
    private Button bMover;
    private Button bPassar;
    /** Fonte 'arial' */
    private CoreFont fArial;

    // Icone de peças livres.
    private ImageSprite iPecasLivres;
    private Label lPecasLivres;

    // Label com o estado do jogo
    private Label lEstado;

    // Indica se este é o primeiro
    private boolean primeiroTurno = true;

    public JogoScene(CliJogo cJogo) {
        this.cJogo = cJogo;

        bTerritorios = new ArrayList<TerButton>();
        iPecas = new ArrayList<TerImageSprite>();
        lPecas = new ArrayList<TerLabel>();
    }

    @Override
    public void load() {
        super.load();

        fArial = CoreFont.load("arial");
        fArial = fArial.tint(Colors.WHITE);

        add(new ImageSprite("bgMundo.jpg", 0, 0));
        add(new ImageSprite("MenuJogo.jpg", 0, 450));

        criarBTerritorios();
        for(TerButton tb: bTerritorios) {
            add(tb);
        }

        criarPecas();
        associarTerritorios();
        for(TerImageSprite tis: iPecas) {
            add(tis);
        }

        criarLPecas();
        for (Label l : lPecas) {
            add(l);
        }

        cJogo.setIPecas(iPecas);
        cJogo.setLPecas(lPecas);

        CoreImage[] ci = new CoreImage[6];
        ci[0] = CoreImage.load("btnTurno.png");      // [0] = posição normal
        ci[1] = CoreImage.load("btnMouseTurno.png"); // [1] = mouse over
        ci[2] = CoreImage.load("btnPressTurno.png"); // [2] = mouse press

        bTrocar = Button.createLabeledButton(ci, fArial, "Trocar Cartas", 10, 460);
        bAtacar = Button.createLabeledButton(ci, fArial, "Atacar", 135, 460);
        bMover = Button.createLabeledButton(ci, fArial, "Movimentacao", 260, 460);
        bPassar = Button.createLabeledButton(ci, fArial, "Passar vez", 385, 460);

        add(bTrocar);
        add(bAtacar);
        add(bMover);
        add(bPassar);

        add(new ImageSprite("txtJogador-CriaJog.png", 90, 540, 700, 42));
        add(new Label(fArial, "Objetivo:   " + cJogo.getJogador().getObjetivo().toString(), 65, 553));

        iPecasLivres = new ImageSprite(TerImageSprite.pegarAsset(cJogo.getJogador().getCor()), 750, 470);
        lPecasLivres = new Label(fArial, "0", iPecasLivres.getViewX() + 3, iPecasLivres.getViewY() + 5);

        add(iPecasLivres);
        add(lPecasLivres);

        add(new Label(fArial, "Exercitos livres", lPecasLivres.getViewX() - 120, lPecasLivres.getViewY()));

        lEstado = new Label(fArial, "", 550, 510);
        add(lEstado);

        // lLog = new Label(fArial, "Sistema de Log\nLog1\nLog2\nLog3\nLog4\nLog5\nLog6", 0, 0);
        // spLog = new ScrollPane(100, 100, 500, 260, 1, 1);
        // spLog.add(lLog);
        // add(spLog);

        cJogo.setLPecasLivres(lPecasLivres);
        cJogo.setLEstado(lEstado);

        prepararGUI();
    }

    private void criarLPecas() {
        for (TerImageSprite i : iPecas) {
            lPecas.add(new TerLabel(fArial, "1", i.getViewX() + 3,
                    i.getViewY() + 5, i.territorio));
        }
    }

    /** Prepara a GUI para se adequar com o 'Mapa' */
    private void prepararGUI() {
        for (Territorio t : cJogo.getMapa().getTerritorios()) {
            for (int i = 0; i < iPecas.size(); i++) {
                if (iPecas.get(i).territorio == t) {
                    iPecas.get(i).setImage(TerImageSprite.pegarAsset(t.getDono().getCor()));
                }
            }
        }
        // Preparando o estado.
        cJogo.definirEstado(CliJogo.Estado.AdicionarExercitos);
    }

    private void criarBTerritorios() {
        CoreImage[] ci = new CoreImage[6];

        ci[0] = CoreImage.load("Dudinka.png");
        ci[1] = CoreImage.load("Dudinka.png");
        ci[2] = CoreImage.load("sDudinka.png");
        ci[3] = CoreImage.load("sDudinka.png");
        ci[4] = CoreImage.load("sDudinka.png");
        ci[5] = CoreImage.load("sDudinka.png");
        bTerritorios.add(new TerButton(ci, 498, 34, true,
                cJogo.getMapa().buscarTerritorio("Dudinka")));

        ci[0] = CoreImage.load("Australia.png");
        ci[1] = CoreImage.load("Australia.png");
        ci[2] = CoreImage.load("sAustralia.png");
        ci[3] = CoreImage.load("sAustralia.png");
        ci[4] = CoreImage.load("sAustralia.png");
        ci[5] = CoreImage.load("sAustralia.png");
        bTerritorios.add(new TerButton(ci, 592, 306, true,
                cJogo.getMapa().buscarTerritorio("Australia")));

        ci[0] = CoreImage.load("Japao.png");
        ci[1] = CoreImage.load("Japao.png");
        ci[2] = CoreImage.load("sJapao.png");
        ci[3] = CoreImage.load("sJapao.png");
        ci[4] = CoreImage.load("sJapao.png");
        ci[5] = CoreImage.load("sJapao.png");
        bTerritorios.add(new TerButton(ci, 727, 142, true,
                cJogo.getMapa().buscarTerritorio("Japao")));

        ci[0] = CoreImage.load("Mongolia.png");
        ci[1] = CoreImage.load("Mongolia.png");
        ci[2] = CoreImage.load("sMongolia.png");
        ci[3] = CoreImage.load("sMongolia.png");
        ci[4] = CoreImage.load("sMongolia.png");
        ci[5] = CoreImage.load("sMongolia.png");
        bTerritorios.add(new TerButton(ci, 566, 126, true,
                cJogo.getMapa().buscarTerritorio("Mongolia")));

        ci[0] = CoreImage.load("Vancouver.png");
        ci[1] = CoreImage.load("Vancouver.png");
        ci[2] = CoreImage.load("sVancouver.png");
        ci[3] = CoreImage.load("sVancouver.png");
        ci[4] = CoreImage.load("sVancouver.png");
        ci[5] = CoreImage.load("sVancouver.png");
        bTerritorios.add(new TerButton(ci, 34, 58, true,
                cJogo.getMapa().buscarTerritorio("Vancouver")));

        ci[0] = CoreImage.load("Omsk.png");
        ci[1] = CoreImage.load("Omsk.png");
        ci[2] = CoreImage.load("sOmsk.png");
        ci[3] = CoreImage.load("sOmsk.png");
        ci[4] = CoreImage.load("sOmsk.png");
        ci[5] = CoreImage.load("sOmsk.png");
        bTerritorios.add(new TerButton(ci, 474, 48, true,
                cJogo.getMapa().buscarTerritorio("Omsk")));

        ci[0] = CoreImage.load("Brasil.png");
        ci[1] = CoreImage.load("Brasil.png");
        ci[2] = CoreImage.load("sBrasil.png");
        ci[3] = CoreImage.load("sBrasil.png");
        ci[4] = CoreImage.load("sBrasil.png");
        ci[5] = CoreImage.load("sBrasil.png");
        bTerritorios.add(new TerButton(ci, 123, 242, true,
                cJogo.getMapa().buscarTerritorio("Brasil")));

        ci[0] = CoreImage.load("Islandia.png");
        ci[1] = CoreImage.load("Islandia.png");
        ci[2] = CoreImage.load("sIslandia.png");
        ci[3] = CoreImage.load("sIslandia.png");
        ci[4] = CoreImage.load("sIslandia.png");
        ci[5] = CoreImage.load("sIslandia.png");
        bTerritorios.add(new TerButton(ci, 299, 61, true,
                cJogo.getMapa().buscarTerritorio("Islandia")));

        ci[0] = CoreImage.load("California.png");
        ci[1] = CoreImage.load("California.png");
        ci[2] = CoreImage.load("sCalifornia.png");
        ci[3] = CoreImage.load("sCalifornia.png");
        ci[4] = CoreImage.load("sCalifornia.png");
        ci[5] = CoreImage.load("sCalifornia.png");
        bTerritorios.add(new TerButton(ci, 19, 97, true,
                cJogo.getMapa().buscarTerritorio("California")));

        ci[0] = CoreImage.load("Ottawa.png");
        ci[1] = CoreImage.load("Ottawa.png");
        ci[2] = CoreImage.load("sOttawa.png");
        ci[3] = CoreImage.load("sOttawa.png");
        ci[4] = CoreImage.load("sOttawa.png");
        ci[5] = CoreImage.load("sOttawa.png");
        bTerritorios.add(new TerButton(ci, 84, 73, true,
                cJogo.getMapa().buscarTerritorio("Ottawa")));

        ci[0] = CoreImage.load("Vietna.png");
        ci[1] = CoreImage.load("Vietna.png");
        ci[2] = CoreImage.load("sVietna.png");
        ci[3] = CoreImage.load("sVietna.png");
        ci[4] = CoreImage.load("sVietna.png");
        ci[5] = CoreImage.load("sVietna.png");
        bTerritorios.add(new TerButton(ci, 594, 181, true,
                cJogo.getMapa().buscarTerritorio("Vietna")));

        ci[0] = CoreImage.load("Congo.png");
        ci[1] = CoreImage.load("Congo.png");
        ci[2] = CoreImage.load("sCongo.png");
        ci[3] = CoreImage.load("sCongo.png");
        ci[4] = CoreImage.load("sCongo.png");
        ci[5] = CoreImage.load("sCongo.png");
        bTerritorios.add(new TerButton(ci, 354, 234, true,
                cJogo.getMapa().buscarTerritorio("Congo")));

        ci[0] = CoreImage.load("Vladivostok.png");
        ci[1] = CoreImage.load("Vladivostok.png");
        ci[2] = CoreImage.load("sVladivostok.png");
        ci[3] = CoreImage.load("sVladivostok.png");
        ci[4] = CoreImage.load("sVladivostok.png");
        ci[5] = CoreImage.load("sVladivostok.png");
        bTerritorios.add(new TerButton(ci, 607, 52, true,
                cJogo.getMapa().buscarTerritorio("Vladivostok")));

        ci[0] = CoreImage.load("Chile.png");
        ci[1] = CoreImage.load("Chile.png");
        ci[2] = CoreImage.load("sChile.png");
        ci[3] = CoreImage.load("sChile.png");
        ci[4] = CoreImage.load("sChile.png");
        ci[5] = CoreImage.load("sChile.png");
        bTerritorios.add(new TerButton(ci, 98, 241, true,
                cJogo.getMapa().buscarTerritorio("Chile")));

        ci[0] = CoreImage.load("OrienteMedio.png");
        ci[1] = CoreImage.load("OrienteMedio.png");
        ci[2] = CoreImage.load("sOrienteMedio.png");
        ci[3] = CoreImage.load("sOrienteMedio.png");
        ci[4] = CoreImage.load("sOrienteMedio.png");
        ci[5] = CoreImage.load("sOrienteMedio.png");
        bTerritorios.add(new TerButton(ci, 424, 138, true,
                cJogo.getMapa().buscarTerritorio("Oriente Medio")));

        ci[0] = CoreImage.load("Suecia.png");
        ci[1] = CoreImage.load("Suecia.png");
        ci[2] = CoreImage.load("sSuecia.png");
        ci[3] = CoreImage.load("sSuecia.png");
        ci[4] = CoreImage.load("sSuecia.png");
        ci[5] = CoreImage.load("sSuecia.png");
        bTerritorios.add(new TerButton(ci, 358, 40, true,
                cJogo.getMapa().buscarTerritorio("Suecia")));

        ci[0] = CoreImage.load("AfricaDoSul.png");
        ci[1] = CoreImage.load("AfricaDoSul.png");
        ci[2] = CoreImage.load("sAfricaDoSul.png");
        ci[3] = CoreImage.load("sAfricaDoSul.png");
        ci[4] = CoreImage.load("sAfricaDoSul.png");
        ci[5] = CoreImage.load("sAfricaDoSul.png");
        bTerritorios.add(new TerButton(ci, 366, 273, true,
                cJogo.getMapa().buscarTerritorio("Africa do Sul")));

        ci[0] = CoreImage.load("Borneo.png");
        ci[1] = CoreImage.load("Borneo.png");
        ci[2] = CoreImage.load("sBorneo.png");
        ci[3] = CoreImage.load("sBorneo.png");
        ci[4] = CoreImage.load("sBorneo.png");
        ci[5] = CoreImage.load("sBorneo.png");
        bTerritorios.add(new TerButton(ci, 666, 260, true,
                cJogo.getMapa().buscarTerritorio("Borneo")));

        ci[0] = CoreImage.load("NovaGuine.png");
        ci[1] = CoreImage.load("NovaGuine.png");
        ci[2] = CoreImage.load("sNovaGuine.png");
        ci[3] = CoreImage.load("sNovaGuine.png");
        ci[4] = CoreImage.load("sNovaGuine.png");
        ci[5] = CoreImage.load("sNovaGuine.png");
        bTerritorios.add(new TerButton(ci, 716, 293, true,
                cJogo.getMapa().buscarTerritorio("Nova Guine")));

        ci[0] = CoreImage.load("Aral.png");
        ci[1] = CoreImage.load("Aral.png");
        ci[2] = CoreImage.load("sAral.png");
        ci[3] = CoreImage.load("sAral.png");
        ci[4] = CoreImage.load("sAral.png");
        ci[5] = CoreImage.load("sAral.png");
        bTerritorios.add(new TerButton(ci, 472, 95, true,
                cJogo.getMapa().buscarTerritorio("Aral")));

        ci[0] = CoreImage.load("Sumatra.png");
        ci[1] = CoreImage.load("Sumatra.png");
        ci[2] = CoreImage.load("sSumatra.png");
        ci[3] = CoreImage.load("sSumatra.png");
        ci[4] = CoreImage.load("sSumatra.png");
        ci[5] = CoreImage.load("sSumatra.png");
        bTerritorios.add(new TerButton(ci, 613, 260, true,
                cJogo.getMapa().buscarTerritorio("Sumatra")));

        ci[0] = CoreImage.load("Inglaterra.png");
        ci[1] = CoreImage.load("Inglaterra.png");
        ci[2] = CoreImage.load("sInglaterra.png");
        ci[3] = CoreImage.load("sInglaterra.png");
        ci[4] = CoreImage.load("sInglaterra.png");
        ci[5] = CoreImage.load("sInglaterra.png");
        bTerritorios.add(new TerButton(ci, 288, 91, true,
                cJogo.getMapa().buscarTerritorio("Inglaterra")));

        ci[0] = CoreImage.load("Moscou.png");
        ci[1] = CoreImage.load("Moscou.png");
        ci[2] = CoreImage.load("sMoscou.png");
        ci[3] = CoreImage.load("sMoscou.png");
        ci[4] = CoreImage.load("sMoscou.png");
        ci[5] = CoreImage.load("sMoscou.png");
        bTerritorios.add(new TerButton(ci, 408, 49, true,
                cJogo.getMapa().buscarTerritorio("Moscou")));

        ci[0] = CoreImage.load("Sudao.png");
        ci[1] = CoreImage.load("Sudao.png");
        ci[2] = CoreImage.load("sSudao.png");
        ci[3] = CoreImage.load("sSudao.png");
        ci[4] = CoreImage.load("sSudao.png");
        ci[5] = CoreImage.load("sSudao.png");
        bTerritorios.add(new TerButton(ci, 388, 200, true,
                cJogo.getMapa().buscarTerritorio("Sudao")));

        ci[0] = CoreImage.load("Groenlandia.png");
        ci[1] = CoreImage.load("Groenlandia.png");
        ci[2] = CoreImage.load("sGroenlandia.png");
        ci[3] = CoreImage.load("sGroenlandia.png");
        ci[4] = CoreImage.load("sGroenlandia.png");
        ci[5] = CoreImage.load("sGroenlandia.png");
        bTerritorios.add(new TerButton(ci, 237, 23, true,
                cJogo.getMapa().buscarTerritorio("Groenlandia")));

        ci[0] = CoreImage.load("Egito.png");
        ci[1] = CoreImage.load("Egito.png");
        ci[2] = CoreImage.load("sEgito.png");
        ci[3] = CoreImage.load("sEgito.png");
        ci[4] = CoreImage.load("sEgito.png");
        ci[5] = CoreImage.load("sEgito.png");
        bTerritorios.add(new TerButton(ci, 364, 141, true,
                cJogo.getMapa().buscarTerritorio("Egito")));

        ci[0] = CoreImage.load("Portugal.png");
        ci[1] = CoreImage.load("Portugal.png");
        ci[2] = CoreImage.load("sPortugal.png");
        ci[3] = CoreImage.load("sPortugal.png");
        ci[4] = CoreImage.load("sPortugal.png");
        ci[5] = CoreImage.load("sPortugal.png");
        bTerritorios.add(new TerButton(ci, 331, 122, true,
                cJogo.getMapa().buscarTerritorio("Portugal")));

        ci[0] = CoreImage.load("Mackenzie.png");
        ci[1] = CoreImage.load("Mackenzie.png");
        ci[2] = CoreImage.load("sMackenzie.png");
        ci[3] = CoreImage.load("sMackenzie.png");
        ci[4] = CoreImage.load("sMackenzie.png");
        ci[5] = CoreImage.load("sMackenzie.png");
        bTerritorios.add(new TerButton(ci, 83, 35, true,
                cJogo.getMapa().buscarTerritorio("Mackenzie")));

        ci[0] = CoreImage.load("Siberia.png");
        ci[1] = CoreImage.load("Siberia.png");
        ci[2] = CoreImage.load("sSiberia.png");
        ci[3] = CoreImage.load("sSiberia.png");
        ci[4] = CoreImage.load("sSiberia.png");
        ci[5] = CoreImage.load("sSiberia.png");
        bTerritorios.add(new TerButton(ci, 545, 25, true,
                cJogo.getMapa().buscarTerritorio("Siberia")));

        ci[0] = CoreImage.load("Labrador.png");
        ci[1] = CoreImage.load("Labrador.png");
        ci[2] = CoreImage.load("sLabrador.png");
        ci[3] = CoreImage.load("sLabrador.png");
        ci[4] = CoreImage.load("sLabrador.png");
        ci[5] = CoreImage.load("sLabrador.png");
        bTerritorios.add(new TerButton(ci, 140, 76, true,
                cJogo.getMapa().buscarTerritorio("Labrador")));

        ci[0] = CoreImage.load("China.png");
        ci[1] = CoreImage.load("China.png");
        ci[2] = CoreImage.load("sChina.png");
        ci[3] = CoreImage.load("sChina.png");
        ci[4] = CoreImage.load("sChina.png");
        ci[5] = CoreImage.load("sChina.png");
        bTerritorios.add(new TerButton(ci, 558, 112, true,
                cJogo.getMapa().buscarTerritorio("China")));

        ci[0] = CoreImage.load("Tchita.png");
        ci[1] = CoreImage.load("Tchita.png");
        ci[2] = CoreImage.load("sTchita.png");
        ci[3] = CoreImage.load("sTchita.png");
        ci[4] = CoreImage.load("sTchita.png");
        ci[5] = CoreImage.load("sTchita.png");
        bTerritorios.add(new TerButton(ci, 558, 84, true,
                cJogo.getMapa().buscarTerritorio("Tchita")));

        ci[0] = CoreImage.load("Argentina.png");
        ci[1] = CoreImage.load("Argentina.png");
        ci[2] = CoreImage.load("sArgentina.png");
        ci[3] = CoreImage.load("sArgentina.png");
        ci[4] = CoreImage.load("sArgentina.png");
        ci[5] = CoreImage.load("sArgentina.png");
        bTerritorios.add(new TerButton(ci, 146, 308, true,
                cJogo.getMapa().buscarTerritorio("Argentina")));

        ci[0] = CoreImage.load("Alemanha.png");
        ci[1] = CoreImage.load("Alemanha.png");
        ci[2] = CoreImage.load("sAlemanha.png");
        ci[3] = CoreImage.load("sAlemanha.png");
        ci[4] = CoreImage.load("sAlemanha.png");
        ci[5] = CoreImage.load("sAlemanha.png");
        bTerritorios.add(new TerButton(ci, 345, 97, true,
                cJogo.getMapa().buscarTerritorio("Alemanha")));

        ci[0] = CoreImage.load("Colombia.png");
        ci[1] = CoreImage.load("Colombia.png");
        ci[2] = CoreImage.load("sColombia.png");
        ci[3] = CoreImage.load("sColombia.png");
        ci[4] = CoreImage.load("sColombia.png");
        ci[5] = CoreImage.load("sColombia.png");
        bTerritorios.add(new TerButton(ci, 105, 217, true,
                cJogo.getMapa().buscarTerritorio("Colombia")));

        ci[0] = CoreImage.load("Mexico.png");
        ci[1] = CoreImage.load("Mexico.png");
        ci[2] = CoreImage.load("sMexico.png");
        ci[3] = CoreImage.load("sMexico.png");
        ci[4] = CoreImage.load("sMexico.png");
        ci[5] = CoreImage.load("sMexico.png");
        bTerritorios.add(new TerButton(ci, 32, 143, true,
                cJogo.getMapa().buscarTerritorio("Mexico")));

        ci[0] = CoreImage.load("Madagascar.png");
        ci[1] = CoreImage.load("Madagascar.png");
        ci[2] = CoreImage.load("sMadagascar.png");
        ci[3] = CoreImage.load("sMadagascar.png");
        ci[4] = CoreImage.load("sMadagascar.png");
        ci[5] = CoreImage.load("sMadagascar.png");
        bTerritorios.add(new TerButton(ci, 471, 290, true,
                cJogo.getMapa().buscarTerritorio("Madagascar")));

        ci[0] = CoreImage.load("Alaska.png");
        ci[1] = CoreImage.load("Alaska.png");
        ci[2] = CoreImage.load("sAlaska.png");
        ci[3] = CoreImage.load("sAlaska.png");
        ci[4] = CoreImage.load("sAlaska.png");
        ci[5] = CoreImage.load("sAlaska.png");
        bTerritorios.add(new TerButton(ci, 3, 26, true,
                cJogo.getMapa().buscarTerritorio("Alaska")));

        ci[0] = CoreImage.load("Polonia.png");
        ci[1] = CoreImage.load("Polonia.png");
        ci[2] = CoreImage.load("sPolonia.png");
        ci[3] = CoreImage.load("sPolonia.png");
        ci[4] = CoreImage.load("sPolonia.png");
        ci[5] = CoreImage.load("sPolonia.png");
        bTerritorios.add(new TerButton(ci, 375, 106, true,
                cJogo.getMapa().buscarTerritorio("Polonia")));

        ci[0] = CoreImage.load("Argelia.png");
        ci[1] = CoreImage.load("Argelia.png");
        ci[2] = CoreImage.load("sArgelia.png");
        ci[3] = CoreImage.load("sArgelia.png");
        ci[4] = CoreImage.load("sArgelia.png");
        ci[5] = CoreImage.load("sArgelia.png");
        bTerritorios.add(new TerButton(ci, 291, 161, true,
                cJogo.getMapa().buscarTerritorio("Argelia")));

        ci[0] = CoreImage.load("NovaYork.png");
        ci[1] = CoreImage.load("NovaYork.png");
        ci[2] = CoreImage.load("sNovaYork.png");
        ci[3] = CoreImage.load("sNovaYork.png");
        ci[4] = CoreImage.load("sNovaYork.png");
        ci[5] = CoreImage.load("sNovaYork.png");
        bTerritorios.add(new TerButton(ci, 66, 116, true,
                cJogo.getMapa().buscarTerritorio("Nova York")));

        ci[0] = CoreImage.load("India.png");
        ci[1] = CoreImage.load("India.png");
        ci[2] = CoreImage.load("sIndia.png");
        ci[3] = CoreImage.load("sIndia.png");
        ci[4] = CoreImage.load("sIndia.png");
        ci[5] = CoreImage.load("sIndia.png");
        bTerritorios.add(new TerButton(ci, 523, 153, true,
                cJogo.getMapa().buscarTerritorio("India")));
    }

    private void criarPecas() {
        iPecas.add(new TerImageSprite("pPreta.png", 202, 280));
        iPecas.add(new TerImageSprite("pPreta.png", 165, 352));
        iPecas.add(new TerImageSprite("pPreta.png", 129, 296));
        iPecas.add(new TerImageSprite("pPreta.png", 143, 215));
        iPecas.add(new TerImageSprite("pPreta.png", 48, 173));
        iPecas.add(new TerImageSprite("pPreta.png", 130, 149));
        iPecas.add(new TerImageSprite("pPreta.png", 45, 123));
        iPecas.add(new TerImageSprite("pPreta.png", 71, 81));
        iPecas.add(new TerImageSprite("pPreta.png", 135, 92));
        iPecas.add(new TerImageSprite("pPreta.png", 202, 108));
        iPecas.add(new TerImageSprite("pPreta.png", 268, 45));
        iPecas.add(new TerImageSprite("pPreta.png", 147, 52));
        iPecas.add(new TerImageSprite("pPreta.png", 50, 39));
        iPecas.add(new TerImageSprite("pPreta.png", 306, 76));
        iPecas.add(new TerImageSprite("pPreta.png", 295, 126));
        iPecas.add(new TerImageSprite("pPreta.png", 323, 149));
        iPecas.add(new TerImageSprite("pPreta.png", 363, 99));
        iPecas.add(new TerImageSprite("pPreta.png", 408, 139));
        iPecas.add(new TerImageSprite("pPreta.png", 378, 57));
        iPecas.add(new TerImageSprite("pPreta.png", 460, 82));
        iPecas.add(new TerImageSprite("pPreta.png", 318, 201));
        iPecas.add(new TerImageSprite("pPreta.png", 404, 182));
        iPecas.add(new TerImageSprite("pPreta.png", 435, 225));
        iPecas.add(new TerImageSprite("pPreta.png", 404, 271));
        iPecas.add(new TerImageSprite("pPreta.png", 408, 332));
        iPecas.add(new TerImageSprite("pPreta.png", 491, 322));
        iPecas.add(new TerImageSprite("pPreta.png", 485, 207));
        iPecas.add(new TerImageSprite("pPreta.png", 517, 140));
        iPecas.add(new TerImageSprite("pPreta.png", 507, 97));
        iPecas.add(new TerImageSprite("pPreta.png", 528, 50));
        iPecas.add(new TerImageSprite("pPreta.png", 588, 65));
        iPecas.add(new TerImageSprite("pPreta.png", 597, 106));
        iPecas.add(new TerImageSprite("pPreta.png", 625, 139));
        iPecas.add(new TerImageSprite("pPreta.png", 648, 171));
        iPecas.add(new TerImageSprite("pPreta.png", 572, 203));
        iPecas.add(new TerImageSprite("pPreta.png", 677, 78));
        iPecas.add(new TerImageSprite("pPreta.png", 770, 167));
        iPecas.add(new TerImageSprite("pPreta.png", 655, 218));
        iPecas.add(new TerImageSprite("pPreta.png", 615, 278));
        iPecas.add(new TerImageSprite("pPreta.png", 691, 271));
        iPecas.add(new TerImageSprite("pPreta.png", 750, 302));
        iPecas.add(new TerImageSprite("pPreta.png", 688, 357));
    }

    /** Os ícones de peças já são carregados para se encaixarem neste método */
    private void associarTerritorios() {
        iPecas.get(0).setTerritorio(cJogo.getMapa().buscarTerritorio("Brasil"));
        iPecas.get(1).setTerritorio(cJogo.getMapa().buscarTerritorio("Argentina"));
        iPecas.get(2).setTerritorio(cJogo.getMapa().buscarTerritorio("Chile"));
        iPecas.get(3).setTerritorio(cJogo.getMapa().buscarTerritorio("Colombia"));
        iPecas.get(4).setTerritorio(cJogo.getMapa().buscarTerritorio("Mexico"));
        iPecas.get(5).setTerritorio(cJogo.getMapa().buscarTerritorio("Nova York"));
        iPecas.get(6).setTerritorio(cJogo.getMapa().buscarTerritorio("California"));
        iPecas.get(7).setTerritorio(cJogo.getMapa().buscarTerritorio("Vancouver"));
        iPecas.get(8).setTerritorio(cJogo.getMapa().buscarTerritorio("Ottawa"));
        iPecas.get(9).setTerritorio(cJogo.getMapa().buscarTerritorio("Labrador"));
        iPecas.get(10).setTerritorio(cJogo.getMapa().buscarTerritorio("Groenlandia"));
        iPecas.get(11).setTerritorio(cJogo.getMapa().buscarTerritorio("Mackenzie"));
        iPecas.get(12).setTerritorio(cJogo.getMapa().buscarTerritorio("Alaska"));
        iPecas.get(13).setTerritorio(cJogo.getMapa().buscarTerritorio("Islandia"));
        iPecas.get(14).setTerritorio(cJogo.getMapa().buscarTerritorio("Inglaterra"));
        iPecas.get(15).setTerritorio(cJogo.getMapa().buscarTerritorio("Portugal"));
        iPecas.get(16).setTerritorio(cJogo.getMapa().buscarTerritorio("Alemanha"));
        iPecas.get(17).setTerritorio(cJogo.getMapa().buscarTerritorio("Polonia"));
        iPecas.get(18).setTerritorio(cJogo.getMapa().buscarTerritorio("Suecia"));
        iPecas.get(19).setTerritorio(cJogo.getMapa().buscarTerritorio("Moscou"));
        iPecas.get(20).setTerritorio(cJogo.getMapa().buscarTerritorio("Argelia"));
        iPecas.get(21).setTerritorio(cJogo.getMapa().buscarTerritorio("Egito"));
        iPecas.get(22).setTerritorio(cJogo.getMapa().buscarTerritorio("Sudao"));
        iPecas.get(23).setTerritorio(cJogo.getMapa().buscarTerritorio("Congo"));
        iPecas.get(24).setTerritorio(cJogo.getMapa().buscarTerritorio("Africa do Sul"));
        iPecas.get(25).setTerritorio(cJogo.getMapa().buscarTerritorio("Madagascar"));
        iPecas.get(26).setTerritorio(cJogo.getMapa().buscarTerritorio("Oriente Medio"));
        iPecas.get(27).setTerritorio(cJogo.getMapa().buscarTerritorio("Aral"));
        iPecas.get(28).setTerritorio(cJogo.getMapa().buscarTerritorio("Omsk"));
        iPecas.get(29).setTerritorio(cJogo.getMapa().buscarTerritorio("Dudinka"));
        iPecas.get(30).setTerritorio(cJogo.getMapa().buscarTerritorio("Siberia"));
        iPecas.get(31).setTerritorio(cJogo.getMapa().buscarTerritorio("Tchita"));
        iPecas.get(32).setTerritorio(cJogo.getMapa().buscarTerritorio("Mongolia"));
        iPecas.get(33).setTerritorio(cJogo.getMapa().buscarTerritorio("China"));
        iPecas.get(34).setTerritorio(cJogo.getMapa().buscarTerritorio("India"));
        iPecas.get(35).setTerritorio(cJogo.getMapa().buscarTerritorio("Vladivostok"));
        iPecas.get(36).setTerritorio(cJogo.getMapa().buscarTerritorio("Japao"));
        iPecas.get(37).setTerritorio(cJogo.getMapa().buscarTerritorio("Vietna"));
        iPecas.get(38).setTerritorio(cJogo.getMapa().buscarTerritorio("Sumatra"));
        iPecas.get(39).setTerritorio(cJogo.getMapa().buscarTerritorio("Borneo"));
        iPecas.get(40).setTerritorio(cJogo.getMapa().buscarTerritorio("Nova Guine"));
        iPecas.get(41).setTerritorio(cJogo.getMapa().buscarTerritorio("Australia"));
    }

    @Override
    public void update(int elapsedTime) {
        super.update(elapsedTime);

        // Se estiver Jogando, somente então começaremos a tratar os eventos.
        if (cJogo.getJogando()) {

            for (TerButton t : this.bTerritorios) {
                if (t.isClicked()) {
                    t.setSelected(cJogo.selecionarTerritorio(t.territorio));
                }
            }

            if (bTrocar.isClicked() 
                    && cJogo.getEstadoJogo() == CliJogo.Estado.AdicionarExercitos
                    && ! primeiroTurno) {
                // TrocarCartasDlg.showDialog(cJogo.getJogador().getCartas());
            }

            if (bAtacar.isClicked()
                    && cJogo.getEstadoJogo() == CliJogo.Estado.AdicionarExercitos
                    && ! primeiroTurno) {
                cJogo.definirEstado(CliJogo.Estado.Atacar);
            }

            if (bMover.isClicked() && ! primeiroTurno) {
                cJogo.definirEstado(CliJogo.Estado.MovimentarExercitos);
            }

            if (bPassar.isClicked()) {
                primeiroTurno = false;
                cJogo.finalizarTurno();
            }
        }
    }

    /** Adiciona o atributo Territorio ao Button */
    public final class TerButton extends Button {

        public Territorio territorio;

        public TerButton(CoreImage[] ci, int x, int y, boolean toggle,
                Territorio territorio) {
            super(ci, x, y, true);
            this.territorio = territorio;
        }

        @Override
        public void update(int elapsedTime) {
            super.update(elapsedTime);
            if (!cJogo.getJogando()) {
                setSelected(false);
            }
        }
    }
}
