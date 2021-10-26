/*
 * ChangeAccountLevelItems.java
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
import com.csgsystems.bp.utilities.TemplateUtility;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.JScrollPane;
import javax.swing.tree.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import java.util.*;
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

/**
 *
 * @author  brot01
 */
public class ChangeAccountLevelItems extends javax.swing.JPanel implements ContextFormListener, HyperlinkListener {

    private static Log log = null;

    static {
        try {
            log = LogFactory.getLog(ChangeAccountLevelItems.class);
        } catch (Exception ex) {}
    }

    /**
     *  Instance of the ContextFinder utility class, which can recursively
     *  search up the component hierarchy for an ICSGContextForm, and then
     *  retrieve its context (cached for later use).
     */
    private ContextFinder ctxFinder = new ContextFinder(this);
    protected AggrPackageAvailable pPackageAvailable = null;
    protected AggrPackageCurrent pPackageCurrent = null;
    protected AggrPackageOrdered pPackageOrdered = null;
    protected CSGBoptTree treeAlaCartProducts = null;
    
    protected boolean m_bHasBeenInitialized = false;


    /** Creates new form OrderServices */
    public ChangeAccountLevelItems() {
        initComponents();
        pPackageAvailable = new AggrPackageAvailable();
        pPackageAvailable.setCanDisplayWithOnlyServiceLevel(false); // Don't display Components for Packages that are ServiceLevel only
        pPackageAvailable.setCanDisplayWithRequiredServiceLevel(false); // Don't display ServiceLevel Components that are Required
        pPackageCurrent = new AggrPackageCurrent();
        pPackageCurrent.setCanDisplayWithOnlyServiceLevel(false); // Don't display Components for Packages that are ServiceLevel only
        pPackageCurrent.setCanDisplayWithRequiredServiceLevel(false); // Don't display ServiceLevel Components that are Required
        pPackageOrdered = new AggrPackageOrdered();
        pPackageOrdered.setCanDisplayWithOnlyServiceLevel(false); // Don't display Components for Packages that are ServiceLevel only
        pPackageOrdered.setCanDisplayWithRequiredServiceLevel(false); // Don't display ServiceLevel Components that are Required
        pPackageOrdered.setOrderedCollectionType(AggrPackageOrdered.PRODUCT_PACKAGE_LIST);
        treeAlaCartProducts = new CSGBoptTree();
        treeAlaCartProducts.setFilterDate(BpcrmOrderWizardUtility.retrieveEffectiveDate(this));
        treeAlaCartProducts.setTreeDefinition(buildCSGBoptTreeXMLDefinition());
        
        treeAlaCartProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeAlaCartProductsMousePressed(evt);
            }
        });

        tpAvailableItems.add(ResourceManager.getString("ChangeAccountLevelItems.tabName.CurrentPackages"), pPackageCurrent);
        tpAvailableItems.add(ResourceManager.getString("ChangeAccountLevelItems.tabName.AvailablePackages"), pPackageAvailable);
        tpAvailableItems.add(ResourceManager.getString("ChangeAccountLevelItems.tabName.OrderedPackages"), pPackageOrdered);
        tpAvailableItems.add(ResourceManager.getString("ChangeAccountLevelItems.tabName.ALaCarte"),new JScrollPane(treeAlaCartProducts));

        // Retrieve the Current Order and set the EffectiveDate to required
        // This is done here because there are other paths in the system where
        // the EffectiveDate cannot be required, so we'll do it at the panel level.
        IPersistentObject order = OrderManager.getInstance().getCurrentOrder();
        if (order != null) {
            IAttribute attribute = order.getAttribute("EffectiveDateWithTimestamp");
            attribute.setRequired(true);
            dtEffectiveDate.initializeControl();
        }

        setDeliveredVelocityTemplatesServiceLevel(Boolean.FALSE);
        setDeliveredVelocityTemplatesToProvisioningPanel(Boolean.FALSE);
        setDeliveredExtraVelocityContextTopic();

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
                    DetailHyperlinkFilter(evt, pnlDeliveredComponents);
                }
            }
        });
        velDeliveredComponentElements.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ 
                    DetailHyperlinkFilter(evt, pnlDeliveredComponents);
                }
            }
        });
        velDeliveredComponentElements.addExtraVelocityContextTopic("TemplateUtility", new TemplateUtility());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        pnlAccount = new javax.swing.JPanel();
        lblEffectiveDate = new com.csgsystems.igpa.controls.CSGLabel();
        lblDisconnectReason = new com.csgsystems.igpa.controls.CSGLabel();
        dtEffectiveDate = new com.csgsystems.igpa.controls.CSGDateEdit();
        cbDisconnectReason = new com.csgsystems.igpa.controls.CSGComboBox();
        pnlOrderInformation = new javax.swing.JPanel();
        spOrderInformation = new javax.swing.JSplitPane();
        tpAvailableItems = new javax.swing.JTabbedPane();
        tpDelivered = new javax.swing.JTabbedPane();
        pnlDeliveredComponents = new javax.swing.JPanel();
        spDeliveredComponents = new javax.swing.JSplitPane();
        velDeliveredComponents = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velDeliveredComponentElements = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velDeliveredContracts = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velDeliveredProducts = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
        velDeliveredNrcs = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();

        setLayout(new java.awt.GridBagLayout());

        setMinimumSize(new java.awt.Dimension(990, 620));
        setPreferredSize(new java.awt.Dimension(990, 620));
        pnlAccount.setLayout(null);

        lblEffectiveDate.setLocalizationKey("ChangeAccountLevelItems.lblEffectiveDate");
        pnlAccount.add(lblEffectiveDate);
        lblEffectiveDate.setBounds(10, 10, 135, 20);

        lblDisconnectReason.setLocalizationKey("ChangeAccountLevelItems.lblDisconnectReason");
        pnlAccount.add(lblDisconnectReason);
        lblDisconnectReason.setBounds(10, 35, 135, 20);

        dtEffectiveDate.setAttributeName("EffectiveDateWithTimestamp");
        dtEffectiveDate.setDomainName("Order");
        dtEffectiveDate.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                dtEffectiveDateVetoableChange(evt);
            }
        });

        pnlAccount.add(dtEffectiveDate);
        dtEffectiveDate.setBounds(145, 10, 160, 20);

        cbDisconnectReason.setAttributeName("DisconnectReason");
        cbDisconnectReason.setDomainName("Order");
        pnlAccount.add(cbDisconnectReason);
        cbDisconnectReason.setBounds(145, 35, 160, 20);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 664;
        gridBagConstraints.ipady = 55;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(pnlAccount, gridBagConstraints);

        pnlOrderInformation.setLayout(new java.awt.GridBagLayout());

        spOrderInformation.setBorder(null);
        spOrderInformation.setDividerLocation(225);
        spOrderInformation.setDividerSize(5);
        spOrderInformation.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        spOrderInformation.setTopComponent(tpAvailableItems);

        tpDelivered.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tpDelivered.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tpDeliveredStateChanged(evt);
            }
        });

        pnlDeliveredComponents.setLayout(new java.awt.GridBagLayout());

        spDeliveredComponents.setDividerLocation(475);
        spDeliveredComponents.setDividerSize(5);
        velDeliveredComponents.setBorder(null);
        velDeliveredComponents.setPreferredSize(new java.awt.Dimension(690, 50));
        velDeliveredComponents.setVelocityTemplateUri("template/DeliveredComponents.vm");
        velDeliveredComponents.setAutoscrolls(true);
        spDeliveredComponents.setLeftComponent(velDeliveredComponents);

        velDeliveredComponentElements.setBorder(null);
        velDeliveredComponentElements.setPreferredSize(new java.awt.Dimension(690, 50));
        velDeliveredComponentElements.setVelocityTemplateUri("template/DeliveredComponentElements.vm");
        velDeliveredComponentElements.setAutoscrolls(true);
        spDeliveredComponents.setRightComponent(velDeliveredComponentElements);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlDeliveredComponents.add(spDeliveredComponents, gridBagConstraints);

        tpDelivered.addTab(ResourceManager.getString( "AggrServiceProductDelivery.tab.DeliveredComponents" ), pnlDeliveredComponents);

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

        spOrderInformation.setBottomComponent(tpDelivered);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnlOrderInformation.add(spOrderInformation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 664;
        gridBagConstraints.ipady = 240;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(pnlOrderInformation, gridBagConstraints);

    }//GEN-END:initComponents

    private void tpDeliveredStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tpDeliveredStateChanged
        refreshDeliveredTabSelected();
    }//GEN-LAST:event_tpDeliveredStateChanged

    private void dtEffectiveDateVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_dtEffectiveDateVetoableChange
        Object obj = evt.getNewValue();
        if (obj instanceof Date) {
            Date newEffectiveDate = (Date)obj;

            IContext context = ctxFinder.findContext();

            IPersistentObject account = context.getObject("Account", null);
            if (account != null) {
                boolean allItemsAreValid = BpcrmOrderWizardUtility.verifyEffectiveDateChange(newEffectiveDate, account);
                if (allItemsAreValid == false) {
                    // We found ordered items that are invalid with the new EffectiveDate.
                    // Pop a dialog showing the invalid objects.  If the user wishes to remove the objects
                    // we put that information into the map on the shutdown of the dialog.  If the user wishes
                    // to remove the items, then we'll allow them to continue, otherwise, we have to veto the date change.
                    Map parameters = new HashMap();
                    parameters.put("NewEffectiveDate", newEffectiveDate);
                    parameters.put("Object", account);
                    ctxFinder.fireActionForObject("invalid-orderable-object-dlg", parameters);

                    Boolean removeInvalidItems = (Boolean)parameters.get("RemoveInvalidItems");
                    if (removeInvalidItems == null || removeInvalidItems == Boolean.FALSE) {
                        java.beans.PropertyVetoException invalidDate = new java.beans.PropertyVetoException(null, evt);
                        throw invalidDate;
                    } else {
                        BpcrmOrderWizardUtility.removeInvalidItemsFromEffectiveDateChange(newEffectiveDate, account);
                    }
                }
            }
        }
    }//GEN-LAST:event_dtEffectiveDateVetoableChange

    public void contextFormStateChanged(com.csgsystems.igpa.forms.ContextFormEvent contextFormEvent) {
        if (ContextFormEvent.POST_INIT_CONTROLS == contextFormEvent.getType() ) {

            if( !m_bHasBeenInitialized ) {
                m_bHasBeenInitialized = true;
            
                // add a listener to the effective date control
                dtEffectiveDate.addBoundDataChangeListener(new BoundDataChangeListener() {
                        public void boundDataChanged(BoundDataChangeEvent bdce) {
                            dtEffectiveDateBoundDataChanged(bdce);
                        }
                    });
            }
        } else if (ContextFormEvent.PRE_INIT_CONTROLS == contextFormEvent.getType() ) {
            //put in code right here to set a transient attribute on the Nrc AlaCarte collectio to make it not use the IsPartOfContract attribute for the change wizards and use it for the other wizards.
            IContext ctx = ctxFinder.findContext();
            if (ctx != null) {
                IPersistentObject account = ctx.getObject("Account", null);
                if (account != null) {
                    IPersistentCollection nrcList = account.getCollection("NrcList", "AccountAlaCarte");
                    if (nrcList != null) {
                        // Make sure that we don't use the IsPartOfContract filter when we are in the AccountChange wizard.
                        // That way we'll show all Nrcs in the list and user can change Nrcs as appropriate.
                        nrcList.setAttributeDataAsBoolean("ViewIsPartOfContractNrcs", true);
                    }
                }
            }
        }
        
        // Forward context events to the inner AggrPackageCurrent panel
        pPackageCurrent.contextFormStateChanged(contextFormEvent);
    }

    public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            DetailHyperlinkFilter(evt, null);
        }
    }

    private void dtEffectiveDateBoundDataChanged(BoundDataChangeEvent bdce) {
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

        // The EffectiveDate has changed, so set the value onto the TransientShoppingCart.
        IContext ctx = ctxFinder.findContext();
        if (ctx != null) {
            // The EffectiveDate has changed, so we need to re-filter out list of packages and package groups
            IPersistentObject account = ctx.getObject("Account", null);
            if (account != null) {                        
                IPersistentCollection packageDefinitionList = account.getCollection("PackageDefinitionList", "Account");
                IPersistentCollection packageGroupList = account.getCollection("PackageGroupList", "Account");
                if (packageDefinitionList != null && packageGroupList != null) {
                    // Reset the collection and initialize our controls
                    packageDefinitionList.reset();
                    packageGroupList.reset();
                    ctxFinder.initControls();
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
                    IPersistentObject objectAdded = BpcrmOrderWizardUtility.addAlaCarteItemToObject(null, effectiveDate, ctx, treeAlaCartProducts, "Account");

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
                                IPersistentObject objectAdded = BpcrmOrderWizardUtility.addAlaCarteItemToObject(null, effectiveDate, ctx, treeAlaCartProducts, "Account");

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

    private void refreshDeliveredTabSelected() {
        if (m_bHasBeenInitialized == true) {
            int tabSelected = tpDelivered.getSelectedIndex();
            switch (tabSelected) {
                case 0:
                    velDeliveredComponents.initializeControl();
                    velDeliveredComponentElements.initializeControl();
                    break;
                case 1:
                    velDeliveredContracts.initializeControl();
                    break;
                case 2:
                    velDeliveredProducts.initializeControl();
                    break;
                case 3:
                    velDeliveredNrcs.initializeControl();
                    break;
            }
        }
    }

    private void setPanelToActive(IPersistentObject object) {
        // Set the appropriate panel active.
        if (object instanceof NrcTransDescr) {
            if (tpDelivered.getSelectedComponent() == velDeliveredNrcs) {
                // If the tab is already active, then just refresh it
                velDeliveredNrcs.initializeControl();
            } else {
                // By setting the tab active it will also refresh it
                tpDelivered.setSelectedComponent(velDeliveredNrcs);
            }
        } else if (object instanceof ProductElement) {
            if (tpDelivered.getSelectedComponent() == velDeliveredProducts) {
                // If the tab is already active, then just refresh it
                velDeliveredProducts.initializeControl();
            } else {
                // By setting the tab active it will also refresh it
                tpDelivered.setSelectedComponent(velDeliveredProducts);
            }
        } else if (object instanceof ContractType) {
            if (tpDelivered.getSelectedComponent() == velDeliveredContracts) {
                // If the tab is already active, then just refresh it
                velDeliveredContracts.initializeControl();
            } else {
                // By setting the tab active it will also refresh it
                tpDelivered.setSelectedComponent(velDeliveredContracts);
            }
        }
    }

    private void addComponentToAccount(IContext context, IPersistentObject account, String collectionName, String subtypeName, int index) {

        IPersistentCollection packageComponentDefList = context.getCollection(collectionName, subtypeName);
        if (packageComponentDefList != null) {

            // Retrieve the EffectiveDate to be used for all items
            Date effectiveDate = BpcrmOrderWizardUtility.retrieveEffectiveDate(this);

            // Retrieve the selected Package from the tree
            boolean isAuthorized = true;
            IPersistentObject packageDefinition = null;
            IPersistentObject productPackage = null;
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
                        if (selectedTab instanceof AggrPackageCurrent || selectedTab instanceof AggrPackageOrdered) {
                            // This is already the ProductPackage so use it.
                            // No need to verify authoization, because the package is already there.
                            productPackage = selectedObject;
                        } else if (selectedTab instanceof AggrPackageAvailable) {
                            // Create a clone because we need a different reference for each different PackageDefinition
                           packageDefinition = selectedObject.createClone(null);
                           if (packageDefinition != null) {
                               // A package was selected and we found it, now verify
                               // that the selected object is authorized for create by security
                               isAuthorized = DomainUtility.isAuthorizedService("Package.PackageCreate");
                               if (isAuthorized == false) {
                                   // Notify the user that the selected object cannot be created
                                   javax.swing.JOptionPane.showMessageDialog(this, ResourceManager.getString("CC-4-11")+packageDefinition.getAttributeDataAsString("DisplayValue"));
                               }
                               if (account != null) {
                                   // Increment the Display to show what Package Instance the components belong to
                                   int incrementor = account.getAttributeDataAsInteger("DisplayNewPackageDefIncrementor");
                                   account.setAttributeDataAsInteger("DisplayNewPackageDefIncrementor", ++incrementor);
                                   packageDefinition.setAttributeDataAsString("TransientDisplayPackageDefInstId", ResourceManager.getString("OrderWizard.text.New")+" - "+new Integer(incrementor).toString());
                                   
                               }
                           }
                        }
                    }
                }
            }

            if (isAuthorized == true) {
                // Spin thru the list of components and pull selected ones
                int count = packageComponentDefList.getCount();
                for (int i=0; i<count; i++) {
                    IPersistentObject origPackageComponentDef = packageComponentDefList.getAt(i);
                    if (origPackageComponentDef != null && origPackageComponentDef.getAttributeDataAsBoolean("IsSelected") == true) {
                        // Set the EffectiveDate on the object, but only if it's a template type.
                        // If it's an instance object, it's already been associated so don't reset the effective date.
                        if (packageDefinition != null) {
                            packageDefinition.setAttributeDataAsDate("EffectiveDateForInstanceObject", effectiveDate);
                        } else {
                            packageDefinition = productPackage.getObject("PackageDefinition", "ProductPackage");

                            // Add the ProductPackage to our Package Definition because it is an existing package
                            // ++++ Do Not Duplicate This Paradigm In Other Objects!! ++++
                            // This is done so that when multiple Components are added, we know to
                            // which ProductPackage instance to associate the new Component instance.
                            boolean success = packageDefinition.addAssociation(productPackage);
                        }

                        // Create a clone because we need a different reference for each different PackageComponentDef
                        IPersistentObject packageComponentDef = origPackageComponentDef.createClone(null);
                        if (packageComponentDef != null) {
                            // Set the EffectiveDate on the object
                            packageComponentDef.setAttributeDataAsDate("EffectiveDateForInstanceObject", effectiveDate);
    
                            // Verify that we've got an Account and that that Component is Account level
                            if (account != null && packageComponentDef.getAttributeDataAsInteger("ComponentLevel") == 1) {
    
                                // Associate the Package to the Component
                                boolean success = packageComponentDef.addAssociation(packageDefinition);
                                if (success == true) {
                                    // Associate the Component to the Account
                                    success = account.addAssociation(packageComponentDef);
                                    if (success != true) {
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
                }
            }

            tpDelivered.setSelectedComponent(pnlDeliveredComponents);
            velDeliveredComponents.initializeControl();
        }
    }    

    protected void setDeliveredVelocityTemplatesServiceLevel(Boolean setServiceLevel) {
        // Set all velocity templates showing Delivered information
        // to the appropriate Account/Service level
        velDeliveredContracts.getSession().putValue("ServiceLevel", setServiceLevel);
        velDeliveredProducts.getSession().putValue("ServiceLevel", setServiceLevel);
        velDeliveredNrcs.getSession().putValue("ServiceLevel", setServiceLevel);
        velDeliveredComponents.getSession().putValue("ServiceLevel", setServiceLevel);
    }

    protected void setDeliveredVelocityTemplatesToProvisioningPanel(Boolean setProvisioningPanel) {
        // Set all velocity templates showing Delivered information
        // to allow changes thru the Edit link.
        velDeliveredContracts.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
        velDeliveredProducts.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
        velDeliveredNrcs.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
        velDeliveredComponents.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
        velDeliveredComponentElements.getSession().putValue("ProvisioningPanel", setProvisioningPanel);
    }

    protected void setDeliveredExtraVelocityContextTopic() {
        // Set all velocity templates showing Delivered information with
        // an additional topic
        velDeliveredContracts.addExtraVelocityContextTopic("OrderManager", OrderManager.getInstance());
        velDeliveredProducts.addExtraVelocityContextTopic("OrderManager", OrderManager.getInstance());
        velDeliveredNrcs.addExtraVelocityContextTopic("OrderManager", OrderManager.getInstance());
        velDeliveredComponents.addExtraVelocityContextTopic("OrderManager", OrderManager.getInstance());
        velDeliveredComponentElements.addExtraVelocityContextTopic("OrderManager", OrderManager.getInstance());
    }

    protected void DetailHyperlinkFilter(javax.swing.event.HyperlinkEvent evt, Object refreshHtmlControl) {
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
                        if ( packCompDef.getAttributeDataAsInteger("MinimumRequired") == 0 )
                        BpcrmOrderWizardUtility.setObjectSelected(context, collectionName, subtypeName, i, false);
                    }
            } else if ("select-component".equals(event) == true) {
                BpcrmOrderWizardUtility.setObjectSelected(context, collectionName,subtypeName,index, true);
            } else if ("unselect-component".equals(event) == true) {
                BpcrmOrderWizardUtility.setObjectSelected(context, collectionName,subtypeName,index, false);
            } else if ("add-components".equals(event) == true) {
                // This is only ever going to be Account level.
                addComponentToAccount(context, account, collectionName, subtypeName, index);
            } else if ("detail".equals(event) == true) {
                if ("component-element-view".equals(action) == true) {
                    // Because the ComponentElements are displayed in a neighboring panel to the Component objects,
                    // this is not an action that is fired to pop a new panel, and thus we'll handle it internally.
                    IPersistentCollection collection = account.getCollection(collectionName, "Account");
                    if (collection != null) {
                        // Retrieve the selected object in the collection
                        IPersistentObject object = collection.getAt(index);
                        if (object != null) {
                            context.addTopic(object);
                            
                            velDeliveredComponentElements.initializeControl();
                        }
                    }
                }
            } else if ("remove".equals(event) == true) {
                // We don't need to specify a service because we should always be account level.
                BpcrmOrderWizardUtility.removeOrderableObject(this, account, null, collectionName, subtypeName, index);
                // Now remove the topic if one is specified
                if (topicName != null) {
                    IPersistentObject topic = context.getObject(topicName, null);
                    if (topic != null) {
                        context.removeTopic(topic);
                    }
                }
            } else if ("search".equals(event) == true) {
                // We've got to pull the right collection from the Account and fire the action.
                IPersistentCollection collection = account.getCollection(collectionName, subtypeName);
                if (collection != null) {
                	// Used to hide the historical date search on the search panel fired from 
                    // the DeliveredComponents.vm template
                     String DisplayParam = "";
                     if (params.get("DisplayHistoricalSearch") != null){
                     	DisplayParam = (String)params.get("DisplayHistoricalSearch");
                     	if (DisplayParam.equals("false")) {
                     		// Set to true by default on the collection.
                     		collection.setAttributeDataAsBoolean("DisplayHistoricalSearch", false);
                     	}
                     } 
                	
                   // Fire the requested action for the selected object                    
                    ctxFinder.fireActionForObject(action, collection);
                }
            } else if ("link".equals(event) == true) {
                // We can specify a service because we'll only fire an action for a single object at a time.
                BpcrmOrderWizardUtility.fireActionForObject(action, ctxFinder, collectionName, subtypeName, index);
            }
        }

        // Now set active and refresh the appropriate tab panel within the Delivered tab pane
        if (refreshHtmlControl != null && refreshHtmlControl instanceof CSGVelocityHTMLEP) {
            if (tpDelivered.getSelectedComponent() == refreshHtmlControl) {
                // If the tab is already active, then just refresh it
                ((CSGVelocityHTMLEP)refreshHtmlControl).initializeControl();
            } else {
                // By setting the tab active it will also refresh it
                tpDelivered.setSelectedComponent((CSGVelocityHTMLEP)refreshHtmlControl);
            }
        } else if (refreshHtmlControl != null && refreshHtmlControl instanceof javax.swing.JPanel) {
            if (tpDelivered.getSelectedComponent() == refreshHtmlControl) {
                // If the tab is already active, then just refresh it
                velDeliveredComponents.initializeControl();
                velDeliveredComponentElements.initializeControl();
            } else {
                // By setting the tab active it will also refresh it
                tpDelivered.setSelectedComponent((javax.swing.JPanel)refreshHtmlControl);
            }
        } else {
            CSGVelocityHTMLEP htmlControl = (CSGVelocityHTMLEP)evt.getSource();
            if (htmlControl != null) {
                htmlControl.initializeControl();
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
        
        xmlDef += BpcrmUtility.beginNode("ChangeAccountLevelItems.treeLabel.AvailableProducts", true, true, null, true);
        
        xmlDef += buildChargesNode();
        xmlDef += buildContractsNode();
        xmlDef += buildProductsNode();
        
        xmlDef += BpcrmUtility.endNode(true, true);
        
        xmlDef += "</csg-tree>";
        
        log.debug(xmlDef);
        // System.out.println(xmlDef);
        return xmlDef;
    }
    
    private String buildChargesNode() {
        String chargesNode = "";
        
        chargesNode += BpcrmUtility.beginNode("ChangeAccountLevelItems.treeLabel.Charges", true, true, null, true);
                
        chargesNode += buildInstallmentNode(0, 1, false);
        chargesNode += buildInstallmentNode(0, 2, true);
        
        chargesNode += BpcrmUtility.endNode(true, true);
        
        return chargesNode;
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
            installmentNodeLabel = "ChangeAccountLevelItems.treeLabel.Installment";
            dataSet += ".InstallmentTypeIdNrc!=#NULL#";
        } else {
            installmentNodeLabel = "ChangeAccountLevelItems.treeLabel.NonInstallment";
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
        
        contractsNode += BpcrmUtility.beginNode("ChangeAccountLevelItems.treeLabel.Contracts", true, true, null, false);
        
        contractsNode += BpcrmUtility.beginDataSet("ContractType.AllowAccount=1.ContractCategory!=9", "Type", "ActiveDate", "InactiveDate");
        contractsNode += BpcrmUtility.beginNode("DescriptionText", false, false, null, false);
        contractsNode += BpcrmUtility.endNode(false, false);
        contractsNode += BpcrmUtility.endDataSet();
        
        contractsNode += BpcrmUtility.endNode(true, false);
        
        return contractsNode;
    }
    
    private String buildProductsNode() {
        String productsNode = "";
        
        productsNode += BpcrmUtility.beginNode("ChangeAccountLevelItems.treelabel.Products", true, true, null, false);
        productsNode += BpcrmUtility.beginDataSet("ProductGroup", "ProductGroupId", null, null);
        productsNode += BpcrmUtility.beginNode("DescriptionText", false, true, "ProductGroupId", false);
        productsNode += BpcrmUtility.beginDataSet("ProductLine", "ProductLineId", null, null);
        productsNode += BpcrmUtility.beginNode("DescriptionText", false, true, "ProductLineId", false);
        productsNode += BpcrmUtility.beginDataSet("ProductElement.LevelCode!=2.ContractUseCode!=1", "ElementId", "DateActive", "DateInactive");
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
    private com.csgsystems.igpa.controls.CSGComboBox cbDisconnectReason;
    private com.csgsystems.igpa.controls.CSGDateEdit dtEffectiveDate;
    private com.csgsystems.igpa.controls.CSGLabel lblDisconnectReason;
    private com.csgsystems.igpa.controls.CSGLabel lblEffectiveDate;
    private javax.swing.JPanel pnlAccount;
    private javax.swing.JPanel pnlDeliveredComponents;
    private javax.swing.JPanel pnlOrderInformation;
    private javax.swing.JSplitPane spDeliveredComponents;
    private javax.swing.JSplitPane spOrderInformation;
    private javax.swing.JTabbedPane tpAvailableItems;
    private javax.swing.JTabbedPane tpDelivered;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velDeliveredComponentElements;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velDeliveredComponents;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velDeliveredContracts;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velDeliveredNrcs;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velDeliveredProducts;
    // End of variables declaration//GEN-END:variables
}
