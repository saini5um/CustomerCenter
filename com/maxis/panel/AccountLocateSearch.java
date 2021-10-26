package com.maxis.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Element;

import com.csgsystems.bp.utilities.CTISocketManager;
import com.csgsystems.domain.arbor.order.OrderManager;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.igpa.controls.CSGVelocityHTMLEP;
import com.csgsystems.igpa.controls.ICSGCardSet;
import com.csgsystems.igpa.controls.ICSGFilterCard;
import com.csgsystems.igpa.forms.ContextFormEvent;
import com.csgsystems.igpa.forms.ContextFormListener;
import com.csgsystems.localization.ResourceManager;
import com.csgsystems.bp.subsystems.AbstractHTMLPanel;

/**
 *
 * @author  brot01
 */
public class AccountLocateSearch extends AbstractHTMLPanel  implements ICSGFilterCard, ContextFormListener {
  
  protected ICSGCardSet cardSet = null;
  protected IContext    context = null;
  protected JPopupMenu menu = null;
  protected String url = null;
  protected int xpos = 0;
  protected int ypos = 0;
  protected boolean CTISearch = true;
  
  /** Creates new form NotesPanel */
  public AccountLocateSearch() {
    initComponents();
    edtAccountExternalId.grabFocus();
    
    // Ask for Instance so that when the READY fires it will be in the correct state
    CTISocketManager ctiManager = CTISocketManager.getInstance(this);
    
    // Set this panel as being a Filter Card for the Search Button.
    btnSearch.setFilterCard(this);
    
    createMenu();
    
    velAccountLocateList.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        int containerX = new Double(((JEditorPane)evt.getSource()).getBounds().getX()).intValue();
        int containerY = new Double(((JEditorPane)evt.getSource()).getBounds().getY()).intValue();
        xpos = evt.getX() + containerX;
        ypos = evt.getY() + containerY;
      }
    });
    
    velAccountLocateList.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
      public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
        String newURL = null;
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
          //display popup menu with billing, orders, services menues
          String type = getType(evt);
          if (type.equals("popup")){
            menu.show(velAccountLocateList, xpos, ypos);
          }else{
            DetailHyperlinkFilter(evt, velAccountLocateList, newURL);
          }
        }else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED){
          //display popup menu with billing, orders, services menues
          String type = getType(evt);
          if (type.equals("popup")){
            menu.show(velAccountLocateList, xpos, ypos);
          }
        }else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED){
          Element element = evt.getSourceElement();
          
          menu.setVisible(false);
        }
      }
    });
  }
  
  
  public final boolean verifyParameters(Map parameters) {
    return parameters.size() > 0; // Must have at least one parameter...
  }
  
  public void setCardSet(ICSGCardSet cardSet) {
    this.cardSet = cardSet;
  }
  
  public void setSearchParameters(Map parameters) {
  }
  
  private String getType(javax.swing.event.HyperlinkEvent evt){
    // parse the url to get the type
    url = evt.getURL().toString();
    HashMap params = parseURLtoMap(url);
    String type = "normal";
    if (params.get("type")!=null){
      type = (String) params.get("type");
    }
    return type;
  }
  
  private void createMenu() {
    menu = new JPopupMenu();
    
    // Billing Inquiry
    JMenuItem menuItem = new JMenuItem( ResourceManager.getString("AccountLocateSearch.menuitem.BillingInquiry") );
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DetailHyperlinkFilter(null,velAccountLocateList, url + "&action=display-customer-care-navbar-invoice-list");
      }
    });
    menu.add(menuItem);
    
    // Services
    menuItem = new JMenuItem( ResourceManager.getString("AccountLocateSearch.menuitem.Services") );
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DetailHyperlinkFilter(null,velAccountLocateList, url + "&action=display-customer-care-navbar-service-list");
      }
    });
    menu.add(menuItem);
    
    // Orders
    menuItem = new JMenuItem( ResourceManager.getString("AccountLocateSearch.menuitem.Orders") );
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DetailHyperlinkFilter(null,velAccountLocateList, url + "&action=display-customer-care-navbar-order-list");
      }
    });
    menu.add(menuItem);
  }
  
  public void contextFormStateChanged(ContextFormEvent evt) {
    if (evt.getType() ==  ContextFormEvent.READY){
            /*  Fired when the panel is open and is active on the screen (not called for dialogs).
             *  For views panels that would like to set a default button, trap this event and set your
             *  default button.
             */
      getRootPane().setDefaultButton(btnSearch);
      
            /*  Send a Ready to the CTI interface because the screen is now up
             *
             */
      
      sendReadyToCTI();
    } else if (evt.getType() == ContextFormEvent.CTI_MESSAGE_RECEIVED) {
      fireCTIAction((String)evt.getSource());
    } else if (evt.getType() ==  ContextFormEvent.VIEW_CLOSED) {
      if(!CTISearch) {
        sendBusyToCTI();
        CTISearch = true;
      }
    }
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
  
  protected void sendReadyToCTI() {
    if (!CTISocketManager.getInstance(this).isCTIEnabled()) {
      log.debug("CTI Not enabled. sendReadyToCTI Ignored");
      return;
    }
    
    CTISocketManager ctiManager = CTISocketManager.getInstance(this);
    try {
      ctiManager.sendMessage("READY");
    } catch (Exception ex) {
      log.debug("Error sending READY to Switch. Error:" + ex.getLocalizedMessage());
    }
    ctiManager.setCSRState(true);
    return;
  }
  
  private void fireCTIAction(final String inputFromSwitch) {
    if (!CTISocketManager.getInstance(this).isCTIEnabled()) {
      log.debug("CTI Not enabled. fireCTIAction ignored");
      return;
    }
    Thread myThread = new Thread(new Runnable() {
      
      public void run() {
        System.out.println("Thread run");
        sendBusyToCTI();
        int div = inputFromSwitch.indexOf("|")+1;
        if (div == 1){
          System.out.println("Invalid Search String "+inputFromSwitch);
          return;
        }
        String searchInfo = inputFromSwitch.substring(div);
        int eq = searchInfo.indexOf("=");
        if (eq == 0) {
          System.out.println("Invalid Search String "+inputFromSwitch);
          return;
        }
        String searchType = searchInfo.substring(0,eq);
        String searchValue = searchInfo.substring(eq+1);
        Map pMap = new HashMap();
        pMap.put("action","acct-summary");
        //
        pMap.put(searchType,searchValue);
        if (searchType.equals("AccountExternalId")) {
          pMap.put("AccountExternalIdType","1");
        }
        IContext ctx = ctxFinder.findContext();
        if (ctx == null) {
          return;
        }
        IPersistentCollection coll = ctx.getCollection("AccountLocateList", null);
        if (coll == null) {
          return;
        }
        coll.resetFilterCriteria();
        coll.setAttributeData(searchType,searchValue);
        if (searchType.equals( "AccountExternalId") ){
          coll.setAttributeData("AccountExternalIdType", "1");
        }
        coll.reset();
        switch (coll.getCount()) {
          case 0:
            sendReadyToCTI();
            velAccountLocateList.setVelocityTemplateUri("template/AccountLocate.vm");
            velAccountLocateList.getSession().remove("page");
            ctxFinder.initControls();
            break;
          case 1:
            boolean ret = ctxFinder.fireActionForObject("cti-action-router", pMap);
            break;
          default:
            sendBusyToCTI();
            velAccountLocateList.setVelocityTemplateUri("template/AccountLocate.vm");
            velAccountLocateList.getSession().remove("page");
            ctxFinder.initControls();
            break;
        }
      }
    });
    myThread.start();
  }
  
  protected void DetailHyperlinkFilter(javax.swing.event.HyperlinkEvent evt,CSGVelocityHTMLEP htmlControl, String url) {
    
    // get url from hyperlink click
    if (url == null){
      url = evt.getURL().toString();
    }
    // create a hashmap containing the url params
    HashMap params = parseURLtoMap(url);
    
    // get object name from map, i.e. Payment, InvoiceDetail, BilledUsage, ...
    String action = (String) params.get("action");
    String object = (String) params.get("object");
    String subtype = (String) params.get("subtype");
    String type = "normal";
    if (params.get("type")!=null){
      type = (String) params.get("type");
    }
    
    if (type.equals("paging")) {
      // screen pagination
      String page = params.get("page").toString();
      
      // put the page selected into the session velocity context object
      htmlControl.getSession().putValue("page",Integer.valueOf(page));
      
      // refresh html detail control with new or modified adjustment list
      htmlControl.initializeControl();
      
    } else {
      
      // index of selected object within collection
      String ind = (String) params.get("index");
      int index = -1;
      if (ind != null) {
        index = Integer.parseInt(ind);
      }
      
      // get this panel's context
      IContext ctx = ctxFinder.findContext();
      if (ctx != null) {
        // found context
        IPersistentObject accountLocate = null;
        IPersistentObject account = null;
        if (index >= 0) {
          // we need to get the object out of a collection using the index
          
          // Create collection of Account Locate to peek inside and get selected object knowing the index
          IPersistentCollection coll = (IPersistentCollection) ctx.getObject("AccountLocateList",null);
          
          if (coll.getCount() > 0){
            accountLocate=coll.getAt(index);
            ctx.addTopic(accountLocate);
          }
          
          // Add the topic so that it will be retrievable by the workflow manager
          // when it attempts to get "account, null" from the context
          // for setting on to bigfoot, it will find it now that the Account reference
          // element on AccountLocate has a different subtype.
          account = (IPersistentObject) accountLocate.getObject("Account", "AccountLocate");
          if (account.getAttributeData("InUse") != null) {
            JOptionPane.showMessageDialog(this, ResourceManager.getString("CC-4-1") );
            OrderManager.getInstance().clearCurrentOrder(); // Clear order created by Unmarshall of Account
            return;
          }
          
          // If we are selecting by OrderNumber, set this onto the collection to be used later
          // If not, make sure we clear the selection
          IPersistentCollection orderList = account.getCollection("OrderList", "Account");
          if (orderList != null) {
            if (coll.getAttributeData("OrderNumber") != null) {
              orderList.setAttributeData("SelectedOrderId", coll.getAttributeData("OrderNumber"));
            } else {
              orderList.setAttributeDataAsLong("SelectedOrderId", 0);
            }
          }
          ctx.addTopic(account);
        }
        // display panel
        ctxFinder.fireActionForObject(action, account);
      }
    }
    
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        lblAccountExternalId = new com.csgsystems.igpa.controls.CSGLabel();
        lblBillLname = new com.csgsystems.igpa.controls.CSGLabel();
        lblBillCompany = new com.csgsystems.igpa.controls.CSGLabel();
        lblCustPhone1 = new com.csgsystems.igpa.controls.CSGLabel();
        lblCustPhone2 = new com.csgsystems.igpa.controls.CSGLabel();
        lblAccountExternalIdType = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceExternalIdType = new com.csgsystems.igpa.controls.CSGLabel();
        lblServiceExternalId = new com.csgsystems.igpa.controls.CSGLabel();
        lblOrderNumber = new com.csgsystems.igpa.controls.CSGLabel();
        edtAccountExternalId = new com.csgsystems.igpa.controls.CSGEdit();
        edtBillLname = new com.csgsystems.igpa.controls.CSGEdit();
        edtBillCompany = new com.csgsystems.igpa.controls.CSGEdit();
        edtCustPhone1 = new com.csgsystems.igpa.controls.CSGEdit();
        edtCustPhone2 = new com.csgsystems.igpa.controls.CSGEdit();
        cbAccountExternalIdType = new com.csgsystems.igpa.controls.CSGComboBox();
        cbServiceExternalIdType = new com.csgsystems.igpa.controls.CSGComboBox();
        edtServiceExternalId = new com.csgsystems.igpa.controls.CSGEdit();
        edtOrderNumber = new com.csgsystems.igpa.controls.CSGEdit();
        jPanel2 = new javax.swing.JPanel();
        btnReset = new com.csgsystems.igpa.controls.CSGResetButton();
        btnSearch = new com.csgsystems.igpa.controls.CSGSearchButton();
        btnCancel = new com.csgsystems.igpa.controls.CSGButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        velAccountLocateList = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();

        setLayout(new java.awt.BorderLayout());

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setMinimumSize(new java.awt.Dimension(995, 670));
        setPreferredSize(new java.awt.Dimension(995, 670));
        jSplitPane1.setDividerLocation(350);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(950, 800));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(950, 800));
        jPanel1.setLayout(null);

        jPanel1.setMinimumSize(new java.awt.Dimension(350, 450));
        jPanel1.setPreferredSize(new java.awt.Dimension(350, 450));
        lblAccountExternalId.setLocalizationKey("AccountLocate.label.AccountExternalId");
        lblAccountExternalId.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(lblAccountExternalId);
        lblAccountExternalId.setBounds(0, 110, 163, 20);

        lblBillLname.setLocalizationKey("AccountLocate.label.BillLname");
        lblBillLname.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(lblBillLname);
        lblBillLname.setBounds(0, 150, 163, 20);

        lblBillCompany.setLocalizationKey("AccountLocate.label.BillCompany");
        lblBillCompany.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(lblBillCompany);
        lblBillCompany.setBounds(0, 180, 163, 20);

        lblCustPhone1.setLocalizationKey("AccountLocate.label.CustPhone1");
        lblCustPhone1.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(lblCustPhone1);
        lblCustPhone1.setBounds(0, 210, 163, 20);

        lblCustPhone2.setLocalizationKey("AccountLocate.label.CustPhone2");
        lblCustPhone2.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(lblCustPhone2);
        lblCustPhone2.setBounds(0, 240, 163, 20);

        lblAccountExternalIdType.setLocalizationKey("AccountLocate.label.AccountExternalIdType");
        lblAccountExternalIdType.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(lblAccountExternalIdType);
        lblAccountExternalIdType.setBounds(0, 80, 163, 20);

        lblServiceExternalIdType.setLocalizationKey("AccountLocate.label.ServiceExternalIdType");
        lblServiceExternalIdType.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(lblServiceExternalIdType);
        lblServiceExternalIdType.setBounds(0, 10, 163, 20);

        lblServiceExternalId.setLocalizationKey("AccountLocate.label.ServiceExternalId");
        lblServiceExternalId.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(lblServiceExternalId);
        lblServiceExternalId.setBounds(0, 40, 163, 20);

        lblOrderNumber.setLocalizationKey("AccountLocate.label.OrderNumber");
        lblOrderNumber.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(lblOrderNumber);
        lblOrderNumber.setBounds(0, 280, 163, 20);

        edtAccountExternalId.setAtomName("AccountExternalId");
        edtAccountExternalId.setDomainName("AccountLocateList");
        edtAccountExternalId.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(edtAccountExternalId);
        edtAccountExternalId.setBounds(170, 110, 173, 20);

        edtBillLname.setAttributeName("BillLnameFind");
        edtBillLname.setDomainName("AccountLocateList");
        edtBillLname.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(edtBillLname);
        edtBillLname.setBounds(170, 150, 173, 20);

        edtBillCompany.setAttributeName("BillCompanyFind");
        edtBillCompany.setDomainName("AccountLocateList");
        edtBillCompany.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(edtBillCompany);
        edtBillCompany.setBounds(170, 180, 173, 20);

        edtCustPhone1.setAtomName("CustPhone1");
        edtCustPhone1.setDomainName("AccountLocateList");
        edtCustPhone1.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(edtCustPhone1);
        edtCustPhone1.setBounds(170, 210, 173, 20);

        edtCustPhone2.setAtomName("CustPhone2");
        edtCustPhone2.setDomainName("AccountLocateList");
        edtCustPhone2.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(edtCustPhone2);
        edtCustPhone2.setBounds(170, 240, 173, 20);

        cbAccountExternalIdType.setAttributeName("AccountExternalIdType");
        cbAccountExternalIdType.setDomainName("AccountLocateList");
        cbAccountExternalIdType.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(cbAccountExternalIdType);
        cbAccountExternalIdType.setBounds(170, 80, 173, 20);

        cbServiceExternalIdType.setAttributeName("ServiceExternalIdType");
        cbServiceExternalIdType.setDomainName("AccountLocateList");
        cbServiceExternalIdType.setMinimumSize(new java.awt.Dimension(140, 20));
        cbServiceExternalIdType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbServiceExternalIdTypeActionPerformed(evt);
            }
        });

        jPanel1.add(cbServiceExternalIdType);
        cbServiceExternalIdType.setBounds(170, 10, 173, 20);

        edtServiceExternalId.setAtomName("ServiceExternalId");
        edtServiceExternalId.setDomainName("AccountLocateList");
        edtServiceExternalId.setMinimumSize(new java.awt.Dimension(140, 20));
        edtServiceExternalId.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                edtServiceExternalIdFocusLost(evt);
            }
        });

        jPanel1.add(edtServiceExternalId);
        edtServiceExternalId.setBounds(170, 40, 173, 20);

        edtOrderNumber.setAttributeName("OrderNumber");
        edtOrderNumber.setDomainName("AccountLocateList");
        edtOrderNumber.setMinimumSize(new java.awt.Dimension(140, 20));
        jPanel1.add(edtOrderNumber);
        edtOrderNumber.setBounds(170, 280, 173, 20);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        btnReset.setLocalizationKey("AccountLocateFilter.Reset");
        btnReset.setMaximumSize(new java.awt.Dimension(160, 23));
        btnReset.setMinimumSize(new java.awt.Dimension(100, 23));
        btnReset.setPreferredSize(new java.awt.Dimension(100, 23));
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(btnReset, gridBagConstraints);

        btnSearch.setLocalizationKey("AccountLocateFilter.Search");
        btnSearch.setMinimumSize(new java.awt.Dimension(100, 23));
        btnSearch.setPreferredSize(new java.awt.Dimension(100, 23));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(btnSearch, gridBagConstraints);

        btnCancel.setLocalizationKey("AccountLocate.btnCancel");
        btnCancel.setMaximumSize(new java.awt.Dimension(75, 25));
        btnCancel.setMinimumSize(new java.awt.Dimension(100, 23));
        btnCancel.setPreferredSize(new java.awt.Dimension(100, 23));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(btnCancel, gridBagConstraints);

        jPanel1.add(jPanel2);
        jPanel2.setBounds(3, 535, 342, 31);

        jLabel1.setMinimumSize(new java.awt.Dimension(140, 20));
        jLabel1.setPreferredSize(new java.awt.Dimension(140, 20));
        jPanel1.add(jLabel1);
        jLabel1.setBounds(14, 159, 140, 20);

        jLabel2.setMinimumSize(new java.awt.Dimension(140, 20));
        jLabel2.setPreferredSize(new java.awt.Dimension(140, 20));
        jPanel1.add(jLabel2);
        jLabel2.setBounds(14, 237, 140, 20);

        jPanel1.add(jLabel3);
        jLabel3.setBounds(0, 0, 0, 0);

        jSplitPane1.setLeftComponent(jPanel1);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel3.setMinimumSize(new java.awt.Dimension(450, 450));
        jPanel3.setPreferredSize(new java.awt.Dimension(450, 450));
        velAccountLocateList.setVelocityTemplateUri("template/AccountLocate_Links.vm");
        jPanel3.add(velAccountLocateList, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel3);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents
  
    private void edtServiceExternalIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_edtServiceExternalIdFocusLost
      edtServiceExternalId.initializeControl();
      cbServiceExternalIdType.initializeControl();
    }//GEN-LAST:event_edtServiceExternalIdFocusLost
    
    private void cbServiceExternalIdTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbServiceExternalIdTypeActionPerformed
      edtServiceExternalId.initializeControl();
      cbServiceExternalIdType.initializeControl();
    }//GEN-LAST:event_cbServiceExternalIdTypeActionPerformed
    
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
      ctxFinder.fireEvent("Cancel");
        /*
        if (CTISocketManager.getInstance(this).isCTIEnabled()) {
          sendBusyToCTI();
        }
         */
    }//GEN-LAST:event_btnCancelActionPerformed
    
    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
      sendReadyToCTI();
      velAccountLocateList.setVelocityTemplateUri("template/AccountLocate_Links.vm");
      velAccountLocateList.getSession().remove("page");
      velAccountLocateList.initializeControl();
    }//GEN-LAST:event_btnResetActionPerformed
    
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
      CTISearch = false;
      System.out.println("*** Search button Clicked");
      velAccountLocateList.setVelocityTemplateUri("template/AccountLocate.vm");
      velAccountLocateList.getSession().remove("page");
      //velAccountLocateList.initializeControl ();      
    }//GEN-LAST:event_btnSearchActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.csgsystems.igpa.controls.CSGButton btnCancel;
    private com.csgsystems.igpa.controls.CSGResetButton btnReset;
    private com.csgsystems.igpa.controls.CSGSearchButton btnSearch;
    private com.csgsystems.igpa.controls.CSGComboBox cbAccountExternalIdType;
    private com.csgsystems.igpa.controls.CSGComboBox cbServiceExternalIdType;
    private com.csgsystems.igpa.controls.CSGEdit edtAccountExternalId;
    private com.csgsystems.igpa.controls.CSGEdit edtBillCompany;
    private com.csgsystems.igpa.controls.CSGEdit edtBillLname;
    private com.csgsystems.igpa.controls.CSGEdit edtCustPhone1;
    private com.csgsystems.igpa.controls.CSGEdit edtCustPhone2;
    private com.csgsystems.igpa.controls.CSGEdit edtOrderNumber;
    private com.csgsystems.igpa.controls.CSGEdit edtServiceExternalId;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSplitPane jSplitPane1;
    private com.csgsystems.igpa.controls.CSGLabel lblAccountExternalId;
    private com.csgsystems.igpa.controls.CSGLabel lblAccountExternalIdType;
    private com.csgsystems.igpa.controls.CSGLabel lblBillCompany;
    private com.csgsystems.igpa.controls.CSGLabel lblBillLname;
    private com.csgsystems.igpa.controls.CSGLabel lblCustPhone1;
    private com.csgsystems.igpa.controls.CSGLabel lblCustPhone2;
    private com.csgsystems.igpa.controls.CSGLabel lblOrderNumber;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceExternalId;
    private com.csgsystems.igpa.controls.CSGLabel lblServiceExternalIdType;
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velAccountLocateList;
    // End of variables declaration//GEN-END:variables
}