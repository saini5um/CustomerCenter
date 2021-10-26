/*
 * PrepaidNoteDetail.java
 *
 * Created on June 8, 2005, 4:58 PM
 */

package com.maxis.panel;

/**
 *
 * @author  Pankaj Saini
 */
public class PrepaidNoteDetail extends javax.swing.JPanel {
    
    /** Creates new form PrepaidNoteDetail */
    public PrepaidNoteDetail() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        cSGLabel1 = new com.csgsystems.igpa.controls.CSGLabel();
        cSGLabel2 = new com.csgsystems.igpa.controls.CSGLabel();
        cSGComboBox1 = new com.csgsystems.igpa.controls.CSGComboBox();
        cSGTextArea1 = new com.csgsystems.igpa.controls.CSGTextArea();
        cSGLabel3 = new com.csgsystems.igpa.controls.CSGLabel();
        cSGComboBox2 = new com.csgsystems.igpa.controls.CSGComboBox();
        cSGLabel4 = new com.csgsystems.igpa.controls.CSGLabel();
        cSGTextArea2 = new com.csgsystems.igpa.controls.CSGTextArea();

        setLayout(null);

        setPreferredSize(new java.awt.Dimension(300, 240));
        cSGLabel1.setLocalizationKey("Note Code:");
        add(cSGLabel1);
        cSGLabel1.setBounds(20, 20, 70, 20);

        cSGLabel2.setLocalizationKey("Note Text:");
        add(cSGLabel2);
        cSGLabel2.setBounds(20, 50, 70, 20);

        cSGComboBox1.setAttributeName("Reason");
        cSGComboBox1.setDomainName("PrepaidNotes");
        add(cSGComboBox1);
        cSGComboBox1.setBounds(110, 120, 150, 20);

        cSGTextArea1.setLineWrap(true);
        cSGTextArea1.setAttributeName("NoteText");
        cSGTextArea1.setDomainName("PrepaidNotes");
        add(cSGTextArea1);
        cSGTextArea1.setBounds(110, 50, 150, 60);

        cSGLabel3.setLocalizationKey("Reason:");
        add(cSGLabel3);
        cSGLabel3.setBounds(20, 120, 60, 20);

        cSGComboBox2.setAttributeName("NoteCode");
        cSGComboBox2.setDomainName("PrepaidNotes");
        add(cSGComboBox2);
        cSGComboBox2.setBounds(110, 20, 150, 20);

        cSGLabel4.setLocalizationKey("Remarks:");
        add(cSGLabel4);
        cSGLabel4.setBounds(20, 150, 50, 20);

        cSGTextArea2.setLineWrap(true);
        cSGTextArea2.setAttributeName("Remarks");
        cSGTextArea2.setDomainName("PrepaidNotes");
        add(cSGTextArea2);
        cSGTextArea2.setBounds(110, 150, 150, 70);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.csgsystems.igpa.controls.CSGComboBox cSGComboBox1;
    private com.csgsystems.igpa.controls.CSGComboBox cSGComboBox2;
    private com.csgsystems.igpa.controls.CSGLabel cSGLabel1;
    private com.csgsystems.igpa.controls.CSGLabel cSGLabel2;
    private com.csgsystems.igpa.controls.CSGLabel cSGLabel3;
    private com.csgsystems.igpa.controls.CSGLabel cSGLabel4;
    private com.csgsystems.igpa.controls.CSGTextArea cSGTextArea1;
    private com.csgsystems.igpa.controls.CSGTextArea cSGTextArea2;
    // End of variables declaration//GEN-END:variables
    
}