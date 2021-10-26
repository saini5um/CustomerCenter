/*
 * JPanel.java
 *
 * Created on December 5, 2002, 4:11 PM
 */

package com.maxis.panel;

import com.csgsystems.igpa.forms.ContextFormListener;
import com.csgsystems.igpa.forms.ContextFormEvent;
import com.csgsystems.igpa.utils.ContextFinder;
import com.csgsystems.localization.ResourceManager;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import org.apache.commons.logging.*;
/**
 *
 * @author  akum01
 */
public class AccountRefundDetails extends javax.swing.JPanel implements ContextFormListener{

    private static Log log = null;

    static {
        try {
            log = LogFactory.getLog(AccountRefundDetails.class);
        } catch (Exception ex) {}
    }

    private ContextFinder ctxFinder = new ContextFinder(this);

    /** Creates new form AccountRefundDetails **/
    public AccountRefundDetails() {
        initComponents();   
    }
    

    private void setupPaymentField() {
        IContext ctx = ctxFinder.findContext(); 
        if (ctx != null) {
            IPersistentObject refund = ctx.getObject("Refund", null);
            if (refund != null) {
                Integer payMethod = (Integer)refund.getAttributeData("RefundType");
 
                // If the value is set to CHECK (1), 
                // then enable the Check Number field    
                if ((payMethod != null) && (payMethod.intValue() == 1)) {
           
                    pnlPaymentCheck.setVisible(true);     
                    pnlPaymentCcard.setVisible(false); 
                    edtCheckNumber.initializeControl();
                    edtCcardId.setVisible(false);
                    edtCcardIdServ.setVisible(false);                     
                } 
            

                // If the value is set to CREDITCARD (2), 
                // then enable the Credit Card fields   
                if ((payMethod != null) && (payMethod.intValue() == 2)) {
             
                    pnlPaymentCheck.setVisible(false);     
                    pnlPaymentCcard.setVisible(true); 
                    edtCcardId.initializeControl();
                    edtCcardIdServ.initializeControl();
                    edtCheckNumber.setVisible(false);
                }

           
                Integer altpayee = (Integer)refund.getAttributeData("AltContactId");

                if (altpayee != null) { 
                    pnlPayee.setVisible(false);     
                    pnlAltPayee.setVisible(true);                 
                } else {
                    pnlPayee.setVisible(true);     
                    pnlAltPayee.setVisible(false); 
                }

            }
        }
    }
   
    public void contextFormStateChanged(ContextFormEvent contextFormEvent) {
        if (contextFormEvent.getType() == ContextFormEvent.PRE_INIT_CONTROLS) {
            setupPaymentField();
        }  
         if (contextFormEvent.getType() == ContextFormEvent.POST_INIT_CONTROLS) {
            edtRefundAmount.setEnabled(false);
        }        
    }    
    
   
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlRefundInfo = new javax.swing.JPanel();
        lblRefundRequestDate = new com.csgsystems.igpa.controls.CSGLabel();
        lblRefundReason = new com.csgsystems.igpa.controls.CSGLabel();
        lblRefundStatus = new com.csgsystems.igpa.controls.CSGLabel();
        lblDateCompleted = new com.csgsystems.igpa.controls.CSGLabel();
        lblSupervisorName = new com.csgsystems.igpa.controls.CSGLabel();
        lblReviewDate = new com.csgsystems.igpa.controls.CSGLabel();
        lblPaymentMethod = new com.csgsystems.igpa.controls.CSGLabel();
        edtRefundReason = new com.csgsystems.igpa.controls.CSGEdit();
        edtRefundStatus = new com.csgsystems.igpa.controls.CSGEdit();
        edtSupervisorName = new com.csgsystems.igpa.controls.CSGEdit();
        edtPaymentMethod = new com.csgsystems.igpa.controls.CSGEdit();
        pnlPaymentCcard = new javax.swing.JPanel();
        edtCcardIdServ = new com.csgsystems.igpa.controls.CSGEdit();
        edtCcardId = new com.csgsystems.igpa.controls.CSGEdit();
        lblCcardId = new com.csgsystems.igpa.controls.CSGLabel();
        lblCcardIdServ = new com.csgsystems.igpa.controls.CSGLabel();
        pnlPaymentCheck = new javax.swing.JPanel();
        edtCheckNumber = new com.csgsystems.igpa.controls.CSGEdit();
        lblCheckNumber = new com.csgsystems.igpa.controls.CSGLabel();
        edtPayeeCounty = new com.csgsystems.igpa.controls.CSGEdit();
        lblPayeeCounty = new com.csgsystems.igpa.controls.CSGLabel();
        lblRefundAmount = new com.csgsystems.igpa.controls.CSGLabel();
        edtRefundAmount = new com.csgsystems.igpa.controls.CSGNumberEdit();
        edtTreasuryDate = new com.csgsystems.igpa.controls.CSGDateEdit();
        edtRequestDate = new com.csgsystems.igpa.controls.CSGDateEdit();
        edtReviewDate1 = new com.csgsystems.igpa.controls.CSGDateEdit();
        pnlPayee = new javax.swing.JPanel();
        lblPayeeFirstName = new com.csgsystems.igpa.controls.CSGLabel();
        lblPayeeLastName = new com.csgsystems.igpa.controls.CSGLabel();
        lblCompanyName = new com.csgsystems.igpa.controls.CSGLabel();
        lblPayeeAddress = new com.csgsystems.igpa.controls.CSGLabel();
        lblPayeeCity = new com.csgsystems.igpa.controls.CSGLabel();
        lblPayeeState = new com.csgsystems.igpa.controls.CSGLabel();
        lblZipPostalCode = new com.csgsystems.igpa.controls.CSGLabel();
        lblCountry = new com.csgsystems.igpa.controls.CSGLabel();
        edtPayeeCountry = new com.csgsystems.igpa.controls.CSGEdit();
        edtZipPostalCode = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeState = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeCity = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeAddress3 = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeAddress2 = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeAddress1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtCompanyName = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeLastName = new com.csgsystems.igpa.controls.CSGEdit();
        edtPayeeFirstName = new com.csgsystems.igpa.controls.CSGEdit();
        pnlAltPayee = new javax.swing.JPanel();
        lblAltPayeeFirstName = new com.csgsystems.igpa.controls.CSGLabel();
        lblAltPayeeLastName = new com.csgsystems.igpa.controls.CSGLabel();
        lblAltCompanyName = new com.csgsystems.igpa.controls.CSGLabel();
        lblAltPayeeAddress = new com.csgsystems.igpa.controls.CSGLabel();
        lblAltPayeeCity = new com.csgsystems.igpa.controls.CSGLabel();
        lblAltPayeeState = new com.csgsystems.igpa.controls.CSGLabel();
        lblAltZipPostalCode = new com.csgsystems.igpa.controls.CSGLabel();
        lblAltPayeeCountry = new com.csgsystems.igpa.controls.CSGLabel();
        edtAltPayeeCountry = new com.csgsystems.igpa.controls.CSGEdit();
        edtAltZipPostalCode = new com.csgsystems.igpa.controls.CSGEdit();
        edtAltPayeeState = new com.csgsystems.igpa.controls.CSGEdit();
        edtAltPayeeCity = new com.csgsystems.igpa.controls.CSGEdit();
        edAltPayeeAddress3 = new com.csgsystems.igpa.controls.CSGEdit();
        edtAltPayeeAddress2 = new com.csgsystems.igpa.controls.CSGEdit();
        edtAltPayeeAddress1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtAltCompanyName = new com.csgsystems.igpa.controls.CSGEdit();
        edtAltPayeeLastName = new com.csgsystems.igpa.controls.CSGEdit();
        edtAltPayeeFirstName = new com.csgsystems.igpa.controls.CSGEdit();
        lblAltPayeeCurrenyCode = new com.csgsystems.igpa.controls.CSGLabel();
        edtAltPayeeCurrencyCode = new com.csgsystems.igpa.controls.CSGEdit();
        edAltPayeeAddress4 = new com.csgsystems.igpa.controls.CSGEdit();
        lblAltPayeeMiddleInit = new com.csgsystems.igpa.controls.CSGLabel();
        edtAltPayeeMiddleInit = new com.csgsystems.igpa.controls.CSGEdit();

        setLayout(null);

        setMinimumSize(new java.awt.Dimension(485, 680));
        setPreferredSize(new java.awt.Dimension(485, 680));
        pnlRefundInfo.setLayout(null);

        pnlRefundInfo.setBorder(new javax.swing.border.TitledBorder(ResourceManager.getString( "RefundDetails.title.RefundInformation" )));
        pnlRefundInfo.setMinimumSize(new java.awt.Dimension(370, 340));
        pnlRefundInfo.setPreferredSize(new java.awt.Dimension(370, 340));
        lblRefundRequestDate.setLocalizationKey("RefundDetails.lblRefundRequestDate");
        lblRefundRequestDate.setMaximumSize(new java.awt.Dimension(179, 16));
        pnlRefundInfo.add(lblRefundRequestDate);
        lblRefundRequestDate.setBounds(10, 20, 120, 20);

        lblRefundReason.setLocalizationKey("RefundDetails.lblRefundReason");
        pnlRefundInfo.add(lblRefundReason);
        lblRefundReason.setBounds(10, 45, 120, 20);

        lblRefundStatus.setLocalizationKey("RefundDetails.lblStatus");
        pnlRefundInfo.add(lblRefundStatus);
        lblRefundStatus.setBounds(10, 70, 120, 20);

        lblDateCompleted.setLocalizationKey("RefundDetails.lblDateCompleted");
        pnlRefundInfo.add(lblDateCompleted);
        lblDateCompleted.setBounds(10, 120, 120, 20);

        lblSupervisorName.setLocalizationKey("RefundDetails.lblSupervisorName");
        pnlRefundInfo.add(lblSupervisorName);
        lblSupervisorName.setBounds(10, 145, 120, 20);

        lblReviewDate.setLocalizationKey("RefundDetails.lblReviewDate");
        pnlRefundInfo.add(lblReviewDate);
        lblReviewDate.setBounds(10, 170, 120, 20);

        lblPaymentMethod.setLocalizationKey("RefundDetails.lblPaymentMethod");
        pnlRefundInfo.add(lblPaymentMethod);
        lblPaymentMethod.setBounds(10, 195, 120, 20);

        edtRefundReason.setAtomName("RefundReasonCode");
        edtRefundReason.setDomainName("Refund");
        pnlRefundInfo.add(edtRefundReason);
        edtRefundReason.setBounds(160, 45, 150, 20);

        edtRefundStatus.setAttributeName("RefundStatus");
        edtRefundStatus.setDomainName("Refund");
        pnlRefundInfo.add(edtRefundStatus);
        edtRefundStatus.setBounds(160, 70, 150, 20);

        edtSupervisorName.setAttributeName("SupervisorName");
        edtSupervisorName.setDomainName("Refund");
        pnlRefundInfo.add(edtSupervisorName);
        edtSupervisorName.setBounds(160, 145, 150, 20);

        edtPaymentMethod.setAttributeName("RefundType");
        edtPaymentMethod.setDomainName("Refund");
        pnlRefundInfo.add(edtPaymentMethod);
        edtPaymentMethod.setBounds(160, 195, 150, 20);

        pnlPaymentCcard.setLayout(null);

        edtCcardIdServ.setAttributeName("CcardIdServ");
        edtCcardIdServ.setDomainName("Refund");
        pnlPaymentCcard.add(edtCcardIdServ);
        edtCcardIdServ.setBounds(155, 30, 150, 20);

        edtCcardId.setAttributeName("CcardId");
        edtCcardId.setDomainName("Refund");
        edtCcardId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtCcardIdActionPerformed(evt);
            }
        });

        pnlPaymentCcard.add(edtCcardId);
        edtCcardId.setBounds(155, 5, 150, 20);

        lblCcardId.setLocalizationKey("RefundDetails.lblCcardId");
        pnlPaymentCcard.add(lblCcardId);
        lblCcardId.setBounds(5, 5, 130, 20);

        lblCcardIdServ.setLocalizationKey("RefundDetails.lblCcardIdServ");
        pnlPaymentCcard.add(lblCcardIdServ);
        lblCcardIdServ.setBounds(5, 30, 130, 20);

        pnlRefundInfo.add(pnlPaymentCcard);
        pnlPaymentCcard.setBounds(5, 215, 350, 60);

        pnlPaymentCheck.setLayout(null);

        edtCheckNumber.setAttributeName("CheckNum");
        edtCheckNumber.setDomainName("Refund");
        pnlPaymentCheck.add(edtCheckNumber);
        edtCheckNumber.setBounds(155, 5, 150, 20);

        lblCheckNumber.setLocalizationKey("RefundDetails.lblCheckNumber");
        lblCheckNumber.setToolTipLocalizationKey("RefundDetails.lblCheckNumber");
        pnlPaymentCheck.add(lblCheckNumber);
        lblCheckNumber.setBounds(5, 5, 130, 20);

        edtPayeeCounty.setAttributeName("PayeeCounty");
        edtPayeeCounty.setDomainName("Refund");
        pnlPaymentCheck.add(edtPayeeCounty);
        edtPayeeCounty.setBounds(155, 30, 150, 20);

        lblPayeeCounty.setLocalizationKey("RefundDetails.lblPayeeCounty");
        lblPayeeCounty.setToolTipLocalizationKey("RefundDetails.lblCheckNumber");
        pnlPaymentCheck.add(lblPayeeCounty);
        lblPayeeCounty.setBounds(5, 30, 130, 20);

        pnlRefundInfo.add(pnlPaymentCheck);
        pnlPaymentCheck.setBounds(5, 215, 350, 60);

        lblRefundAmount.setLocalizationKey("RefundDetails.lblRefundAmount");
        lblRefundAmount.setMaximumSize(new java.awt.Dimension(132, 16));
        pnlRefundInfo.add(lblRefundAmount);
        lblRefundAmount.setBounds(10, 95, 120, 20);

        edtRefundAmount.setText("cSGNumberEdit1");
        edtRefundAmount.setAttributeName("Amount");
        edtRefundAmount.setControlFormat(3);
        edtRefundAmount.setDomainName("Refund");
        pnlRefundInfo.add(edtRefundAmount);
        edtRefundAmount.setBounds(160, 95, 150, 20);

        edtTreasuryDate.setAttributeName("TreasuryDate");
        edtTreasuryDate.setDomainName("Refund");
        pnlRefundInfo.add(edtTreasuryDate);
        edtTreasuryDate.setBounds(160, 120, 150, 20);

        edtRequestDate.setAttributeName("RequestDate");
        edtRequestDate.setDomainName("Refund");
        pnlRefundInfo.add(edtRequestDate);
        edtRequestDate.setBounds(160, 20, 150, 20);

        edtReviewDate1.setAttributeName("ReviewDate");
        edtReviewDate1.setDomainName("Refund");
        pnlRefundInfo.add(edtReviewDate1);
        edtReviewDate1.setBounds(160, 170, 150, 20);

        add(pnlRefundInfo);
        pnlRefundInfo.setBounds(20, 20, 370, 280);

        pnlPayee.setLayout(null);

        pnlPayee.setBorder(new javax.swing.border.TitledBorder(ResourceManager.getString( "RefundDetails.title.PayeeInformation" )));
        pnlPayee.setMinimumSize(new java.awt.Dimension(370, 280));
        pnlPayee.setPreferredSize(new java.awt.Dimension(370, 280));
        lblPayeeFirstName.setLocalizationKey("RefundDetails.lblPayeeFirstName");
        pnlPayee.add(lblPayeeFirstName);
        lblPayeeFirstName.setBounds(10, 20, 120, 20);

        lblPayeeLastName.setLocalizationKey("RefundDetails.lblPayeeLastName");
        pnlPayee.add(lblPayeeLastName);
        lblPayeeLastName.setBounds(10, 45, 120, 20);

        lblCompanyName.setLocalizationKey("RefundDetails.lblCompanyName");
        pnlPayee.add(lblCompanyName);
        lblCompanyName.setBounds(10, 70, 120, 20);

        lblPayeeAddress.setLocalizationKey("RefundDetails.lblPayeeAddress");
        pnlPayee.add(lblPayeeAddress);
        lblPayeeAddress.setBounds(10, 95, 120, 20);

        lblPayeeCity.setLocalizationKey("RefundDetails.lblPayeeCity");
        pnlPayee.add(lblPayeeCity);
        lblPayeeCity.setBounds(10, 170, 120, 20);

        lblPayeeState.setLocalizationKey("RefundDetails.lblPayeeState");
        pnlPayee.add(lblPayeeState);
        lblPayeeState.setBounds(10, 195, 120, 20);

        lblZipPostalCode.setLocalizationKey("RefundDetails.lblZipPostalCode");
        pnlPayee.add(lblZipPostalCode);
        lblZipPostalCode.setBounds(10, 220, 120, 20);

        lblCountry.setLocalizationKey("RefundDetails.lblPayeeCountry");
        pnlPayee.add(lblCountry);
        lblCountry.setBounds(10, 245, 120, 20);

        edtPayeeCountry.setAttributeName("PayeeCountryCode");
        edtPayeeCountry.setDomainName("Refund");
        pnlPayee.add(edtPayeeCountry);
        edtPayeeCountry.setBounds(160, 245, 150, 20);

        edtZipPostalCode.setAttributeName("PayeeZip");
        edtZipPostalCode.setDomainName("Refund");
        pnlPayee.add(edtZipPostalCode);
        edtZipPostalCode.setBounds(160, 220, 150, 20);

        edtPayeeState.setAttributeName("PayeeState");
        edtPayeeState.setDomainName("Refund");
        pnlPayee.add(edtPayeeState);
        edtPayeeState.setBounds(160, 195, 150, 20);

        edtPayeeCity.setAttributeName("PayeeCity");
        edtPayeeCity.setDomainName("Refund");
        pnlPayee.add(edtPayeeCity);
        edtPayeeCity.setBounds(160, 170, 150, 20);

        edtPayeeAddress3.setAttributeName("PayeeAddress3");
        edtPayeeAddress3.setDomainName("Refund");
        pnlPayee.add(edtPayeeAddress3);
        edtPayeeAddress3.setBounds(160, 145, 150, 20);

        edtPayeeAddress2.setAttributeName("PayeeAddress2");
        edtPayeeAddress2.setDomainName("Refund");
        pnlPayee.add(edtPayeeAddress2);
        edtPayeeAddress2.setBounds(160, 120, 150, 20);

        edtPayeeAddress1.setAttributeName("PayeeAddress1");
        edtPayeeAddress1.setDomainName("Refund");
        pnlPayee.add(edtPayeeAddress1);
        edtPayeeAddress1.setBounds(160, 95, 150, 20);

        edtCompanyName.setAttributeName("PayeeCompany");
        edtCompanyName.setDomainName("Refund");
        pnlPayee.add(edtCompanyName);
        edtCompanyName.setBounds(160, 70, 150, 20);

        edtPayeeLastName.setAttributeName("PayeeLast");
        edtPayeeLastName.setDomainName("Refund");
        pnlPayee.add(edtPayeeLastName);
        edtPayeeLastName.setBounds(160, 45, 150, 20);

        edtPayeeFirstName.setAtomName("PayeeFirst");
        edtPayeeFirstName.setDomainName("Refund");
        pnlPayee.add(edtPayeeFirstName);
        edtPayeeFirstName.setBounds(160, 20, 150, 20);

        add(pnlPayee);
        pnlPayee.setBounds(20, 310, 370, 290);

        pnlAltPayee.setLayout(null);

        pnlAltPayee.setBorder(new javax.swing.border.TitledBorder(ResourceManager.getString( "RefundDetails.title.PayeeInformation" )));
        pnlAltPayee.setMinimumSize(new java.awt.Dimension(370, 350));
        pnlAltPayee.setPreferredSize(new java.awt.Dimension(370, 350));
        lblAltPayeeFirstName.setLocalizationKey("RefundDetails.lblPayeeFirstName");
        pnlAltPayee.add(lblAltPayeeFirstName);
        lblAltPayeeFirstName.setBounds(10, 20, 120, 20);

        lblAltPayeeLastName.setLocalizationKey("RefundDetails.lblPayeeLastName");
        pnlAltPayee.add(lblAltPayeeLastName);
        lblAltPayeeLastName.setBounds(10, 70, 120, 20);

        lblAltCompanyName.setLocalizationKey("RefundDetails.lblCompanyName");
        pnlAltPayee.add(lblAltCompanyName);
        lblAltCompanyName.setBounds(10, 95, 120, 20);

        lblAltPayeeAddress.setLocalizationKey("RefundDetails.lblPayeeAddress");
        pnlAltPayee.add(lblAltPayeeAddress);
        lblAltPayeeAddress.setBounds(10, 120, 120, 20);

        lblAltPayeeCity.setLocalizationKey("RefundDetails.lblPayeeCity");
        pnlAltPayee.add(lblAltPayeeCity);
        lblAltPayeeCity.setBounds(10, 220, 120, 20);

        lblAltPayeeState.setLocalizationKey("RefundDetails.lblPayeeState");
        pnlAltPayee.add(lblAltPayeeState);
        lblAltPayeeState.setBounds(10, 245, 120, 20);

        lblAltZipPostalCode.setLocalizationKey("RefundDetails.lblZipPostalCode");
        pnlAltPayee.add(lblAltZipPostalCode);
        lblAltZipPostalCode.setBounds(10, 270, 120, 20);

        lblAltPayeeCountry.setLocalizationKey("RefundDetails.lblPayeeCountry");
        pnlAltPayee.add(lblAltPayeeCountry);
        lblAltPayeeCountry.setBounds(10, 295, 120, 20);

        edtAltPayeeCountry.setAttributeName("CountryCode");
        edtAltPayeeCountry.setDomainName("Contact");
        edtAltPayeeCountry.setEnabled(false);
        pnlAltPayee.add(edtAltPayeeCountry);
        edtAltPayeeCountry.setBounds(160, 295, 150, 20);

        edtAltZipPostalCode.setAttributeName("PostalCode");
        edtAltZipPostalCode.setDomainName("Contact");
        edtAltZipPostalCode.setEnabled(false);
        pnlAltPayee.add(edtAltZipPostalCode);
        edtAltZipPostalCode.setBounds(160, 270, 150, 20);

        edtAltPayeeState.setAttributeName("State");
        edtAltPayeeState.setDomainName("Contact");
        edtAltPayeeState.setEnabled(false);
        pnlAltPayee.add(edtAltPayeeState);
        edtAltPayeeState.setBounds(160, 245, 150, 20);

        edtAltPayeeCity.setAttributeName("City");
        edtAltPayeeCity.setDomainName("Contact");
        edtAltPayeeCity.setEnabled(false);
        pnlAltPayee.add(edtAltPayeeCity);
        edtAltPayeeCity.setBounds(160, 220, 150, 20);

        edAltPayeeAddress3.setAttributeName("AddressLine3");
        edAltPayeeAddress3.setDomainName("Contact");
        edAltPayeeAddress3.setEnabled(false);
        pnlAltPayee.add(edAltPayeeAddress3);
        edAltPayeeAddress3.setBounds(160, 170, 150, 20);

        edtAltPayeeAddress2.setAttributeName("AddressLine2");
        edtAltPayeeAddress2.setDomainName("Contact");
        edtAltPayeeAddress2.setEnabled(false);
        pnlAltPayee.add(edtAltPayeeAddress2);
        edtAltPayeeAddress2.setBounds(160, 145, 150, 20);

        edtAltPayeeAddress1.setAttributeName("AddressLine1");
        edtAltPayeeAddress1.setDomainName("Contact");
        edtAltPayeeAddress1.setEnabled(false);
        pnlAltPayee.add(edtAltPayeeAddress1);
        edtAltPayeeAddress1.setBounds(160, 120, 150, 20);

        edtAltCompanyName.setAttributeName("Company");
        edtAltCompanyName.setDomainName("Contact");
        edtAltCompanyName.setEnabled(false);
        pnlAltPayee.add(edtAltCompanyName);
        edtAltCompanyName.setBounds(160, 95, 150, 20);

        edtAltPayeeLastName.setAttributeName("LastName");
        edtAltPayeeLastName.setDomainName("Contact");
        edtAltPayeeLastName.setEnabled(false);
        pnlAltPayee.add(edtAltPayeeLastName);
        edtAltPayeeLastName.setBounds(160, 70, 150, 20);

        edtAltPayeeFirstName.setAttributeName("FirstName");
        edtAltPayeeFirstName.setDomainName("Contact");
        edtAltPayeeFirstName.setEnabled(false);
        pnlAltPayee.add(edtAltPayeeFirstName);
        edtAltPayeeFirstName.setBounds(160, 20, 150, 20);

        lblAltPayeeCurrenyCode.setLocalizationKey("RefundCreate.lblAltCurrencyCode");
        pnlAltPayee.add(lblAltPayeeCurrenyCode);
        lblAltPayeeCurrenyCode.setBounds(10, 320, 120, 20);

        edtAltPayeeCurrencyCode.setAttributeName("AltCurrencyCode");
        edtAltPayeeCurrencyCode.setDomainName("Refund");
        edtAltPayeeCurrencyCode.setEnabled(false);
        pnlAltPayee.add(edtAltPayeeCurrencyCode);
        edtAltPayeeCurrencyCode.setBounds(160, 320, 150, 20);

        edAltPayeeAddress4.setAttributeName("AddressLine4");
        edAltPayeeAddress4.setDomainName("Contact");
        edAltPayeeAddress4.setEnabled(false);
        pnlAltPayee.add(edAltPayeeAddress4);
        edAltPayeeAddress4.setBounds(160, 195, 150, 20);

        lblAltPayeeMiddleInit.setLocalizationKey("RefundCreate.lblPayeeMiddleInit");
        pnlAltPayee.add(lblAltPayeeMiddleInit);
        lblAltPayeeMiddleInit.setBounds(10, 45, 120, 20);

        edtAltPayeeMiddleInit.setAttributeName("MiddleName");
        edtAltPayeeMiddleInit.setDomainName("Contact");
        edtAltPayeeMiddleInit.setEnabled(false);
        pnlAltPayee.add(edtAltPayeeMiddleInit);
        edtAltPayeeMiddleInit.setBounds(160, 45, 150, 20);

        add(pnlAltPayee);
        pnlAltPayee.setBounds(20, 310, 370, 360);

    }
    // </editor-fold>//GEN-END:initComponents

    private void edtCcardIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtCcardIdActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_edtCcardIdActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.csgsystems.igpa.controls.CSGEdit edAltPayeeAddress3;
    private com.csgsystems.igpa.controls.CSGEdit edAltPayeeAddress4;
    private com.csgsystems.igpa.controls.CSGEdit edtAltCompanyName;
    private com.csgsystems.igpa.controls.CSGEdit edtAltPayeeAddress1;
    private com.csgsystems.igpa.controls.CSGEdit edtAltPayeeAddress2;
    private com.csgsystems.igpa.controls.CSGEdit edtAltPayeeCity;
    private com.csgsystems.igpa.controls.CSGEdit edtAltPayeeCountry;
    private com.csgsystems.igpa.controls.CSGEdit edtAltPayeeCurrencyCode;
    private com.csgsystems.igpa.controls.CSGEdit edtAltPayeeFirstName;
    private com.csgsystems.igpa.controls.CSGEdit edtAltPayeeLastName;
    private com.csgsystems.igpa.controls.CSGEdit edtAltPayeeMiddleInit;
    private com.csgsystems.igpa.controls.CSGEdit edtAltPayeeState;
    private com.csgsystems.igpa.controls.CSGEdit edtAltZipPostalCode;
    private com.csgsystems.igpa.controls.CSGEdit edtCcardId;
    private com.csgsystems.igpa.controls.CSGEdit edtCcardIdServ;
    private com.csgsystems.igpa.controls.CSGEdit edtCheckNumber;
    private com.csgsystems.igpa.controls.CSGEdit edtCompanyName;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeAddress1;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeAddress2;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeAddress3;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeCity;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeCountry;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeCounty;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeFirstName;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeLastName;
    private com.csgsystems.igpa.controls.CSGEdit edtPayeeState;
    private com.csgsystems.igpa.controls.CSGEdit edtPaymentMethod;
    private com.csgsystems.igpa.controls.CSGNumberEdit edtRefundAmount;
    private com.csgsystems.igpa.controls.CSGEdit edtRefundReason;
    private com.csgsystems.igpa.controls.CSGEdit edtRefundStatus;
    private com.csgsystems.igpa.controls.CSGDateEdit edtRequestDate;
    private com.csgsystems.igpa.controls.CSGDateEdit edtReviewDate1;
    private com.csgsystems.igpa.controls.CSGEdit edtSupervisorName;
    private com.csgsystems.igpa.controls.CSGDateEdit edtTreasuryDate;
    private com.csgsystems.igpa.controls.CSGEdit edtZipPostalCode;
    private com.csgsystems.igpa.controls.CSGLabel lblAltCompanyName;
    private com.csgsystems.igpa.controls.CSGLabel lblAltPayeeAddress;
    private com.csgsystems.igpa.controls.CSGLabel lblAltPayeeCity;
    private com.csgsystems.igpa.controls.CSGLabel lblAltPayeeCountry;
    private com.csgsystems.igpa.controls.CSGLabel lblAltPayeeCurrenyCode;
    private com.csgsystems.igpa.controls.CSGLabel lblAltPayeeFirstName;
    private com.csgsystems.igpa.controls.CSGLabel lblAltPayeeLastName;
    private com.csgsystems.igpa.controls.CSGLabel lblAltPayeeMiddleInit;
    private com.csgsystems.igpa.controls.CSGLabel lblAltPayeeState;
    private com.csgsystems.igpa.controls.CSGLabel lblAltZipPostalCode;
    private com.csgsystems.igpa.controls.CSGLabel lblCcardId;
    private com.csgsystems.igpa.controls.CSGLabel lblCcardIdServ;
    private com.csgsystems.igpa.controls.CSGLabel lblCheckNumber;
    private com.csgsystems.igpa.controls.CSGLabel lblCompanyName;
    private com.csgsystems.igpa.controls.CSGLabel lblCountry;
    private com.csgsystems.igpa.controls.CSGLabel lblDateCompleted;
    private com.csgsystems.igpa.controls.CSGLabel lblPayeeAddress;
    private com.csgsystems.igpa.controls.CSGLabel lblPayeeCity;
    private com.csgsystems.igpa.controls.CSGLabel lblPayeeCounty;
    private com.csgsystems.igpa.controls.CSGLabel lblPayeeFirstName;
    private com.csgsystems.igpa.controls.CSGLabel lblPayeeLastName;
    private com.csgsystems.igpa.controls.CSGLabel lblPayeeState;
    private com.csgsystems.igpa.controls.CSGLabel lblPaymentMethod;
    private com.csgsystems.igpa.controls.CSGLabel lblRefundAmount;
    private com.csgsystems.igpa.controls.CSGLabel lblRefundReason;
    private com.csgsystems.igpa.controls.CSGLabel lblRefundRequestDate;
    private com.csgsystems.igpa.controls.CSGLabel lblRefundStatus;
    private com.csgsystems.igpa.controls.CSGLabel lblReviewDate;
    private com.csgsystems.igpa.controls.CSGLabel lblSupervisorName;
    private com.csgsystems.igpa.controls.CSGLabel lblZipPostalCode;
    private javax.swing.JPanel pnlAltPayee;
    private javax.swing.JPanel pnlPayee;
    private javax.swing.JPanel pnlPaymentCcard;
    private javax.swing.JPanel pnlPaymentCheck;
    private javax.swing.JPanel pnlRefundInfo;
    // End of variables declaration//GEN-END:variables
    
}
