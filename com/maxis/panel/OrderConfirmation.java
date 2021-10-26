/*
 * OrderConfirmation.java
 *
 * Created on November 23, 2002, 11:48 AM
 */

package com.maxis.panel;


/**
 *
 * @author  prev01
 */
public class OrderConfirmation extends AbstractHTMLPanel {
    
    /** Creates new form OrderConfirmation */
    public OrderConfirmation() {
        initComponents();
        velAccountConfirmation.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                DetailHyperlinkFilter(evt, velAccountConfirmation);
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        velAccountConfirmation = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();

        setLayout(new java.awt.BorderLayout());

        setPreferredSize(new java.awt.Dimension(670, 550));

        velAccountConfirmation.setVelocityTemplateUri("template/OrderInfo.vm");
        add(velAccountConfirmation, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velAccountConfirmation;
    // End of variables declaration//GEN-END:variables
    
}
