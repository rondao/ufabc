package War;

import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class RNA {

    /** Vetor com os pesos. */
    private double pesos[];
    /** Limiar */
    private double bias;
    /** ArrayList contendo dados a ser treinados.
    /* e cada dado eh um vetor contendo as entradas para a RNA.
     */
    private ArrayList<double[]> dados;
    /** Taxa de aprendizado. */
    private double neta;
    /** Pontuacao do estado do jogo após os dados inseridos serem jogados. */
    private double pontuacao;

    public RNA(double[] pesos, double neta, double bias) {
        dados = new ArrayList<double[]>();

        this.pesos = pesos;
        this.neta = neta;
        this.bias = bias;
    }

    /** Treina todos os dados inseridos. */
    public void treinar(double pontuacaoFinal) {

        // Se a pontuacao final foi maior, as jogadas foram boas. yd = 1;
        // Caso contrario foram ruins. yd = -1;
        int yd = (pontuacaoFinal > pontuacao) ? 1 : -1;
        boolean tudoTreinado = false;
        while (!tudoTreinado) {
            tudoTreinado = true;
            for (double[] dado : dados) {
                double u = 0;

                // Cada dado multiplicado pelo seu peso.
                for (int i = 0; i < dado.length; i++) {
                    u += dado[i] * pesos[i];
                }
                // Reduzindo o limiar.
                u -= bias;

                int y = (u >= 0) ? 1 : -1;

                // Caso verdade. E preciso treinar a RNA.
                if (y != yd) {
                    // Alterando todos os pesos.
                    for (int i = 0; i < pesos.length; i++) {
                        pesos[i] = pesos[i] + neta * (yd - y) * dado[i];
                    }
                    // Alterando o Bias.
                    bias = bias + neta * (yd - y) * (-1);

                    // Houve alteracao. Nem tudo esta treinado.
                    tudoTreinado = false;
                }
            }
        }
        // Apos tudo treinado limpar para novos dados.
        dados.clear();
    }

    /** Adiciona um novo dado a ser treinado. */
    public void addDado(double[] dado) {
        dados.add(dado);
    }

    /** Seta a pontuacao da execucao dos dados. */
    public void setPontuacao(double pontuacao) {
        this.pontuacao = pontuacao;
    }

    public double getPontuacao() {
        return this.pontuacao;
    }

    /** Apresenta os pesos atuais. */
    public void showPesos(String str) {
        for (int i = 0; i < pesos.length; i++) {
            str += "w" + (i+1) + ": " + pesos[i] + "\n";
        }
        str += "w0: " + bias;

        JOptionPane.showMessageDialog(null, str);
    }
}
