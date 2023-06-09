package WarGUI;

import War.Carta;
import java.util.ArrayList;

/**
 * Janela para selecionar as cartas a serem trocadas.
 * @author Rafael Rondão / Vinicius Zanquini
 */
public class TrocarCartasDlg extends javax.swing.JDialog {

    // Cartas escolhidas para a troca.
    private ArrayList<Carta> cartasTroca;

    /** Creates new form TrocarCartasDlg */
    public TrocarCartasDlg(java.awt.Frame parent, boolean modal,
            final ArrayList<Carta> cartasJog) {
        super(parent, modal);
        initComponents();

        final ArrayList<String> cartasStr = new ArrayList<String>();
        for (Carta c : cartasJog) {
            if (c.getSimbolo() == Carta.CURINGA) {
                cartasStr.add("Carta Curinga");
            } else {
                cartasStr.add(c.getPais().getNome() + " - " + c.getStrSimbolo());
            }
        }
        
        cbCartas.setModel(new javax.swing.AbstractListModel() {
            public int getSize() { return cartasStr.size(); }
            public Object getElementAt(int i) { return cartasStr.toArray()[i]; }
        });

        setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        cbCartas = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Selecione as Cartas para trocar. Use o Ctrl.");

        jToggleButton1.setText("OK");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(cbCartas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jToggleButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        for (Object obj : cbCartas.getSelectedValues()) {
            cartasTroca.add((Carta) obj);
            // Limpando a janela e retornando.
            dispose();
        }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    /**
     * Cria uma janela para ler o ArrayList<Carta>.
     * @param cartas As cartas para selecionar.
     */
    public static ArrayList<Carta> showDialog(ArrayList<Carta> cartas) {
        TrocarCartasDlg tcDlg = new TrocarCartasDlg(null, true, cartas);

        return tcDlg.cartasTroca;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList cbCartas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}
