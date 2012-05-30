/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ValidatorView.java
 *
 * Created on 14-Mar-2012, 15:40:53
 */
package uk.ac.liv.mzquantml.validator.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import uk.ac.liv.mzquantml.validator.MzQuantMLValidator;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 */
public class ValidatorView extends javax.swing.JFrame {

    private static File currentDirectory;

    /**
     * Creates new form ValidatorView
     */
    public ValidatorView() {

        //... Setting standard look and feel ...//
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getStackTrace());
        } catch (InstantiationException ex) {
            System.out.println(ex.getStackTrace());
        } catch (IllegalAccessException ex) {
            System.out.println(ex.getStackTrace());
        } catch (UnsupportedLookAndFeelException ex) {
            System.out.println(ex.getStackTrace());
        }

        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;

        // Move the window
        this.setLocation(x, y);

        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jtfFileName = new javax.swing.JTextField();
        jbFileSelector = new javax.swing.JButton();
        jbValidate = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaValidationResults = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MzQuantML file validator --- Beta Version");
        setMinimumSize(new java.awt.Dimension(661, 416));
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jbFileSelector.setText("...");
        jbFileSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbFileSelectorActionPerformed(evt);
            }
        });

        jbValidate.setText("Validate Now!");
        jbValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbValidateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jtfFileName, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbFileSelector))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jbValidate)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtfFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbFileSelector))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(jbValidate)
                .addGap(20, 20, 20))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Validation Results"));

        jtaValidationResults.setColumns(20);
        jtaValidationResults.setRows(5);
        jScrollPane1.setViewportView(jtaValidationResults);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-721)/2, (screenSize.height-486)/2, 721, 486);
    }// </editor-fold>//GEN-END:initComponents

    private void jbValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbValidateActionPerformed
        try {
            // TODO add your handling code here:
            String fileName = this.jtfFileName.getText();
            List<Message> results = MzQuantMLValidator.main(fileName);
            this.jtaValidationResults.setLineWrap(true);
            String results_mod = results.toString();
            this.jtaValidationResults.setText(results_mod.substring(2, results_mod.length() - 1));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ValidatorView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbValidateActionPerformed

    private void jbFileSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbFileSelectorActionPerformed
        // TODO add your handling code here:            
        JFileChooser fileChooser = new javax.swing.JFileChooser("user.home");
        fileChooser.setDialogTitle("Select a MzQuantML file");
        fileChooser.setCurrentDirectory(currentDirectory);

        //... Applying file extension filters ...//
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MzQuantML (*.mzq)", "mzq");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            this.jtfFileName.setText(file.getAbsolutePath());
            currentDirectory = file.getParentFile();
        }
    }//GEN-LAST:event_jbFileSelectorActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ValidatorView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ValidatorView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ValidatorView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ValidatorView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new ValidatorView().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbFileSelector;
    private javax.swing.JButton jbValidate;
    private javax.swing.JTextArea jtaValidationResults;
    private javax.swing.JTextField jtfFileName;
    // End of variables declaration//GEN-END:variables
}
