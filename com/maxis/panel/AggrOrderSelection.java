/*
 * OrderServices.java 
 *
 * Created on July 18, 2002, 2:57 PM
 */

package com.maxis.panel;

import com.csgsystems.igpa.utils.ContextFinder;
import com.csgsystems.igpa.controls.*;
import com.csgsystems.igpa.controls.models.*;
import com.csgsystems.bopt.*;
import com.csgsystems.bp.utilities.BpcrmUtility;
import com.csgsystems.bp.utilities.BpcrmOrderWizardUtility;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JScrollPane;
import javax.swing.tree.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.domain.framework.context.*;
import com.csgsystems.domain.arbor.utilities.*;
import com.csgsystems.domain.arbor.businessobject.*;
import com.csgsystems.domain.arbor.order.*;
import com.csgsystems.igpa.forms.*;
import com.csgsystems.localization.ResourceManager;
import com.csgsystems.error.*;
import org.apache.commons.logging.*;

import com.csgsystems.bp.utilities.CTISocketManager;
import com.csgsystems.bp.utilities.TemplateUtility;

import com.csgsystems.bp.subsystems.ChangeServiceLevelItems;

/**
 *
 * @author  prev01
 */
public class AggrOrderSelection extends javax.swing.JPanel implements ContextFormListener, HyperlinkListener {
 
    private static Log log = null;
    protected boolean CTISearch = true;

    static {
        try {
            log = LogFactory.getLog(ChangeServiceLevelItems.class);
        } catch (Exception ex) {}
    }

    /**
     *  Instance of the ContextFinder utility class, which can recursively
     *  search up the component hierarchy for an ICSGContextForm, and then
     *  retrieve its context (cached for later use).
     */
    private ContextFinder ctxFinder = new ContextFinder(this);

    protected AggrPackageCurrent pPackageCurrent = null;
    protected AggrPackageAvailable pPackageAvailable = null;
    protected AggrPackageOrdered pPackageOrdered = null;
    protected CSGBoptTree treeAlaCartProducts = null;
    
    protected boolean m_bShowCurrentPackagesTab = false;
    protected boolean m_bHasBeenInitialized = false;

    /** Creates new form OrderServices */
    public AggrOrderSelection() {
        initComponents();

        pPackageCurrent = new AggrPackageCurrent();
        pPackageAvailable = new AggrPackageAvailable();
        pPackageOrdered = new AggrPackageOrdered();
        pPackageOrdered.setOrderedCollectionType(AggrPackageOrdered.PACKAGE_DEFINITION_LIST);
        treeAlaCartProducts = new CSGBoptTree();
        treeAlaCartProducts.setFilterDate(BpcrmOrderWizardUtility.retrieveEffectiveDate(this));
        treeAlaCartProducts.setTreeDefinition(buildCSGBoptTreeXMLDefinition());
        
        treeAlaCartProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeAlaCartProductsMousePressed(evt);
            }
        });

        
        // Retrieve the Current Order and set the EffectiveDate to required
        // This is done here because there are other paths in the system where
        // the EffectiveDate cannot be required, so we'll do it at the panel level.
        IPersistentObject order = OrderManager.getInstance().getCurrentOrder();
        if (order != null) {
            IAttribute attribute = order.getAttribute("EffectiveDateWithTimestamp");
            attribute.setRequired(true);
            dateEffectiveDate.initializeControl();
        }

        // Set the appropriate Action on the Ordered Velocity templates
        setOrderedVelocityTemplatesToProvisioningPanel(Boolean.FALSE);

        // **************************************************************************
        // add event handlers to respond to click events on velocity templates
        // **************************************************************************

        //
        // Current Components Panel
        //
        pPackageCurrent.addHyperlinkListener(this);
        
        //
        // Available Components Panel
        //
        pPackageAvailable.addHyperlinkListener(this);
        
        //
        // Ordered Components Panel
        //
        pPackageOrdered.addHyperlinkListener(this);
        
        //
        // Ordered Objects TabPanel
        //
        velOrderedComponents.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, pnlOrderedComponents);
                }
            }
        });
        velOrderedComponents.addExtraVelocityContextTopic("TemplateUtility", new TemplateUtility());
        velOrderedComponentMembers.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, pnlOrderedComponents);
                }
            }
        });
        velOrderedContracts.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, velOrderedContracts);
                }
            }
        });
        velOrderedContracts.addExtraVelocityContextTopic("TemplateUtility", new TemplateUtility());
        velOrderedProducts.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, velOrderedProducts);
                }
            }
        });
        velOrderedProducts.addExtraVelocityContextTopic("TemplateUtility", new TemplateUtility());
        velOrderedNrcs.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, velOrderedNrcs);
                }
            }
        });
        velOrderedNrcs.addExtraVelocityContextTopic("TemplateUtility", new TemplateUtility());

        sendBusyToCTI();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlDefaultInformation = new javax.swing.JPanel();
        lblEffectiveDate = new com.csgsystems.igpa.controls.CSGLabel();
        dateEffectiveDate = new com.csgsystems.igpa.controls.CSGDateEdit();
        pnlOrderInformation = new javax.swing.JPanel();
        spOrderInformation = new javax.swing.JSplitPane();
        tpAvailableItems = new javax.swing.JTabbedPane();
        tpOrderedItems = new javax.swing.JTabbedPane();
        pnlOrderedComponents = new javax.swing.JPanel();
        splOrderedComponents = new javax.swing.JSplitPane();
        velOrderedComponents = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velOrderedComponentMembers = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velOrderedContracts = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velOrderedProducts = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velOrderedNrcs = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();

        setLayout(new java.awt.GridBagLayout());

        setMinimumSize(new java.awt.Dimension(675, 620));
        setPreferredSize(new java.awt.Dimension(990, 620));
        pnlDefaultInformation.setLayout(null);

        pnlDefaultInformation.setPreferredSize(new java.awt.Dimension(850, 100));
        lblEffectiveDate.setLocalizationKey("AggrOrderSelection.lblEffectiveDate");
        pnlDefaultInformation.add(lblEffectiveDate);
        lblEffectiveDate.setBounds(10, 10, 135, 20);

        dateEffectiveDate.setAttributeName("EffectiveDateWithTimestamp");
        dateEffectiveDate.setDomainName("Order");
        dateEffectiveDate.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                dateEffectiveDateVetoableChange(evt);
            }
        });

        pnlDefaultInformation.add(dateEffectiveDate);
        dateEffectiveDate.setBounds(150, 10, 170, 20);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 664;
        gridBagConstraints.ipady = 35;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(pnlDefaultInformation, gridBagConstraints);

        pnlOrderInformation.setLayout(new java.awt.GridBagLayout());

        spOrderInformation.setBorder(null);
        spOrderInformation.setDividerLocation(225);
        spOrderInformation.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        spOrderInformation.setTopComponent(tpAvailableItems);

        tpOrderedItems.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tpOrderedItems.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tpOrderedItemsStateChanged(evt);
            }
        });

        pnlOrderedComponents.setLayout(new java.awt.GridBagLayout());

        splOrderedComponents.setBorder(null);
        splOrderedComponents.setDividerLocation(475);
        splOrderedComponents.setMinimumSize(new java.awt.Dimension(20, 20));
        splOrderedComponents.setPreferredSize(new java.awt.Dimension(20, 20));
        velOrderedComponents.setBorder(null);
        velOrderedComponents.setPreferredSize(new java.awt.Dimension(690, 50));
        velOrderedComponents.setVelocityTemplateUri("template/OrderedComponents.vm");
        velOrderedComponents.setAutoscrolls(true);
        splOrderedComponents.setLeftComponent(velOrderedComponents);

        velOrderedComponentMembers.setBorder(null);
        velOrderedComponentMembers.setPreferredSize(new java.awt.Dimension(690, 50));
        velOrderedComponentMembers.setVelocityTemplateUri("template/OrderedComponentMembers.vm");
        velOrderedComponentMembers.setAutoscrolls(true);
        splOrderedComponents.setRightComponent(velOrderedComponentMembers);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlOrderedComponents.add(splOrderedComponents, gridBagConstraints);

        tpOrderedItems.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.OrderedComponents" ), pnlOrderedComponents);

        velOrderedContracts.setBorder(null);
        velOrderedContracts.setPreferredSize(new java.awt.Dimension(690, 50));
        velOrderedContracts.setVelocityTemplateUri("template/OrderedContracts.vm");
        velOrderedContracts.setAutoscrolls(true);
        tpOrderedItems.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.OrderedContracts" ), velOrderedContracts);

        velOrderedProducts.setBorder(null);
        velOrderedProducts.setPreferredSize(new java.awt.Dimension(690, 50));
        velOrderedProducts.setVelocityTemplateUri("template/OrderedProducts.vm");
        velOrderedProducts.setAutoscrolls(true);
        tpOrderedItems.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.OrderedProducts" ), velOrderedProducts);

        velOrderedNrcs.setBorder(null);
        velOrderedNrcs.setPreferredSize(new java.awt.Dimension(690, 50));
        velOrderedNrcs.setVelocityTemplateUri("template/OrderedNrcs.vm");
        velOrderedNrcs.setAutoscrolls(true);
        tpOrderedItems.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.OrderedNrcs" ), velOrderedNrcs);

        spOrderInformation.setBottomComponent(tpOrderedItems);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlOrderInformation.add(spOrderInformation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 664;
        gridBagConstraints.ipady = 264;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(pnlOrderInformation, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void tpOrderedItemsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tpOrderedItemsStateChanged
        refreshOrderedTabSelected();
    }//GEN-LAST:event_tpOrderedItemsStateChanged

    private void dateEffectiveDateVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_dateEffectiveDateVetoableChange
        Object obj = evt.getNewValue();
        if (obj instanceof Date) {
            Date newEffectiveDate = (Date)obj;

            IContext ctx = ctxFinder.findContext();

            IPersistentObject transientShoppingCart = ctx.getObject("TransientShoppingCart", null);
            if (transientShoppingCart != null) {
                boolean allItemsAreValid = BpcrmOrderWizardUtility.verifyEffectiveDateChange(newEffectiveDate, transientShoppingCart);
                if (allItemsAreValid == false) {
                    // We found ordered items that are invalid with the new EffectiveDate.
                    // Pop a dialog showing the invalid objects.  If the user wishes to remove the objects
                    // we put that information into the map on the shutdown of the dialog.  If the user wishes
                    // to remove the items, then we'll allow them to continue, otherwise, we have to veto the date change.
                    Map parameters = new HashMap();
                    parameters.put("NewEffectiveDate", newEffectiveDate);
                    parameters.put("Object", transientShoppingCart);
                    ctxFinder.fireActionForObject("invalid-orderable-object-dlg", parameters);

                    Boolean removeInvalidItems = (Boolean)parameters.get("RemoveInvalidItems");
                    if (removeInvalidItems == null || removeInvalidItems == Boolean.FALSE) {
                        java.beans.PropertyVetoException invalidDate = new java.beans.PropertyVetoException(null, evt);
                        throw invalidDate;
                    } else {
                        BpcrmOrderWizardUtility.removeInvalidItemsFromEffectiveDateChange(newEffectiveDate, transientShoppingCart);
                    }
                }
            }

            IPersistentObject account = ctx.getObject("Account");
            if (account == null) {
                System.out.println("Could not get Account from context");
            } else {
                IPersistentCollection serviceList = account.getCollection("ServiceList", "Account");
                if (serviceList != null) {
                    Iterator serviceIter = serviceList.iterator();
                    while (serviceIter.hasNext()) {
                        IPersistentObject service = (IPersistentObject) serviceIter.next();
                        if (service != null) {
                            if (service.isNewObject() == false) {
                                continue;
                            }
                            IPersistentObject servicePricingPlan = service.getObject("ServicePricingPlan", "Service");
                            if (servicePricingPlan != null) {
                                if (UtilityOrderableObjects.verifyEffectiveDateForService(newEffectiveDate, servicePricingPlan) == false) {
                                    // We found a service that is invalid with the new EffectiveDate.
                                    // Notify the user that this date cannot be chosen until the invalid items are removed.
                                    javax.swing.JOptionPane.showMessageDialog(BpcrmUtility.getApplicationFrame(), ResourceManager.getString("CC-4-3"), ResourceManager.getString("OrderWizard.title.InvalidEffectiveDateChange"), javax.swing.JOptionPane.ERROR_MESSAGE);

                                    java.beans.PropertyVetoException invalidDate = new java.beans.PropertyVetoException(null, evt);
                                    throw invalidDate;
                                }
                            }
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_dateEffectiveDateVetoableChange

    public void contextFormStateChanged(com.csgsystems.igpa.forms.ContextFormEvent contextFormEvent) {
        if (ContextFormEvent.POST_INIT_CONTROLS == contextFormEvent.getType()) {

            if (m_bHasBeenInitialized == false) {
                m_bHasBeenInitialized = true;
                // add a listener to the effective date control
                dateEffectiveDate.addBoundDataChangeListener(new BoundDataChangeListener() {
                    public void boundDataChanged(BoundDataChangeEvent bdce) {
                        dateEffectiveDateBoundDataChanged(bdce);
                    }
                });   
            }
        } else if (ContextFormEvent.PRE_OPEN == contextFormEvent.getType()) {
            IContext ctx = ctxFinder.findContext();
            IPersistentObject account = ctx.getObject("Account");
            if (account != null && account.getAttributeDataAsBoolean("AccountConsideredNew") == false) {
                m_bShowCurrentPackagesTab = true;
                tpAvailableItems.add(ResourceManager.getString("AggrOrderSelection.tabName.CurrentPackages"), pPackageCurrent);
            }

            tpAvailableItems.add(ResourceManager.getString("AggrOrderSelection.tabName.AvailablePackages"), pPackageAvailable);
            tpAvailableItems.add(ResourceManager.getString("AggrOrderSelection.tabName.OrderedPackages"), pPackageOrdered);
            tpAvailableItems.add(ResourceManager.getString("AggrOrderSelection.tabName.ALaCarte"),new JScrollPane(treeAlaCartProducts));

        } else if (ContextFormEvent.PRE_INIT_CONTROLS == contextFormEvent.getType() ) {
            // Set the Account
            IContext ctx = ctxFinder.findContext();
            if (ctx != null) {
                IPersistentObject account = ctx.getObject("Account", null);
                if (account != null) {
                    IPersistentCollection nrcList = account.getCollection("NrcList", "AccountAlaCarte");
                    if (nrcList != null) {
                        // Make sure that we don't use the IsPartOfContract filter when we are in the AccountChange wizard.
                        // That way we'll show all Nrcs in the list and user can change Nrcs as appropriate.
                        nrcList.setAttributeDataAsBoolean("ViewIsPartOfContractNrcs", false);
                    }
                }

                // Set the Services that have already been faulted
                IPersistentCollection serviceList = account.getCollection("ServiceList", "Account");
                if (serviceList != null) {
                    Iterator iterator = serviceList.iterator();
                    while (iterator.hasNext()) {
                        IPersistentObject service = (IPersistentObject)iterator.next();
                        if (service != null) {
                            IPersistentCollection nrcList = service.getCollection("NrcList", "AccountAlaCarte");
                            if (nrcList != null) {
                                // Make sure that we don't use the IsPartOfContract filter when we are in the AccountChange wizard.
                                // That way we'll show all Nrcs in the list and user can change Nrcs as appropriate.
                                nrcList.setAttributeDataAsBoolean("ViewIsPartOfContractNrcs", false);
                            }
                        }
                    }
                }
            }
        } else if (contextFormEvent.getType() == ContextFormEvent.CTI_MESSAGE_RECEIVED) {
        	sendBusyToCTI();        	
        } 

       // Forward context events to the inner AggrPackageCurrent panel
       pPackageCurrent.contextFormStateChanged(contextFormEvent);
    }

    private void sendBusyToCTI() {
        if (!CTISocketManager.getInstance(this).isCTIEnabled()) {
            log.debug("CTI Not enabled. sendBusyToCTI ignored");
            return;
        }

        CTISocketManager ctiManager = CTISocketManager.getInstance(this);
        try {
            ctiManager.setCSRState(false);
            ctiManager.sendMessage("BUSY");
        } catch (Exception ex) {
            ctiManager.setCSRState(true);
        }
    }

    protected void setOrderedVelocityTemplatesToProvisioningPanel(Boolean setProvisionPanel) {
        // Set all velocity templates showing Ordered information
        // to the appropriate action - Add vs. Remove
        velOrderedContracts.getSession().putValue("ProvisioningPanel", setProvisionPanel);
        velOrderedProducts.getSession().putValue("ProvisioningPanel", setProvisionPanel);
        velOrderedNrcs.getSession().putValue("ProvisioningPanel", setProvisionPanel);
        velOrderedComponents.getSession().putValue("ProvisioningPanel", setProvisionPanel);
        velOrderedComponentMembers.getSession().putValue("ProvisioningPanel", setProvisionPanel);
    }

    private void dateEffectiveDateBoundDataChanged(BoundDataChangeEvent bdce) {
        // The EffectiveDate has changed, so set the value onto the TransientShoppingCar.
        // Retrieve the Current Order and get the EffectiveDate from it
        IPersistentObject order = OrderManager.getInstance().getCurrentOrder();
        Date effectiveDate = new Date(); // Default to Today's Date.  Should always get overridden.
        if (order != null) {
            effectiveDate = order.getAttributeDataAsDate("EffectiveDate");
        
            // Set the effective date onto the treeAlaCartProducts control
            treeAlaCartProducts.setFilterDate(effectiveDate);
        } else {
            // We couldn't retrieve the order, this is bad
            log.error("Could not retrieve the current order.");
        }

        IContext ctx = ctxFinder.findContext();
        if (ctx != null) {
            IPersistentObject transientShoppingCart = ctx.getObject("TransientShoppingCart", null);
            if (transientShoppingCart != null) {
                // Set the effective date onto the shopping cart which will ripple thru to all new objects
                transientShoppingCart.setAttributeData("EffectiveDt", effectiveDate);
            }

            // Set the EffectiveDate on the Account if it's new
            IPersistentObject account = ctx.getObject("Account", null);
            if (account != null) {
                if (account.isNewObject()) {
                    account.setAttributeDataAsDate("DateActive", effectiveDate);
                }

                // The EffectiveDate has changed, so we need to re-filter our list of packages and package groups
                IPersistentCollection packageDefinitionList = account.getCollection("PackageDefinitionList", "Account");
                IPersistentCollection packageGroupList = account.getCollection("PackageGroupList", "Account");
                if (packageDefinitionList != null && packageGroupList != null) {
                    // Reset the collection and initialize our controls
                    packageDefinitionList.reset();
                    packageGroupList.reset();
                    ctxFinder.initControls();
                }
            } else {
                // We couldn't retrieve the account, this is bad
                log.error("Could not retrieve the account.");
            }
        }
    }

    public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            DetailHyperlinkFilter(evt, null);
        }
    }

    public void DetailHyperlinkFilter(javax.swing.event.HyperlinkEvent evt, Object refreshHtmlControl) {
        // This is a very customized version of the DetailHyperlinkFilter.
        // If/When this is brought into the fold as far as the AbstractHTMLPanel is concerned,
        // it would be best to have an overloaded method that we could fill out because of the
        // logic that is involved.  The links basically support either an add operation, a remove
        // operation or a fireEvent operation.

        // get url from hyperlink click
        String url = evt.getURL().toString();
System.out.println("URL: "+url);

        // create a hashmap containing the url params
        HashMap params = BpcrmOrderWizardUtility.parseURLtoMap(url);
        String action = (String)params.get("action");             
        String collectionName = (String)params.get("collection");             
        String subtypeName = (String)params.get("subtype");             
        String indexString = (String)params.get("index"); 
        String event = (String)params.get("event"); 
        String topicName = (String)params.get("topic"); 

        String type = "normal";
        if (params.get("type") != null){
            type = (String)params.get("type");
        }     

        int index = -1;
        if (indexString != null) {
            index = Integer.parseInt(indexString);
        }

        IContext context = ctxFinder.findContext();
        if (context != null) {
            IPersistentObject account = ctxFinder.findContext().getObject("Account");

            if ("paging".equals(type) == true) {
                CSGVelocityHTMLEP htmlControl = (CSGVelocityHTMLEP)evt.getSource();
                if (htmlControl != null) {
                    BpcrmOrderWizardUtility.doPagination(params, htmlControl);
                }
            } else if ("select-all-components".equals(event) == true) {
                IPersistentCollection coll = (IPersistentCollection)context.getObject(collectionName, subtypeName);
                if ( coll != null )
                    for ( int i = 0; i < coll.getCount(); i++ )
                        BpcrmOrderWizardUtility.setObjectSelected(context, collectionName, subtypeName, i, true);                    
            } else if ("unselect-all-components".equals(event) == true) {
                IPersistentCollection coll = (IPersistentCollection)context.getObject(collectionName, subtypeName);
                if ( coll != null )
                    for ( int i = 0; i < coll.getCount(); i++ ) {
                        IPersistentObject packCompDef = coll.getAt(i);
//                        if ( packCompDef.getAttributeDataAsInteger("MinimumRequired") == 0 )
                        BpcrmOrderWizardUtility.setObjectSelected(context, collectionName, subtypeName, i, false);
                    }
            } else if ("select-component".equals(event) == true) {
                BpcrmOrderWizardUtility.setObjectSelected(context, collectionName,subtypeName,index, true);
            } else if ("unselect-component".equals(event) == true) {
                BpcrmOrderWizardUtility.setObjectSelected(context, collectionName,subtypeName,index, false);
            } else if ("add-components".equals(event) == true) {
                addComponentToTransientShoppingCart(context, account, collectionName, subtypeName, index);
            } else if ("remove".equals(event) == true) {
                removeOrderableObjectFromTransientShoppingCart(context, account, collectionName, subtypeName, index);
                // Now remove the topic if one is specified
                if (topicName != null) {
                    IPersistentObject topic = context.getObject(topicName, null);
                    if (topic != null) {
                        context.removeTopic(topic);
                    }
                }
            } else if ("detail".equals(event) == true) {
                if ("component-member-view".equals(action) == true) {
                    // Because the ComponentMembers are displayed in a neighboring panel to the ComponentDef objects,
                    // this is not an action that is fired to pop a new panel, and thus we'll handle it internally.
                    IPersistentObject transientShoppingCart = context.getObject("TransientShoppingCart", null);
                    if (transientShoppingCart != null) {
                        IPersistentCollection collection = transientShoppingCart.getCollection(collectionName, "TransientShoppingCart");
                        if (collection != null) {
                            // Retrieve the selected object in the collection
                            IPersistentObject object = collection.getAt(index);
                            if (object != null) {
                                context.addTopic(object);
                                
                                velOrderedComponentMembers.initializeControl();
                            }
                        }
                    }
                }
            } else if ("search".equals(event) == true) {
                // We've got to pull the right collection from the TransientShoppingCart and fire the action.
                IPersistentObject transientShoppingCart = context.getObject("TransientShoppingCart", null);
                if (transientShoppingCart != null) {
                    IPersistentCollection collection = transientShoppingCart.getCollection(collectionName, subtypeName);
                    if (collection != null) {
                       // Fire the requested action for the selected object                    
                        ctxFinder.fireActionForObject(action, collection);
                    }
                }
            } else if ("link".equals(event) == true) {
                BpcrmOrderWizardUtility.fireActionForObject(action, ctxFinder, collectionName, subtypeName, index);
            }
        }

        // Now set active and refresh the appropriate tab panel within the Delivered tab pane
        if (refreshHtmlControl != null && refreshHtmlControl instanceof CSGVelocityHTMLEP) {
            if (tpOrderedItems.getSelectedComponent() == refreshHtmlControl) {
                // If the tab is already active, then just refresh it
                ((CSGVelocityHTMLEP)refreshHtmlControl).initializeControl();
            } else {
                // By setting the tab active it will also refresh it
                tpOrderedItems.setSelectedComponent((CSGVelocityHTMLEP)refreshHtmlControl);
            }
        } else if (refreshHtmlControl != null && refreshHtmlControl instanceof javax.swing.JPanel) {
            if (tpOrderedItems.getSelectedComponent() == refreshHtmlControl) {
                // If the tab is already active, then just refresh it
                velOrderedComponents.initializeControl();
                velOrderedComponentMembers.initializeControl();
            } else {
                // By setting the tab active it will also refresh it
                tpOrderedItems.setSelectedComponent((javax.swing.JPanel)refreshHtmlControl);
            }
        } else {
            CSGVelocityHTMLEP htmlControl = (CSGVelocityHTMLEP)evt.getSource();
            if (htmlControl != null) {
                htmlControl.initializeControl();
            }
        }
    }

    private void addComponentToTransientShoppingCart(IContext context, IPersistentObject account, String collectionName, String subtypeName, int index) {
        IPersistentObject transientShoppingCart = context.getObject("TransientShoppingCart", null);
        IPersistentCollection packageComponentDefList = context.getCollection(collectionName, subtypeName);
        if (transientShoppingCart != null && packageComponentDefList != null) {

            // Retrieve the EffectiveDate to be used for all items
            Date effectiveDate = BpcrmOrderWizardUtility.retrieveEffectiveDate(this);

            // Retrieve the selected Package from the tree
            boolean isAuthorized = false;
            IPersistentObject packageDefinition = null;
            java.awt.Component selectedTab = tpAvailableItems.getSelectedComponent();
            TreePath selectedPath = null;
            if (selectedTab instanceof AggrPackageCurrent) {
                selectedPath = pPackageCurrent.treePackage.getSelectionPath();
            } else if (selectedTab instanceof AggrPackageAvailable) {
                selectedPath = pPackageAvailable.treePackage.getSelectionPath();
            } else if (selectedTab instanceof AggrPackageOrdered) {
                selectedPath = pPackageOrdered.treePackage.getSelectionPath();
            }

            if (selectedPath != null) {
                CollectionTreeNode node = (CollectionTreeNode)selectedPath.getLastPathComponent();
                if (node != null) {
                    IPersistentObject selectedObject = node.getObject();
                    if (selectedObject != null) {
                        if (selectedTab instanceof AggrPackageCurrent) {
                            // Retrieve the PackageDefinition for our ProductPackage instance object and
                            // associate to ourselves to follow the previously defined paradigm.
                            packageDefinition = selectedObject.getObject("PackageDefinition", "ProductPackage");
                            if (packageDefinition != null) {
                                // Add the ProductPackage to our PackageDefinition because it is an existing package
                                // ++++ Do Not Duplicate This Paradigm In Other Objects!! ++++
                                // This is done so that when multiple Components are added, we know to
                                // which ProductPackage instance to associate the new Component instance.
                                packageDefinition.addAssociation(selectedObject);
                            }
                        } else if (selectedTab instanceof AggrPackageAvailable) {
                            // Create a clone because we need a different reference for each different PackageDefinition
                            packageDefinition = selectedObject.createClone(null);

                            // Increment the Display to show what Package Instance the components belong to
                            int incrementor = transientShoppingCart.getAttributeDataAsInteger("DisplayNewPackageDefIncrementor");
                            transientShoppingCart.setAttributeDataAsInteger("DisplayNewPackageDefIncrementor", ++incrementor);
                            packageDefinition.setAttributeDataAsString("TransientDisplayPackageDefInstId", ResourceManager.getString("OrderWizard.text.New")+" - "+new Integer(incrementor).toString());
                        } else if (selectedTab instanceof AggrPackageOrdered) {
                            // Use the same PackageDefintion because we want the same reference in this case
                            packageDefinition = selectedObject;
                        }
                    }
                }
            }

           // Verfiy that the Package is eligible to be added to the Account
            if (packageDefinition != null) {
                Map map = new HashMap();
                map.put("PackageDefinition", packageDefinition);
                map.put("EffectiveDate", effectiveDate);
                map.put("TransientShoppingCart", transientShoppingCart);

                boolean success = account.sendMessage("isPackageEligible", map);
                if (success == false) {
                    // Display the Error and leave.  IsAuthorized will never get set, so we're done.
                    javax.swing.JOptionPane.showMessageDialog(this, account.getError().getErrorMessage());
                } else {
                    // Verify that the selected object is authorized for create by security
                    isAuthorized = DomainUtility.isAuthorizedService("Package.PackageCreate");
                    if (isAuthorized == false) {
                        // Notify the user that the selected object cannot be created
                        javax.swing.JOptionPane.showMessageDialog(this, ResourceManager.getString("CC-4-4")+packageDefinition.getAttributeDataAsString("DisplayValue"));
                    }
                }
            } else {
                log.debug("PackageDefinition object not retrieved from Selected Tree");
            }

            // If we've passed all of our eligibility checking, then continue with the add of the Package and Components
            if (isAuthorized == true) {
                // Spin thru the list of components and pull selected ones
                int count = packageComponentDefList.getCount();
                for (int i=0; i<count; i++) {
                    IPersistentObject origPackageComponentDef = packageComponentDefList.getAt(i);
                    if (origPackageComponentDef != null && origPackageComponentDef.getAttributeDataAsBoolean("IsSelected") == true) {

                        // Create a clone because we need a different reference for each different PackageComponentDef
                        IPersistentObject packageComponentDef = origPackageComponentDef.createClone(null);
                        if (packageComponentDef != null) {
                            // Set the EffectiveDate on the Component
                            packageComponentDef.setAttributeDataAsDate("EffectiveDateForInstanceObject", effectiveDate);
                            
                            // Set the EffectiveDate on the Package
                            packageDefinition.setAttributeDataAsDate("EffectiveDateForInstanceObject", effectiveDate);
                           
                            // Associate the Package to the Component
                            boolean success = packageComponentDef.addAssociation(packageDefinition);
                            if (success == true) {
                                // Add the Component into the TransientShoppingCart
                                success = transientShoppingCart.addAssociation(packageComponentDef);
                                if (success = false) {
                                    IError error = packageComponentDef.getError();
                                    String message = error.getErrorMessage();
                                    javax.swing.JOptionPane.showMessageDialog(this, message);
                                }
                            } else {
                                IError error = packageDefinition.getError();
                                String message = error.getErrorMessage();
                                javax.swing.JOptionPane.showMessageDialog(this, message);
                            }
                        }
                    }
                }

                tpOrderedItems.setSelectedComponent(pnlOrderedComponents);
                velOrderedComponents.initializeControl();
            }
        }
    }

    private void removeOrderableObjectFromTransientShoppingCart(IContext context, IPersistentObject account, String collectionName, String subtypeName, int index) {
        IPersistentObject transientShoppingCart = context.getObject("TransientShoppingCart", null);
        if (transientShoppingCart != null) {
            IPersistentCollection collection = transientShoppingCart.getCollection(collectionName, "TransientShoppingCart");
            if (collection != null) {
                // Verify that the object exists in the collection
                IPersistentObject object = collection.getAt(index);
                if (object != null && object.isNewObject() == true) {
                    
                    // Remove the object from our collection
                    collection.remove(index);

                    // Eligibility stuff is only appropriate for Components
                    if ("PackageComponentDefList".equals(collectionName)) {

                        // If that was a required component and we don't already have at 
                        // least one of that ComponentId, then remove all Components of the package.                            
                        int minimumRequired = object.getAttributeDataAsInteger("MinimumRequired");
                        if (minimumRequired > 0) {
                            int instanceCount = 0;
                            int count = collection.getCount();
                            for (int i=0; i<count; i++) {
                                IPersistentObject packageComponentDef = collection.getAt(i);
                                if (packageComponentDef != null) {
                                    if (packageComponentDef.getAttributeDataAsString("DisplayPackageDefInstId") == object.getAttributeDataAsString("DisplayPackageDefInstId") &&
                                        packageComponentDef.getAttributeDataAsInteger("MinimumRequired") == object.getAttributeDataAsInteger("MinimumRequired")) {
                                        instanceCount++;
                                    }
                                }
                            }
                            
                            if (minimumRequired > instanceCount) {
                                // Looks like there aren't enough and since they removed one, we'll remove all Components of this Package.
                                for (int i=count-1; i>=0; i--) {
                                    IPersistentObject packageComponentDef = collection.getAt(i);
                                    if (packageComponentDef != null) {
                                        if (packageComponentDef.getAttributeDataAsString("DisplayPackageDefInstId") == object.getAttributeDataAsString("DisplayPackageDefInstId")) {
                                            collection.remove(i);
                                        }
                                    }
                                }
                                
                            }
                        }

                        // Retrieve the EffectiveDate to be used for all items
                        Date effectiveDate = BpcrmOrderWizardUtility.retrieveEffectiveDate(this);

                        Map map = new HashMap();
                        map.put("EffectiveDate", effectiveDate);
                        IPersistentObject transientshoppingCart = context.getObject("TransientShoppingCart", null);
                        if (transientshoppingCart != null) {
                            map.put("TransientShoppingCart", transientshoppingCart);
                        }
                        account.sendMessage("resetPackageEligibility", map);
                    }
                } else if (object != null) {
                    // This indicates that we have a persisted object, so we must be within the ServiceOrderChange wizard
                    IPersistentObject orderItem = OrderManager.getInstance().getOrderItem(object);
                    if (orderItem != null) {
                        BpcrmOrderWizardUtility.markItemForCancellation(this, orderItem);
                    }
                }
            }
        }
    }

    private void treeAlaCartProductsMousePressed(java.awt.event.MouseEvent evt) {
        if (SwingUtilities.isLeftMouseButton(evt)) {
            if (evt.getClickCount() == 2) {
                // A DoubleClick acts as an Add operation.

                // Retrieve the EffectiveDate to be used for all items
                Date effectiveDate = BpcrmOrderWizardUtility.retrieveEffectiveDate(this);
                if (effectiveDate != null) {
                    IContext ctx = ctxFinder.findContext();
                    IPersistentObject objectAdded = BpcrmOrderWizardUtility.addAlaCarteItemToObject(this, effectiveDate, ctx, treeAlaCartProducts, "TransientShoppingCart");

                    // Set the appropriate panel active and refresh it.
                    setPanelToActive(objectAdded);
                }
            }
        } else if (SwingUtilities.isRightMouseButton(evt)) {
            TreePath selPath = treeAlaCartProducts.getSelectionPath();
            if (selPath != null) { 
                boolean isMenuDefined = false;
                JPopupMenu menu = new JPopupMenu();

                BoptTreeNode node = (BoptTreeNode)selPath.getLastPathComponent();
                if (node != null && node.isLeaf() == true) {

                    // Setup Add menu item for the AlaCarte orderable item
                    JMenuItem addMenuItem = new JMenuItem(ResourceManager.getString("AggrOrderSelection.menuitem.Add"));
                    addMenuItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            // Retrieve the EffectiveDate to be used for all items
                            Date effectiveDate = BpcrmOrderWizardUtility.retrieveEffectiveDate(null);
                            if (effectiveDate != null) {
                                IContext ctx = ctxFinder.findContext();
                                IPersistentObject objectAdded = BpcrmOrderWizardUtility.addAlaCarteItemToObject(null, effectiveDate, ctx, treeAlaCartProducts, "TransientShoppingCart");

                                // Set the appropriate panel active and refresh it.
                                setPanelToActive(objectAdded);
                            }
                        }
                    });
                    menu.add(addMenuItem);
                    isMenuDefined = true;
                }

                if (isMenuDefined == true) {
                    menu.show(treeAlaCartProducts, evt.getX(), evt.getY());
                }
            }
        }
    }

    private void refreshOrderedTabSelected() {
        if (m_bHasBeenInitialized == true) {
            int tabSelected = tpOrderedItems.getSelectedIndex();
            switch (tabSelected) {
                case 0:
                    velOrderedComponents.initializeControl();
                    velOrderedComponentMembers.initializeControl();
                    break;
                case 1:
                    velOrderedContracts.initializeControl();
                    break;
                case 2:
                    velOrderedProducts.initializeControl();
                    break;
                case 3:
                    velOrderedNrcs.initializeControl();
                    break;
            }
        }
    }

    private void setPanelToActive(IPersistentObject object) {
        // Set the appropriate panel active.
        if (object instanceof NrcTransDescr || object instanceof NrcTransDescrList) {
            if (tpOrderedItems.getSelectedComponent() == velOrderedNrcs) {
                // If the tab is already active, then just refresh it
                velOrderedNrcs.initializeControl();
            } else {
                // By setting the tab active it will also refresh it
                tpOrderedItems.setSelectedComponent(velOrderedNrcs);
            }
        } else if (object instanceof ProductElement || object instanceof ProductElementList) {
            if (tpOrderedItems.getSelectedComponent() == velOrderedProducts) {
                // If the tab is already active, then just refresh it
                velOrderedProducts.initializeControl();
            } else {
                // By setting the tab active it will also refresh it
                tpOrderedItems.setSelectedComponent(velOrderedProducts);
            }
        } else if (object instanceof ContractType || object instanceof ContractTypeList) {
            if (tpOrderedItems.getSelectedComponent() == velOrderedContracts) {
                // If the tab is already active, then just refresh it
                velOrderedContracts.initializeControl();
            } else {
                // By setting the tab active it will also refresh it
                tpOrderedItems.setSelectedComponent(velOrderedContracts);
            }
        } else if (object instanceof PackageComponentDefList) {
            if (tpOrderedItems.getSelectedComponent() == pnlOrderedComponents) {
                // If the tab is already active, then just refresh it
                velOrderedComponents.initializeControl();
                velOrderedComponentMembers.initializeControl();
            } else {
                // By setting the tab active it will also refresh it
                tpOrderedItems.setSelectedComponent((javax.swing.JPanel)pnlOrderedComponents);
            }
        }
    }

    /**
     * Builds the xml definition for the CSGBoptTree.  Created this way because certain filters are based
     * on outside parameters and therefore not static.  In this case, the charges are being filter by
     * the TransSign if the BIP.ALLOW_NEGATIVE_NEW_CHARGES flag is zero or non-existent.  Otherwise,
     * negative charges can be shown.
     */
    private String buildCSGBoptTreeXMLDefinition() {
        String xmlDef = "<?xml version=\"1.0\"?><csg-tree>";
        
        xmlDef += BpcrmUtility.beginNode("AggrOrderSelection.treeLabel.AvailableProducts", true, true, null, true);
        
        xmlDef += buildChargesNode();
        xmlDef += buildContractsNode();
        xmlDef += buildProductsNode();
        
        xmlDef += BpcrmUtility.endNode(true, true);
        
        xmlDef += "</csg-tree>";
        
        log.debug(xmlDef);
        //System.out.println(xmlDef);
        return xmlDef;
    }
    
    private String buildChargesNode() {
        String chargesNode = "";
        
        chargesNode += BpcrmUtility.beginNode("AggrOrderSelection.treeLabel.Charges", true, true, null, true);
                
        chargesNode += buildChargesAccountServiceNodes("AggrOrderSelection.treeLabel.AccountLevel", 0);
        chargesNode += buildChargesAccountServiceNodes("AggrOrderSelection.treeLabel.ServiceLevel", 1);
        
        chargesNode += BpcrmUtility.endNode(true, true);
        
        return chargesNode;
    }
    
    private String buildChargesAccountServiceNodes(String nodeLabel, int triggerLevel) {
        String chargesSubNode = "";
        
        chargesSubNode += BpcrmUtility.beginNode(nodeLabel, true, true, null, true);
        
        chargesSubNode += buildInstallmentNode(triggerLevel, 1, false);
        chargesSubNode += buildInstallmentNode(triggerLevel, 2, true);
        
        chargesSubNode += BpcrmUtility.endNode(true, true);
        
        return chargesSubNode;
    }
    
    private String buildInstallmentNode(int triggerLevel, int nrcCategory, boolean isInstallment) {
        String installmentNode = "";
        String dataSet = "NrcTransDescr.IsEnterable=1.TriggerLevel=" + triggerLevel + ".NrcCategory=" + nrcCategory;
        String installmentNodeLabel = null;
        
        MultiKeyParams mkp = BoptFactory.getSystemParameters();
        String allowNegativeNewCharges = (String)mkp.get("BIP.ALLOW_NEGATIVE_NEW_CHARGES");
        
        if (allowNegativeNewCharges == null || !allowNegativeNewCharges.equals("1")) {
            dataSet += ".TransSign=1";
        }
        
        if (isInstallment) {
            installmentNodeLabel = "AggrOrderSelection.treeLabel.Installment";
            dataSet += ".InstallmentTypeIdNrc!=#NULL#";
        } else {
            installmentNodeLabel = "AggrOrderSelection.treeLabel.NonInstallment";
        }
        
        installmentNode += BpcrmUtility.beginNode(installmentNodeLabel, true, true, null, false);
        installmentNode += BpcrmUtility.beginDataSet(dataSet, "TypeIdNrc", null, null);
        installmentNode += BpcrmUtility.beginNode("DescriptionText", false, false, null, false);
        installmentNode += BpcrmUtility.endNode(false, false);
        installmentNode += BpcrmUtility.endDataSet();
        installmentNode += BpcrmUtility.endNode(true, false);
        
        return installmentNode;
    }
    
    private String buildContractsNode() {
        String contractsNode = "";
        
        contractsNode += BpcrmUtility.beginNode("AggrOrderSelection.treeLabel.Contracts", true, true, null, true);
        
        contractsNode += buildContractsAccountServiceNodes("AggrOrderSelection.treeLabel.AccountLevel", 0);
        contractsNode += buildContractsAccountServiceNodes("AggrOrderSelection.treeLabel.ServiceLevel", 1);
        
        contractsNode += BpcrmUtility.endNode(true, true);
        
        return contractsNode;
    }
    
    private String buildContractsAccountServiceNodes(String nodeLabel, int level) { // 0 for Account, 1 for Service
        String contractsSubNode = "";
        String dataSet = null;
        
        if (level == 0) {
            dataSet = "ContractType.AllowAccount=1.ContractCategory!=9";
        } else {
            dataSet = "ContractType.AllowServInst=1";
        }
        
        contractsSubNode += BpcrmUtility.beginNode(nodeLabel, true, true, null, false);
        contractsSubNode += BpcrmUtility.beginDataSet(dataSet, "Type", "ActiveDate", "InactiveDate");
        contractsSubNode += BpcrmUtility.beginNode("DescriptionText", false, false, null, false);
        contractsSubNode += BpcrmUtility.endNode(false, false);
        contractsSubNode += BpcrmUtility.endDataSet();
        contractsSubNode += BpcrmUtility.endNode(true, false);
        
        return contractsSubNode;
    }
    
    private String buildProductsNode() {
        String productsNode = "";
        
        productsNode += BpcrmUtility.beginNode("AggrOrderSelection.treelabel.Products", true, true, null, false);
        productsNode += BpcrmUtility.beginDataSet("ProductGroup", "ProductGroupId", null, null);
        productsNode += BpcrmUtility.beginNode("DescriptionText", false, true, "ProductGroupId", false);
        productsNode += BpcrmUtility.beginDataSet("ProductLine", "ProductLineId", null, null);
        productsNode += BpcrmUtility.beginNode("DescriptionText", false, true, "ProductLineId", false);
        productsNode += BpcrmUtility.beginDataSet("ProductElement.ContractUseCode!=1", "ElementId", "DateActive", "DateInactive");
        productsNode += BpcrmUtility.beginNode("DescriptionText", false, false, "ProductLineId", false);
        productsNode += BpcrmUtility.endNode(false, false);
        productsNode += BpcrmUtility.endDataSet();
        productsNode += BpcrmUtility.endNode(true, false);
        productsNode += BpcrmUtility.endDataSet();
        productsNode += BpcrmUtility.endNode(true, false);
        productsNode += BpcrmUtility.endDataSet();
        productsNode += BpcrmUtility.endNode(true, false);
               
        return productsNode;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.csgsystems.igpa.controls.CSGDateEdit dateEffectiveDate;
    private com.csgsystems.igpa.controls.CSGLabel lblEffectiveDate;
    private javax.swing.JPanel pnlDefaultInformation;
    private javax.swing.JPanel pnlOrderInformation;
    private javax.swing.JPanel pnlOrderedComponents;
    private javax.swing.JSplitPane spOrderInformation;
    private javax.swing.JSplitPane splOrderedComponents;
    private javax.swing.JTabbedPane tpAvailableItems;
    private javax.swing.JTabbedPane tpOrderedItems;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velOrderedComponentMembers;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velOrderedComponents;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velOrderedContracts;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velOrderedNrcs;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velOrderedProducts;
    // End of variables declaration//GEN-END:variables
}
