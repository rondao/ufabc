package War;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class CriadorTerritorios {

    public Mapa mapa;

    public static void main(String[] args) {
        
        CriadorTerritorios CT = new CriadorTerritorios();
    }

    public CriadorTerritorios() {
        mapa = new Mapa();

        criarTerritorios();

        Dudinka();
        Australia();
        Japao();
        Mongolia();
        Vancouver();
        Omsk();
        Brasil();
        Islandia();
        California();
        Ottawa();
        Vietna();
        Congo();
        Vladivostok();
        Chile();
        OrienteMedio();
        Suecia();
        AfricaDoSul();
        Borneo();
        NovaGuine();
        Aral();
        Sumatra();
        Inglaterra();
        Moscou();
        Sudao();
        Groenlandia();
        Egito();
        Portugal();
        Mackenzie();
        Siberia();
        Labrador();
        China();
        Tchita();
        Argentina();
        Alemanha();
        Colombia();
        Mexico();
        Madagascar();
        Alaska();
        Polonia();
        Argelia();
        NovaYork();
        India();

        Oceania();
        Africa();
        AmericaDoSul();
        Europa();
        Asia();
        AmericaDoNorte();

        try {
            salvarMapa();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    public void salvarMapa() throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream("mapa.wmp");
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(mapa);
        oos.close();

        System.out.println("Mapa salvo com sucesso!");
        return;
    }

    public void criarTerritorios() {
        mapa.adicionaTerritorio(new Territorio("Dudinka"));
        mapa.adicionaTerritorio(new Territorio("Australia"));
        mapa.adicionaTerritorio(new Territorio("Japao"));
        mapa.adicionaTerritorio(new Territorio("Mongolia"));
        mapa.adicionaTerritorio(new Territorio("Vancouver"));
        mapa.adicionaTerritorio(new Territorio("Omsk"));
        mapa.adicionaTerritorio(new Territorio("Brasil"));
        mapa.adicionaTerritorio(new Territorio("Islandia"));
        mapa.adicionaTerritorio(new Territorio("California"));
        mapa.adicionaTerritorio(new Territorio("Ottawa"));
        mapa.adicionaTerritorio(new Territorio("Vietna"));
        mapa.adicionaTerritorio(new Territorio("Congo"));
        mapa.adicionaTerritorio(new Territorio("Vladivostok"));
        mapa.adicionaTerritorio(new Territorio("Chile"));
        mapa.adicionaTerritorio(new Territorio("Oriente Medio"));
        mapa.adicionaTerritorio(new Territorio("Suecia"));
        mapa.adicionaTerritorio(new Territorio("Africa do Sul"));
        mapa.adicionaTerritorio(new Territorio("Borneo"));
        mapa.adicionaTerritorio(new Territorio("Nova Guine"));
        mapa.adicionaTerritorio(new Territorio("Aral"));
        mapa.adicionaTerritorio(new Territorio("Sumatra"));
        mapa.adicionaTerritorio(new Territorio("Inglaterra"));
        mapa.adicionaTerritorio(new Territorio("Moscou"));
        mapa.adicionaTerritorio(new Territorio("Sudao"));
        mapa.adicionaTerritorio(new Territorio("Groenlandia"));
        mapa.adicionaTerritorio(new Territorio("Egito"));
        mapa.adicionaTerritorio(new Territorio("Portugal"));
        mapa.adicionaTerritorio(new Territorio("Mackenzie"));
        mapa.adicionaTerritorio(new Territorio("Siberia"));
        mapa.adicionaTerritorio(new Territorio("Labrador"));
        mapa.adicionaTerritorio(new Territorio("China"));
        mapa.adicionaTerritorio(new Territorio("Tchita"));
        mapa.adicionaTerritorio(new Territorio("Argentina"));
        mapa.adicionaTerritorio(new Territorio("Alemanha"));
        mapa.adicionaTerritorio(new Territorio("Colombia"));
        mapa.adicionaTerritorio(new Territorio("Mexico"));
        mapa.adicionaTerritorio(new Territorio("Madagascar"));
        mapa.adicionaTerritorio(new Territorio("Alaska"));
        mapa.adicionaTerritorio(new Territorio("Polonia"));
        mapa.adicionaTerritorio(new Territorio("Argelia"));
        mapa.adicionaTerritorio(new Territorio("Nova York"));
        mapa.adicionaTerritorio(new Territorio("India"));
    }

    public void Oceania() {
        ArrayList<Continente> C = mapa.getContinentes();
        C.add(new Continente("Oceania", 2));

        C.get(0).adicionaTerritorio(mapa.buscarTerritorio("Sumatra"));
        C.get(0).adicionaTerritorio(mapa.buscarTerritorio("Borneo"));
        C.get(0).adicionaTerritorio(mapa.buscarTerritorio("Nova Guine"));
        C.get(0).adicionaTerritorio(mapa.buscarTerritorio("Australia"));
    }

    public void Africa() {
        ArrayList<Continente> C = mapa.getContinentes();
        C.add(new Continente("Africa", 3));

        C.get(1).adicionaTerritorio(mapa.buscarTerritorio("Argelia"));
        C.get(1).adicionaTerritorio(mapa.buscarTerritorio("Egito"));
        C.get(1).adicionaTerritorio(mapa.buscarTerritorio("Sudao"));
        C.get(1).adicionaTerritorio(mapa.buscarTerritorio("Congo"));
        C.get(1).adicionaTerritorio(mapa.buscarTerritorio("Africa do Sul"));
        C.get(1).adicionaTerritorio(mapa.buscarTerritorio("Madagascar"));
    }

    public void AmericaDoSul() {
        ArrayList<Continente> C = mapa.getContinentes();
        C.add(new Continente("America do Sul", 2));

        C.get(2).adicionaTerritorio(mapa.buscarTerritorio("Brasil"));
        C.get(2).adicionaTerritorio(mapa.buscarTerritorio("Argentina"));
        C.get(2).adicionaTerritorio(mapa.buscarTerritorio("Chile"));
        C.get(2).adicionaTerritorio(mapa.buscarTerritorio("Colombia"));
    }

    public void Europa() {
        ArrayList<Continente> C = mapa.getContinentes();
        C.add(new Continente("Europa", 5));

        C.get(3).adicionaTerritorio(mapa.buscarTerritorio("Islandia"));
        C.get(3).adicionaTerritorio(mapa.buscarTerritorio("Inglaterra"));
        C.get(3).adicionaTerritorio(mapa.buscarTerritorio("Portugal"));
        C.get(3).adicionaTerritorio(mapa.buscarTerritorio("Alemanha"));
        C.get(3).adicionaTerritorio(mapa.buscarTerritorio("Polonia"));
        C.get(3).adicionaTerritorio(mapa.buscarTerritorio("Moscou"));
        C.get(3).adicionaTerritorio(mapa.buscarTerritorio("Suecia"));
    }

    public void Asia() {
        ArrayList<Continente> C = mapa.getContinentes();
        C.add(new Continente("Asia", 7));

        C.get(4).adicionaTerritorio(mapa.buscarTerritorio("Oriente Medio"));
        C.get(4).adicionaTerritorio(mapa.buscarTerritorio("India"));
        C.get(4).adicionaTerritorio(mapa.buscarTerritorio("Vietna"));
        C.get(4).adicionaTerritorio(mapa.buscarTerritorio("Japao"));
        C.get(4).adicionaTerritorio(mapa.buscarTerritorio("China"));
        C.get(4).adicionaTerritorio(mapa.buscarTerritorio("Aral"));
        C.get(4).adicionaTerritorio(mapa.buscarTerritorio("Omsk"));
        C.get(4).adicionaTerritorio(mapa.buscarTerritorio("Dudinka"));
        C.get(4).adicionaTerritorio(mapa.buscarTerritorio("Mongolia"));
        C.get(4).adicionaTerritorio(mapa.buscarTerritorio("Tchita"));
        C.get(4).adicionaTerritorio(mapa.buscarTerritorio("Siberia"));
        C.get(4).adicionaTerritorio(mapa.buscarTerritorio("Vladivostok"));
    }

    public void AmericaDoNorte() {
        ArrayList<Continente> C = mapa.getContinentes();
        C.add(new Continente("America do Norte", 5));

        C.get(5).adicionaTerritorio(mapa.buscarTerritorio("Alaska"));
        C.get(5).adicionaTerritorio(mapa.buscarTerritorio("Mackenzie"));
        C.get(5).adicionaTerritorio(mapa.buscarTerritorio("Groenlandia"));
        C.get(5).adicionaTerritorio(mapa.buscarTerritorio("Vancouver"));
        C.get(5).adicionaTerritorio(mapa.buscarTerritorio("Ottawa"));
        C.get(5).adicionaTerritorio(mapa.buscarTerritorio("Labrador"));
        C.get(5).adicionaTerritorio(mapa.buscarTerritorio("California"));
        C.get(5).adicionaTerritorio(mapa.buscarTerritorio("Nova York"));
        C.get(5).adicionaTerritorio(mapa.buscarTerritorio("Mexico"));
    }

    public void Dudinka() {
        Territorio T = mapa.buscarTerritorio("Dudinka");

        T.criaFronteira(mapa.buscarTerritorio("Siberia"));
        T.criaFronteira(mapa.buscarTerritorio("Tchita"));
        T.criaFronteira(mapa.buscarTerritorio("Mongolia"));
        T.criaFronteira(mapa.buscarTerritorio("Omsk"));
    }

    public void Australia() {
        Territorio T = mapa.buscarTerritorio("Australia");

        T.criaFronteira(mapa.buscarTerritorio("Nova Guine"));
        T.criaFronteira(mapa.buscarTerritorio("Borneo"));
        T.criaFronteira(mapa.buscarTerritorio("Sumatra"));
    }

    public void Japao() {
        Territorio T = mapa.buscarTerritorio("Japao");

        T.criaFronteira(mapa.buscarTerritorio("Vladivostok"));
        T.criaFronteira(mapa.buscarTerritorio("China"));
    }

    public void Mongolia() {
        Territorio T = mapa.buscarTerritorio("Mongolia");

        T.criaFronteira(mapa.buscarTerritorio("China"));
        T.criaFronteira(mapa.buscarTerritorio("Tchita"));
        T.criaFronteira(mapa.buscarTerritorio("Dudinka"));
        T.criaFronteira(mapa.buscarTerritorio("Omsk"));
    }

    public void Vancouver() {
        Territorio T = mapa.buscarTerritorio("Vancouver");

        T.criaFronteira(mapa.buscarTerritorio("Alaska"));
        T.criaFronteira(mapa.buscarTerritorio("Mackenzie"));
        T.criaFronteira(mapa.buscarTerritorio("Ottawa"));
        T.criaFronteira(mapa.buscarTerritorio("California"));
    }

    public void Omsk() {
        Territorio T = mapa.buscarTerritorio("Omsk");

        T.criaFronteira(mapa.buscarTerritorio("China"));
        T.criaFronteira(mapa.buscarTerritorio("Aral"));
        T.criaFronteira(mapa.buscarTerritorio("Mongolia"));
        T.criaFronteira(mapa.buscarTerritorio("Dudinka"));
        T.criaFronteira(mapa.buscarTerritorio("Moscou"));
    }

    public void Brasil() {
        Territorio T = mapa.buscarTerritorio("Brasil");

        T.criaFronteira(mapa.buscarTerritorio("Colombia"));
        T.criaFronteira(mapa.buscarTerritorio("Chile"));
        T.criaFronteira(mapa.buscarTerritorio("Argentina"));
        T.criaFronteira(mapa.buscarTerritorio("Argelia"));
    }

    public void Islandia() {
        Territorio T = mapa.buscarTerritorio("Islandia");

        T.criaFronteira(mapa.buscarTerritorio("Groenlandia"));
        T.criaFronteira(mapa.buscarTerritorio("Inglaterra"));
    }

    public void California() {
        Territorio T = mapa.buscarTerritorio("California");

        T.criaFronteira(mapa.buscarTerritorio("Mexico"));
        T.criaFronteira(mapa.buscarTerritorio("Nova York"));
        T.criaFronteira(mapa.buscarTerritorio("Ottawa"));
        T.criaFronteira(mapa.buscarTerritorio("Vancouver"));
    }

    public void Ottawa() {
        Territorio T = mapa.buscarTerritorio("Ottawa");

        T.criaFronteira(mapa.buscarTerritorio("Labrador"));
        T.criaFronteira(mapa.buscarTerritorio("Nova York"));
        T.criaFronteira(mapa.buscarTerritorio("California"));
        T.criaFronteira(mapa.buscarTerritorio("Vancouver"));
        T.criaFronteira(mapa.buscarTerritorio("Mackenzie"));
    }

    public void Vietna() {
        Territorio T = mapa.buscarTerritorio("Vietna");

        T.criaFronteira(mapa.buscarTerritorio("Borneo"));
        T.criaFronteira(mapa.buscarTerritorio("China"));
        T.criaFronteira(mapa.buscarTerritorio("India"));
        T.criaFronteira(mapa.buscarTerritorio("Sumatra"));
    }

    public void Congo() {
        Territorio T = mapa.buscarTerritorio("Congo");

        T.criaFronteira(mapa.buscarTerritorio("Africa do Sul"));
        T.criaFronteira(mapa.buscarTerritorio("Sudao"));
        T.criaFronteira(mapa.buscarTerritorio("Argelia"));
    }

    public void Vladivostok() {
        Territorio T = mapa.buscarTerritorio("Vladivostok");

        T.criaFronteira(mapa.buscarTerritorio("Siberia"));
        T.criaFronteira(mapa.buscarTerritorio("Tchita"));
        T.criaFronteira(mapa.buscarTerritorio("China"));
        T.criaFronteira(mapa.buscarTerritorio("Japao"));
        T.criaFronteira(mapa.buscarTerritorio("Alaska"));
    }

    public void Chile() {
        Territorio T = mapa.buscarTerritorio("Chile");

        T.criaFronteira(mapa.buscarTerritorio("Colombia"));
        T.criaFronteira(mapa.buscarTerritorio("Brasil"));
        T.criaFronteira(mapa.buscarTerritorio("Argentina"));
    }

    public void OrienteMedio() {
        Territorio T = mapa.buscarTerritorio("Oriente Medio");

        T.criaFronteira(mapa.buscarTerritorio("India"));
        T.criaFronteira(mapa.buscarTerritorio("Aral"));
        T.criaFronteira(mapa.buscarTerritorio("Moscou"));
        T.criaFronteira(mapa.buscarTerritorio("Polonia"));
        T.criaFronteira(mapa.buscarTerritorio("Egito"));
    }

    public void Suecia() {
        Territorio T = mapa.buscarTerritorio("Suecia");

        T.criaFronteira(mapa.buscarTerritorio("Moscou"));
        T.criaFronteira(mapa.buscarTerritorio("Inglaterra"));
    }

    public void AfricaDoSul() {
        Territorio T = mapa.buscarTerritorio("Africa do Sul");

        T.criaFronteira(mapa.buscarTerritorio("Madagascar"));
        T.criaFronteira(mapa.buscarTerritorio("Congo"));
        T.criaFronteira(mapa.buscarTerritorio("Sudao"));
    }

    public void Borneo() {
        Territorio T = mapa.buscarTerritorio("Borneo");

        T.criaFronteira(mapa.buscarTerritorio("Australia"));
        T.criaFronteira(mapa.buscarTerritorio("Nova Guine"));
        T.criaFronteira(mapa.buscarTerritorio("Vietna"));
    }

    public void NovaGuine() {
        Territorio T = mapa.buscarTerritorio("Nova Guine");

        T.criaFronteira(mapa.buscarTerritorio("Borneo"));
        T.criaFronteira(mapa.buscarTerritorio("Australia"));
    }

    public void Aral() {
        Territorio T = mapa.buscarTerritorio("Aral");

        T.criaFronteira(mapa.buscarTerritorio("Oriente Medio"));
        T.criaFronteira(mapa.buscarTerritorio("India"));
        T.criaFronteira(mapa.buscarTerritorio("China"));
        T.criaFronteira(mapa.buscarTerritorio("Omsk"));
        T.criaFronteira(mapa.buscarTerritorio("Moscou"));
    }

    public void Sumatra() {
        Territorio T = mapa.buscarTerritorio("Sumatra");

        T.criaFronteira(mapa.buscarTerritorio("Australia"));
        T.criaFronteira(mapa.buscarTerritorio("India"));
    }

    public void Inglaterra() {
        Territorio T = mapa.buscarTerritorio("Inglaterra");

        T.criaFronteira(mapa.buscarTerritorio("Islandia"));
        T.criaFronteira(mapa.buscarTerritorio("Suecia"));
        T.criaFronteira(mapa.buscarTerritorio("Alemanha"));
        T.criaFronteira(mapa.buscarTerritorio("Portugal"));
    }

    public void Moscou() {
        Territorio T = mapa.buscarTerritorio("Moscou");

        T.criaFronteira(mapa.buscarTerritorio("Suecia"));
        T.criaFronteira(mapa.buscarTerritorio("Polonia"));
        T.criaFronteira(mapa.buscarTerritorio("Oriente Medio"));
        T.criaFronteira(mapa.buscarTerritorio("Aral"));
        T.criaFronteira(mapa.buscarTerritorio("Omsk"));
    }

    public void Sudao() {
        Territorio T = mapa.buscarTerritorio("Sudao");

        T.criaFronteira(mapa.buscarTerritorio("Madagascar"));
        T.criaFronteira(mapa.buscarTerritorio("Africa do Sul"));
        T.criaFronteira(mapa.buscarTerritorio("Congo"));
        T.criaFronteira(mapa.buscarTerritorio("Argelia"));
        T.criaFronteira(mapa.buscarTerritorio("Egito"));
    }

    public void Groenlandia() {
        Territorio T = mapa.buscarTerritorio("Groenlandia");

        T.criaFronteira(mapa.buscarTerritorio("Islandia"));
        T.criaFronteira(mapa.buscarTerritorio("Mackenzie"));
        T.criaFronteira(mapa.buscarTerritorio("Labrador"));
    }

    public void Egito() {
        Territorio T = mapa.buscarTerritorio("Egito");

        T.criaFronteira(mapa.buscarTerritorio("Sudao"));
        T.criaFronteira(mapa.buscarTerritorio("Argelia"));
        T.criaFronteira(mapa.buscarTerritorio("Polonia"));
        T.criaFronteira(mapa.buscarTerritorio("Oriente Medio"));
    }

    public void Portugal() {
        Territorio T = mapa.buscarTerritorio("Portugal");

        T.criaFronteira(mapa.buscarTerritorio("Inglaterra"));
        T.criaFronteira(mapa.buscarTerritorio("Alemanha"));
        T.criaFronteira(mapa.buscarTerritorio("Polonia"));
        T.criaFronteira(mapa.buscarTerritorio("Egito"));
        T.criaFronteira(mapa.buscarTerritorio("Argelia"));
    }

    public void Mackenzie() {
        Territorio T = mapa.buscarTerritorio("Mackenzie");

        T.criaFronteira(mapa.buscarTerritorio("Alaska"));
        T.criaFronteira(mapa.buscarTerritorio("Vancouver"));
        T.criaFronteira(mapa.buscarTerritorio("Ottawa"));
        T.criaFronteira(mapa.buscarTerritorio("Groenlandia"));
    }

    public void Siberia() {
        Territorio T = mapa.buscarTerritorio("Siberia");

        T.criaFronteira(mapa.buscarTerritorio("Dudinka"));
        T.criaFronteira(mapa.buscarTerritorio("Tchita"));
        T.criaFronteira(mapa.buscarTerritorio("Vladivostok"));
    }

    public void Labrador() {
        Territorio T = mapa.buscarTerritorio("Labrador");

        T.criaFronteira(mapa.buscarTerritorio("Nova York"));
        T.criaFronteira(mapa.buscarTerritorio("Ottawa"));
        T.criaFronteira(mapa.buscarTerritorio("Groenlandia"));
    }

    public void China() {
        Territorio T = mapa.buscarTerritorio("China");

        T.criaFronteira(mapa.buscarTerritorio("Vietna"));
        T.criaFronteira(mapa.buscarTerritorio("India"));
        T.criaFronteira(mapa.buscarTerritorio("Aral"));
        T.criaFronteira(mapa.buscarTerritorio("Omsk"));
        T.criaFronteira(mapa.buscarTerritorio("Mongolia"));
        T.criaFronteira(mapa.buscarTerritorio("Tchita"));
        T.criaFronteira(mapa.buscarTerritorio("Vladivostok"));
        T.criaFronteira(mapa.buscarTerritorio("Japao"));
    }

    public void Tchita() {
        Territorio T = mapa.buscarTerritorio("Tchita");

        T.criaFronteira(mapa.buscarTerritorio("China"));
        T.criaFronteira(mapa.buscarTerritorio("Mongolia"));
        T.criaFronteira(mapa.buscarTerritorio("Dudinka"));
        T.criaFronteira(mapa.buscarTerritorio("Siberia"));
        T.criaFronteira(mapa.buscarTerritorio("Vladivostok"));
    }

    public void Argentina() {
        Territorio T = mapa.buscarTerritorio("Argentina");

        T.criaFronteira(mapa.buscarTerritorio("Brasil"));
        T.criaFronteira(mapa.buscarTerritorio("Chile"));
    }

    public void Alemanha() {
        Territorio T = mapa.buscarTerritorio("Alemanha");

        T.criaFronteira(mapa.buscarTerritorio("Inglaterra"));
        T.criaFronteira(mapa.buscarTerritorio("Portugal"));
        T.criaFronteira(mapa.buscarTerritorio("Polonia"));
    }

    public void Colombia() {
        Territorio T = mapa.buscarTerritorio("Colombia");

        T.criaFronteira(mapa.buscarTerritorio("Mexico"));
        T.criaFronteira(mapa.buscarTerritorio("Chile"));
        T.criaFronteira(mapa.buscarTerritorio("Brasil"));
    }

    public void Mexico() {
        Territorio T = mapa.buscarTerritorio("Mexico");

        T.criaFronteira(mapa.buscarTerritorio("Colombia"));
        T.criaFronteira(mapa.buscarTerritorio("California"));
        T.criaFronteira(mapa.buscarTerritorio("Nova York"));
    }

    public void Madagascar() {
        Territorio T = mapa.buscarTerritorio("Madagascar");

        T.criaFronteira(mapa.buscarTerritorio("Africa do Sul"));
        T.criaFronteira(mapa.buscarTerritorio("Sudao"));
    }

    public void Alaska() {
        Territorio T = mapa.buscarTerritorio("Alaska");

        T.criaFronteira(mapa.buscarTerritorio("Mackenzie"));
        T.criaFronteira(mapa.buscarTerritorio("Vancouver"));
        T.criaFronteira(mapa.buscarTerritorio("Vladivostok"));
    }

    public void Polonia() {
        Territorio T = mapa.buscarTerritorio("Polonia");

        T.criaFronteira(mapa.buscarTerritorio("Oriente Medio"));
        T.criaFronteira(mapa.buscarTerritorio("Portugal"));
        T.criaFronteira(mapa.buscarTerritorio("Alemanha"));
        T.criaFronteira(mapa.buscarTerritorio("Moscou"));
        T.criaFronteira(mapa.buscarTerritorio("Egito"));
    }

    public void Argelia() {
        Territorio T = mapa.buscarTerritorio("Argelia");

        T.criaFronteira(mapa.buscarTerritorio("Brasil"));
        T.criaFronteira(mapa.buscarTerritorio("Congo"));
        T.criaFronteira(mapa.buscarTerritorio("Sudao"));
        T.criaFronteira(mapa.buscarTerritorio("Egito"));
        T.criaFronteira(mapa.buscarTerritorio("Portugal"));
    }

    public void NovaYork() {
        Territorio T = mapa.buscarTerritorio("Nova York");

        T.criaFronteira(mapa.buscarTerritorio("Mexico"));
        T.criaFronteira(mapa.buscarTerritorio("California"));
        T.criaFronteira(mapa.buscarTerritorio("Ottawa"));
        T.criaFronteira(mapa.buscarTerritorio("Labrador"));
    }

    public void India() {
        Territorio T = mapa.buscarTerritorio("India");

        T.criaFronteira(mapa.buscarTerritorio("Sumatra"));
        T.criaFronteira(mapa.buscarTerritorio("Vietna"));
        T.criaFronteira(mapa.buscarTerritorio("China"));
        T.criaFronteira(mapa.buscarTerritorio("Aral"));
        T.criaFronteira(mapa.buscarTerritorio("Oriente Medio"));
    }
}
