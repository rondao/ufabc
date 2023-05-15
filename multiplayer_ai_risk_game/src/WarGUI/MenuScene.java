package WarGUI;

import WarCli.CliJogo;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import pulpcore.image.Colors;
import pulpcore.image.CoreFont;
import pulpcore.scene.Scene2D;
import pulpcore.image.CoreImage;
import pulpcore.sprite.Button;
import pulpcore.sprite.ImageSprite;
import pulpcore.sprite.Label;
import pulpcore.sprite.TextField;
import pulpcore.Stage;

/**
 * Contém a Tela de menu.
 * Nela é especificado o IP e Porta do Servidor, e o nome do jogador.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class MenuScene extends Scene2D {

    /** Botão para Iniciar o Jogo */
    Button btnJogar;
    /** Campo para o IP do Host */
    TextField txtHostIP;
    /** Campo para a Porta do Host */
    TextField txtPorta;
    /** Campo para o nome do Host */
    TextField txtNome;
    /** Fonte 'arial' */
    CoreFont fArial;
    /** Engine para o jogo do Cliente */
    CliJogo cJogo;

    /** Método executado na criação deste objeto */
    @Override
    public void load() {
        super.load();

        fArial = CoreFont.load("arial");
        fArial = fArial.tint(Colors.WHITE);

        // Adiciona na tela o fundo principal
        add(new ImageSprite("bgMenuPrin.jpg", 0, 0));

        // Sprite dos campos de texto
        add(new ImageSprite("txtJogador-CriaJog.png", 200, 300));
        add(new ImageSprite("txtJogador-CriaJog.png", 200, 350));
        add(new ImageSprite("txtJogador-CriaJog.png", 200, 400));

        // Campo IP
        txtHostIP = new TextField(fArial, fArial, "", 235, 310, 350, 16);
        txtHostIP.setMaxNumChars(20);
        txtHostIP.caretColor.set(Colors.WHITE);
        // Label IP
        add(new Label(fArial, "Host IP:", 150, 315));

        // Campo Porta
        txtPorta = new TextField(fArial, fArial, "", 235, 360, 350, 16);
        txtPorta.setMaxNumChars(20);
        txtPorta.caretColor.set(Colors.WHITE);
        // Label Porta
        add(new Label(fArial, "Porta:", 150, 365));

        // Campo Nome
        txtNome = new TextField(fArial, fArial, "", 235, 410, 350, 16);
        txtNome.setMaxNumChars(20);
        txtNome.caretColor.set(Colors.WHITE);
        // Label Nome
        add(new Label(fArial, "Nome:", 150, 415));

        add(txtHostIP);
        add(txtPorta);
        add(txtNome);

        // 'ci_Btn' guarda as possíveis maneiras que os botões poderam estar
        CoreImage[] ci_Btn = new CoreImage[3];
        ci_Btn[0] = CoreImage.load("btnMenu.png");      // [0] = posição normal
        ci_Btn[1] = CoreImage.load("btnMouseMenu.png"); // [1] = mouse over
        ci_Btn[2] = CoreImage.load("btnPressMenu.png"); // [2] = clicado

        btnJogar = Button.createLabeledButton(ci_Btn, fArial, "Jogar", 300, 450);
        add(btnJogar);
    }

    /** Onde são tratado os eventos */
    @Override
    public void update(int elapsedTime) {
        super.update(elapsedTime);

        if (btnJogar.isClicked()) {
            try {
                cJogo = new CliJogo(txtHostIP.getText(),
                        Integer.parseInt(txtPorta.getText()),
                        txtNome.getText());

                Stage.replaceScene( new JogoScene(cJogo));
            } catch (UnknownHostException ex) {
                System.err.println(ex.toString());
                JOptionPane.showMessageDialog(null, "Host não encontrado!",
                        "Aviso!", JOptionPane.OK_OPTION);
            } catch (IOException ex) {
                System.err.println(ex.toString());
                JOptionPane.showMessageDialog(null, "Erro na Conexao!",
                        "Aviso!", JOptionPane.OK_OPTION);
            }
        }
    }
}
