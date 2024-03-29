
/*
 * ValidatorView.java
 *
 * Created on 14-Mar-2012, 15:40:53
 */
package uk.ac.liv.mzquantml.validator.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.log4j.Level;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.liv.mzquantml.validator.MzQuantMLSchemaValidator;
import uk.ac.liv.mzquantml.validator.MzQuantMLValidator;
import uk.ac.liv.mzquantml.validator.cvmapping.MzQuantMLCvValidator;
import uk.ac.liv.mzquantml.validator.utils.Gzipper;
import uk.ac.liv.mzquantml.validator.utils.Message;

/**
 *
 * @author Da Qi
 */
public class ValidatorView extends javax.swing.JFrame {

    private static File currentDirectory;
    private static File mzqFile;

    /**
     * Creates new form ValidatorView
     */
    public ValidatorView() {

        //... Setting standard look and feel ...//
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException ex) {
            System.out.println(ex.getStackTrace());
        }
        catch (InstantiationException ex) {
            System.out.println(ex.getStackTrace());
        }
        catch (IllegalAccessException ex) {
            System.out.println(ex.getStackTrace());
        }
        catch (UnsupportedLookAndFeelException ex) {
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
        jtfSchema = new javax.swing.JTextField();
        jbSchema = new javax.swing.JButton();
        schemaValidationCheckbox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaValidationResults = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MzQuantML file validator -- v 1.0 (build 1.0.2.20150319)");
        setMinimumSize(new java.awt.Dimension(661, 416));

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

        jtfSchema.setText("Use default schema mzQuantML_1_0_0.xsd");
        jtfSchema.setEnabled(false);

        jbSchema.setText("Schema");
        jbSchema.setEnabled(false);
        jbSchema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSchemaActionPerformed(evt);
            }
        });

        schemaValidationCheckbox.setText("Schema validation (Warning: performing schema validation on large files could take hours!)");
        schemaValidationCheckbox.setToolTipText("It is recommended to use other XML software for schema validation.");
        schemaValidationCheckbox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                schemaValidationCheckboxStateChanged(evt);
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
                        .addComponent(schemaValidationCheckbox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbValidate))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jtfFileName)
                            .addComponent(jtfSchema))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbSchema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbFileSelector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtfFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbFileSelector))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtfSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbSchema))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbValidate)
                    .addComponent(schemaValidationCheckbox))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Validation Results"));

        jtaValidationResults.setEditable(false);
        jtaValidationResults.setColumns(20);
        jtaValidationResults.setRows(5);
        jScrollPane1.setViewportView(jtaValidationResults);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
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

        getAccessibleContext().setAccessibleName("MzQuantML file validator -- v 1.0 (build 1.0.2.20150319)");

        setSize(new java.awt.Dimension(721, 486));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbValidateActionPerformed

        long startTime = System.currentTimeMillis();
        // Set the view to the the starting point every time click the validate button
        this.jtaValidationResults.setText("");
        this.jScrollPane1.getViewport().setViewPosition(new Point(0, 0));
        this.update(this.getGraphics());

        // Start validate.
        String mzqFn = this.jtfFileName.getText();

        if (mzqFn.isEmpty()) {
            JOptionPane.showMessageDialog(this.jPanel2, "Please input an mzQuantML file!");
        }
        else {
            this.rootPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            this.jtaValidationResults.setLineWrap(true);
            this.jtaValidationResults.setText("INFO: Starting validation process......\nINFO: Loading MzQuantML file......\n\n");
            this.update(this.getGraphics());
            if (mzqFn.endsWith(".mzq")) {
                mzqFile = new File(mzqFn);
            }
            else if (mzqFn.endsWith(".gz")) {
                this.jtaValidationResults.append("Uncompressing the gz file ... \n\n");
                this.update(this.getGraphics());
                mzqFile = getUnZipFile(mzqFn);
            }
//            try {
            // if the schema validation checkbox is selected, do schema validation first
            if (this.schemaValidationCheckbox.isSelected()) {
                doSchemaValidation();
                this.update(this.getGraphics());
            }

            // semantic validation
            doSemantciValidation();

            // cv mapping rule validation
            try {
                doCvMappingValidation();
            }
            catch (FileNotFoundException ex) {
                this.jtaValidationResults.append("Can't find mzQuantML-mapping_1.0.0.xml or ontologies.xml! \n\n");
                this.update(this.getGraphics());
            }
            catch (Exception e) {
                this.jtaValidationResults.append("Exception in Cv mapping validation: " + e.getMessage() + "\n");
                this.update(this.getGraphics());
            }


            /*
             * final output
             */
            this.jtaValidationResults.append("\nMzQuantML validation process is finished.The file is valid unless there is error message displaying above.\n");
            this.update(this.getGraphics());
            long stopTime = System.currentTimeMillis();
            System.out.println("Running time (ms): " + String.valueOf(stopTime - startTime));

        }
        // clean the unzipped file from temp
        if (mzqFn.endsWith(".gz")) {
            Gzipper.deleteFile(mzqFile);
        }

        this.rootPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jbValidateActionPerformed

    private void jbFileSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbFileSelectorActionPerformed

        JFileChooser fileChooser = new javax.swing.JFileChooser("user.home");
        fileChooser.setDialogTitle("Select a MzQuantML file");
        fileChooser.setCurrentDirectory(currentDirectory);

        //... Applying file extension filters ...//
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Uncompressed or Gzip Compressed MzQuantML (*.mzq; *.gz)", "mzq", "gz");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            this.jtfFileName.setText(file.getAbsolutePath());
            currentDirectory = file.getParentFile();
        }
    }//GEN-LAST:event_jbFileSelectorActionPerformed

    private void jbSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSchemaActionPerformed

        JFileChooser fileChooser = new javax.swing.JFileChooser("user.home");
        fileChooser.setDialogTitle("Select a MzQuantML schema file");
        fileChooser.setCurrentDirectory(currentDirectory);

        //... Applying file extension filters ...//
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Schema (*.xsd)", "xsd");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            this.jtfSchema.setText(file.getAbsolutePath());
            currentDirectory = file.getParentFile();
        }
    }//GEN-LAST:event_jbSchemaActionPerformed

    private void schemaValidationCheckboxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_schemaValidationCheckboxStateChanged

        this.jbSchema.setEnabled(schemaValidationCheckbox.isSelected());
        this.jtfSchema.setEnabled(schemaValidationCheckbox.isSelected());
    }//GEN-LAST:event_schemaValidationCheckboxStateChanged

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
        }
        catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ValidatorView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ValidatorView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ValidatorView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex) {
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
    private javax.swing.JButton jbSchema;
    private javax.swing.JButton jbValidate;
    private javax.swing.JTextArea jtaValidationResults;
    private javax.swing.JTextField jtfFileName;
    private javax.swing.JTextField jtfSchema;
    private javax.swing.JCheckBox schemaValidationCheckbox;
    // End of variables declaration//GEN-END:variables

    private void doSchemaValidation() {
        // check if the schema file exists
        if (!this.jtfSchema.getText().isEmpty() && (new File(this.jtfSchema.getText())).isFile()) {
            File schemaFile = new File(this.jtfSchema.getText());
            MzQuantMLSchemaValidator msv = new MzQuantMLSchemaValidator(mzqFile, schemaFile);
            List<Message> svRes = msv.getValidateResult();

            if (!svRes.isEmpty()) {
                this.jtaValidationResults.append("\nSchema validation message(s): \n\n");
                this.update(this.getGraphics());
            }
            else {
                this.jtaValidationResults.append("\nDocument is schema valid.\n");
                this.update(this.getGraphics());
            }

            for (Message msg : svRes) {
                this.jtaValidationResults.append(msg.getMessage());
                this.update(this.getGraphics());
            }
        }
        else {
            if (!new File(this.jtfSchema.getText()).isFile()) {
                this.jtaValidationResults.append("Use default schema file mzQuantML_1_0_0.xsd.\n");
                this.update(this.getGraphics());
            }
            File schemaFile = getSchemaFile();

            this.jtaValidationResults.append("\nStart schema validation (Warning: this could take hours for large file)...... \n");
            this.update(this.getGraphics());

            MzQuantMLSchemaValidator msv = new MzQuantMLSchemaValidator(mzqFile, schemaFile);
            List<Message> svRes = msv.getValidateResult();

            if (!svRes.isEmpty()) {
                this.jtaValidationResults.append("\nSchema validation message(s): \n\n");
                this.update(this.getGraphics());
            }
            else {
                this.jtaValidationResults.append("\nDocument is schema valid.\n");
                this.update(this.getGraphics());
            }

            for (Message msg : svRes) {
                this.jtaValidationResults.append(msg.getMessage());
                this.update(this.getGraphics());
            }
        }
    }

    private void doSemantciValidation() {
        try {
            MzQuantMLValidator mzqValidator = new MzQuantMLValidator(mzqFile);
            List<Message> results = mzqValidator.validate();
            if (!results.isEmpty() && messagesContain(results, Level.ERROR)) {
                this.jtaValidationResults.append("Semantic validation message(s): \n\n");
                this.update(this.getGraphics());
            }
            for (Message m : results) {
                // control the semantic message level to display
                if (m.getLevel().isGreaterOrEqual(Level.ERROR)) {
                    this.jtaValidationResults.append(m.getLevel().toString() + ": " + m.getMessage());
                    this.update(this.getGraphics());
                }
            }
        }
        catch (FileNotFoundException ex) {
            this.jtaValidationResults.append(ex.getMessage());
            this.update(this.getGraphics());
        }
        catch (Exception e) {
            this.jtaValidationResults.append(e.getMessage());
            this.update(this.getGraphics());
        }
    }

    private void doCvMappingValidation()
            throws FileNotFoundException {
        InputStream ontology = getOntologiesFileInputStream();
        InputStream mappingRule = getGeneralMappingRuleInputStream();
        MzQuantMLCvValidator cvValidator;
        try {
            cvValidator = new MzQuantMLCvValidator(ontology, mappingRule);
            final Collection<ValidatorMessage> validationResult = cvValidator.startValidation(mzqFile.getAbsolutePath());

            if (!validationResult.isEmpty()) {
                this.jtaValidationResults.append("\nCv mapping validation message(s): \n");
                this.update(this.getGraphics());
            }
            for (ValidatorMessage vMsg : validationResult) {
                // convert MessageLevel to log4j Level
                Level lev = Level.toLevel(vMsg.getLevel().toString());
                if (vMsg.getRule() != null) {
                    this.jtaValidationResults.append("\n" + lev + ": " + vMsg.getRule().toString() + " -- " + vMsg.getMessage() + "\n");
                }
                else {
                    this.jtaValidationResults.append("\n" + lev + ": " + vMsg.getMessage() + "\n");
                }
                this.update(this.getGraphics());
            }
        }
        catch (ValidatorException vex) {
            this.jtaValidationResults.append("ValidationException in cvValidator: " + vex.getMessage() + "\n");
            this.update(this.getGraphics());
        }
        catch (OntologyLoaderException olex) {
            this.jtaValidationResults.append("OntologyLoaderException in cvValidator; " + olex.getMessage() + "\n");
            this.update(this.getGraphics());
        }
        catch (Exception ex) {
            this.jtaValidationResults.append("Other exceptions in  doCvMappingValidation: " + ex.getMessage() + "\n");
            this.update(this.getGraphics());
        }
    }

    private File getSchemaFile() {
        InputStream is = null;
        OutputStream os = null;
        File file = null;
        try {
            //File file = new File(getClass().getClassLoader().getResource("mzQuantML_1_0_0.xsd").getFile());            
            //is = new FileInputStream("mzQuantML_1_0_0.xsd");
            is = getClass().getClassLoader().getResourceAsStream("mzQuantML_1_0_0.xsd");
            file = File.createTempFile("tmp_" + new Random().toString(), ".xsd");
            os = new FileOutputStream(file);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
        }
        catch (IOException ex) {
            this.jtaValidationResults.append("IOException in getSchemaFile, schema file not found" + ex.getMessage() + "\n");
            this.update(this.getGraphics());
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return file;
    }

    private File getUnZipFile(String mzqFn) {
        File f = Gzipper.extractFile(new File(mzqFn));
        return f;
    }

    /*
     * protected methods
     */
    protected InputStream getOntologiesFileInputStream()
            throws FileNotFoundException {

        File file = new File(getClass().getClassLoader().getResource("ontologies.xml").getFile());
        if (!file.exists()) {
            ClassLoader cl = this.getClass().getClassLoader();
            return cl.getResourceAsStream("ontologies.xml");
        }
        return new FileInputStream(file);
    }

    protected InputStream getGeneralMappingRuleInputStream()
            throws FileNotFoundException {
        String mappingRuleFile = getClass().getClassLoader().
                getResource("mzQuantML-mapping_1.0.0.xml").getFile();
        File file = new File(mappingRuleFile);
        if (!file.exists()) {
            ClassLoader cl = this.getClass().getClassLoader();
            return cl.getResourceAsStream("mzQuantML-mapping_1.0.0.xml");
        }
        return new FileInputStream(file);
    }

    /**
     * Check if the list of message contains message with minimum level Lvl
     *
     * @param msgList The list of Message
     * @param lev     The minimum of message level
     *
     * @return true if the list contains message with minimum level
     */
    private boolean messagesContain(List<Message> msgList, Level lev) {

        for (Message msg : msgList) {
            if (msg.getLevel().isGreaterOrEqual(lev)) {
                return true;
            }
        }
        return false;
    }

}
