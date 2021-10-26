package com.maxis.panel;
/*
 * AggrServiceProductDelivery.java
 *
 * Created on July 22, 2002, 9:36 AM
 */
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.event.*;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// csg imports
import com.csgsystems.igpa.utils.ContextFinder;
import com.csgsystems.igpa.controls.models.*;
import com.csgsystems.domain.framework.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.context.*;
import com.csgsystems.domain.framework.lookup.LookupDomain;
import com.csgsystems.error.*;
import com.csgsystems.domain.arbor.businessobject.*;
import com.csgsystems.domain.arbor.order.*;
import com.csgsystems.bopt.*;
import com.csgsystems.igpa.forms.*;
import com.csgsystems.localization.ResourceManager;
import com.csgsystems.domain.arbor.utilities.DomainUtility;
import com.csgsystems.igpa.controls.CSGVelocityHTMLEP;
import com.csgsystems.bp.utilities.BpcrmOrderWizardUtility;

/**
 *
 * @author  prev01
 */
public class MXSAggrServiceProductDelivery extends javax.swing.JPanel implements ContextFormListener {
    /**
     *  Instance of the ContextFinder utility class, which can recursively
     *  search up the component hierarchy for an ICSGContextForm, and then
     *  retrieve its context (cached for later use).
     */
    private ContextFinder ctxFinder = new ContextFinder(this);
    private boolean m_modelNotSet = true;
    private IPersistentObject m_selectedNode = null;
    private boolean m_isInventoryUsed = false;
    private boolean m_serviceRateClassEligibilityRequired = false;
    protected boolean m_bHasBeenInitialized = false;

    protected static Log log = null;
    static {
        try {
            log = LogFactory.getLog(AggrServiceProductDelivery.class);
        } catch (Exception ex) {}
    }


    /** Creates new form ProductDelivery */
    public MXSAggrServiceProductDelivery() {
        initComponents();

        treeAccountServices.setModelInfo(buildAccountServicesTreeXMLDefinition());
        treeAccountServices.setCellRenderer(new ObjectStateTreeCellRenderer());
        
        MultiKeyParams mkp = BoptFactory.getSystemParameters();
        if (mkp == null) {
            System.out.println("Could not get System Parameters");
        } else {
            String serviceRateClassEligibility = (String)mkp.get("csr.SERVICE_RATE_CLASS_ELIGIBILITY");
            if (serviceRateClassEligibility != null && "0".equals(serviceRateClassEligibility) == false) {
                m_serviceRateClassEligibilityRequired = true;
            }
        }

        m_isInventoryUsed = DomainUtility.isInventoryUsed();
        if (m_isInventoryUsed == false) {
            // Disable Inventory
            //tpDelivered.removeTabAt(6);
            tpDelivered.remove(inventoryScroll);
            inventoryScroll = null;              
        }

        // Set the appropriate Action on the Ordered Velocity templates
        setOrderedVelocityTemplatesToProvisioningPanel(Boolean.TRUE);
        setDeliveredVelocityTemplatesToProvisioningPanel(Boolean.TRUE);
        setDeliveredExtraVelocityContextTopic();

        // **************************************************************************
        // add event handlers to respond to click events on velocity templates
        // **************************************************************************

        //
        // Available Services Panel
        //
        velAvailableServices.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, null);
                }
            }
        });
        
        //
        // Ordered Objects TabPanel
        //
        velOrderedComponents.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, velDeliveredComponents);
                }
            }
        });
        velOrderedContracts.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, velDeliveredContracts);
                }
            }
        });
        velOrderedProducts.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, velDeliveredProducts);
                }
            }
        });
        velOrderedNrcs.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, velDeliveredNrcs);
                }
            }
        });

        //
        // Delivered Objects TabPanel
        //
        velDeliveredProducts.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, velDeliveredProducts);
                }
            }
        });
        velDeliveredContracts.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, velDeliveredContracts);
                }
            }
        });
        velDeliveredNrcs.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, velDeliveredNrcs);
                }
            }
        });
        velDeliveredComponents.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, velDeliveredComponents);
                }
            }
        });
        velDeliveredPackages.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, velDeliveredPackages);
                }
            }
        });
    }

    public void contextFormStateChanged(com.csgsystems.igpa.forms.ContextFormEvent contextFormEvent) {
        if (ContextFormEvent.PRE_INIT_CONTROLS == contextFormEvent.getType()) {
            if (m_modelNotSet) {
                grdInventory.setModelInfo();
                m_modelNotSet = false;
                grdInventory.setListeners();
                grdInventory.setHeaderActionListener(0, new ActionListener() { 
                    public void actionPerformed(ActionEvent evt) {
                    	if (m_selectedNode instanceof Service) {
                    		launchInventorySearch();
                    	} else if(m_selectedNode instanceof Account) {                    		
                    		launchInventoryReserve();
                    	}
                    }
                });
                grdInventory.addMouseListener(new MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        inventoryOptions(evt);
                    }
                });
                TableColumn headerCol = grdInventory.getColumnModel().getColumn(0);
                headerCol.setHeaderRenderer(new TableHeaderCellRenderer() );
            }

            // If the SystemParameter is set, setup the filter parameters on the collection.
            MultiKeyParams mkp = BoptFactory.getSystemParameters();
            String singleEntityForPackage = (String)mkp.get("csr.SINGLE_ENTITY_FOR_PACKAGE");
            if (singleEntityForPackage != null && "1".equals(singleEntityForPackage) == true) {
                IContext context = ctxFinder.findContext();
                if (context != null) {
                    IPersistentObject account = context.getObject("Account", null);
                    UtilityOrderableObjects.setPackageFilterCriteria(account, null, null);

                    // Setup additional Package searching collection
                    IPersistentObject productPackageList = PersistentObjectFactory.getFactory().createNew("ProductPackageList", "SingleEntityPackageSearch");
                    if (productPackageList != null) {
                        // Add our new object as a topic on our context
                        context.addTopic(productPackageList);
                    }
                }
            }
        } else if (ContextFormEvent.POST_INIT_CONTROLS == contextFormEvent.getType() && !m_bHasBeenInitialized) {
            m_bHasBeenInitialized = true;

            treeAccountServices.setModelInfo(buildAccountServicesTreeXMLDefinition());
            treeAccountServices.initializeControl();
            treeAccountServices.setSelectionRow(0);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        splitProvisioning = new javax.swing.JSplitPane();
        pnlAvailable = new javax.swing.JPanel();
        splitAvailable = new javax.swing.JSplitPane();
        velAvailableServices = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        tpOrdered = new javax.swing.JTabbedPane();
        velOrderedComponents = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velOrderedContracts = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velOrderedProducts = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velOrderedNrcs = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        pnlDelivered = new javax.swing.JPanel();
        splitDelivered = new javax.swing.JSplitPane();
        spAccountServices = new javax.swing.JScrollPane();
        treeAccountServices = new com.csgsystems.igpa.controls.CSGCollectionTree();
        tpDelivered = new javax.swing.JTabbedPane();
        velDeliveredPackages = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velDeliveredComponents = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velDeliveredContracts = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velDeliveredProducts = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velDeliveredNrcs = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        inventoryScroll = new javax.swing.JScrollPane();
        grdInventory = new com.csgsystems.igpa.controls.CSGCollectionGrid();

        setLayout(new java.awt.GridBagLayout());

        setMinimumSize(new java.awt.Dimension(670, 580));
        setPreferredSize(new java.awt.Dimension(990, 620));
        splitProvisioning.setDividerLocation(480);
        splitProvisioning.setDividerSize(7);
        pnlAvailable.setLayout(new java.awt.GridBagLayout());

        splitAvailable.setDividerLocation(175);
        splitAvailable.setDividerSize(5);
        splitAvailable.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        velAvailableServices.setBorder(null);
        velAvailableServices.setPreferredSize(new java.awt.Dimension(50, 50));
        velAvailableServices.setVelocityTemplateUri("template/AvailableServices.vm");
        velAvailableServices.setAutoscrolls(true);
        splitAvailable.setLeftComponent(velAvailableServices);

        tpOrdered.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tpOrdered.setPreferredSize(new java.awt.Dimension(50, 50));
        tpOrdered.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tpOrderedStateChanged(evt);
            }
        });

        velOrderedComponents.setBorder(null);
        velOrderedComponents.setPreferredSize(new java.awt.Dimension(690, 50));
        velOrderedComponents.setVelocityTemplateUri("template/OrderedComponents.vm");
        velOrderedComponents.setAutoscrolls(true);
        tpOrdered.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.OrderedComponents" ), velOrderedComponents);

        velOrderedContracts.setBorder(null);
        velOrderedContracts.setPreferredSize(new java.awt.Dimension(690, 50));
        velOrderedContracts.setVelocityTemplateUri("template/OrderedContracts.vm");
        velOrderedContracts.setAutoscrolls(true);
        tpOrdered.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.OrderedContracts" ), velOrderedContracts);

        velOrderedProducts.setBorder(null);
        velOrderedProducts.setPreferredSize(new java.awt.Dimension(690, 50));
        velOrderedProducts.setVelocityTemplateUri("template/OrderedProducts.vm");
        velOrderedProducts.setAutoscrolls(true);
        tpOrdered.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.OrderedProducts" ), velOrderedProducts);

        velOrderedNrcs.setBorder(null);
        velOrderedNrcs.setPreferredSize(new java.awt.Dimension(690, 50));
        velOrderedNrcs.setVelocityTemplateUri("template/OrderedNrcs.vm");
        velOrderedNrcs.setAutoscrolls(true);
        tpOrdered.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.OrderedNrcs" ), velOrderedNrcs);

        splitAvailable.setRightComponent(tpOrdered);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlAvailable.add(splitAvailable, gridBagConstraints);

        splitProvisioning.setLeftComponent(pnlAvailable);

        pnlDelivered.setLayout(new java.awt.GridBagLayout());

        pnlDelivered.setPreferredSize(new java.awt.Dimension(100, 100));
        splitDelivered.setDividerLocation(175);
        splitDelivered.setDividerSize(5);
        splitDelivered.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        treeAccountServices.setDomainName("Account");
        treeAccountServices.setMaximumSize(null);
        treeAccountServices.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeAccountServicesMousePressed(evt);
            }
        });
        treeAccountServices.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeAccountServicesValueChanged(evt);
            }
        });

        spAccountServices.setViewportView(treeAccountServices);

        splitDelivered.setLeftComponent(spAccountServices);

        tpDelivered.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tpDelivered.setMaximumSize(null);
        tpDelivered.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tpDeliveredStateChanged(evt);
            }
        });

        velDeliveredPackages.setBorder(null);
        velDeliveredPackages.setPreferredSize(new java.awt.Dimension(690, 50));
        velDeliveredPackages.setVelocityTemplateUri("template/DeliveredPackages.vm");
        velDeliveredPackages.setAutoscrolls(true);
        tpDelivered.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.DeliveredPackages" ), velDeliveredPackages);

        velDeliveredComponents.setBorder(null);
        velDeliveredComponents.setPreferredSize(new java.awt.Dimension(690, 50));
        velDeliveredComponents.setVelocityTemplateUri("template/DeliveredComponents.vm");
        velDeliveredComponents.setAutoscrolls(true);
        tpDelivered.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.DeliveredComponents" ), velDeliveredComponents);

        velDeliveredContracts.setBorder(null);
        velDeliveredContracts.setPreferredSize(new java.awt.Dimension(690, 50));
        velDeliveredContracts.setVelocityTemplateUri("template/DeliveredContracts.vm");
        velDeliveredContracts.setAutoscrolls(true);
        tpDelivered.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.DeliveredContracts" ), velDeliveredContracts);

        velDeliveredProducts.setBorder(null);
        velDeliveredProducts.setPreferredSize(new java.awt.Dimension(690, 50));
        velDeliveredProducts.setVelocityTemplateUri("template/DeliveredProducts.vm");
        velDeliveredProducts.setAutoscrolls(true);
        tpDelivered.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.DeliveredProducts" ), velDeliveredProducts);

        velDeliveredNrcs.setBorder(null);
        velDeliveredNrcs.setPreferredSize(new java.awt.Dimension(690, 50));
        velDeliveredNrcs.setVelocityTemplateUri("template/DeliveredNrcs.vm");
        velDeliveredNrcs.setAutoscrolls(true);
        tpDelivered.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.DeliveredNrcs" ), velDeliveredNrcs);

        inventoryScroll.setBorder(null);
        grdInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        grdInventory.setCareAboutCollectionReset(false);
        grdInventory.setCollectionName("InvElementList");
        grdInventory.setCollectionSubtype("Service");
        grdInventory.setColumnAttributeNames(new String[] {"DisplayNumber", "InventoryTypeId", "InventoryLineId"});
        grdInventory.setColumnTitleLocalizationKeys(new String[] {"AggrServiceProductDelivery.grdInventory.Identifier", "AggrServiceProductDelivery.grdInventory.Type", "AggrServiceProductDelivery.grdInventory.Line"});
        inventoryScroll.setViewportView(grdInventory);

        tpDelivered.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.Inventory" ), null, inventoryScroll, "null");

        splitDelivered.setRightComponent(tpDelivered);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 150;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlDelivered.add(splitDelivered, gridBagConstraints);

        splitProvisioning.setRightComponent(pnlDelivered);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(splitProvisioning, gridBagConstraints);

    }//GEN-END:initComponents

    private void tpDeliveredStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tpDeliveredStateChanged
        refreshDeliveredTabSelected();
    }//GEN-LAST:event_tpDeliveredStateChanged

    private void tpOrderedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tpOrderedStateChanged
        refreshOrderedTabSelected();
    }//GEN-LAST:event_tpOrderedStateChanged

    private void treeAccountServicesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeAccountServicesMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            TreePath selPath = treeAccountServices.getSelectionPath();
            if (selPath != null) { 
                CollectionTreeNode node = (CollectionTreeNode)selPath.getLastPathComponent();
                if (node != null) {
                    final IPersistentObject target = node.getObject();
                    if (target != null) { 

                        boolean isMenuDefined = false;
                        JPopupMenu menu = new JPopupMenu();
                        
                        if (target instanceof Account) {
                            // Setup WorkingSet menu item for Account node
                            menu.add(new AbstractAction(ResourceManager.getString("AggrServiceProductDelivery.menuitem.ChangeWorkingSet")) {
                                public void actionPerformed(ActionEvent e) {
                                    ctxFinder.fireActionForObject("service-workingset-dlg", target);
                                    treeAccountServices.setModelInfo(buildAccountServicesTreeXMLDefinition());
                                    treeAccountServices.initializeControl();
                                    treeAccountServices.setSelectionRow(0);
                                }
                            });
                            // If the SystemParameter is set, setup the menu item to add more packages.
                            MultiKeyParams mkp = BoptFactory.getSystemParameters();
                            String singleEntityForPackage = (String)mkp.get("csr.SINGLE_ENTITY_FOR_PACKAGE");
                            if (singleEntityForPackage != null && "1".equals(singleEntityForPackage) == true) {
                                menu.add(new AbstractAction(ResourceManager.getString("AggrServiceProductDelivery.menuitem.PackageSearch")) {
                                    public void actionPerformed(ActionEvent e) {
                                        performPackageSearch();
                                    }
                                });
                            }
                            // Setup Inventory menu item if necessary
                            if (m_isInventoryUsed) {
                                JMenuItem inventoryMenuItem = new JMenuItem(ResourceManager.getString("AggrServiceProductDelivery.menuitem.Inventory"));
                                inventoryMenuItem.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                    	launchInventoryReserve();
                                    }
                                });
                                menu.add(inventoryMenuItem);
                            }                            
                            isMenuDefined = true;
                        } else {
                            if (target instanceof Service && target.isNewObject() == true) {
                                // Setup Remove menu item for Service
                                JMenuItem removeMenuItem = new JMenuItem(ResourceManager.getString("AggrServiceProductDelivery.menuitem.Remove"));
                                removeMenuItem.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        removeService();
                                    }
                                });
                                menu.add(removeMenuItem);
                            }

                            // Setup Inventory menu item if necessary
                            if (m_isInventoryUsed) {
                                JMenuItem inventoryMenuItem = new JMenuItem(ResourceManager.getString("AggrServiceProductDelivery.menuitem.Inventory"));
                                inventoryMenuItem.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        launchInventorySearch();
                                    }
                                });
                                menu.add(inventoryMenuItem);
                            }

                            // Allow the user to add external ids.
                            JMenuItem serviceExternalIdMenuItem = new JMenuItem(ResourceManager.getString("AggrServiceProductDelivery.menuitem.AddExternalId"));
                            serviceExternalIdMenuItem.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    launchAddExternalId();
                                }
                            });
                            menu.add(serviceExternalIdMenuItem);

                            // Setup Service RateClass menu item if necessary
                            if (isServiceRateClassMenuDisplayed(target) == true) {
                                JMenuItem serviceRateClassMenuItem = new JMenuItem(ResourceManager.getString("AggrServiceProductDelivery.menuitem.ServiceRateClass"));
                                serviceRateClassMenuItem.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        launchServiceRateClassDetail();
                                    }
                                });
                                menu.add(serviceRateClassMenuItem);
                            }
                            
                            isMenuDefined = true;
                        }

                        if (isMenuDefined == true) {
                            menu.show(treeAccountServices, evt.getX(), evt.getY());
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_treeAccountServicesMousePressed

    private void refreshDeliveredTabSelected() {
        if (m_bHasBeenInitialized == true) {
            int tabSelected = tpDelivered.getSelectedIndex();
            switch (tabSelected) {
                case 0:
                    velDeliveredPackages.initializeControl();
                    break;
                case 1:
                    velDeliveredComponents.initializeControl();
                    break;
                case 2:
                    velDeliveredContracts.initializeControl();
                    break;
                case 3:
                    velDeliveredProducts.initializeControl();
                    break;
                case 4:
                    velDeliveredNrcs.initializeControl();
                    break;
                case 5:
                    grdInventory.initializeControl();
                    break;
            }
        }
    }

    private void refreshOrderedTabSelected() {
        if (m_bHasBeenInitialized == true) {
            int tabSelected = tpOrdered.getSelectedIndex();
            switch (tabSelected) {
                case 0:
                    velOrderedComponents.initializeControl();
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

    private void performPackageSearch() {    
        ctxFinder.fireActionForObject("acct-package-single-entity-search-dlg", ctxFinder.findContext().getObject("ProductPackageList", "SingleEntityPackageSearch"));
        IContext context = ctxFinder.findContext();
        if (context != null) {
            // We're using the SingletonManager to retrieve the Account that is to be changed.
            TreePath selectedPath = treeAccountServices.getSelectionPath();
            if (selectedPath != null && selectedPath.getPathCount() == 1) {
                CollectionTreeNode node = (CollectionTreeNode)selectedPath.getLastPathComponent();
                if (node != null) {
                    IPersistentObject account = node.getObject();

                    IPersistentCollection productPackageList = account.getCollection("ProductPackageList", "Account");
                    IPersistentCollection searchProductPackageList = context.getCollection("ProductPackageList", "SingleEntityPackageSearch");
                    if (searchProductPackageList != null) {
                        searchProductPackageList.setAttributeDataAsInteger("ParentAccountInternalId", account.getAttributeDataAsInteger("AccountInternalId"));
                        int count = searchProductPackageList.getCount();
                        for (int i=0; i<count; i++) {
                            IPersistentObject productPackage = searchProductPackageList.getAt(i);
                            if (productPackage != null && productPackageList.isObjectCollectedNoFault(productPackage.getId()) == false) {
                                productPackageList.add(productPackage);
                            }
                        }
                    }
                }
            }
        }
        treeAccountServices.setModelInfo(buildAccountServicesTreeXMLDefinition());
        treeAccountServices.initializeControl();
        treeAccountServices.setSelectionRow(0);
        refreshDeliveredTabSelected();
    }

    private boolean isServiceRateClassMenuDisplayed(IPersistentObject service) {
        boolean success = false;

        // If SystemParameter is set and Service.RateClass is null,
        // we'll have to get the information from the user.
        //==============================================================================================
        // Note:  If these conditions are such that we will allow the user to set the Service.RateClass,
        // the requirements are that we never prevent the user from changing the Service.RateClass once
        // components have been added to the Service, even though the components may become invalid by
        // changing the Service.RateClass.
        //==============================================================================================
        if (m_serviceRateClassEligibilityRequired == true && service.isNewObject() == true) {
            
            IPersistentObject servicePricingPlan = service.getObject("ServicePricingPlan", "Service");
            if (servicePricingPlan != null && servicePricingPlan.getAttributeData("RateClass") == null) {
                success = true;
            }
        }

        return success;
    }
    
    private void treeAccountServicesValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeAccountServicesValueChanged
        // Add your handling code here:
        //CollectionTreeNode node = (CollectionTreeNode) selPath.getLastPathComponent();
        CollectionTreeNode node = (CollectionTreeNode)treeAccountServices.getLastSelectedPathComponent();
        if (node == null) return;
        IContext ctx = ctxFinder.findContext();
        if (ctx == null) return;
        IPersistentObject target = node.getObject();
        if (target.getId().isInstanceOf("Account", null) == true) {
            IPersistentCollection pColl = target.getCollection("InvElementList","Reserved");
            ctx.addTopic(pColl,true);
            grdInventory.setCollectionSubtype("Reserved");
            grdInventory.initializeControl();
            m_selectedNode = target;

            setOrderedVelocityTemplatesServiceLevel(Boolean.FALSE);
            setDeliveredVelocityTemplatesServiceLevel(Boolean.FALSE);
            
        } else if (target.getId().isInstanceOf("Service", null) == true) {
            // Inventory Stuff
            // Need to add to Collection and the Service to the Context because Inventory Search Blindly associatons to the first
            // Servce it finds on the context
            if (target instanceof LookupDomain) {
                IPersistentObject object = target.getObject("Service", null);
                ctx.addTopic(object,true);
            } else {
                ctx.addTopic(target,true);
            }

            IPersistentCollection pColl = target.getCollection("InvElementList","Service");
            ctx.addTopic(pColl,true);
            grdInventory.setCollectionSubtype("Service");
            grdInventory.initializeControl();
            m_selectedNode = target;
            
            setOrderedVelocityTemplatesServiceLevel(Boolean.TRUE);
            setDeliveredVelocityTemplatesServiceLevel(Boolean.TRUE);
        }
        // Only refresh the selected tabs as the others will get initialized when selected.
        refreshDeliveredTabSelected();
    }//GEN-LAST:event_treeAccountServicesValueChanged


    /**
     * Builds the xml definition for the Services tree.
     * Created this way because we want to show the number of services currently in the working set.
     */
    private String buildAccountServicesTreeXMLDefinition() {

        // Format the header node to indicate the number of services currently in the working set.
        String headerString = "";
        IContext context = ctxFinder.findContext();
        if (context != null) {
            IPersistentObject account = ctxFinder.findContext().getObject("Account");
            IPersistentCollection serviceList = null;
            if (account != null) {
            	serviceList = account.getCollection("ServiceList", "Account");
            }
            if (account != null && serviceList != null) {
                int totalFoundCount = serviceList.executeFindCount();
                int count = serviceList.getCount();
                if (totalFoundCount != count) {
                    headerString = ResourceManager.getInstance().formatStringResource("AggrServiceProductDelivery.text.ServicesPartial", new Object[] {new Integer(count), new Integer(totalFoundCount)});
                } else {
                    headerString = ResourceManager.getInstance().formatStringResource("AggrServiceProductDelivery.text.ServicesAll", new Object[] {new Integer(totalFoundCount)});
                }
            } else {
                headerString = ResourceManager.getString("AggrServiceProductDelivery.text.AccountLevel");
            }
        }

        String xmlDef = "<?xml version=\"1.0\"?>" +
                            "<csg-tree>" +
                                "<node>" +
                                "<label static=\"" + headerString + "\" />" +
                                    "<children>" + 
                                        "<data-set name=\"ServiceList\" subtype=\"Account\">" +
                                            "<node>" +
                                                "<label column-formatter-name=\"com.csgsystems.domain.arbor.formatter.ServiceDeliveryDescription\" />" +
                                            "</node>" +
                                        "</data-set>" +
                                    "</children>" +
                                "</node>" +
                            "</csg-tree>";
        return xmlDef;
    }
    
    private void launchInventorySearch() {
        if (m_selectedNode != null && m_selectedNode instanceof Service) {
            int index[] = treeAccountServices.getSelectionRows();
            ctxFinder.fireActionForObject("new-cust-acq-wiz-inventory-search-dlg", m_selectedNode);
            refreshDeliveredTabSelected();
            grdInventory.initializeControl();
            treeAccountServices.initializeControl();
            treeAccountServices.setSelectionRow(index[0]);
        }
    }

    private void launchInventoryReserve() {
        if (m_selectedNode != null && m_selectedNode instanceof Account) {
            int index[] = treeAccountServices.getSelectionRows();
            ctxFinder.fireActionForObject("acct-inventory-search-dlg", m_selectedNode);
            refreshDeliveredTabSelected();
            grdInventory.initializeControl();
            treeAccountServices.initializeControl();
            treeAccountServices.setSelectionRow(index[0]);
        }
    }    
    
    private void launchServiceRateClassDetail() {
        if (m_selectedNode != null && m_selectedNode instanceof Service) {
            int index[] = treeAccountServices.getSelectionRows();
            ctxFinder.fireActionForObject("service-rate-class-detail-dlg", m_selectedNode);
            refreshDeliveredTabSelected();
            grdInventory.initializeControl();
            treeAccountServices.initializeControl();
            treeAccountServices.setSelectionRow(index[0]);
        }
    }
    
    private void launchAddExternalId() {
        if (m_selectedNode != null && m_selectedNode instanceof Service) {
            int index[] = treeAccountServices.getSelectionRows();
            ctxFinder.fireActionForObject("service-externalId-add", m_selectedNode);
            refreshDeliveredTabSelected();
            grdInventory.initializeControl();
            treeAccountServices.initializeControl();
            treeAccountServices.setSelectionRow(index[0]);
        }
    }

    private void inventoryOptions(MouseEvent evt) {
        if (SwingUtilities.isRightMouseButton(evt)) {
            int sel = grdInventory.getSelectedRow();
            if (sel >= 0) {
                final IPersistentObject pInventorySelected = ((CollectionTableModel)grdInventory.getModel()).getObject(sel);
                JPopupMenu menu = new JPopupMenu();
                JMenuItem pSwapInv = new JMenuItem(ResourceManager.getString("AggrServiceProductDelivery.menuitem.Swap"));
                pSwapInv.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        swapInventory(pInventorySelected);
                    }
                });
                JMenuItem pUnAssign = new JMenuItem(ResourceManager.getString("AggrServiceProductDelivery.menuitem.UnAssign"));
                pUnAssign.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        unAssignInventory(pInventorySelected);  
                    }
                });
                //
                menu.add(pSwapInv);
                menu.add(pUnAssign);
                menu.show(grdInventory, evt.getX(), evt.getY());
            }
        }
    }

    private void swapInventory (IPersistentObject pInv) {
        Map pMap = new java.util.HashMap();        
        pMap.put("Service", m_selectedNode);
        pMap.put("InvElement", pInv);
        ctxFinder.fireActionForObject("service-inventory-swap", pMap);
    }

    private void unAssignInventory (IPersistentObject pInv) {
        Map pMap = new java.util.HashMap();        
        pMap.put("Service", m_selectedNode);
        pMap.put("InvElement", pInv);
        ctxFinder.fireActionForObject("service-inventory-unassign", pMap);
    }

    protected void setOrderedVelocityTemplatesToProvisioningPanel(Boolean setProvisioningPanel) {
        // Set all velocity templates showing Ordered information
        // to the appropriate action - Add vs. Remove
        velOrderedContracts.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
        velOrderedProducts.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
        velOrderedNrcs.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
        velOrderedComponents.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
    }

    protected void setOrderedVelocityTemplatesServiceLevel(Boolean setServiceLevel) {
        // Set all velocity templates showing Ordered information
        // to the appropriate Account/Service level
        velOrderedContracts.getSession().putValue("ServiceLevel", setServiceLevel);
        velOrderedProducts.getSession().putValue("ServiceLevel", setServiceLevel);
        velOrderedNrcs.getSession().putValue("ServiceLevel", setServiceLevel);
        velOrderedComponents.getSession().putValue("ServiceLevel", setServiceLevel);
    }

    protected void setDeliveredVelocityTemplatesToProvisioningPanel(Boolean setProvisioningPanel) {
        // Set all velocity templates showing Delivered information
        // to allow changes thru the Edit link.
        velDeliveredContracts.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
        velDeliveredProducts.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
        velDeliveredNrcs.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
        velDeliveredComponents.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
        velDeliveredPackages.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
    }

    protected void setDeliveredVelocityTemplatesServiceLevel(Boolean setServiceLevel) {
        // Set all velocity templates showing Delivered information
        // to the appropriate Account/Service level
        velDeliveredContracts.getSession().putValue("ServiceLevel", setServiceLevel);
        velDeliveredProducts.getSession().putValue("ServiceLevel", setServiceLevel);
        velDeliveredNrcs.getSession().putValue("ServiceLevel", setServiceLevel);
        velDeliveredComponents.getSession().putValue("ServiceLevel", setServiceLevel);
        velDeliveredPackages.getSession().putValue("ServiceLevel", setServiceLevel);
    }

    protected void setDeliveredExtraVelocityContextTopic() {
        // Set all velocity templates showing Delivered information with
        // an additional topic
        velDeliveredContracts.addExtraVelocityContextTopic("OrderManager", OrderManager.getInstance());
        velDeliveredProducts.addExtraVelocityContextTopic("OrderManager", OrderManager.getInstance());
        velDeliveredNrcs.addExtraVelocityContextTopic("OrderManager", OrderManager.getInstance());
        velDeliveredComponents.addExtraVelocityContextTopic("OrderManager", OrderManager.getInstance());
        velDeliveredPackages.addExtraVelocityContextTopic("OrderManager", OrderManager.getInstance());
    }

    protected void DetailHyperlinkFilter(javax.swing.event.HyperlinkEvent evt, CSGVelocityHTMLEP refreshHtmlControl) {
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
                    htmlControl.initializeControl();
                }
            } else if ("add-service".equals(event) == true) {
                addService(account, collectionName, subtypeName, index);
            } else if ("add".equals(event) == true) {
                // Specify the level, not a service, because there may be multiple services to which
                // we'll associate this object, so we'll have to see which ones are selected.
                addComponent(context, account, collectionName, subtypeName, index);
            } else if ("remove".equals(event) == true) {
                IPersistentObject service = null;

                // There should only be a single object selected in the tree
                TreePath[] selPaths = treeAccountServices.getSelectionPaths();
                if (selPaths != null) {
                    int pathCount = selPaths.length;
                    if (pathCount == 1) {
                        CollectionTreeNode pathComponent = (CollectionTreeNode)selPaths[0].getLastPathComponent();
                        if (pathComponent != null) {
                            IPersistentObject target = pathComponent.getObject();
                            if (target != null) {
                                if (target instanceof Account) {
                                    // Nothing to do, we've already got the Account object
                                } else if (target instanceof LookupDomain) {
                                    target = target.getObject("Service", null);
                                    service = target;
                                } else if (target instanceof Service) {
                                    service = target;
                                }
                            }
                        }
                        BpcrmOrderWizardUtility.removeOrderableObject(this, account, service, collectionName, subtypeName, index);
                    }
                }
            } else if ("search".equals(event) == true) {
                // We've got to pull the right collection from either the TransientShoppingCart or
                // the Account/Service and fire the action.
/*
//TODO: Need to handle this
                // Do this for Ordered panels
                IPersistentObject transientShoppingCart = context.getObject("TransientShoppingCart", null);
                if (transientShoppingCart != null) {
                    IPersistentCollection collection = transientShoppingCart.getCollection(collectionName, subtypeName);
                    if (collection != null) {
                       // Fire the requested action for the selected object                    
                        ctxFinder.fireActionForObject(action, collection);
                    }
                }

                // Do this for Delivered panels
                IPersistentCollection collection = null;
                if (service != null) {
                    collection = service.getCollection(collectionName, subtypeName);
                } else {
                    collection = account.getCollection(collectionName, subtypeName);
                }

                if (collection != null) {
                    // Fire the requested action for the selected object                    
                    ctxFinder.fireActionForObject(action, collection);
                }
*/
            } else if ("link".equals(event) == true) {
                // We can specify a service because we'll only fire an action for a single object at a time.
                BpcrmOrderWizardUtility.fireActionForObject(action, ctxFinder, collectionName, subtypeName, index);
                refreshHtmlControl = null;
            }
        }

        // Now set active and refresh the appropriate tab panel within the Delivered tab pane
        if (refreshHtmlControl != null) {
            if (tpDelivered.getSelectedComponent() == refreshHtmlControl) {
                // If the tab is already active, then just refresh it
                refreshHtmlControl.initializeControl();
            } else {
                // By setting the tab active it will also refresh it
                tpDelivered.setSelectedComponent(refreshHtmlControl);
            }
        }
    }           

    private void addService(IPersistentObject account, String collectionName, String subtype, int index) {
        IPersistentCollection collection = null;
        collection = ctxFinder.findContext().getCollection(collectionName, subtype);

        if (collection != null) {
            IPersistentObject object = collection.getAt(index);
            if (object != null) {
                boolean ret = account.canAssociate(object);
                if (ret == true) {
                    ret = account.addAssociation(object);
                    if (ret == true) {
                        // This is kinda different, but we've got to set some additional info on the tree
                        // as well as just initializing the control.
                        treeAccountServices.setModelInfo(buildAccountServicesTreeXMLDefinition());
                        treeAccountServices.initializeControl();
                        treeAccountServices.setSelectionRow(0);
                    } else {
                        if (account.getError() != null) {
                            ctxFinder.displayHTMLError(account.getError());
                            account.resetError();
                        }
                    }
                } else {
                    System.out.println("can Associcate Service->Account failed");
                }
            } else {
                System.out.println("Could not get object from list of selected services");
            }
        }
    }    

    private void removeService() {
        // Retrieve the selected paths in the tree
        TreePath[] selPaths = treeAccountServices.getSelectionPaths();
        if (selPaths != null) {
            int pathCount = selPaths.length;
            for (int i=0; i<pathCount; i++) {
                CollectionTreeNode pathComponent = (CollectionTreeNode)selPaths[i].getLastPathComponent();
                if (pathComponent != null) {
                    IPersistentObject service = pathComponent.getObject();
                    // We need to check for isNewObject here as well in case they've selected multiple
                    // services and some may not be new even tho the right click is only available on new objects.
                    if (service != null && service instanceof Service && service.isNewObject()) {
                        IContext context = ctxFinder.findContext();
                        if (context == null) {
                            System.out.println("Could not find Context");
                        } else {
                            IPersistentObject account = context.getObject("Account", null);
                            if (account == null) {
                                System.out.println("Could not get Account from Context");
                            } else {
                                // Since we've now got all the parts necessary, let's first make sure that we
                                // remove all of the orderable objects on this service if there are any.
                                boolean continueLooping = true;
                                while (continueLooping == true) {
                                    continueLooping = false;

                                    // We need to spin thru all of the objects on the service and remove the Orderable Objects
                                    removeServiceOrderableObjects(service, "NrcList");
                                    removeServiceOrderableObjects(service, "ProductList");
                                    removeServiceOrderableObjects(service, "CustomerContractList");
                                    removeServiceOrderableObjects(service, "ComponentList");     
                                    removeServiceOrderableObjects(service, "InvElementList");                                                                          
                                    
                                }

                                // Now remove the Service
                                boolean ret = account.removeAssociation(service);
                                if (ret == false) {
                                    IError error = account.getError();
                                    String message = ResourceManager.getString("AggrServiceProductDelivery.text.UnknownError");
                                    if (error != null) {
                                        message = error.getErrorMessage();
                                    }
                                    javax.swing.JOptionPane.showMessageDialog(this, message);
                                } else {
                                    treeAccountServices.setModelInfo(buildAccountServicesTreeXMLDefinition());
                                    treeAccountServices.initializeControl();
                                    treeAccountServices.setSelectionRow(0);
                                    refreshDeliveredTabSelected();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeServiceOrderableObjects(IPersistentObject service, String collectionName) {
        IPersistentCollection collection = service.getCollection(collectionName, "Service");
        if (collection != null) {
            int count = collection.getCount();
            for (int i=count-1; i>=0; i--) {
                IPersistentObject object = collection.getAt(i);
                if (object != null && canRemoveOrderableObject(object) == true) {
                	if (collectionName.equals("InvElementList")) {
                		object.setSubtype("cancelView");                        
                        IContext context = ctxFinder.findContext();
                        IPersistentCollection coll = context.getCollection("InvElementList","Reserved");
                        if (coll != null) {
                         coll.reset();
                        }                                                
                	}                	
                    // Remove the object from the service
                    service.removeAssociation(object);
                }
            }
        }
    }

    private boolean canRemoveOrderableObject(IPersistentObject object) {
        boolean canRemoveItem = true;

        if (object instanceof Nrc) {
            boolean isPartOfContract = object.getAttributeDataAsBoolean("IsPartOfContract");
            if (isPartOfContract == true) {
                // We can't remove this Nrc because it is part of a Contract.
                canRemoveItem = false;
            }
        } else if (object instanceof Product) {
            boolean isPartOfComponent = object.getAttributeDataAsBoolean("IsPartOfComponent");
            boolean isPartOfContract = object.getAttributeDataAsBoolean("IsPartOfContract");
            if (isPartOfComponent == true || isPartOfContract == true) {
                // We can't remove this Product because it is part of a Component or a Contract.
                canRemoveItem = false;
            }
        } else if (object instanceof CustomerContract) {
            boolean isPartOfComponent = object.getAttributeDataAsBoolean("IsPartOfComponent");
            if (isPartOfComponent == true) {
                // We can't remove this CustomerContract because it is part of a Component.
                canRemoveItem = false;
            }
        } else if (object instanceof Component) {
            // Nothing prevents us from removing the component
        }
        return canRemoveItem;
    }

    private void addComponent(IContext context, IPersistentObject account, String collectionName, String subtypeName, int index) {

        IPersistentObject transientShoppingCart = context.getObject("TransientShoppingCart", null);

        // There may be multiple objects in the tree selected to which we want to add this OrderableObject
        TreePath[] selPaths = treeAccountServices.getSelectionPaths();
        if (selPaths != null) {
            // This is used to copy the last Service eligibility rows if we are using the same type
            // of service object.  We only account for the immediately preceding service object.
            // This cuts our performance somewhat, and if necessary, we can improve it later.
            IPersistentObject lastServiceTarget = null;

            IPersistentObject orderableObject = null;
            IPersistentCollection collection = null;
            if (transientShoppingCart != null) {
                collection = transientShoppingCart.getCollection(collectionName, "TransientShoppingCart");
                if (collection != null) {
                    orderableObject = collection.getAt(index);
                }
            }

            int pathCount = selPaths.length;
            for (int i=0; i<pathCount; i++) {
                CollectionTreeNode pathComponent = (CollectionTreeNode)selPaths[i].getLastPathComponent();
                if (pathComponent != null) {
                    boolean canAssociateObject = true;

                    IPersistentObject target = pathComponent.getObject();
                    if (target != null && target instanceof Account) {
                        // There are no validation specifics for Account level objects
                    } else if (target != null && (target instanceof Service || target instanceof LookupDomain)) {
                        if (target instanceof LookupDomain) {
                            target = target.getObject("Service", null);
                        }

                        // Make sure that the RateClass is populated before we continue
                        if (target.isNewObject() == true &&
                            m_serviceRateClassEligibilityRequired == true &&
                            target.getAttributeDataAsInteger("RateClass") < 0) {

                            // Post an error message because we don't have a Service.RateClass
                            String message = ResourceManager.getString("AggrServiceProductDelivery.text.ServiceRateClassRequired");
                            javax.swing.JOptionPane.showMessageDialog(this, message);
                            canAssociateObject = false;
                        }

                        // Use the eligibility information from the previous service object if
                        // it's the same type as the one that we're using now.
                        if (lastServiceTarget != null) {
                            if (collection != null && collection instanceof PackageComponentDefList) {
                                Map params = new HashMap();
                                params.put("OtherService", lastServiceTarget);
                                target.sendMessage("copyEligibilityIfAble", params);
                            }
                        }
                        
                        // Set the last service target to the current for the next service.
                        lastServiceTarget = target;
                    } else {
                        canAssociateObject = false;
                    }


                    if (canAssociateObject == true && orderableObject != null) {
                        boolean success = target.addAssociation(orderableObject);
                        if (success == false) {
                            IError error = target.getError();
                            if (error != null) {
                                ctxFinder.displayHTMLError( error );
                            } else {
                                String message = ResourceManager.getString("AggrServiceProductDelivery.text.UnknownError");
                                javax.swing.JOptionPane.showMessageDialog(this, message);
                            }
                        }
                    }
                }
            }
        }
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.csgsystems.igpa.controls.CSGCollectionGrid grdInventory;
    private javax.swing.JScrollPane inventoryScroll;
    private javax.swing.JPanel pnlAvailable;
    private javax.swing.JPanel pnlDelivered;
    private javax.swing.JScrollPane spAccountServices;
    private javax.swing.JSplitPane splitAvailable;
    private javax.swing.JSplitPane splitDelivered;
    private javax.swing.JSplitPane splitProvisioning;
    private javax.swing.JTabbedPane tpDelivered;
    private javax.swing.JTabbedPane tpOrdered;
    private com.csgsystems.igpa.controls.CSGCollectionTree treeAccountServices;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velAvailableServices;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velDeliveredComponents;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velDeliveredContracts;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velDeliveredNrcs;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velDeliveredPackages;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velDeliveredProducts;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velOrderedComponents;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velOrderedContracts;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velOrderedNrcs;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velOrderedProducts;
    // End of variables declaration//GEN-END:variables
    
}
