/*
 * AccountNameAddressEdit.java
 *
 * Created on December 5, 2002, 12:54 PM
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
 *@todo billtitle change to dropdown
 * @author  prev01
 */
public class MXSAccountNameAddressEdit extends javax.swing.JPanel implements ContextFormListener {
  
  private static int BILLING = 0;
  private static int CUSTOMER = 1;
  
  /**
   *  Instance of the ContextFinder utility class, which can recursively
   *  search up the component hierarchy for an ICSGContextForm, and then
   *  retrieve its context (cached for later use).
   */
  private ContextFinder ctxFinder = new ContextFinder(this);
  boolean firstRun = true;
  /** Creates new form AccountNameAddressEdit */
  public MXSAccountNameAddressEdit() {
    initComponents();
  }
  
  private void firstRun() {
    if(firstRun) {
      IContext ctx = ctxFinder.findContext();
      IPersistentObject account = ctx.getObject("Account", null);
      //account.setAttributeData("ReferencePostcode",  account.getAttributeDataAsString("BillZip"));
      
      account.getAttribute("BillAddress1").setMaxLength(40);
      account.getAttribute("BillAddress2").setMaxLength(40);
      account.getAttribute("BillAddress3").setMaxLength(40);
      account.getAttribute("CustAddress1").setMaxLength(40);
      account.getAttribute("CustAddress2").setMaxLength(40);
      account.getAttribute("CustAddress3").setMaxLength(40);


      firstRun = false;
    }
  }
  
  public void contextFormStateChanged(com.csgsystems.igpa.forms.ContextFormEvent contextFormEvent) {
    firstRun();
    System.out.println("*** Context form changed");
    if (ContextFormEvent.POST_INIT_CONTROLS == contextFormEvent.getType()) {
      // add a listener to the controls that make up a geocode find
      edtCity.addBoundDataChangeListener(new BoundDataChangeListener() {
        public void boundDataChanged(BoundDataChangeEvent bdce) {
          doGeocodeFind(CUSTOMER);
        }
      });
      
      edtCity1.addBoundDataChangeListener(new BoundDataChangeListener() {
        public void boundDataChanged(BoundDataChangeEvent bdce) {
          doGeocodeFind(BILLING);
        }
      });
      
      edtStateProv.addBoundDataChangeListener(new BoundDataChangeListener() {
        public void boundDataChanged(BoundDataChangeEvent bdce) {
          doGeocodeFind(CUSTOMER);
        }
      });
      
      edtStateProv1.addBoundDataChangeListener(new BoundDataChangeListener() {
        public void boundDataChanged(BoundDataChangeEvent bdce) {
          doGeocodeFind(BILLING);
        }
      });
      
      edtZipPostalCode.addBoundDataChangeListener(new BoundDataChangeListener() {
        public void boundDataChanged(BoundDataChangeEvent bdce) {
          doGeocodeFind(CUSTOMER);
        }
      });
      
      edtZipPostalCode1.addBoundDataChangeListener(new BoundDataChangeListener() {
        public void boundDataChanged(BoundDataChangeEvent bdce) {
          doGeocodeFind(BILLING);
        }
      });
      
      cbCountry.addBoundDataChangeListener(new BoundDataChangeListener() {
        public void boundDataChanged(BoundDataChangeEvent bdce) {
          doGeocodeFind(CUSTOMER);
          changeCityState();
        }
      });
      
      cbCountry1.addBoundDataChangeListener(new BoundDataChangeListener() {
        public void boundDataChanged(BoundDataChangeEvent bdce) {
          doGeocodeFind(BILLING);
          changeCityState();
        }
      });
      
      cbFranchise.addBoundDataChangeListener(new BoundDataChangeListener() {
        public void boundDataChanged(BoundDataChangeEvent bdce) {
          doGeocodeFind(CUSTOMER);
        }
      });
      
      cbFranchise1.addBoundDataChangeListener(new BoundDataChangeListener() {
        public void boundDataChanged(BoundDataChangeEvent bdce) {
          doGeocodeFind(BILLING);
        }
      });
      
      IContext ctx = ctxFinder.findContext();
      IPersistentObject account = ctx.getObject("Account", null);
      if (account.getAttributeDataAsBoolean("VirtualIsCountyFreeForm") == true) {
        //ccbCustCounty.setVisible(false);
        //ccbBillCounty.setVisible(false);
        /*
        lblCustGeocode.setVisible(false);
        edtCustGeocode.setVisible(false);
        lblBillGeocode.setVisible(false);
        edtBillGeocode.setVisible(false);
         */
        //edtCounty.setVisible(true);
        //edtCounty1.setVisible(true);
        checkSelectedCountry();
      } else {
        //ccbCustCounty.setVisible(true);
        //ccbBillCounty.setVisible(true);
        /*
        lblCustGeocode.setVisible(true);
        edtCustGeocode.setVisible(true);
        lblBillGeocode.setVisible(true);
        edtBillGeocode.setVisible(true);
         */
        //edtCounty.setVisible(false);
        //edtCounty1.setVisible(false);
        checkSelectedCountry();
      }
    }
  }

    private void changeCityState() {
        IContext ctx = ctxFinder.findContext();
        IPersistentObject account = ctx.getObject("Account", null);
        // BillCountryCode
        if ( account.getAttributeDataAsInteger("BillCountryCode") == 458 ) {
            account.getAttribute("BillState").setReadOnly(true);
            account.getAttribute("BillCity").setReadOnly(true);
            edtCity1.initializeControl();
            edtStateProv1.initializeControl();
            //14 dec 2005  KenanFX – CC – Common-61 MH: 
            //force focus to the BillZip if its malaysia, so that validation will be made
            edtZipPostalCode1.grabFocus();
        } else {
            account.getAttribute("BillState").setReadOnly(false);
            account.getAttribute("BillCity").setReadOnly(false);
            edtCity1.initializeControl();
            edtStateProv1.initializeControl();            
        }
        // CustCountryCode
        if ( account.getAttributeDataAsInteger("CustCountryCode") == 458 ) {
            account.getAttribute("CustState").setReadOnly(true);
            account.getAttribute("CustCity").setReadOnly(true);
            edtCity.initializeControl();
            edtStateProv.initializeControl();
        } else {
            account.getAttribute("CustState").setReadOnly(false);
            account.getAttribute("CustCity").setReadOnly(false);
            edtCity.initializeControl();
            edtStateProv.initializeControl();            
        }
    }
  
  private void doGeocodeFind(int nFlag) {
    IContext ctx = ctxFinder.findContext();
    IPersistentObject account = ctx.getObject("Account", null);
    
    if (nFlag == CUSTOMER) {
      account.sendMessage("resetGeocodeCustomerAddressList", null);
      //ccbCustCounty.initializeControl();
      //edtCustGeocode.initializeControl();
    } else {
      account.sendMessage("resetGeocodeBillingAddressList", null);
      //ccbBillCounty.initializeControl();
      //edtBillGeocode.initializeControl();
    }
    checkSelectedCountry();
  }
  
  private void checkSelectedCountry() {
    // BillCountryCode
    if(cbCountry1.getSelectedItem() != null && "Malaysia".equals(cbCountry1.getSelectedItem().toString())) {
      edtStateProv1.setEnabled(false);
      edtCity1.setEnabled(false);
    }else {
      edtStateProv1.setEnabled(true);
      edtCity1.setEnabled(true);
    }
    // CustCountryCode
    if(cbCountry.getSelectedItem() != null && "Malaysia".equals(cbCountry.getSelectedItem().toString())) {
      edtStateProv.setEnabled(false);
      edtCity.setEnabled(false);
    }else {
      edtStateProv.setEnabled(true);
      edtCity.setEnabled(true);
    }
  }
  
  private void refreshData() {
    IContext ctx = ctxFinder.findContext();
    IPersistentObject account = ctx.getObject("Account", null);
    this.edtStateProv1.setText(account.getAttributeDataAsString("BillState"));
    this.edtCity1.setText(account.getAttributeDataAsString("BillCity"));
    this.edtStateProv.setText(account.getAttributeDataAsString("CustState"));
    this.edtCity.setText(account.getAttributeDataAsString("CustCity"));
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel11 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        lblCompanyName = new com.csgsystems.igpa.controls.CSGLabel();
        edtCompanyName = new com.csgsystems.igpa.controls.CSGEdit();
        lblPhoneDay = new com.csgsystems.igpa.controls.CSGLabel();
        edtPhoneDay = new com.csgsystems.igpa.controls.CSGEdit();
        lblPhoneNight = new com.csgsystems.igpa.controls.CSGLabel();
        edtPhoneNight = new com.csgsystems.igpa.controls.CSGEdit();
        lblLanguage = new com.csgsystems.igpa.controls.CSGLabel();
        cbLanguage = new com.csgsystems.igpa.controls.CSGComboBox();
        lblVipCode = new com.csgsystems.igpa.controls.CSGLabel();
        cbVipCode = new com.csgsystems.igpa.controls.CSGComboBox();
        lblGlobalContractStatus = new com.csgsystems.igpa.controls.CSGLabel();
        chkGlobalContractStatus = new com.csgsystems.igpa.controls.CSGCheckBox();
        jPanel4 = new javax.swing.JPanel();
        lblAccountID = new com.csgsystems.igpa.controls.CSGLabel();
        edtAccountID = new com.csgsystems.igpa.controls.CSGEdit();
        lblBillTitle = new com.csgsystems.igpa.controls.CSGLabel();
        cSGComboBox1 = new com.csgsystems.igpa.controls.CSGComboBox();
        lblFirstName = new com.csgsystems.igpa.controls.CSGLabel();
        edtFirstName = new com.csgsystems.igpa.controls.CSGEdit();
        lblMiddleName = new com.csgsystems.igpa.controls.CSGLabel();
        edtMiddleName = new com.csgsystems.igpa.controls.CSGEdit();
        lblLastName = new com.csgsystems.igpa.controls.CSGLabel();
        edtLastName = new com.csgsystems.igpa.controls.CSGEdit();
        lblFax = new com.csgsystems.igpa.controls.CSGLabel();
        edtFax = new com.csgsystems.igpa.controls.CSGEdit();
        jPanel3 = new javax.swing.JPanel();
        lblAddressLine11 = new com.csgsystems.igpa.controls.CSGLabel();
        lblCity1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblStateProv1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblCountry1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblFranchise1 = new com.csgsystems.igpa.controls.CSGLabel();
        edtAddressLine21 = new com.csgsystems.igpa.controls.CSGEdit();
        cSGEdit11 = new com.csgsystems.igpa.controls.CSGEdit();
        edtCity1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtStateProv1 = new com.csgsystems.igpa.controls.CSGEdit();
        cbCountry1 = new com.csgsystems.igpa.controls.CSGComboBox();
        cbFranchise1 = new com.csgsystems.igpa.controls.CSGComboBox();
        edtAddressLine11 = new com.csgsystems.igpa.controls.CSGEdit();
        edtZipPostalCode1 = new com.csgsystems.igpa.controls.CSGEdit();
        lblZipPostalCode1 = new com.csgsystems.igpa.controls.CSGLabel();
        edtEMail1 = new com.csgsystems.igpa.controls.CSGEdit();
        lblEMail1 = new com.csgsystems.igpa.controls.CSGLabel();
        jPanel1 = new javax.swing.JPanel();
        lblAddressLine1 = new com.csgsystems.igpa.controls.CSGLabel();
        edtAddressLine1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtAddressLine2 = new com.csgsystems.igpa.controls.CSGEdit();
        edtCity = new com.csgsystems.igpa.controls.CSGEdit();
        edtStateProv = new com.csgsystems.igpa.controls.CSGEdit();
        lblCity = new com.csgsystems.igpa.controls.CSGLabel();
        lblStateProv = new com.csgsystems.igpa.controls.CSGLabel();
        lblCountry = new com.csgsystems.igpa.controls.CSGLabel();
        lblFranchise = new com.csgsystems.igpa.controls.CSGLabel();
        cbCountry = new com.csgsystems.igpa.controls.CSGComboBox();
        cbFranchise = new com.csgsystems.igpa.controls.CSGComboBox();
        cSGEdit1 = new com.csgsystems.igpa.controls.CSGEdit();
        jPanel2 = new javax.swing.JPanel();
        lblAddressLine2 = new com.csgsystems.igpa.controls.CSGLabel();
        edtAddressLine3 = new com.csgsystems.igpa.controls.CSGEdit();
        edtAddressLine4 = new com.csgsystems.igpa.controls.CSGEdit();
        edtCity2 = new com.csgsystems.igpa.controls.CSGEdit();
        edtStateProv2 = new com.csgsystems.igpa.controls.CSGEdit();
        lblCity2 = new com.csgsystems.igpa.controls.CSGLabel();
        lblStateProv2 = new com.csgsystems.igpa.controls.CSGLabel();
        lblZipPostalCode2 = new com.csgsystems.igpa.controls.CSGLabel();
        edtZipPostalCode2 = new com.csgsystems.igpa.controls.CSGEdit();
        lblCounty2 = new com.csgsystems.igpa.controls.CSGLabel();
        lblCountry2 = new com.csgsystems.igpa.controls.CSGLabel();
        lblFranchise2 = new com.csgsystems.igpa.controls.CSGLabel();
        cbCountry2 = new com.csgsystems.igpa.controls.CSGComboBox();
        cbFranchise2 = new com.csgsystems.igpa.controls.CSGComboBox();
        cSGEdit2 = new com.csgsystems.igpa.controls.CSGEdit();
        ccbCustCounty1 = new com.csgsystems.igpa.controls.CSGCollectionComboBox();
        edtZipPostalCode = new com.csgsystems.igpa.controls.CSGEdit();
        lblZipPostalCode = new com.csgsystems.igpa.controls.CSGLabel();
        cSGLabel1 = new com.csgsystems.igpa.controls.CSGLabel();
        cSGLabel2 = new com.csgsystems.igpa.controls.CSGLabel();
        cSGLabel3 = new com.csgsystems.igpa.controls.CSGLabel();
        cSGLabel4 = new com.csgsystems.igpa.controls.CSGLabel();
        cSGEdit3 = new com.csgsystems.igpa.controls.CSGEdit();
        cSGEdit4 = new com.csgsystems.igpa.controls.CSGEdit();
        cSGEdit5 = new com.csgsystems.igpa.controls.CSGEdit();
        cSGEdit6 = new com.csgsystems.igpa.controls.CSGEdit();
        edtEMail2 = new com.csgsystems.igpa.controls.CSGEdit();
        lblEMail2 = new com.csgsystems.igpa.controls.CSGLabel();

        setLayout(null);

        setPreferredSize(new java.awt.Dimension(650, 550));
        jPanel11.setLayout(null);

        jPanel11.setBorder(new javax.swing.border.TitledBorder(ResourceManager.getString( "AccountBillingNameAndAddress.title.BillingNameAndAddress" )));
        jPanel11.setPreferredSize(new java.awt.Dimension(650, 270));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblCompanyName.setLocalizationKey("AccountBillingNameAndAddress.lblCompanyName");
        jPanel5.add(lblCompanyName);

        edtCompanyName.setAttributeName("BillCompany");
        edtCompanyName.setDomainName("Account");
        jPanel5.add(edtCompanyName);

        lblPhoneDay.setLocalizationKey("AccountBillingNameAndAddress.lblPhoneDay");
        jPanel5.add(lblPhoneDay);

        edtPhoneDay.setAttributeName("CustPhone1");
        edtPhoneDay.setDomainName("Account");
        jPanel5.add(edtPhoneDay);

        lblPhoneNight.setLocalizationKey("AccountBillingNameAndAddress.lblPhoneNight");
        jPanel5.add(lblPhoneNight);

        edtPhoneNight.setAttributeName("CustPhone2");
        edtPhoneNight.setDomainName("Account");
        jPanel5.add(edtPhoneNight);

        lblLanguage.setLocalizationKey("AccountBillingNameAndAddress.lblLanguage");
        jPanel5.add(lblLanguage);

        cbLanguage.setAttributeName("LanguageCode");
        cbLanguage.setDomainName("Account");
        cbLanguage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbLanguageActionPerformed(evt);
            }
        });

        jPanel5.add(cbLanguage);

        lblVipCode.setLocalizationKey("AccountSummaryDetail.vm.label.VIPCode");
        jPanel5.add(lblVipCode);

        cbVipCode.setAttributeName("VipCode");
        cbVipCode.setDomainName("Account");
        jPanel5.add(cbVipCode);

        lblGlobalContractStatus.setLocalizationKey("AccountSummaryDetail.vm.label.GlobalContractStatus");
        jPanel5.add(lblGlobalContractStatus);

        chkGlobalContractStatus.setAttributeName("GlobalContractStatus");
        chkGlobalContractStatus.setDomainName("Account");
        chkGlobalContractStatus.setFalseValue("0");
        chkGlobalContractStatus.setTrueValue("1");
        jPanel5.add(chkGlobalContractStatus);

        jPanel11.add(jPanel5);
        jPanel5.setBounds(320, 20, 300, 160);

        lblAccountID.setLocalizationKey("AccountBillingNameAndAddress.lblAccountID");
        jPanel4.add(lblAccountID);

        edtAccountID.setEditable(false);
        edtAccountID.setAttributeName("AccountExternalId");
        edtAccountID.setDomainName("Account");
        edtAccountID.setEnabled(false);
        jPanel4.add(edtAccountID);

        lblBillTitle.setLocalizationKey("AccountBillingNameAndAddress.lblBillTitle");
        jPanel4.add(lblBillTitle);

        cSGComboBox1.setAttributeName("BillTitle");
        cSGComboBox1.setDomainName("Account");
        jPanel4.add(cSGComboBox1);

        lblFirstName.setLocalizationKey("AccountBillingNameAndAddress.lblFirstName");
        jPanel4.add(lblFirstName);

        edtFirstName.setAttributeName("BillFname");
        edtFirstName.setDomainName("Account");
        jPanel4.add(edtFirstName);

        lblMiddleName.setLocalizationKey("AccountBillingNameAndAddress.lblMiddleName");
        jPanel4.add(lblMiddleName);

        edtMiddleName.setAttributeName("BillMinit");
        edtMiddleName.setDomainName("Account");
        jPanel4.add(edtMiddleName);

        lblLastName.setLocalizationKey("AccountBillingNameAndAddress.lblLastName");
        jPanel4.add(lblLastName);

        edtLastName.setAttributeName("BillLname");
        edtLastName.setDomainName("Account");
        jPanel4.add(edtLastName);

        lblFax.setLocalizationKey("AccountSummaryDetail.vm.label.StatementFaxNumber");
        jPanel4.add(lblFax);

        edtFax.setAtomName("StatementToFaxno");
        edtFax.setDomainName("Account");
        jPanel4.add(edtFax);

        jPanel11.add(jPanel4);
        jPanel4.setBounds(10, 20, 300, 160);

        add(jPanel11);
        jPanel11.setBounds(5, 5, 635, 190);

        jPanel3.setLayout(null);

        jPanel3.setBorder(new javax.swing.border.TitledBorder(ResourceManager.getString( "AccountBillingNameAndAddress.title.BillingAddress" )));
        lblAddressLine11.setLocalizationKey("AccountBillingNameAndAddress.lblAddressLine1");
        jPanel3.add(lblAddressLine11);
        lblAddressLine11.setBounds(10, 20, 140, 20);

        lblCity1.setLocalizationKey("AccountBillingNameAndAddress.lblCity");
        jPanel3.add(lblCity1);
        lblCity1.setBounds(10, 120, 140, 20);

        lblStateProv1.setLocalizationKey("AccountBillingNameAndAddress.lblStateProv");
        jPanel3.add(lblStateProv1);
        lblStateProv1.setBounds(10, 145, 140, 20);

        lblCountry1.setLocalizationKey("AccountBillingNameAndAddress.lblCountry");
        jPanel3.add(lblCountry1);
        lblCountry1.setBounds(10, 170, 140, 20);

        lblFranchise1.setLocalizationKey("AccountBillingNameAndAddress.lblFranchise");
        jPanel3.add(lblFranchise1);
        lblFranchise1.setBounds(10, 195, 140, 20);

        edtAddressLine21.setAttributeName("BillAddress2");
        edtAddressLine21.setDomainName("Account");
        edtAddressLine21.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                edtAddressLine21KeyTyped(evt);
            }
        });

        jPanel3.add(edtAddressLine21);
        edtAddressLine21.setBounds(155, 45, 150, 20);

        cSGEdit11.setAtomName("BillAddress3");
        cSGEdit11.setDomainName("Account");
        cSGEdit11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cSGEdit11KeyTyped(evt);
            }
        });

        jPanel3.add(cSGEdit11);
        cSGEdit11.setBounds(155, 70, 150, 20);

        edtCity1.setAttributeName("BillCity");
        edtCity1.setDomainName("Account");
        edtCity1.setEnabled(false);
        jPanel3.add(edtCity1);
        edtCity1.setBounds(155, 120, 150, 20);

        edtStateProv1.setAttributeName("BillState");
        edtStateProv1.setDomainName("Account");
        edtStateProv1.setEnabled(false);
        jPanel3.add(edtStateProv1);
        edtStateProv1.setBounds(155, 145, 150, 20);

        cbCountry1.setAttributeName("BillCountryCode");
        cbCountry1.setDomainName("Account");
        jPanel3.add(cbCountry1);
        cbCountry1.setBounds(155, 170, 150, 20);

        cbFranchise1.setAttributeName("BillFranchiseTaxCode");
        cbFranchise1.setDomainName("Account");
        jPanel3.add(cbFranchise1);
        cbFranchise1.setBounds(155, 195, 150, 20);

        edtAddressLine11.setAttributeName("BillAddress1");
        edtAddressLine11.setDomainName("Account");
        edtAddressLine11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                edtAddressLine11KeyTyped(evt);
            }
        });

        jPanel3.add(edtAddressLine11);
        edtAddressLine11.setBounds(155, 20, 150, 20);

        edtZipPostalCode1.setAttributeName("BillZip");
        edtZipPostalCode1.setDomainName("Account");
        edtZipPostalCode1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                lostFocusHandler(evt);
            }
        });

        jPanel3.add(edtZipPostalCode1);
        edtZipPostalCode1.setBounds(155, 95, 150, 20);

        lblZipPostalCode1.setLocalizationKey("AccountBillingNameAndAddress.lblZipPostalCode");
        jPanel3.add(lblZipPostalCode1);
        lblZipPostalCode1.setBounds(10, 95, 140, 20);

        edtEMail1.setAttributeName("CustEmail");
        edtEMail1.setDomainName("Account");
        jPanel3.add(edtEMail1);
        edtEMail1.setBounds(155, 220, 150, 20);

        lblEMail1.setLocalizationKey("AccountSummaryDetail.vm.label.Email");
        jPanel3.add(lblEMail1);
        lblEMail1.setBounds(10, 220, 140, 20);

        add(jPanel3);
        jPanel3.setBounds(5, 200, 315, 350);

        jPanel1.setLayout(null);

        jPanel1.setBorder(new javax.swing.border.TitledBorder(ResourceManager.getString( "AccountAlternateAddress.title.AlternateAddress" )));
        jPanel1.setMinimumSize(new java.awt.Dimension(315, 265));
        jPanel1.setPreferredSize(new java.awt.Dimension(315, 265));
        lblAddressLine1.setLocalizationKey("AccountAlternateAddress.lblAddressLine1");
        jPanel1.add(lblAddressLine1);
        lblAddressLine1.setBounds(10, 20, 140, 20);

        edtAddressLine1.setAttributeName("CustAddress1");
        edtAddressLine1.setDomainName("Account");
        edtAddressLine1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                edtAddressLine1KeyTyped(evt);
            }
        });

        jPanel1.add(edtAddressLine1);
        edtAddressLine1.setBounds(150, 20, 150, 20);

        edtAddressLine2.setAttributeName("CustAddress2");
        edtAddressLine2.setDomainName("Account");
        edtAddressLine2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                edtAddressLine2KeyTyped(evt);
            }
        });

        jPanel1.add(edtAddressLine2);
        edtAddressLine2.setBounds(150, 45, 150, 20);

        edtCity.setAttributeName("CustCity");
        edtCity.setDomainName("Account");
        jPanel1.add(edtCity);
        edtCity.setBounds(150, 120, 150, 20);

        edtStateProv.setAttributeName("CustState");
        edtStateProv.setDomainName("Account");
        jPanel1.add(edtStateProv);
        edtStateProv.setBounds(150, 145, 150, 20);

        lblCity.setLocalizationKey("AccountAlternateAddress.lblCity");
        jPanel1.add(lblCity);
        lblCity.setBounds(10, 120, 140, 20);

        lblStateProv.setLocalizationKey("AccountAlternateAddress.lblStateProv");
        jPanel1.add(lblStateProv);
        lblStateProv.setBounds(10, 145, 140, 20);

        lblCountry.setLocalizationKey("AccountAlternateAddress.lblCountry");
        jPanel1.add(lblCountry);
        lblCountry.setBounds(10, 170, 140, 20);

        lblFranchise.setLocalizationKey("AccountAlternateAddress.lblFranchise");
        jPanel1.add(lblFranchise);
        lblFranchise.setBounds(10, 195, 140, 20);

        cbCountry.setAttributeName("CustCountryCode");
        cbCountry.setDomainName("Account");
        jPanel1.add(cbCountry);
        cbCountry.setBounds(150, 170, 150, 20);

        cbFranchise.setAttributeName("CustFranchiseTaxCode");
        cbFranchise.setDomainName("Account");
        jPanel1.add(cbFranchise);
        cbFranchise.setBounds(150, 195, 150, 20);

        cSGEdit1.setAttributeName("CustAddress3");
        cSGEdit1.setDomainName("Account");
        cSGEdit1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cSGEdit1KeyTyped(evt);
            }
        });

        jPanel1.add(cSGEdit1);
        cSGEdit1.setBounds(150, 70, 150, 20);

        jPanel2.setLayout(null);

        jPanel2.setBorder(new javax.swing.border.TitledBorder(ResourceManager.getString( "AccountAlternateAddress.title.AlternateAddress" )));
        jPanel2.setMinimumSize(new java.awt.Dimension(315, 265));
        lblAddressLine2.setLocalizationKey("AccountAlternateAddress.lblAddressLine1");
        jPanel2.add(lblAddressLine2);
        lblAddressLine2.setBounds(10, 20, 140, 20);

        edtAddressLine3.setAttributeName("CustAddress1");
        edtAddressLine3.setDomainName("Account");
        jPanel2.add(edtAddressLine3);
        edtAddressLine3.setBounds(150, 20, 150, 20);

        edtAddressLine4.setAttributeName("CustAddress2");
        edtAddressLine4.setDomainName("Account");
        jPanel2.add(edtAddressLine4);
        edtAddressLine4.setBounds(150, 45, 150, 20);

        edtCity2.setAttributeName("CustCity");
        edtCity2.setDomainName("Account");
        jPanel2.add(edtCity2);
        edtCity2.setBounds(150, 95, 150, 20);

        edtStateProv2.setAttributeName("CustState");
        edtStateProv2.setDomainName("Account");
        jPanel2.add(edtStateProv2);
        edtStateProv2.setBounds(150, 120, 150, 20);

        lblCity2.setLocalizationKey("AccountAlternateAddress.lblCity");
        jPanel2.add(lblCity2);
        lblCity2.setBounds(10, 95, 140, 20);

        lblStateProv2.setLocalizationKey("AccountAlternateAddress.lblStateProv");
        jPanel2.add(lblStateProv2);
        lblStateProv2.setBounds(10, 120, 140, 20);

        lblZipPostalCode2.setLocalizationKey("AccountAlternateAddress.lblZipPostalCode");
        jPanel2.add(lblZipPostalCode2);
        lblZipPostalCode2.setBounds(10, 145, 140, 20);

        edtZipPostalCode2.setAttributeName("CustZip");
        edtZipPostalCode2.setDomainName("Account");
        jPanel2.add(edtZipPostalCode2);
        edtZipPostalCode2.setBounds(150, 145, 150, 20);

        lblCounty2.setLocalizationKey("AccountAlternateAddress.lblCounty");
        jPanel2.add(lblCounty2);
        lblCounty2.setBounds(10, 220, 140, 20);

        lblCountry2.setLocalizationKey("AccountAlternateAddress.lblCountry");
        jPanel2.add(lblCountry2);
        lblCountry2.setBounds(10, 170, 140, 20);

        lblFranchise2.setLocalizationKey("AccountAlternateAddress.lblFranchise");
        jPanel2.add(lblFranchise2);
        lblFranchise2.setBounds(10, 195, 140, 20);

        cbCountry2.setAttributeName("CustCountryCode");
        cbCountry2.setDomainName("Account");
        jPanel2.add(cbCountry2);
        cbCountry2.setBounds(150, 170, 150, 20);

        cbFranchise2.setAttributeName("CustFranchiseTaxCode");
        cbFranchise2.setDomainName("Account");
        jPanel2.add(cbFranchise2);
        cbFranchise2.setBounds(150, 195, 150, 20);

        cSGEdit2.setAttributeName("CustAddress3");
        cSGEdit2.setDomainName("Account");
        jPanel2.add(cSGEdit2);
        cSGEdit2.setBounds(150, 70, 150, 20);

        ccbCustCounty1.setAttributeName("CustGeocode");
        ccbCustCounty1.setContextObserver(true);
        ccbCustCounty1.setDomainName("Account");
        ccbCustCounty1.setEnumCollectionName("GeocodeList");
        ccbCustCounty1.setEnumCollectionSubtype("Account-CustomerAddress");
        ccbCustCounty1.setEnumDisplayAttributeName("VirtualCountyGeocode");
        ccbCustCounty1.setEnumKeyAttributeName("Geocode");
        jPanel2.add(ccbCustCounty1);
        ccbCustCounty1.setBounds(150, 220, 150, 20);

        jPanel1.add(jPanel2);
        jPanel2.setBounds(325, 195, 315, 265);

        edtZipPostalCode.setAttributeName("CustZip");
        edtZipPostalCode.setDomainName("Account");
        edtZipPostalCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                lostFocusHandler(evt);
            }
        });

        jPanel1.add(edtZipPostalCode);
        edtZipPostalCode.setBounds(150, 95, 150, 20);

        lblZipPostalCode.setLocalizationKey("AccountAlternateAddress.lblZipPostalCode");
        jPanel1.add(lblZipPostalCode);
        lblZipPostalCode.setBounds(10, 95, 140, 20);

        cSGLabel1.setLocalizationKey("AccountSummaryDetail.vm.label.PrimaryContact");
        jPanel1.add(cSGLabel1);
        cSGLabel1.setBounds(10, 220, 140, 20);

        cSGLabel2.setLocalizationKey("AccountSummaryDetail.vm.label.PrimaryPhone");
        jPanel1.add(cSGLabel2);
        cSGLabel2.setBounds(10, 245, 140, 20);

        cSGLabel3.setLocalizationKey("AccountSummaryDetail.vm.label.SecondaryContact");
        jPanel1.add(cSGLabel3);
        cSGLabel3.setBounds(10, 270, 140, 20);

        cSGLabel4.setLocalizationKey("AccountSummaryDetail.vm.label.SecondaryPhone");
        jPanel1.add(cSGLabel4);
        cSGLabel4.setBounds(10, 295, 140, 20);

        cSGEdit3.setAttributeName("Contact1Name");
        cSGEdit3.setDomainName("Account");
        jPanel1.add(cSGEdit3);
        cSGEdit3.setBounds(150, 220, 150, 20);

        cSGEdit4.setAtomName("Contact1Phone");
        cSGEdit4.setDomainName("Account");
        jPanel1.add(cSGEdit4);
        cSGEdit4.setBounds(150, 245, 150, 20);

        cSGEdit5.setAtomName("Contact2Name");
        cSGEdit5.setDomainName("Account");
        jPanel1.add(cSGEdit5);
        cSGEdit5.setBounds(150, 270, 150, 20);

        cSGEdit6.setAtomName("Contact2Phone");
        cSGEdit6.setDomainName("Account");
        jPanel1.add(cSGEdit6);
        cSGEdit6.setBounds(150, 295, 150, 20);

        edtEMail2.setAttributeName("StatementToEmail");
        edtEMail2.setDomainName("Account");
        jPanel1.add(edtEMail2);
        edtEMail2.setBounds(150, 320, 150, 20);

        lblEMail2.setLocalizationKey("AccountSummaryDetail.vm.label.StatementEmail");
        jPanel1.add(lblEMail2);
        lblEMail2.setBounds(10, 320, 140, 20);

        add(jPanel1);
        jPanel1.setBounds(325, 200, 315, 350);

    }
    // </editor-fold>//GEN-END:initComponents

    private void cSGEdit1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cSGEdit1KeyTyped
        // TODO add your handling code here:
        cSGEdit1.keyTyped(evt);
    }//GEN-LAST:event_cSGEdit1KeyTyped

    private void edtAddressLine2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edtAddressLine2KeyTyped
        // TODO add your handling code here:
        edtAddressLine2.keyTyped(evt);
    }//GEN-LAST:event_edtAddressLine2KeyTyped

    private void edtAddressLine1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edtAddressLine1KeyTyped
        // TODO add your handling code here:
        edtAddressLine1.keyTyped(evt);
    }//GEN-LAST:event_edtAddressLine1KeyTyped

    private void cSGEdit11KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cSGEdit11KeyTyped
        // TODO add your handling code here:
        cSGEdit11.keyTyped(evt);
    }//GEN-LAST:event_cSGEdit11KeyTyped

    private void edtAddressLine21KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edtAddressLine21KeyTyped
        // TODO add your handling code here:
        edtAddressLine21.keyTyped(evt);
    }//GEN-LAST:event_edtAddressLine21KeyTyped

    private void edtAddressLine11KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edtAddressLine11KeyTyped
        // TODO add your handling code here:
        edtAddressLine11.keyTyped(evt);
    }//GEN-LAST:event_edtAddressLine11KeyTyped
  
  private void lostFocusHandler(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lostFocusHandler
    // TODO add your handling code here:
    System.out.println("*** Lost focus Refreshing Vars");
    refreshData();
  }//GEN-LAST:event_lostFocusHandler
          
    private void cbLanguageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbLanguageActionPerformed
      // Add your handling code here:
    }//GEN-LAST:event_cbLanguageActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.csgsystems.igpa.controls.CSGComboBox cSGComboBox1;
    private com.csgsystems.igpa.controls.CSGEdit cSGEdit1;
    private com.csgsystems.igpa.controls.CSGEdit cSGEdit11;
    private com.csgsystems.igpa.controls.CSGEdit cSGEdit2;
    private com.csgsystems.igpa.controls.CSGEdit cSGEdit3;
    private com.csgsystems.igpa.controls.CSGEdit cSGEdit4;
    private com.csgsystems.igpa.controls.CSGEdit cSGEdit5;
    private com.csgsystems.igpa.controls.CSGEdit cSGEdit6;
    private com.csgsystems.igpa.controls.CSGLabel cSGLabel1;
    private com.csgsystems.igpa.controls.CSGLabel cSGLabel2;
    private com.csgsystems.igpa.controls.CSGLabel cSGLabel3;
    private com.csgsystems.igpa.controls.CSGLabel cSGLabel4;
    private com.csgsystems.igpa.controls.CSGComboBox cbCountry;
    private com.csgsystems.igpa.controls.CSGComboBox cbCountry1;
    private com.csgsystems.igpa.controls.CSGComboBox cbCountry2;
    private com.csgsystems.igpa.controls.CSGComboBox cbFranchise;
    private com.csgsystems.igpa.controls.CSGComboBox cbFranchise1;
    private com.csgsystems.igpa.controls.CSGComboBox cbFranchise2;
    private com.csgsystems.igpa.controls.CSGComboBox cbLanguage;
    private com.csgsystems.igpa.controls.CSGComboBox cbVipCode;
    private com.csgsystems.igpa.controls.CSGCollectionComboBox ccbCustCounty1;
    private com.csgsystems.igpa.controls.CSGCheckBox chkGlobalContractStatus;
    private com.csgsystems.igpa.controls.CSGEdit edtAccountID;
    private com.csgsystems.igpa.controls.CSGEdit edtAddressLine1;
    private com.csgsystems.igpa.controls.CSGEdit edtAddressLine11;
    private com.csgsystems.igpa.controls.CSGEdit edtAddressLine2;
    private com.csgsystems.igpa.controls.CSGEdit edtAddressLine21;
    private com.csgsystems.igpa.controls.CSGEdit edtAddressLine3;
    private com.csgsystems.igpa.controls.CSGEdit edtAddressLine4;
    private com.csgsystems.igpa.controls.CSGEdit edtCity;
    private com.csgsystems.igpa.controls.CSGEdit edtCity1;
    private com.csgsystems.igpa.controls.CSGEdit edtCity2;
    private com.csgsystems.igpa.controls.CSGEdit edtCompanyName;
    private com.csgsystems.igpa.controls.CSGEdit edtEMail1;
    private com.csgsystems.igpa.controls.CSGEdit edtEMail2;
    private com.csgsystems.igpa.controls.CSGEdit edtFax;
    private com.csgsystems.igpa.controls.CSGEdit edtFirstName;
    private com.csgsystems.igpa.controls.CSGEdit edtLastName;
    private com.csgsystems.igpa.controls.CSGEdit edtMiddleName;
    private com.csgsystems.igpa.controls.CSGEdit edtPhoneDay;
    private com.csgsystems.igpa.controls.CSGEdit edtPhoneNight;
    private com.csgsystems.igpa.controls.CSGEdit edtStateProv;
    private com.csgsystems.igpa.controls.CSGEdit edtStateProv1;
    private com.csgsystems.igpa.controls.CSGEdit edtStateProv2;
    private com.csgsystems.igpa.controls.CSGEdit edtZipPostalCode;
    private com.csgsystems.igpa.controls.CSGEdit edtZipPostalCode1;
    private com.csgsystems.igpa.controls.CSGEdit edtZipPostalCode2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private com.csgsystems.igpa.controls.CSGLabel lblAccountID;
    private com.csgsystems.igpa.controls.CSGLabel lblAddressLine1;
    private com.csgsystems.igpa.controls.CSGLabel lblAddressLine11;
    private com.csgsystems.igpa.controls.CSGLabel lblAddressLine2;
    private com.csgsystems.igpa.controls.CSGLabel lblBillTitle;
    private com.csgsystems.igpa.controls.CSGLabel lblCity;
    private com.csgsystems.igpa.controls.CSGLabel lblCity1;
    private com.csgsystems.igpa.controls.CSGLabel lblCity2;
    private com.csgsystems.igpa.controls.CSGLabel lblCompanyName;
    private com.csgsystems.igpa.controls.CSGLabel lblCountry;
    private com.csgsystems.igpa.controls.CSGLabel lblCountry1;
    private com.csgsystems.igpa.controls.CSGLabel lblCountry2;
    private com.csgsystems.igpa.controls.CSGLabel lblCounty2;
    private com.csgsystems.igpa.controls.CSGLabel lblEMail1;
    private com.csgsystems.igpa.controls.CSGLabel lblEMail2;
    private com.csgsystems.igpa.controls.CSGLabel lblFax;
    private com.csgsystems.igpa.controls.CSGLabel lblFirstName;
    private com.csgsystems.igpa.controls.CSGLabel lblFranchise;
    private com.csgsystems.igpa.controls.CSGLabel lblFranchise1;
    private com.csgsystems.igpa.controls.CSGLabel lblFranchise2;
    private com.csgsystems.igpa.controls.CSGLabel lblGlobalContractStatus;
    private com.csgsystems.igpa.controls.CSGLabel lblLanguage;
    private com.csgsystems.igpa.controls.CSGLabel lblLastName;
    private com.csgsystems.igpa.controls.CSGLabel lblMiddleName;
    private com.csgsystems.igpa.controls.CSGLabel lblPhoneDay;
    private com.csgsystems.igpa.controls.CSGLabel lblPhoneNight;
    private com.csgsystems.igpa.controls.CSGLabel lblStateProv;
    private com.csgsystems.igpa.controls.CSGLabel lblStateProv1;
    private com.csgsystems.igpa.controls.CSGLabel lblStateProv2;
    private com.csgsystems.igpa.controls.CSGLabel lblVipCode;
    private com.csgsystems.igpa.controls.CSGLabel lblZipPostalCode;
    private com.csgsystems.igpa.controls.CSGLabel lblZipPostalCode1;
    private com.csgsystems.igpa.controls.CSGLabel lblZipPostalCode2;
    // End of variables declaration//GEN-END:variables
  
}
