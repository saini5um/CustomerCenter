/*
 * ServiceNameAddressEdit.java
 *
 * Created on December 8, 2002, 11:57 AM
 */

package com.maxis.panel;

import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.igpa.forms.*;
import com.csgsystems.igpa.utils.ContextFinder;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.igpa.controls.BoundDataChangeEvent;
import com.csgsystems.igpa.controls.BoundDataChangeListener;
import com.csgsystems.localization.ResourceManager;

/**
 *
 * @author  prev01
 */
public class ServiceNameAddressEdit extends javax.swing.JPanel implements ContextFormListener {
    /**
     *  Instance of the ContextFinder utility class, which can recursively
     *  search up the component hierarchy for an ICSGContextForm, and then
     *  retrieve its context (cached for later use).
     */
    protected ContextFinder ctxFinder = new ContextFinder(this);
    private boolean initializedOnce = false;
    
    public void contextFormStateChanged(ContextFormEvent contextFormEvent) {
        if (contextFormEvent.getType() == ContextFormEvent.PRE_INIT_CONTROLS ) {
        	doGeocodeServiceFind();
            doGeocodeBServiceFind();
        } else if (contextFormEvent.getType() == ContextFormEvent.POST_INIT_CONTROLS ) {
            if (!initializedOnce) {
                IContext ctx = ctxFinder.findContext();
                if (ctx != null) {
                    IPersistentObject service = ctx.getObject("Service",null);
                                        
                    if (service == null) {
                        return;
                    }
                    IPersistentObject obj = service.getObject("ServicePricingPlan", "Service");
                    if (obj == null) {
                        return;
                    }
                    int emfType = obj.getAttributeDataAsInteger("EmfType");           
                    if (emfType == 2 || emfType == 3){
                        jTabbedPane1.addTab(ResourceManager.getString("ServiceDetail.tab.BAddress"), pnlBNameAddress);
                    }
                    
                    changeBCityState();
                    initializedOnce = true;
                }
            }

            // add a listener to the controls that make up a geocode find
            edtServiceCity.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeServiceFind();
                    }
                });    
            
            edtServiceState.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeServiceFind();
                    }
                });  
        
            edtServiceZip.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeServiceFind();
                        edtServiceCity.initializeControl();
                        edtServiceState.initializeControl();
                    }
                }); 
        
            cbServiceCountryCode.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeServiceFind();
                        changeCityState();
                    }
                });  
        
            cbServiceFranchiseTaxCode.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeServiceFind();
                    }
                });
                
            // add a listener to the controls that make up a geocode find
            edtServiceCity1.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeBServiceFind();
                    }
                });    
            
            edtServiceState1.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeBServiceFind();
                    }
                });  
        
            edtServiceZip1.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeBServiceFind();
                        edtServiceCity1.initializeControl();
                        edtServiceState1.initializeControl();
                    }
                }); 
        
            cbServiceCountryCode1.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeBServiceFind();
                        changeBCityState();
                    }
                });  
        
            cbServiceFranchiseTaxCode1.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        doGeocodeBServiceFind();
                    }
                });
                
            IContext ctx = ctxFinder.findContext();
            IPersistentObject service = ctx.getObject("Service", null);
            if (service.getAttributeDataAsBoolean("VirtualIsCountyFreeForm") == true) {
                ccbServiceCounty.setVisible(false);
                ccbServiceCounty1.setVisible(false);
                lblServiceGeocode.setVisible(false);
                lblBServiceGeocode.setVisible(false);  
                edtServiceGeocode.setVisible(false);
                edtBServiceGeocode.setVisible(false);
                edtServiceCounty.setVisible(true);
                edtBServiceCounty.setVisible(true);              
            } else {
                ccbServiceCounty.setVisible(true);
                ccbServiceCounty1.setVisible(true);
                lblServiceGeocode.setVisible(true);
                lblBServiceGeocode.setVisible(true);  
                edtServiceGeocode.setVisible(true);
                edtBServiceGeocode.setVisible(true);                
                edtServiceCounty.setVisible(false);
                edtBServiceCounty.setVisible(false);                
            }                                  
        }
    }

    private void changeCityState() {
        IContext ctx = ctxFinder.findContext();
        IPersistentObject service = ctx.getObject("Service", null);
        
        if ( service.getAttributeDataAsInteger("ServiceCountryCode") == 458 ) {
            service.getAttribute("ServiceState").setReadOnly(true);
            service.getAttribute("ServiceCity").setReadOnly(true);
            edtServiceCity.initializeControl();
            edtServiceState.initializeControl();
        } else {
            service.getAttribute("ServiceState").setReadOnly(false);
            service.getAttribute("ServiceCity").setReadOnly(false);
            edtServiceCity.initializeControl();
            edtServiceState.initializeControl();            
        }
    }
    
    private void changeBCityState() {
        IContext ctx = ctxFinder.findContext();
        IPersistentObject service = ctx.getObject("Service", null);
        
        if ( service.getAttributeDataAsInteger("BServiceCountryCode") == 458 ) {
            service.getAttribute("BServiceState").setReadOnly(true);
            service.getAttribute("BServiceCity").setReadOnly(true);
            edtServiceCity1.initializeControl();
            edtServiceState1.initializeControl();
        } else {
            service.getAttribute("BServiceState").setReadOnly(false);
            service.getAttribute("BServiceCity").setReadOnly(false);
            edtServiceCity1.initializeControl();
            edtServiceState1.initializeControl();            
        }
    }
    
    private void doGeocodeServiceFind() {
        IContext ctx = ctxFinder.findContext();
        IPersistentObject service = ctx.getObject("Service", null);   
        
        service.sendMessage("resetGeocodeServiceAddressList", null);
        ccbServiceCounty.initializeControl();
        edtServiceGeocode.initializeControl();  
    }
    
    private void doGeocodeBServiceFind() {
        IContext ctx = ctxFinder.findContext();
        IPersistentObject service = ctx.getObject("Service", null);
        
        service.sendMessage("resetGeocodeBServiceAddressList", null);
        ccbServiceCounty1.initializeControl(); 
        edtBServiceGeocode.initializeControl();
    }
    
    /** Creates new form ServiceNameAddressEdit */
    public ServiceNameAddressEdit() {
        initComponents();        
        pnlANameAddress.remove(edtServiceCounty);
        pnlANameAddress.remove(lblServiceCounty);
        pnlBNameAddress.remove(edtBServiceCounty);
        pnlBNameAddress.remove(lblServiceCounty1);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlNameAddress = new javax.swing.JPanel();
        pnlANameAddress = new javax.swing.JPanel();
        lblServiceFname = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceLname = new com.csgsystems.igpa.controls.CSGLabel();
        lblCompanyName = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceAddress = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceCity = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceState = new com.csgsystems.igpa.controls.CSGLabel();
        lblSericeZip = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceCounty = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceCountryCode = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceFranchiseTaxCode = new com.csgsystems.igpa.controls.CSGLabel();
        lblBillRevRcvCostCtr = new com.csgsystems.igpa.controls.CSGLabel();
        lblServicedPhone = new com.csgsystems.igpa.controls.CSGLabel();
        lblServicePhone2 = new com.csgsystems.igpa.controls.CSGLabel();
        edtServiceFname = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceLname = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceCompany = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceAddress1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceAddress2 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceAddress3 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceCity = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceState = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceZip = new com.csgsystems.igpa.controls.CSGEdit();
        ccbServiceCounty = new com.csgsystems.igpa.controls.CSGCollectionComboBox();
        cbServiceCountryCode = new com.csgsystems.igpa.controls.CSGComboBox();
        cbRevRcvCostCtr = new com.csgsystems.igpa.controls.CSGComboBox();
        cbServiceFranchiseTaxCode = new com.csgsystems.igpa.controls.CSGComboBox();
        edtServicePhone = new com.csgsystems.igpa.controls.CSGEdit();
        edtServicePhone2 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceCounty = new com.csgsystems.igpa.controls.CSGEdit();
        lblServiceGeocode = new com.csgsystems.igpa.controls.CSGLabel();
        edtServiceGeocode = new com.csgsystems.igpa.controls.CSGEdit();
        pnlBNameAddress = new javax.swing.JPanel();
        lblServiceFname1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceLname1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblCompanyName1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceAddress1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceCity1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceState1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblSericeZip1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceCounty1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceCountryCode1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceFranchiseTaxCode1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblBillRevRcvCostCtr1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblServicedPhone1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblServicePhone21 = new com.csgsystems.igpa.controls.CSGLabel();
        edtServiceFname1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceLname1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceCompany1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceAddress11 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceAddress21 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceAddress31 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceCity1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceState1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServiceZip1 = new com.csgsystems.igpa.controls.CSGEdit();
        ccbServiceCounty1 = new com.csgsystems.igpa.controls.CSGCollectionComboBox();
        cbServiceCountryCode1 = new com.csgsystems.igpa.controls.CSGComboBox();
        cbRevRcvCostCtr1 = new com.csgsystems.igpa.controls.CSGComboBox();
        cbServiceFranchiseTaxCode1 = new com.csgsystems.igpa.controls.CSGComboBox();
        edtServicePhone1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtServicePhone21 = new com.csgsystems.igpa.controls.CSGEdit();
        edtBServiceCounty = new com.csgsystems.igpa.controls.CSGEdit();
        lblBServiceGeocode = new com.csgsystems.igpa.controls.CSGLabel();
        edtBServiceGeocode = new com.csgsystems.igpa.controls.CSGEdit();

        setLayout(null);

        setPreferredSize(new java.awt.Dimension(360, 440));
        pnlNameAddress.setLayout(new java.awt.GridBagLayout());

        pnlNameAddress.setName("Name Address A");
        pnlANameAddress.setLayout(null);

        pnlANameAddress.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 1, 1)));
        pnlANameAddress.setPreferredSize(new java.awt.Dimension(95, 50));
        lblServiceFname.setLocalizationKey("ServiceDetail.label.ServiceFname");
        pnlANameAddress.add(lblServiceFname);
        lblServiceFname.setBounds(5, 0, 155, 20);

        lblServiceLname.setLocalizationKey("ServiceDetail.label.ServiceLame");
        pnlANameAddress.add(lblServiceLname);
        lblServiceLname.setBounds(5, 25, 155, 20);

        lblCompanyName.setLocalizationKey("ServiceDetail.label.CompanyName");
        pnlANameAddress.add(lblCompanyName);
        lblCompanyName.setBounds(5, 50, 155, 20);

        lblServiceAddress.setLocalizationKey("ServiceDetail.label.ServiceAddress");
        pnlANameAddress.add(lblServiceAddress);
        lblServiceAddress.setBounds(5, 75, 155, 20);

        lblServiceCity.setLocalizationKey("ServiceDetail.label.ServiceCity");
        pnlANameAddress.add(lblServiceCity);
        lblServiceCity.setBounds(5, 150, 155, 20);

        lblServiceState.setLocalizationKey("ServiceDetail.label.ServiceState");
        pnlANameAddress.add(lblServiceState);
        lblServiceState.setBounds(5, 175, 155, 20);

        lblSericeZip.setLocalizationKey("ServiceDetail.label.ServiceZip");
        pnlANameAddress.add(lblSericeZip);
        lblSericeZip.setBounds(5, 200, 155, 20);

        lblServiceCounty.setLocalizationKey("ServiceDetail.label.ServiceCounty");
        pnlANameAddress.add(lblServiceCounty);
        lblServiceCounty.setBounds(5, 225, 155, 20);

        lblServiceCountryCode.setLocalizationKey("ServiceDetail.label.ServiceCountryCode");
        pnlANameAddress.add(lblServiceCountryCode);
        lblServiceCountryCode.setBounds(5, 275, 155, 20);

        lblServiceFranchiseTaxCode.setLocalizationKey("ServiceDetail.label.ServiceFranchiseTaxCode");
        pnlANameAddress.add(lblServiceFranchiseTaxCode);
        lblServiceFranchiseTaxCode.setBounds(5, 300, 155, 20);

        lblBillRevRcvCostCtr.setLocalizationKey("ServiceDetail.label.RevRcvCostCtr");
        pnlANameAddress.add(lblBillRevRcvCostCtr);
        lblBillRevRcvCostCtr.setBounds(5, 325, 155, 20);

        lblServicedPhone.setLocalizationKey("ServiceDetail.label.ServiceDayPhone");
        pnlANameAddress.add(lblServicedPhone);
        lblServicedPhone.setBounds(5, 350, 155, 20);

        lblServicePhone2.setLocalizationKey("ServiceDetail.label.ServiceEveningPhone");
        pnlANameAddress.add(lblServicePhone2);
        lblServicePhone2.setBounds(5, 375, 155, 20);

        edtServiceFname.setAttributeName("ServiceFname");
        edtServiceFname.setContextObserver(true);
        edtServiceFname.setDomainName("Service");
        pnlANameAddress.add(edtServiceFname);
        edtServiceFname.setBounds(165, 0, 165, 20);

        edtServiceLname.setAttributeName("ServiceLname");
        edtServiceLname.setContextObserver(true);
        edtServiceLname.setDomainName("Service");
        pnlANameAddress.add(edtServiceLname);
        edtServiceLname.setBounds(165, 25, 165, 20);

        edtServiceCompany.setAttributeName("ServiceCompany");
        edtServiceCompany.setContextObserver(true);
        edtServiceCompany.setDomainName("Service");
        pnlANameAddress.add(edtServiceCompany);
        edtServiceCompany.setBounds(165, 50, 165, 20);

        edtServiceAddress1.setAtomName("ServiceAddress1");
        edtServiceAddress1.setContextObserver(true);
        edtServiceAddress1.setDomainName("Service");
        pnlANameAddress.add(edtServiceAddress1);
        edtServiceAddress1.setBounds(165, 75, 165, 20);

        edtServiceAddress2.setAttributeName("ServiceAddress2");
        edtServiceAddress2.setContextObserver(true);
        edtServiceAddress2.setDomainName("Service");
        pnlANameAddress.add(edtServiceAddress2);
        edtServiceAddress2.setBounds(165, 100, 165, 20);

        edtServiceAddress3.setAttributeName("ServiceAddress3");
        edtServiceAddress3.setContextObserver(true);
        edtServiceAddress3.setDomainName("Service");
        pnlANameAddress.add(edtServiceAddress3);
        edtServiceAddress3.setBounds(165, 125, 165, 20);

        edtServiceCity.setAttributeName("ServiceCity");
        edtServiceCity.setContextObserver(true);
        edtServiceCity.setDomainName("Service");
        pnlANameAddress.add(edtServiceCity);
        edtServiceCity.setBounds(165, 150, 165, 20);

        edtServiceState.setAttributeName("ServiceState");
        edtServiceState.setContextObserver(true);
        edtServiceState.setDomainName("Service");
        pnlANameAddress.add(edtServiceState);
        edtServiceState.setBounds(165, 175, 165, 20);

        edtServiceZip.setAttributeName("ServiceZip");
        edtServiceZip.setContextObserver(true);
        edtServiceZip.setDomainName("Service");
        pnlANameAddress.add(edtServiceZip);
        edtServiceZip.setBounds(165, 200, 165, 20);

        ccbServiceCounty.setAttributeName("ServiceGeocode");
        ccbServiceCounty.setContextObserver(true);
        ccbServiceCounty.setDomainName("Service");
        ccbServiceCounty.setEnumCollectionName("GeocodeList");
        ccbServiceCounty.setEnumCollectionSubtype("Service-ServiceAddress");
        ccbServiceCounty.setEnumDisplayAttributeName("VirtualCountyGeocode");
        ccbServiceCounty.setEnumKeyAttributeName("Geocode");
        ccbServiceCounty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ccbServiceCountyActionPerformed(evt);
            }
        });

        pnlANameAddress.add(ccbServiceCounty);
        ccbServiceCounty.setBounds(165, 225, 165, 20);

        cbServiceCountryCode.setAttributeName("ServiceCountryCode");
        cbServiceCountryCode.setContextObserver(true);
        cbServiceCountryCode.setDomainName("Service");
        pnlANameAddress.add(cbServiceCountryCode);
        cbServiceCountryCode.setBounds(165, 275, 165, 20);

        cbRevRcvCostCtr.setAttributeName("RevRcvCostCtr");
        cbRevRcvCostCtr.setContextObserver(true);
        cbRevRcvCostCtr.setDomainName("Service");
        pnlANameAddress.add(cbRevRcvCostCtr);
        cbRevRcvCostCtr.setBounds(165, 325, 165, 20);

        cbServiceFranchiseTaxCode.setAttributeName("ServiceFranchiseTaxCode");
        cbServiceFranchiseTaxCode.setContextObserver(true);
        cbServiceFranchiseTaxCode.setDomainName("Service");
        pnlANameAddress.add(cbServiceFranchiseTaxCode);
        cbServiceFranchiseTaxCode.setBounds(165, 300, 165, 20);

        edtServicePhone.setAttributeName("ServicePhone");
        edtServicePhone.setContextObserver(true);
        edtServicePhone.setDomainName("Service");
        pnlANameAddress.add(edtServicePhone);
        edtServicePhone.setBounds(165, 350, 165, 20);

        edtServicePhone2.setAttributeName("ServicePhone2");
        edtServicePhone2.setContextObserver(true);
        edtServicePhone2.setDomainName("Service");
        pnlANameAddress.add(edtServicePhone2);
        edtServicePhone2.setBounds(165, 375, 165, 20);

        edtServiceCounty.setAtomName("ServiceCounty");
        edtServiceCounty.setDomainName("Service");
        pnlANameAddress.add(edtServiceCounty);
        edtServiceCounty.setBounds(165, 225, 165, 20);

        lblServiceGeocode.setLocalizationKey("ServiceDetail.label.ServiceGeocode");
        pnlANameAddress.add(lblServiceGeocode);
        lblServiceGeocode.setBounds(5, 250, 155, 20);

        edtServiceGeocode.setAtomName("ServiceGeocode");
        edtServiceGeocode.setDomainName("Service");
        edtServiceGeocode.setFocusable(false);
        edtServiceGeocode.setEnabled(false);
        pnlANameAddress.add(edtServiceGeocode);
        edtServiceGeocode.setBounds(165, 250, 165, 20);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
        pnlNameAddress.add(pnlANameAddress, gridBagConstraints);

        jTabbedPane1.addTab(ResourceManager.getString( "ServiceDetail.tab.AAddress" ), pnlNameAddress);

        add(jTabbedPane1);
        jTabbedPane1.setBounds(5, 5, 350, 430);

        pnlBNameAddress.setLayout(null);

        pnlBNameAddress.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 1, 1)));
        pnlBNameAddress.setPreferredSize(new java.awt.Dimension(95, 50));
        lblServiceFname1.setLocalizationKey("ServiceDetail.label.ServiceFname");
        pnlBNameAddress.add(lblServiceFname1);
        lblServiceFname1.setBounds(5, 0, 155, 20);

        lblServiceLname1.setLocalizationKey("ServiceDetail.label.ServiceLame");
        pnlBNameAddress.add(lblServiceLname1);
        lblServiceLname1.setBounds(5, 25, 155, 20);

        lblCompanyName1.setLocalizationKey("ServiceDetail.label.CompanyName");
        pnlBNameAddress.add(lblCompanyName1);
        lblCompanyName1.setBounds(5, 50, 155, 20);

        lblServiceAddress1.setLocalizationKey("ServiceDetail.label.ServiceAddress");
        pnlBNameAddress.add(lblServiceAddress1);
        lblServiceAddress1.setBounds(5, 75, 155, 20);

        lblServiceCity1.setLocalizationKey("ServiceDetail.label.ServiceCity");
        pnlBNameAddress.add(lblServiceCity1);
        lblServiceCity1.setBounds(5, 150, 155, 20);

        lblServiceState1.setLocalizationKey("ServiceDetail.label.ServiceState");
        pnlBNameAddress.add(lblServiceState1);
        lblServiceState1.setBounds(5, 175, 155, 20);

        lblSericeZip1.setLocalizationKey("ServiceDetail.label.ServiceZip");
        pnlBNameAddress.add(lblSericeZip1);
        lblSericeZip1.setBounds(5, 200, 155, 20);

        lblServiceCounty1.setLocalizationKey("ServiceDetail.label.ServiceCounty");
        pnlBNameAddress.add(lblServiceCounty1);
        lblServiceCounty1.setBounds(5, 225, 155, 20);

        lblServiceCountryCode1.setLocalizationKey("ServiceDetail.label.ServiceCountryCode");
        pnlBNameAddress.add(lblServiceCountryCode1);
        lblServiceCountryCode1.setBounds(5, 275, 155, 20);

        lblServiceFranchiseTaxCode1.setLocalizationKey("ServiceDetail.label.ServiceFranchiseTaxCode");
        pnlBNameAddress.add(lblServiceFranchiseTaxCode1);
        lblServiceFranchiseTaxCode1.setBounds(5, 300, 155, 20);

        lblBillRevRcvCostCtr1.setLocalizationKey("ServiceDetail.label.RevRcvCostCtr");
        pnlBNameAddress.add(lblBillRevRcvCostCtr1);
        lblBillRevRcvCostCtr1.setBounds(5, 325, 155, 20);

        lblServicedPhone1.setLocalizationKey("ServiceDetail.label.ServicePhone");
        pnlBNameAddress.add(lblServicedPhone1);
        lblServicedPhone1.setBounds(5, 350, 155, 20);

        lblServicePhone21.setLocalizationKey("ServiceDetail.label.ServicePhone2");
        pnlBNameAddress.add(lblServicePhone21);
        lblServicePhone21.setBounds(5, 375, 155, 20);

        edtServiceFname1.setAtomName("BServiceFname");
        edtServiceFname1.setContextObserver(true);
        edtServiceFname1.setDomainName("Service");
        pnlBNameAddress.add(edtServiceFname1);
        edtServiceFname1.setBounds(165, 0, 165, 20);

        edtServiceLname1.setAttributeName("BServiceLname");
        edtServiceLname1.setContextObserver(true);
        edtServiceLname1.setDomainName("Service");
        pnlBNameAddress.add(edtServiceLname1);
        edtServiceLname1.setBounds(165, 25, 165, 20);

        edtServiceCompany1.setAttributeName("BServiceCompany");
        edtServiceCompany1.setContextObserver(true);
        edtServiceCompany1.setDomainName("Service");
        pnlBNameAddress.add(edtServiceCompany1);
        edtServiceCompany1.setBounds(165, 50, 165, 20);

        edtServiceAddress11.setAtomName("BServiceAddress1");
        edtServiceAddress11.setContextObserver(true);
        edtServiceAddress11.setDomainName("Service");
        pnlBNameAddress.add(edtServiceAddress11);
        edtServiceAddress11.setBounds(165, 75, 165, 20);

        edtServiceAddress21.setAttributeName("BServiceAddress2");
        edtServiceAddress21.setContextObserver(true);
        edtServiceAddress21.setDomainName("Service");
        pnlBNameAddress.add(edtServiceAddress21);
        edtServiceAddress21.setBounds(165, 100, 165, 20);

        edtServiceAddress31.setAttributeName("BServiceAddress3");
        edtServiceAddress31.setContextObserver(true);
        edtServiceAddress31.setDomainName("Service");
        pnlBNameAddress.add(edtServiceAddress31);
        edtServiceAddress31.setBounds(165, 125, 165, 20);

        edtServiceCity1.setAttributeName("BServiceCity");
        edtServiceCity1.setContextObserver(true);
        edtServiceCity1.setDomainName("Service");
        pnlBNameAddress.add(edtServiceCity1);
        edtServiceCity1.setBounds(165, 150, 165, 20);

        edtServiceState1.setAttributeName("BServiceState");
        edtServiceState1.setContextObserver(true);
        edtServiceState1.setDomainName("Service");
        pnlBNameAddress.add(edtServiceState1);
        edtServiceState1.setBounds(165, 175, 165, 20);

        edtServiceZip1.setAttributeName("BServiceZip");
        edtServiceZip1.setContextObserver(true);
        edtServiceZip1.setDomainName("Service");
        pnlBNameAddress.add(edtServiceZip1);
        edtServiceZip1.setBounds(165, 200, 165, 20);

        ccbServiceCounty1.setAttributeName("BServiceGeocode");
        ccbServiceCounty1.setContextObserver(true);
        ccbServiceCounty1.setDomainName("Service");
        ccbServiceCounty1.setEnumCollectionName("GeocodeList");
        ccbServiceCounty1.setEnumCollectionSubtype("Service-BServiceAddress");
        ccbServiceCounty1.setEnumDisplayAttributeName("VirtualCountyGeocode");
        ccbServiceCounty1.setEnumKeyAttributeName("Geocode");
        ccbServiceCounty1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ccbBServiceCountyActionPerformed(evt);
            }
        });

        pnlBNameAddress.add(ccbServiceCounty1);
        ccbServiceCounty1.setBounds(165, 225, 165, 20);

        cbServiceCountryCode1.setAttributeName("BServiceCountryCode");
        cbServiceCountryCode1.setContextObserver(true);
        cbServiceCountryCode1.setDomainName("Service");
        pnlBNameAddress.add(cbServiceCountryCode1);
        cbServiceCountryCode1.setBounds(165, 275, 165, 20);

        cbRevRcvCostCtr1.setAttributeName("BRevRcvCostCtr");
        cbRevRcvCostCtr1.setContextObserver(true);
        cbRevRcvCostCtr1.setDomainName("Service");
        pnlBNameAddress.add(cbRevRcvCostCtr1);
        cbRevRcvCostCtr1.setBounds(165, 325, 165, 20);

        cbServiceFranchiseTaxCode1.setAtomName("BServiceFranchiseTaxCode");
        cbServiceFranchiseTaxCode1.setContextObserver(true);
        cbServiceFranchiseTaxCode1.setDomainName("Service");
        pnlBNameAddress.add(cbServiceFranchiseTaxCode1);
        cbServiceFranchiseTaxCode1.setBounds(165, 300, 165, 20);

        edtServicePhone1.setAttributeName("BServicePhone");
        edtServicePhone1.setContextObserver(true);
        edtServicePhone1.setDomainName("Service");
        pnlBNameAddress.add(edtServicePhone1);
        edtServicePhone1.setBounds(165, 350, 165, 20);

        edtServicePhone21.setAttributeName("BServicePhone2");
        edtServicePhone21.setContextObserver(true);
        edtServicePhone21.setDomainName("Service");
        pnlBNameAddress.add(edtServicePhone21);
        edtServicePhone21.setBounds(165, 375, 165, 20);

        edtBServiceCounty.setAtomName("ServiceCounty");
        edtBServiceCounty.setDomainName("Service");
        pnlBNameAddress.add(edtBServiceCounty);
        edtBServiceCounty.setBounds(165, 225, 165, 20);

        lblBServiceGeocode.setLocalizationKey("ServiceDetail.label.ServiceGeocode");
        pnlBNameAddress.add(lblBServiceGeocode);
        lblBServiceGeocode.setBounds(5, 250, 155, 20);

        edtBServiceGeocode.setAtomName("BServiceGeocode");
        edtBServiceGeocode.setDomainName("Service");
        edtBServiceGeocode.setFocusable(false);
        edtBServiceGeocode.setEnabled(false);
        pnlBNameAddress.add(edtBServiceGeocode);
        edtBServiceGeocode.setBounds(165, 250, 165, 20);

        add(pnlBNameAddress);
        pnlBNameAddress.setBounds(480, 50, 375, 410);

    }//GEN-END:initComponents

    private void ccbBServiceCountyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ccbBServiceCountyActionPerformed
        edtBServiceGeocode.initializeControl();
    }//GEN-LAST:event_ccbBServiceCountyActionPerformed

    private void ccbServiceCountyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ccbServiceCountyActionPerformed
        edtServiceGeocode.initializeControl();        
    }//GEN-LAST:event_ccbServiceCountyActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.csgsystems.igpa.controls.CSGComboBox cbRevRcvCostCtr;
    private com.csgsystems.igpa.controls.CSGComboBox cbRevRcvCostCtr1;
    private com.csgsystems.igpa.controls.CSGComboBox cbServiceCountryCode;
    private com.csgsystems.igpa.controls.CSGComboBox cbServiceCountryCode1;
    private com.csgsystems.igpa.controls.CSGComboBox cbServiceFranchiseTaxCode;
    private com.csgsystems.igpa.controls.CSGComboBox cbServiceFranchiseTaxCode1;
    private com.csgsystems.igpa.controls.CSGCollectionComboBox ccbServiceCounty;
    private com.csgsystems.igpa.controls.CSGCollectionComboBox ccbServiceCounty1;
    private com.csgsystems.igpa.controls.CSGEdit edtBServiceCounty;
    private com.csgsystems.igpa.controls.CSGEdit edtBServiceGeocode;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceAddress1;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceAddress11;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceAddress2;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceAddress21;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceAddress3;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceAddress31;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceCity;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceCity1;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceCompany;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceCompany1;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceCounty;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceFname;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceFname1;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceGeocode;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceLname;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceLname1;
    private com.csgsystems.igpa.controls.CSGEdit edtServicePhone;
    private com.csgsystems.igpa.controls.CSGEdit edtServicePhone1;
    private com.csgsystems.igpa.controls.CSGEdit edtServicePhone2;
    private com.csgsystems.igpa.controls.CSGEdit edtServicePhone21;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceState;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceState1;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceZip;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceZip1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private com.csgsystems.igpa.controls.CSGLabel lblBServiceGeocode;
    private com.csgsystems.igpa.controls.CSGLabel lblBillRevRcvCostCtr;
    private com.csgsystems.igpa.controls.CSGLabel lblBillRevRcvCostCtr1;
    private com.csgsystems.igpa.controls.CSGLabel lblCompanyName;
    private com.csgsystems.igpa.controls.CSGLabel lblCompanyName1;
    private com.csgsystems.igpa.controls.CSGLabel lblSericeZip;
    private com.csgsystems.igpa.controls.CSGLabel lblSericeZip1;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceAddress;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceAddress1;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceCity;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceCity1;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceCountryCode;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceCountryCode1;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceCounty;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceCounty1;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceFname;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceFname1;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceFranchiseTaxCode;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceFranchiseTaxCode1;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceGeocode;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceLname;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceLname1;
    private com.csgsystems.igpa.controls.CSGLabel lblServicePhone2;
    private com.csgsystems.igpa.controls.CSGLabel lblServicePhone21;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceState;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceState1;
    private com.csgsystems.igpa.controls.CSGLabel lblServicedPhone;
    private com.csgsystems.igpa.controls.CSGLabel lblServicedPhone1;
    private javax.swing.JPanel pnlANameAddress;
    private javax.swing.JPanel pnlBNameAddress;
    private javax.swing.JPanel pnlNameAddress;
    // End of variables declaration//GEN-END:variables
    
}
