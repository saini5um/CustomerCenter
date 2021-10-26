/*
 * AllocateFunds.java
 *
 * Created on December 4, 2003, 7:52 AM
 */

package com.maxis.panel;

import com.csgsystems.igpa.forms.ContextFormListener;
import com.csgsystems.igpa.forms.ContextFormEvent;
import com.csgsystems.igpa.utils.ContextFinder;
import com.csgsystems.localization.ResourceManager;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.security.SecurityManager;
import org.apache.commons.logging.*;
import com.csgsystems.fx.security.remote.*;
import com.csgsystems.fx.security.util.FxException;
import com.csgsystems.igpa.forms.ICSGContextForm;
import com.maxis.util.*;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author  akum01
 */
public class AllocateFunds extends javax.swing.JPanel implements ContextFormListener {

    private static Log log = null;
    
    static {
        try {
            log = LogFactory.getLog(AllocateFunds.class);
        } catch (Exception ex) {
        }
    }

    private ContextFinder ctxFinder = new ContextFinder(this);

    /** Creates new form AllocateFunds */
    public AllocateFunds() {
        initComponents();
    }

    private void setupAllocateFunds() {
        IContext ctx = ctxFinder.findContext();
        if (ctx != null) {
            IPersistentObject account = ctx.getObject("Account", null);
            if (account != null) {
                long lAmountAvailableToDistribute = account.getAttributeDataAsLong("VirtualAvailableAmountToDistribute");
                long lAmountAvailableToRefund = account.getAttributeDataAsLong("VirtualAvailableAmountToRefund");

                // determine if user is authorized and check the allocation amounts; set the radio buttons
                // accordingly 
                SecurityManager securityManager = SecurityManager.getInstance();
                try {
                    if (!securityManager.isAuthorized("Refund.RefundCreate", SecurityManager.SERVICE_RSRC)
                        || lAmountAvailableToRefund == 0) {
                            
                        btnRefund.setEnabled(false);
                    }
                    
                    if (!securityManager.isAuthorized("DistributeFunds.DistributeFunds", SecurityManager.SERVICE_RSRC)
                        || lAmountAvailableToDistribute == 0) {
                            
                        btnDistribute.setEnabled(false);
                    }
                } catch (Exception e) {
                    log.error("Error checking authorization", e);
                }
                
                if (btnRefund.isEnabled()) {
                    btnRefund.doClick();
                } else if (btnDistribute.isEnabled()) {
                    btnDistribute.doClick();
                } else {
                    // Disable all the fields
                    pnlAllocateRefund.setEnabled(false);
                    pnlPayeeDetails.setEnabled(false);

                    cbRefundReasonCode.setEnabled(false);
                    cbPayMethod.setEnabled(false);
                    cbBalanceId.setEnabled(false);
                    edtAmountToRefund.setEnabled(false);
                    chkAlternatePayee.setEnabled(false);
                }
            }
        }
    }

    public void contextFormStateChanged(ContextFormEvent contextFormEvent) {
        if (contextFormEvent.getType() == ContextFormEvent.POST_INIT_CONTROLS) {
            setupAllocateFunds();
            ctxFinder.enableButton(ICSGContextForm.APPLY_BUTTON, false);
        } else if ( contextFormEvent.getType() == ContextFormEvent.PRE_UPDATE_BIGFOOT ) {
            IContext context = ctxFinder.findContext();
            IPersistentObject refund = context.getObject("Refund", null);
            if ( refund == null ) System.out.println("null refund object!");
            if ( refund.getAttribute("CheckNum").isEmpty() ) return;
            
            if ( updateRefundStatus(refund) == false )
                JOptionPane.showMessageDialog(null, "Failed to update check number", "Error!", JOptionPane.ERROR_MESSAGE);
            
        } 
    }

    public boolean updateRefundStatus(IPersistentObject obj) {
        boolean success = false;
        
        String strQuery = "UPDATE REFUND SET check_num =  ?" ;
               strQuery = strQuery + "WHERE tracking_id = ? and tracking_id_serv = ?";
               
        RemoteDBConnection conn = tuxJDBCManager.getInstance().getCurrentConnection("CUST");
        if ( conn == null )  return success;
        
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(new SQL(strQuery, "update"));
            stmt.setString(1, obj.getAttributeDataAsString("CheckNum"));
            stmt.setString(2, obj.getAttributeDataAsString("TrackingId"));
            stmt.setString(3, obj.getAttributeDataAsString("TrackingIdServ"));
        } catch (FxException fx) {
            fx.printStackTrace();
            success = false;
        } catch (SQLException sEx) {
            sEx.printStackTrace();
            success = false;
        }

        try {
            int retval = stmt.executeUpdate();
            stmt.close();
            success = true;
        } catch (SQLException sEx) {
            sEx.printStackTrace();
            success = false;
        }
        
        return success;
     }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        btngrpAllocateFunds = new javax.swing.ButtonGroup();
        pnlAllocateFunds = new javax.swing.JPanel();
        btnDistribute = new javax.swing.JRadioButton();
        btnRefund = new javax.swing.JRadioButton();
        edtRefundBalanceAmount = new com.csgsystems.igpa.controls.CSGNumberEdit();
        edtInvoiceBalanceAmount = new com.csgsystems.igpa.controls.CSGNumberEdit();
        pnlAllocateRefund = new javax.swing.JPanel();
        lblAmountToRefund = new com.csgsystems.igpa.controls.CSGLabel();
        lblRefundReasonCode = new com.csgsystems.igpa.controls.CSGLabel();
        lblBalanceId = new com.csgsystems.igpa.controls.CSGLabel();
        lblPaymentMethod = new com.csgsystems.igpa.controls.CSGLabel();
        cbRefundReasonCode = new com.csgsystems.igpa.controls.CSGComboBox();
        cbBalanceId = new com.csgsystems.igpa.controls.CSGComboBox();
        cbPayMethod = new com.csgsystems.igpa.controls.CSGComboBox();
        edtAmountToRefund = new com.csgsystems.igpa.controls.CSGNumberEdit();
        pnlPaymentDetails = new javax.swing.JPanel();
        lblCCardId = new com.csgsystems.igpa.controls.CSGLabel();
        lblCheckNum = new com.csgsystems.igpa.controls.CSGLabel();
        edtCCardId = new com.csgsystems.igpa.controls.CSGEdit();
        edtCheckNum = new com.csgsystems.igpa.controls.CSGEdit();
        pnlPayeeDetails = new javax.swing.JPanel();
        lblPayeeFirstName = new com.csgsystems.igpa.controls.CSGLabel();
        lblPayeeLastName = new com.csgsystems.igpa.controls.CSGLabel();
        lblCompanyName = new com.csgsystems.igpa.controls.CSGLabel();
        lblPayeeAddress = new com.csgsystems.igpa.controls.CSGLabel();
        lblCity = new com.csgsystems.igpa.controls.CSGLabel();
        lblState = new com.csgsystems.igpa.controls.CSGLabel();
        lblZipPostalCode = new com.csgsystems.igpa.controls.CSGLabel();
        lblCountry = new com.csgsystems.igpa.controls.CSGLabel();
        edtPayeeFirstName = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeLastName = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeCompanyName = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeAddressLine1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeAddressLine2 = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeAddressLine3 = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeCity = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeState = new com.csgsystems.igpa.controls.CSGEdit();
        edtZipPostalCode = new com.csgsystems.igpa.controls.CSGEdit();
        lblAlternatePayee = new com.csgsystems.igpa.controls.CSGLabel();
        chkAlternatePayee = new com.csgsystems.igpa.controls.CSGCheckBox();
        lblPayeeMiddleInit = new com.csgsystems.igpa.controls.CSGLabel();
        edtAltPayeeMiddleInit = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeAddressLine4 = new com.csgsystems.igpa.controls.CSGEdit();
        lblAltCurrencyCode = new com.csgsystems.igpa.controls.CSGLabel();
        cbAltCurrencyCode = new com.csgsystems.igpa.controls.CSGComboBox();
        cbPayeeCountryCode = new com.csgsystems.igpa.controls.CSGComboBox();

        setLayout(null);

        setMinimumSize(new java.awt.Dimension(435, 650));
        setPreferredSize(new java.awt.Dimension(435, 650));
        pnlAllocateFunds.setLayout(null);

        pnlAllocateFunds.setBorder(new javax.swing.border.TitledBorder(ResourceManager.getString( "RefundCreate.title.AllocateSuspenseFunds" )));
        btngrpAllocateFunds.add(btnDistribute);
        btnDistribute.setText(ResourceManager.getString( "RefundCreate.btnDistribute" ));
        btnDistribute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DistributeActionPerformed(evt);
            }
        });

        pnlAllocateFunds.add(btnDistribute);
        btnDistribute.setBounds(20, 15, 225, 23);

        btngrpAllocateFunds.add(btnRefund);
        btnRefund.setText(ResourceManager.getString( "RefundCreate.btnRefund" ));
        btnRefund.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refundActionPerformed(evt);
            }
        });

        pnlAllocateFunds.add(btnRefund);
        btnRefund.setBounds(20, 40, 225, 23);

        edtRefundBalanceAmount.setAttributeName("VirtualAvailableAmountToRefund");
        edtRefundBalanceAmount.setControlFormat(3);
        edtRefundBalanceAmount.setDomainName("Account");
        pnlAllocateFunds.add(edtRefundBalanceAmount);
        edtRefundBalanceAmount.setBounds(250, 45, 150, 20);

        edtInvoiceBalanceAmount.setAttributeName("VirtualAvailableAmountToDistribute");
        edtInvoiceBalanceAmount.setControlFormat(3);
        edtInvoiceBalanceAmount.setDomainName("Account");
        pnlAllocateFunds.add(edtInvoiceBalanceAmount);
        edtInvoiceBalanceAmount.setBounds(250, 20, 150, 20);

        add(pnlAllocateFunds);
        pnlAllocateFunds.setBounds(10, 5, 415, 75);

        pnlAllocateRefund.setLayout(null);

        pnlAllocateRefund.setBorder(new javax.swing.border.TitledBorder(ResourceManager.getString( "RefundCreate.title.RefundDetails" )));
        pnlAllocateRefund.setMinimumSize(new java.awt.Dimension(380, 170));
        lblAmountToRefund.setLocalizationKey("RefundCreate.lblAmountToRefund");
        pnlAllocateRefund.add(lblAmountToRefund);
        lblAmountToRefund.setBounds(20, 20, 225, 20);

        lblRefundReasonCode.setLocalizationKey("RefundCreate.lblRefundReason");
        pnlAllocateRefund.add(lblRefundReasonCode);
        lblRefundReasonCode.setBounds(20, 45, 225, 20);

        lblBalanceId.setLocalizationKey("RefundCreate.lblBalanceId");
        pnlAllocateRefund.add(lblBalanceId);
        lblBalanceId.setBounds(20, 70, 225, 20);

        lblPaymentMethod.setLocalizationKey("RefundCreate.lblPaymentMethod");
        pnlAllocateRefund.add(lblPaymentMethod);
        lblPaymentMethod.setBounds(20, 95, 225, 20);

        cbRefundReasonCode.setAtomName("RefundReasonCode");
        cbRefundReasonCode.setDomainName("Refund");
        pnlAllocateRefund.add(cbRefundReasonCode);
        cbRefundReasonCode.setBounds(250, 45, 150, 20);

        cbBalanceId.setAttributeName("OpenItemId");
        cbBalanceId.setDomainName("Refund");
        pnlAllocateRefund.add(cbBalanceId);
        cbBalanceId.setBounds(250, 70, 150, 20);

        cbPayMethod.setAttributeName("RefundType");
        cbPayMethod.setDomainName("Refund");
        cbPayMethod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPayMethodActionPerformed(evt);
            }
        });

        pnlAllocateRefund.add(cbPayMethod);
        cbPayMethod.setBounds(250, 95, 150, 20);

        edtAmountToRefund.setAttributeName("Amount");
        edtAmountToRefund.setControlFormat(3);
        edtAmountToRefund.setDomainName("Refund");
        pnlAllocateRefund.add(edtAmountToRefund);
        edtAmountToRefund.setBounds(250, 20, 150, 20);

        pnlPaymentDetails.setLayout(null);

        lblCCardId.setLocalizationKey("RefundCreate.lblCCardId");
        pnlPaymentDetails.add(lblCCardId);
        lblCCardId.setBounds(30, 0, 210, 20);

        lblCheckNum.setLocalizationKey("RefundCreate.lblCheckNum");
        pnlPaymentDetails.add(lblCheckNum);
        lblCheckNum.setBounds(30, 25, 210, 20);

        edtCCardId.setAttributeName("PayeeCounty");
        edtCCardId.setDomainName("Refund");
        pnlPaymentDetails.add(edtCCardId);
        edtCCardId.setBounds(240, 0, 150, 20);

        edtCheckNum.setAttributeName("CheckNum");
        edtCheckNum.setDomainName("Refund");
        pnlPaymentDetails.add(edtCheckNum);
        edtCheckNum.setBounds(240, 25, 150, 20);

        pnlAllocateRefund.add(pnlPaymentDetails);
        pnlPaymentDetails.setBounds(10, 120, 390, 0);

        add(pnlAllocateRefund);
        pnlAllocateRefund.setBounds(10, 85, 415, 180);

        pnlPayeeDetails.setLayout(null);

        pnlPayeeDetails.setBorder(new javax.swing.border.TitledBorder(ResourceManager.getString( "RefundCreate.title.PayeeInformation" )));
        pnlPayeeDetails.setMinimumSize(new java.awt.Dimension(380, 660));
        pnlPayeeDetails.setPreferredSize(new java.awt.Dimension(380, 660));
        lblPayeeFirstName.setLocalizationKey("RefundCreate.lblPayeeFirstName");
        pnlPayeeDetails.add(lblPayeeFirstName);
        lblPayeeFirstName.setBounds(20, 45, 225, 20);

        lblPayeeLastName.setLocalizationKey("RefundCreate.lblPayeeLastName");
        lblPayeeLastName.setMaximumSize(new java.awt.Dimension(164, 16));
        pnlPayeeDetails.add(lblPayeeLastName);
        lblPayeeLastName.setBounds(20, 95, 225, 20);

        lblCompanyName.setLocalizationKey("RefundCreate.lblCompanyName");
        lblCompanyName.setMaximumSize(new java.awt.Dimension(164, 16));
        pnlPayeeDetails.add(lblCompanyName);
        lblCompanyName.setBounds(20, 120, 225, 20);

        lblPayeeAddress.setLocalizationKey("RefundCreate.lblAddress");
        lblPayeeAddress.setMaximumSize(new java.awt.Dimension(164, 16));
        pnlPayeeDetails.add(lblPayeeAddress);
        lblPayeeAddress.setBounds(20, 145, 225, 20);

        lblCity.setLocalizationKey("RefundCreate.lblCity");
        lblCity.setMaximumSize(new java.awt.Dimension(164, 16));
        pnlPayeeDetails.add(lblCity);
        lblCity.setBounds(20, 245, 140, 20);

        lblState.setLocalizationKey("RefundCreate.lblState");
        lblState.setMaximumSize(new java.awt.Dimension(164, 16));
        pnlPayeeDetails.add(lblState);
        lblState.setBounds(20, 270, 140, 20);

        lblZipPostalCode.setLocalizationKey("RefundCreate.lblZipPostalCode");
        lblZipPostalCode.setMaximumSize(new java.awt.Dimension(164, 16));
        pnlPayeeDetails.add(lblZipPostalCode);
        lblZipPostalCode.setBounds(20, 295, 140, 20);

        lblCountry.setLocalizationKey("RefundCreate.lblCountry");
        lblCountry.setMaximumSize(new java.awt.Dimension(164, 16));
        pnlPayeeDetails.add(lblCountry);
        lblCountry.setBounds(20, 320, 140, 20);

        edtPayeeFirstName.setAttributeName("PayeeFirst");
        edtPayeeFirstName.setDomainName("Refund");
        edtPayeeFirstName.setEnabled(false);
        pnlPayeeDetails.add(edtPayeeFirstName);
        edtPayeeFirstName.setBounds(250, 45, 150, 20);

        edtPayeeLastName.setAttributeName("PayeeLast");
        edtPayeeLastName.setDomainName("Refund");
        edtPayeeLastName.setEnabled(false);
        pnlPayeeDetails.add(edtPayeeLastName);
        edtPayeeLastName.setBounds(250, 95, 150, 20);

        edtPayeeCompanyName.setAttributeName("PayeeCompany");
        edtPayeeCompanyName.setDomainName("Refund");
        edtPayeeCompanyName.setEnabled(false);
        pnlPayeeDetails.add(edtPayeeCompanyName);
        edtPayeeCompanyName.setBounds(250, 120, 150, 20);

        edtPayeeAddressLine1.setAttributeName("PayeeAddress1");
        edtPayeeAddressLine1.setDomainName("Refund");
        edtPayeeAddressLine1.setEnabled(false);
        pnlPayeeDetails.add(edtPayeeAddressLine1);
        edtPayeeAddressLine1.setBounds(250, 145, 150, 20);

        edtPayeeAddressLine2.setAttributeName("PayeeAddress2");
        edtPayeeAddressLine2.setDomainName("Refund");
        edtPayeeAddressLine2.setEnabled(false);
        pnlPayeeDetails.add(edtPayeeAddressLine2);
        edtPayeeAddressLine2.setBounds(250, 170, 150, 20);

        edtPayeeAddressLine3.setAttributeName("PayeeAddress3");
        edtPayeeAddressLine3.setDomainName("Refund");
        edtPayeeAddressLine3.setEnabled(false);
        pnlPayeeDetails.add(edtPayeeAddressLine3);
        edtPayeeAddressLine3.setBounds(250, 195, 150, 20);

        edtPayeeCity.setAttributeName("PayeeCity");
        edtPayeeCity.setDomainName("Refund");
        edtPayeeCity.setEnabled(false);
        pnlPayeeDetails.add(edtPayeeCity);
        edtPayeeCity.setBounds(250, 245, 150, 20);

        edtPayeeState.setAttributeName("PayeeState");
        edtPayeeState.setDomainName("Refund");
        edtPayeeState.setEnabled(false);
        pnlPayeeDetails.add(edtPayeeState);
        edtPayeeState.setBounds(250, 270, 150, 20);

        edtZipPostalCode.setAttributeName("PayeeZip");
        edtZipPostalCode.setDomainName("Refund");
        edtZipPostalCode.setEnabled(false);
        pnlPayeeDetails.add(edtZipPostalCode);
        edtZipPostalCode.setBounds(250, 295, 150, 20);

        lblAlternatePayee.setLocalizationKey("RefundCreate.lblAlternatePayee");
        pnlPayeeDetails.add(lblAlternatePayee);
        lblAlternatePayee.setBounds(20, 20, 225, 20);

        chkAlternatePayee.setAttributeName("AltPayee");
        chkAlternatePayee.setDomainName("Refund");
        chkAlternatePayee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAlternatePayee(evt);
            }
        });

        pnlPayeeDetails.add(chkAlternatePayee);
        chkAlternatePayee.setBounds(250, 20, 21, 21);

        lblPayeeMiddleInit.setLocalizationKey("RefundCreate.lblPayeeMiddleInit");
        lblPayeeMiddleInit.setMaximumSize(new java.awt.Dimension(164, 16));
        pnlPayeeDetails.add(lblPayeeMiddleInit);
        lblPayeeMiddleInit.setBounds(20, 70, 225, 20);

        edtAltPayeeMiddleInit.setAttributeName("PayeeMiddleName");
        edtAltPayeeMiddleInit.setDomainName("Refund");
        edtAltPayeeMiddleInit.setEnabled(false);
        pnlPayeeDetails.add(edtAltPayeeMiddleInit);
        edtAltPayeeMiddleInit.setBounds(250, 70, 150, 20);

        edtPayeeAddressLine4.setAttributeName("PayeeAddress4");
        edtPayeeAddressLine4.setDomainName("Refund");
        edtPayeeAddressLine4.setEnabled(false);
        pnlPayeeDetails.add(edtPayeeAddressLine4);
        edtPayeeAddressLine4.setBounds(250, 220, 150, 20);

        lblAltCurrencyCode.setLocalizationKey("RefundCreate.lblAltCurrencyCode");
        lblAltCurrencyCode.setMaximumSize(new java.awt.Dimension(164, 16));
        pnlPayeeDetails.add(lblAltCurrencyCode);
        lblAltCurrencyCode.setBounds(20, 345, 140, 20);

        cbAltCurrencyCode.setAttributeName("AltCurrencyCode");
        cbAltCurrencyCode.setDomainName("Refund");
        pnlPayeeDetails.add(cbAltCurrencyCode);
        cbAltCurrencyCode.setBounds(250, 345, 150, 20);

        cbPayeeCountryCode.setAttributeName("PayeeCountryCode");
        cbPayeeCountryCode.setDomainName("Refund");
        pnlPayeeDetails.add(cbPayeeCountryCode);
        cbPayeeCountryCode.setBounds(250, 320, 150, 20);

        add(pnlPayeeDetails);
        pnlPayeeDetails.setBounds(10, 270, 415, 375);

    }
    // </editor-fold>//GEN-END:initComponents

    private void cbPayMethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPayMethodActionPerformed
        setupPayMethod();
    }//GEN-LAST:event_cbPayMethodActionPerformed

    private void setupPayMethod() {  
        IContext ctx = ctxFinder.findContext();
        IPersistentObject refund = ctx.getObject("Refund", null);
        int RefundType = refund.getAttributeDataAsInteger("RefundType");
        switch(RefundType) {
            case 1:
                // none selected or check
                pnlPaymentDetails.setEnabled(true);
                pnlPaymentDetails.setBounds(10, 120, 390, 45);
                if(btnRefund.isSelected()){
                    refund.getAttribute("PayeeCounty").setReadOnly(false);
                    refund.getAttribute("CheckNum").setReadOnly(false);
                    refund.getAttribute("PayeeCounty").setRequired(true);
                    refund.getAttribute("CheckNum").setRequired(true);
                }
                else{
                    refund.getAttribute("PayeeCounty").setReadOnly(true);
                    refund.getAttribute("CheckNum").setReadOnly(true);
                    refund.getAttribute("PayeeCounty").setRequired(false);
                    refund.getAttribute("CheckNum").setRequired(false);
                }
                break;
            default:
                pnlPaymentDetails.setEnabled(false);
                pnlPaymentDetails.setBounds(10, 120, 390, 0);
                refund.getAttribute("PayeeCounty").setReadOnly(true);
                refund.getAttribute("CheckNum").setReadOnly(true);
                refund.getAttribute("PayeeCounty").setEmpty(true);
                refund.getAttribute("CheckNum").setEmpty(true);
                refund.getAttribute("PayeeCounty").setEmpty(true);
                refund.getAttribute("CheckNum").setEmpty(true);
                if(btnDistribute.isSelected()){
                    refund.getAttribute("PayeeCounty").setRequired(true);
                    refund.getAttribute("CheckNum").setRequired(true);
                }
                else{
                    refund.getAttribute("PayeeCounty").setRequired(false);
                    refund.getAttribute("CheckNum").setRequired(false);
                }
                break;
        }
        edtCCardId.initializeControl();
        edtCheckNum.initializeControl();
    }  
        
    private void chkAlternatePayee(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAlternatePayee
        edtPayeeFirstName.initializeControl();
        edtAltPayeeMiddleInit.initializeControl();
        edtPayeeLastName.initializeControl();
        edtPayeeCompanyName.initializeControl();
        edtPayeeAddressLine1.initializeControl();
        edtPayeeAddressLine2.initializeControl();
        edtPayeeAddressLine3.initializeControl();
        edtPayeeAddressLine4.initializeControl();
        edtPayeeCity.initializeControl();
        edtPayeeState.initializeControl();
        edtZipPostalCode.initializeControl();
        cbPayeeCountryCode.initializeControl();
        cbAltCurrencyCode.initializeControl();
    }//GEN-LAST:event_chkAlternatePayee

    private void DistributeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DistributeActionPerformed
        // Add your handling code here:
        if (btnDistribute.isSelected()) {
            if (chkAlternatePayee.isSelected()) {
                chkAlternatePayee.doClick();
            }
            
            pnlAllocateRefund.setEnabled(false);
            pnlPayeeDetails.setEnabled(false);

            cbRefundReasonCode.setEnabled(false);
            cbPayMethod.setEnabled(false);
            cbBalanceId.setEnabled(false);
            edtAmountToRefund.setEnabled(false);
            chkAlternatePayee.setEnabled(false);
            setupPayMethod();
                
            // allocate funds
            IContext ctx = ctxFinder.findContext();
            if (ctx != null) {
                ctx.sendMessage("setPaymentAllocate", null);
            }
        }

    }//GEN-LAST:event_DistributeActionPerformed

    private void refundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refundActionPerformed
        // Add your handling code here:
        if (btnRefund.isSelected()) {
            pnlAllocateRefund.setEnabled(true);
            pnlPayeeDetails.setEnabled(true);

            cbRefundReasonCode.setEnabled(true);
            cbPayMethod.setEnabled(true);
            cbBalanceId.setEnabled(true);
            edtAmountToRefund.setEnabled(true);
            chkAlternatePayee.setEnabled(true);
            setupPayMethod();
            
            // Create refund
            IContext ctx = ctxFinder.findContext();
            if (ctx != null) {
                ctx.sendMessage("setRefundAllocate", null);
            }
        }

    }//GEN-LAST:event_refundActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton btnDistribute;
    private javax.swing.JRadioButton btnRefund;
    private javax.swing.ButtonGroup btngrpAllocateFunds;
    private com.csgsystems.igpa.controls.CSGComboBox cbAltCurrencyCode;
    private com.csgsystems.igpa.controls.CSGComboBox cbBalanceId;
    private com.csgsystems.igpa.controls.CSGComboBox cbPayMethod;
    private com.csgsystems.igpa.controls.CSGComboBox cbPayeeCountryCode;
    private com.csgsystems.igpa.controls.CSGComboBox cbRefundReasonCode;
    private com.csgsystems.igpa.controls.CSGCheckBox chkAlternatePayee;
    private com.csgsystems.igpa.controls.CSGEdit edtAltPayeeMiddleInit;
    private com.csgsystems.igpa.controls.CSGNumberEdit edtAmountToRefund;
    private com.csgsystems.igpa.controls.CSGEdit edtCCardId;
    private com.csgsystems.igpa.controls.CSGEdit edtCheckNum;
    private com.csgsystems.igpa.controls.CSGNumberEdit edtInvoiceBalanceAmount;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeAddressLine1;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeAddressLine2;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeAddressLine3;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeAddressLine4;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeCity;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeCompanyName;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeFirstName;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeLastName;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeState;
    private com.csgsystems.igpa.controls.CSGNumberEdit edtRefundBalanceAmount;
    private com.csgsystems.igpa.controls.CSGEdit edtZipPostalCode;
    private com.csgsystems.igpa.controls.CSGLabel lblAltCurrencyCode;
    private com.csgsystems.igpa.controls.CSGLabel lblAlternatePayee;
    private com.csgsystems.igpa.controls.CSGLabel lblAmountToRefund;
    private com.csgsystems.igpa.controls.CSGLabel lblBalanceId;
    private com.csgsystems.igpa.controls.CSGLabel lblCCardId;
    private com.csgsystems.igpa.controls.CSGLabel lblCheckNum;
    private com.csgsystems.igpa.controls.CSGLabel lblCity;
    private com.csgsystems.igpa.controls.CSGLabel lblCompanyName;
    private com.csgsystems.igpa.controls.CSGLabel lblCountry;
    private com.csgsystems.igpa.controls.CSGLabel lblPayeeAddress;
    private com.csgsystems.igpa.controls.CSGLabel lblPayeeFirstName;
    private com.csgsystems.igpa.controls.CSGLabel lblPayeeLastName;
    private com.csgsystems.igpa.controls.CSGLabel lblPayeeMiddleInit;
    private com.csgsystems.igpa.controls.CSGLabel lblPaymentMethod;
    private com.csgsystems.igpa.controls.CSGLabel lblRefundReasonCode;
    private com.csgsystems.igpa.controls.CSGLabel lblState;
    private com.csgsystems.igpa.controls.CSGLabel lblZipPostalCode;
    private javax.swing.JPanel pnlAllocateFunds;
    private javax.swing.JPanel pnlAllocateRefund;
    private javax.swing.JPanel pnlPayeeDetails;
    private javax.swing.JPanel pnlPaymentDetails;
    // End of variables declaration//GEN-END:variables

}
