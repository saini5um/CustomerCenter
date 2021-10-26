/*
 * BaseAccountRating.java
 *
 * Created on August 26, 2002, 12:51 PM
 */

package com.maxis.panel;

import com.csgsystems.localization.ResourceManager;

/**
 *
 * @author  mccm06
 */
public class BaseAccountRating extends javax.swing.JPanel {
    
    /** Creates new form BaseAccountRating */
    public BaseAccountRating() {
        initComponents();
    }
            
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        lblExchangeRateClass = new com.csgsystems.igpa.controls.CSGLabel();
        lblCurrency = new com.csgsystems.igpa.controls.CSGLabel();
        lblOwningCostCenter = new com.csgsystems.igpa.controls.CSGLabel();
        cbOwningCostCenter = new com.csgsystems.igpa.controls.CSGComboBox();
        cbCurrency = new com.csgsystems.igpa.controls.CSGComboBox();
        cbExchangeRateClass = new com.csgsystems.igpa.controls.CSGComboBox();
        lblCreditRating = new com.csgsystems.igpa.controls.CSGLabel();
        edtCreditRating = new com.csgsystems.igpa.controls.CSGEdit();
        edtChargeThreshold = new com.csgsystems.igpa.controls.CSGNumberEdit();
        lblChargeThreshold = new com.csgsystems.igpa.controls.CSGLabel();
        edtCreditThreshold = new com.csgsystems.igpa.controls.CSGNumberEdit();
        lblCreditThreshold = new com.csgsystems.igpa.controls.CSGLabel();
        lblCreditStatus = new com.csgsystems.igpa.controls.CSGLabel();
        edtCreditStatus = new com.csgsystems.igpa.controls.CSGNumberEdit();

        setLayout(new java.awt.BorderLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 0, 0)));
        setMinimumSize(new java.awt.Dimension(300, 185));
        setPreferredSize(new java.awt.Dimension(885, 605));
        jPanel1.setLayout(null);

        jPanel1.setBorder(new javax.swing.border.TitledBorder(ResourceManager.getString( "AccountRating.title.Rating" )));
        lblExchangeRateClass.setLocalizationKey("AccountRating.lblExchangeRateClass");
        jPanel1.add(lblExchangeRateClass);
        lblExchangeRateClass.setBounds(10, 20, 140, 20);

        lblCurrency.setLocalizationKey("AccountRating.lblCurrency");
        jPanel1.add(lblCurrency);
        lblCurrency.setBounds(10, 45, 140, 20);

        lblOwningCostCenter.setLocalizationKey("AccountRating.lblOwningCostCenter");
        jPanel1.add(lblOwningCostCenter);
        lblOwningCostCenter.setBounds(10, 70, 140, 20);

        cbOwningCostCenter.setAttributeName("OwningCostCtr");
        cbOwningCostCenter.setDomainName("Account");
        jPanel1.add(cbOwningCostCenter);
        cbOwningCostCenter.setBounds(150, 70, 150, 20);

        cbCurrency.setAttributeName("CurrencyCode");
        cbCurrency.setDomainName("Account");
        jPanel1.add(cbCurrency);
        cbCurrency.setBounds(150, 45, 150, 20);

        cbExchangeRateClass.setAttributeName("ExrateClass");
        cbExchangeRateClass.setDomainName("Account");
        cbExchangeRateClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbExchangeRateClassActionPerformed(evt);
            }
        });

        jPanel1.add(cbExchangeRateClass);
        cbExchangeRateClass.setBounds(150, 20, 150, 20);

        lblCreditRating.setLocalizationKey("AccountCreditRating.lblCreditRating");
        jPanel1.add(lblCreditRating);
        lblCreditRating.setBounds(10, 95, 140, 20);

        edtCreditRating.setAttributeName("CreditRating");
        edtCreditRating.setDomainName("Account");
        jPanel1.add(edtCreditRating);
        edtCreditRating.setBounds(150, 95, 150, 20);

        edtChargeThreshold.setText("cSGNumberEdit1");
        edtChargeThreshold.setAttributeName("ChargeThreshold");
        edtChargeThreshold.setControlFormat(3);
        edtChargeThreshold.setDomainName("Account");
        jPanel1.add(edtChargeThreshold);
        edtChargeThreshold.setBounds(150, 170, 150, 20);

        lblChargeThreshold.setLocalizationKey("AccountRating.lblChargeThreshold");
        jPanel1.add(lblChargeThreshold);
        lblChargeThreshold.setBounds(10, 170, 140, 20);

        edtCreditThreshold.setText("cSGNumberEdit1");
        edtCreditThreshold.setAttributeName("CreditThresh");
        edtCreditThreshold.setControlFormat(3);
        edtCreditThreshold.setDomainName("Account");
        jPanel1.add(edtCreditThreshold);
        edtCreditThreshold.setBounds(150, 120, 150, 20);

        lblCreditThreshold.setLocalizationKey("AccountRating.lblCreditThreshold");
        jPanel1.add(lblCreditThreshold);
        lblCreditThreshold.setBounds(10, 120, 140, 20);

        lblCreditStatus.setLocalizationKey("AccountRating.lblCreditStatus");
        jPanel1.add(lblCreditStatus);
        lblCreditStatus.setBounds(10, 145, 140, 20);

        edtCreditStatus.setAttributeName("CredStatus");
        edtCreditStatus.setDomainName("Account");
        jPanel1.add(edtCreditStatus);
        edtCreditStatus.setBounds(150, 145, 150, 20);

        add(jPanel1, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents

    private void cbExchangeRateClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbExchangeRateClassActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_cbExchangeRateClassActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.csgsystems.igpa.controls.CSGComboBox cbCurrency;
    private com.csgsystems.igpa.controls.CSGComboBox cbExchangeRateClass;
    private com.csgsystems.igpa.controls.CSGComboBox cbOwningCostCenter;
    private com.csgsystems.igpa.controls.CSGNumberEdit edtChargeThreshold;
    private com.csgsystems.igpa.controls.CSGEdit edtCreditRating;
    private com.csgsystems.igpa.controls.CSGNumberEdit edtCreditStatus;
    private com.csgsystems.igpa.controls.CSGNumberEdit edtCreditThreshold;
    private javax.swing.JPanel jPanel1;
    private com.csgsystems.igpa.controls.CSGLabel lblChargeThreshold;
    private com.csgsystems.igpa.controls.CSGLabel lblCreditRating;
    private com.csgsystems.igpa.controls.CSGLabel lblCreditStatus;
    private com.csgsystems.igpa.controls.CSGLabel lblCreditThreshold;
    private com.csgsystems.igpa.controls.CSGLabel lblCurrency;
    private com.csgsystems.igpa.controls.CSGLabel lblExchangeRateClass;
    private com.csgsystems.igpa.controls.CSGLabel lblOwningCostCenter;
    // End of variables declaration//GEN-END:variables
    
}
