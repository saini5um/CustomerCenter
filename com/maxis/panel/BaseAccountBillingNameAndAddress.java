/*
 * BaseAccountBillingNameAndAddress.java
 *
 * Created on August 22, 2002, 3:24 PM
 */

package com.maxis.panel;

import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.igpa.controls.BoundDataChangeEvent;
import com.csgsystems.igpa.controls.BoundDataChangeListener;
import com.csgsystems.igpa.forms.ContextFormEvent;
import com.csgsystems.igpa.forms.ContextFormListener;
import com.csgsystems.igpa.utils.ContextFinder;
import com.csgsystems.localization.ResourceManager;

/**
 *
 * @author  mccm06
 */
public class BaseAccountBillingNameAndAddress extends javax.swing.JPanel implements ContextFormListener{
    
    /**
     *  Instance of the ContextFinder utility class, which can recursively
     *  search up the component hierarchy for an ICSGContextForm, and then
     *  retrieve its context (cached for later use).
     */
    private ContextFinder ctxFinder = new ContextFinder(this);
    
    /** Creates new form BaseAccountBillingNameAndAddress */
    public BaseAccountBillingNameAndAddress() {
        initComponents();
    }
    
    public void contextFormStateChanged(com.csgsystems.igpa.forms.ContextFormEvent contextFormEvent) {
        if (ContextFormEvent.POST_INIT_CONTROLS == contextFormEvent.getType()) {
            // add a listener to the controls that make up a geocode find
            edtCity.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeFind();
                    }
                });    
                
            edtStateProv.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeFind();
                    }
                });  
            
            edtZipPostalCode.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeFind();
                        edtCity.initializeControl();
                        edtStateProv.initializeControl();
                    }
                });  
            
            cbCountry.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeFind();
                        changeCityState();
                    }
                });  
            
            cbFranchise.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeFind();
                    }
                });  
                
            IContext ctx = ctxFinder.findContext();
            IPersistentObject account = ctx.getObject("Account", null);
            if (account.getAttributeDataAsBoolean("VirtualIsCountyFreeForm") == true) {
//                ccbCounty.setVisible(false);
                edtBillGeocode.setVisible(false);
                lblBillGeocode.setVisible(false);
//                edtCounty.setVisible(true);
            } else {
//                ccbCounty.setVisible(true);
                edtBillGeocode.setVisible(true);
                lblBillGeocode.setVisible(true);                
//                edtCounty.setVisible(false);
            }           
        }
    }
    
    private void changeCityState() {
        IContext ctx = ctxFinder.findContext();
        IPersistentObject account = ctx.getObject("Account", null);
        
        if ( account.getAttributeDataAsInteger("BillCountryCode") == 458 ) {
            account.getAttribute("BillState").setReadOnly(true);
            account.getAttribute("BillCity").setReadOnly(true);
            edtCity.initializeControl();
            edtStateProv.initializeControl();
            //14 dec 2005  KenanFX – CC – Common-61 MH: 
            //force focus to the BillZip if its malaysia, so that validation will be made
            edtZipPostalCode.grabFocus();
        } else {
            account.getAttribute("BillState").setReadOnly(false);
            account.getAttribute("BillCity").setReadOnly(false);
            edtCity.initializeControl();
            edtStateProv.initializeControl();            
        }
    }
    
    private void doGeocodeFind() {
        IContext ctx = ctxFinder.findContext();
        IPersistentObject account = ctx.getObject("Account", null);
        
        account.sendMessage("resetGeocodeBillingAddressList", null);
//        ccbCounty.initializeControl();
        edtBillGeocode.initializeControl();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblAccountID = new com.csgsystems.igpa.controls.CSGLabel();
        lblFirstName = new com.csgsystems.igpa.controls.CSGLabel();
        lblMiddleName = new com.csgsystems.igpa.controls.CSGLabel();
        lblLastName = new com.csgsystems.igpa.controls.CSGLabel();
        lblCompanyName = new com.csgsystems.igpa.controls.CSGLabel();
        lblPhoneDay = new com.csgsystems.igpa.controls.CSGLabel();
        lblPhoneNight = new com.csgsystems.igpa.controls.CSGLabel();
        edtAccountID = new com.csgsystems.igpa.controls.CSGEdit();
        edtFirstName = new com.csgsystems.igpa.controls.CSGEdit();
        edtMiddleName = new com.csgsystems.igpa.controls.CSGEdit();
        edtLastName = new com.csgsystems.igpa.controls.CSGEdit();
        edtCompanyName = new com.csgsystems.igpa.controls.CSGEdit();
        edtPhoneDay = new com.csgsystems.igpa.controls.CSGEdit();
        edtPhoneNight = new com.csgsystems.igpa.controls.CSGEdit();
        blankPlaceholder = new javax.swing.JLabel();
        lblBillTitle = new com.csgsystems.igpa.controls.CSGLabel();
        edtBillTitle = new com.csgsystems.igpa.controls.CSGComboBox();
        lblCustFax = new com.csgsystems.igpa.controls.CSGLabel();
        edtCustFax = new com.csgsystems.igpa.controls.CSGEdit();
        lblCustEmail = new com.csgsystems.igpa.controls.CSGLabel();
        edtCustEmail = new com.csgsystems.igpa.controls.CSGEdit();
        jPanel3 = new javax.swing.JPanel();
        lblAddressLine1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblCity = new com.csgsystems.igpa.controls.CSGLabel();
        lblStateProv = new com.csgsystems.igpa.controls.CSGLabel();
        lblCountry = new com.csgsystems.igpa.controls.CSGLabel();
        lblFranchise = new com.csgsystems.igpa.controls.CSGLabel();
        edtAddressLine2 = new com.csgsystems.igpa.controls.CSGEdit();
        edtAddressLine3 = new com.csgsystems.igpa.controls.CSGEdit();
        edtCity = new com.csgsystems.igpa.controls.CSGEdit();
        edtStateProv = new com.csgsystems.igpa.controls.CSGEdit();
        cbCountry = new com.csgsystems.igpa.controls.CSGComboBox();
        cbFranchise = new com.csgsystems.igpa.controls.CSGComboBox();
        edtAddressLine1 = new com.csgsystems.igpa.controls.CSGEdit();
        blankPlaceholder1 = new javax.swing.JLabel();
        lblBillGeocode = new com.csgsystems.igpa.controls.CSGLabel();
        edtBillGeocode = new com.csgsystems.igpa.controls.CSGEdit();
        lblZipPostalCode = new com.csgsystems.igpa.controls.CSGLabel();
        edtZipPostalCode = new com.csgsystems.igpa.controls.CSGEdit();
        lblRevRcvCostCtr = new com.csgsystems.igpa.controls.CSGLabel();
        cbRevRcvCostCtr = new com.csgsystems.igpa.controls.CSGComboBox();
        lblGlobalContractsApply = new com.csgsystems.igpa.controls.CSGLabel();
        chkGlobalContractsApply = new com.csgsystems.igpa.controls.CSGCheckBox();
        lblVipCode = new com.csgsystems.igpa.controls.CSGLabel();
        cbVipCode = new com.csgsystems.igpa.controls.CSGComboBox();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 0, 0)));
        setMinimumSize(new java.awt.Dimension(610, 270));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(new javax.swing.border.TitledBorder(ResourceManager.getString( "AccountBillingNameAndAddress.title.BillingNameAndAddress" )));
        jPanel1.setMinimumSize(new java.awt.Dimension(860, 400));
        jPanel1.setPreferredSize(new java.awt.Dimension(860, 400));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        lblAccountID.setLocalizationKey("AccountBillingNameAndAddress.lblAccountID");
        lblAccountID.setMaximumSize(null);
        lblAccountID.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel2.add(lblAccountID, gridBagConstraints);

        lblFirstName.setLocalizationKey("AccountBillingNameAndAddress.lblFirstName");
        lblFirstName.setMaximumSize(null);
        lblFirstName.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel2.add(lblFirstName, gridBagConstraints);

        lblMiddleName.setLocalizationKey("AccountBillingNameAndAddress.lblMiddleName");
        lblMiddleName.setMaximumSize(null);
        lblMiddleName.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel2.add(lblMiddleName, gridBagConstraints);

        lblLastName.setLocalizationKey("Customer Name:");
        lblLastName.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel2.add(lblLastName, gridBagConstraints);

        lblCompanyName.setLocalizationKey("AccountBillingNameAndAddress.lblCompanyName");
        lblCompanyName.setMaximumSize(null);
        lblCompanyName.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel2.add(lblCompanyName, gridBagConstraints);

        lblPhoneDay.setLocalizationKey("Telephone No 1:");
        lblPhoneDay.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel2.add(lblPhoneDay, gridBagConstraints);

        lblPhoneNight.setLocalizationKey("Telephone No 2:");
        lblPhoneNight.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel2.add(lblPhoneNight, gridBagConstraints);

        edtAccountID.setAttributeName("AccountExternalId");
        edtAccountID.setDomainName("Account");
        edtAccountID.setMaximumSize(null);
        edtAccountID.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(edtAccountID, gridBagConstraints);

        edtFirstName.setAttributeName("BillFname");
        edtFirstName.setDomainName("Account");
        edtFirstName.setMaximumSize(null);
        edtFirstName.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(edtFirstName, gridBagConstraints);

        edtMiddleName.setAttributeName("BillMinit");
        edtMiddleName.setDomainName("Account");
        edtMiddleName.setMaximumSize(null);
        edtMiddleName.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(edtMiddleName, gridBagConstraints);

        edtLastName.setAttributeName("BillLname");
        edtLastName.setDomainName("Account");
        edtLastName.setMaximumSize(null);
        edtLastName.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(edtLastName, gridBagConstraints);

        edtCompanyName.setAttributeName("BillCompany");
        edtCompanyName.setDomainName("Account");
        edtCompanyName.setMaximumSize(null);
        edtCompanyName.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(edtCompanyName, gridBagConstraints);

        edtPhoneDay.setAttributeName("CustPhone1");
        edtPhoneDay.setDomainName("Account");
        edtPhoneDay.setMaximumSize(null);
        edtPhoneDay.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(edtPhoneDay, gridBagConstraints);

        edtPhoneNight.setAttributeName("CustPhone2");
        edtPhoneNight.setDomainName("Account");
        edtPhoneNight.setMaximumSize(null);
        edtPhoneNight.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(edtPhoneNight, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(blankPlaceholder, gridBagConstraints);

        lblBillTitle.setLocalizationKey("AccountBillingNameAndAddress.lblBillTitle");
        lblBillTitle.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel2.add(lblBillTitle, gridBagConstraints);

        edtBillTitle.setAttributeName("BillTitle");
        edtBillTitle.setDomainName("Account");
        edtBillTitle.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(edtBillTitle, gridBagConstraints);

        lblCustFax.setLocalizationKey("Fax:");
        lblCustFax.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel2.add(lblCustFax, gridBagConstraints);

        edtCustFax.setAtomName("CustFaxno");
        edtCustFax.setDomainName("Account");
        edtCustFax.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(edtCustFax, gridBagConstraints);

        lblCustEmail.setLocalizationKey("AccountSummaryDetail.vm.label.Email");
        lblCustEmail.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel2.add(lblCustEmail, gridBagConstraints);

        edtCustEmail.setAttributeName("CustEmail");
        edtCustEmail.setDomainName("Account");
        edtCustEmail.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(edtCustEmail, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 10);
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        lblAddressLine1.setLocalizationKey("AccountBillingNameAndAddress.lblAddressLine1");
        lblAddressLine1.setMaximumSize(null);
        lblAddressLine1.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(lblAddressLine1, gridBagConstraints);

        lblCity.setLocalizationKey("AccountBillingNameAndAddress.lblCity");
        lblCity.setMaximumSize(null);
        lblCity.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(lblCity, gridBagConstraints);

        lblStateProv.setLocalizationKey("State:");
        lblStateProv.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(lblStateProv, gridBagConstraints);

        lblCountry.setLocalizationKey("AccountBillingNameAndAddress.lblCountry");
        lblCountry.setMaximumSize(null);
        lblCountry.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(lblCountry, gridBagConstraints);

        lblFranchise.setLocalizationKey("Tax:");
        lblFranchise.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(lblFranchise, gridBagConstraints);

        edtAddressLine2.setAttributeName("BillAddress2");
        edtAddressLine2.setDomainName("Account");
        edtAddressLine2.setMaximumSize(null);
        edtAddressLine2.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(edtAddressLine2, gridBagConstraints);

        edtAddressLine3.setAtomName("BillAddress3");
        edtAddressLine3.setDomainName("Account");
        edtAddressLine3.setMaximumSize(null);
        edtAddressLine3.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(edtAddressLine3, gridBagConstraints);

        edtCity.setAttributeName("BillCity");
        edtCity.setDomainName("Account");
        edtCity.setMaximumSize(null);
        edtCity.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(edtCity, gridBagConstraints);

        edtStateProv.setAttributeName("BillState");
        edtStateProv.setDomainName("Account");
        edtStateProv.setMaximumSize(null);
        edtStateProv.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(edtStateProv, gridBagConstraints);

        cbCountry.setAttributeName("BillCountryCode");
        cbCountry.setDomainName("Account");
        cbCountry.setMaximumSize(null);
        cbCountry.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(cbCountry, gridBagConstraints);

        cbFranchise.setAttributeName("BillFranchiseTaxCode");
        cbFranchise.setDomainName("Account");
        cbFranchise.setMaximumSize(null);
        cbFranchise.setMinimumSize(new java.awt.Dimension(150, 20));
        cbFranchise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFranchiseActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(cbFranchise, gridBagConstraints);

        edtAddressLine1.setAttributeName("BillAddress1");
        edtAddressLine1.setDomainName("Account");
        edtAddressLine1.setMaximumSize(null);
        edtAddressLine1.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(edtAddressLine1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(blankPlaceholder1, gridBagConstraints);

        lblBillGeocode.setLocalizationKey("AccountBillingNameAndAddress.lblBillGeocode");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanel3.add(lblBillGeocode, gridBagConstraints);

        edtBillGeocode.setAtomName("BillGeocode");
        edtBillGeocode.setDomainName("Account");
        edtBillGeocode.setEnabled(false);
        edtBillGeocode.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(edtBillGeocode, gridBagConstraints);

        lblZipPostalCode.setLocalizationKey("Postcode:");
        lblZipPostalCode.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(lblZipPostalCode, gridBagConstraints);

        edtZipPostalCode.setAttributeName("BillZip");
        edtZipPostalCode.setDomainName("Account");
        edtZipPostalCode.setMaximumSize(null);
        edtZipPostalCode.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(edtZipPostalCode, gridBagConstraints);

        lblRevRcvCostCtr.setLocalizationKey("Revenue Cost Center:");
        lblRevRcvCostCtr.setMaximumSize(null);
        lblRevRcvCostCtr.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(lblRevRcvCostCtr, gridBagConstraints);

        cbRevRcvCostCtr.setAttributeName("RevRcvCostCtr");
        cbRevRcvCostCtr.setDomainName("Account");
        cbRevRcvCostCtr.setMaximumSize(null);
        cbRevRcvCostCtr.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(cbRevRcvCostCtr, gridBagConstraints);

        lblGlobalContractsApply.setLocalizationKey("Global Contracts Apply?:");
        lblGlobalContractsApply.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        jPanel3.add(lblGlobalContractsApply, gridBagConstraints);

        chkGlobalContractsApply.setAttributeName("GlobalContractStatus");
        chkGlobalContractsApply.setDomainName("Account");
        chkGlobalContractsApply.setFalseValue("0");
        chkGlobalContractsApply.setTrueValue("1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        jPanel3.add(chkGlobalContractsApply, gridBagConstraints);

        lblVipCode.setLocalizationKey("Collections Code:");
        lblVipCode.setMinimumSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        jPanel3.add(lblVipCode, gridBagConstraints);

        cbVipCode.setAttributeName("VipCode");
        cbVipCode.setDomainName("Account");
        cbVipCode.setMinimumSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        jPanel3.add(cbVipCode, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 5);
        jPanel1.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void cbFranchiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFranchiseActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_cbFranchiseActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel blankPlaceholder;
    private javax.swing.JLabel blankPlaceholder1;
    private com.csgsystems.igpa.controls.CSGComboBox cbCountry;
    private com.csgsystems.igpa.controls.CSGComboBox cbFranchise;
    private com.csgsystems.igpa.controls.CSGComboBox cbRevRcvCostCtr;
    private com.csgsystems.igpa.controls.CSGComboBox cbVipCode;
    private com.csgsystems.igpa.controls.CSGCheckBox chkGlobalContractsApply;
    private com.csgsystems.igpa.controls.CSGEdit edtAccountID;
    private com.csgsystems.igpa.controls.CSGEdit edtAddressLine1;
    private com.csgsystems.igpa.controls.CSGEdit edtAddressLine2;
    private com.csgsystems.igpa.controls.CSGEdit edtAddressLine3;
    private com.csgsystems.igpa.controls.CSGEdit edtBillGeocode;
    private com.csgsystems.igpa.controls.CSGComboBox edtBillTitle;
    private com.csgsystems.igpa.controls.CSGEdit edtCity;
    private com.csgsystems.igpa.controls.CSGEdit edtCompanyName;
    private com.csgsystems.igpa.controls.CSGEdit edtCustEmail;
    private com.csgsystems.igpa.controls.CSGEdit edtCustFax;
    private com.csgsystems.igpa.controls.CSGEdit edtFirstName;
    private com.csgsystems.igpa.controls.CSGEdit edtLastName;
    private com.csgsystems.igpa.controls.CSGEdit edtMiddleName;
    private com.csgsystems.igpa.controls.CSGEdit edtPhoneDay;
    private com.csgsystems.igpa.controls.CSGEdit edtPhoneNight;
    private com.csgsystems.igpa.controls.CSGEdit edtStateProv;
    private com.csgsystems.igpa.controls.CSGEdit edtZipPostalCode;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private com.csgsystems.igpa.controls.CSGLabel lblAccountID;
    private com.csgsystems.igpa.controls.CSGLabel lblAddressLine1;
    private com.csgsystems.igpa.controls.CSGLabel lblBillGeocode;
    private com.csgsystems.igpa.controls.CSGLabel lblBillTitle;
    private com.csgsystems.igpa.controls.CSGLabel lblCity;
    private com.csgsystems.igpa.controls.CSGLabel lblCompanyName;
    private com.csgsystems.igpa.controls.CSGLabel lblCountry;
    private com.csgsystems.igpa.controls.CSGLabel lblCustEmail;
    private com.csgsystems.igpa.controls.CSGLabel lblCustFax;
    private com.csgsystems.igpa.controls.CSGLabel lblFirstName;
    private com.csgsystems.igpa.controls.CSGLabel lblFranchise;
    private com.csgsystems.igpa.controls.CSGLabel lblGlobalContractsApply;
    private com.csgsystems.igpa.controls.CSGLabel lblLastName;
    private com.csgsystems.igpa.controls.CSGLabel lblMiddleName;
    private com.csgsystems.igpa.controls.CSGLabel lblPhoneDay;
    private com.csgsystems.igpa.controls.CSGLabel lblPhoneNight;
    private com.csgsystems.igpa.controls.CSGLabel lblRevRcvCostCtr;
    private com.csgsystems.igpa.controls.CSGLabel lblStateProv;
    private com.csgsystems.igpa.controls.CSGLabel lblVipCode;
    private com.csgsystems.igpa.controls.CSGLabel lblZipPostalCode;
    // End of variables declaration//GEN-END:variables
    
}
