package com.maxis.panel;

import java.util.*;

import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.igpa.utils.ContextFinder;
import com.csgsystems.igpa.controls.CSGVelocityHTMLEP;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.domain.arbor.businessobject.Item;
import com.csgsystems.domain.arbor.order.*;
import com.csgsystems.error.IError;
import com.csgsystems.igpa.forms.ContextFormListener;
import com.csgsystems.igpa.forms.ContextFormEvent;
import com.csgsystems.localization.ResourceManager;

import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.*;
//Edited by Lee Wynn, Teoh (20/05/2005)
import com.csgsystems.bp.subsystems.*;
/**
 *
 * @author  schp01
 */
public class MXS_InventorySearch extends AbstractHTMLPanel implements ContextFormListener,KeyListener {
  //Added by Lee Wynn, Teoh (24/5/2005)
  private int selectedIndex;
  protected ContextFinder ctxFinder = new ContextFinder(this);
  protected JPopupMenu availableActionsMenu = null;
  protected JPopupMenu reservedActionsMenu = null;
  protected JPopupMenu assignedActionsMenu = null;
  protected String url = null;
  protected int xpos = 0;
  protected int ypos = 0;
  protected IAttribute iAtt;
  
  public MXS_InventorySearch() {
    initComponents();
    
    
    htmlInventoryLineList.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
      public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
          DetailHyperlinkFilter(evt, htmlInventoryLineList);
          setSelectedRow(evt,htmlInventoryLineList,null);
          setInventoryLineId();
        }
      }
    });
    
        /* **************************************
         * AVAILABLE
         ***************************************** */
    // add listener to get mouse position
    htmlAvailableInventory.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        int containerX = new Double(((JEditorPane)evt.getSource()).getBounds().getX()).intValue();
        int containerY = new Double(((JEditorPane)evt.getSource()).getBounds().getY()).intValue();
        xpos = evt.getX() + containerX;
        ypos = evt.getY() + containerY;
      }
    });
    // add event handler to respond to click event
    htmlAvailableInventory.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
      public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
        String newURL = null;
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
          //display popup menu with reserve, assign
          String linktype = getLinkType(evt);
          if (linktype.equals("popup")){
            availableActionsMenu.show(htmlAvailableInventory, xpos, ypos);
          }else{
            DetailHyperlinkFilter(evt, htmlAvailableInventory, newURL);
          }
        }else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED){
          //display popup menu with reserve, assign
          String linktype = getLinkType(evt);
          if (linktype.equals("popup")){
            availableActionsMenu.show(htmlAvailableInventory, xpos, ypos);
          }
        }else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED){
          if (availableActionsMenu != null) {
            availableActionsMenu.setVisible(false);
          }
        }
      }
    });
    
    
        /* **************************************
         * RESERVED
         ***************************************** */
    // add listener to get mouse position
    htmlReservedInventory.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        int containerX = new Double(((JEditorPane)evt.getSource()).getBounds().getX()).intValue();
        int containerY = new Double(((JEditorPane)evt.getSource()).getBounds().getY()).intValue();
        xpos = evt.getX() + containerX;
        ypos = evt.getY() + containerY;
      }
    });
    // add event handler to respond to click event
    htmlReservedInventory.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
      public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
        String newURL = null;
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
          //display popup menu with assign, unreserve
          String linktype = getLinkType(evt);
          if (linktype.equals("popup")){
            // create popup menu for reserved inventory "actions" link for reserve, assign
            createReservedMenu(evt);
            reservedActionsMenu.show(htmlReservedInventory, xpos, ypos);
          }else{
            DetailHyperlinkFilter(evt, htmlReservedInventory, newURL);
          }
        }else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED){
          //display popup menu with assign, unreserve
          String linktype = getLinkType(evt);
          if (linktype.equals("popup")){
            // create popup menu for reserved inventory "actions" link for reserve, assign
            createReservedMenu(evt);
            reservedActionsMenu.show(htmlReservedInventory, xpos, ypos);
          }
        }else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED){
          if (reservedActionsMenu != null) {
            reservedActionsMenu.setVisible(false);
          }
        }
      }
    });
    
    
    
        /* **************************************
         * ASSIGNED
         ***************************************** */
    // add listener to get mouse position
    htmlAssignedInventory.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        int containerX = new Double(((JEditorPane)evt.getSource()).getBounds().getX()).intValue();
        int containerY = new Double(((JEditorPane)evt.getSource()).getBounds().getY()).intValue();
        xpos = evt.getX() + containerX;
        ypos = evt.getY() + containerY;
      }
    });
    // add event handler to respond to click event
    htmlAssignedInventory.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
      public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
        String newURL = null;
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
          //display popup menu with swap, unassign
          String linktype = getLinkType(evt);
          if (linktype.equals("popup")){
            // create popup menu for assigned inventory "actions" link for unassign, swap
            createAssignedMenu(evt);
            assignedActionsMenu.show(htmlAssignedInventory, xpos, ypos);
          }else{
            DetailHyperlinkFilter(evt, htmlAssignedInventory, newURL);
          }
        }else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED){
          //display popup menu with swap assign
          String linktype = getLinkType(evt);
          if (linktype.equals("popup")){
            // create popup menu for assigned inventory "actions" link for unassign, swap
            createAssignedMenu(evt);
            assignedActionsMenu.show(htmlAssignedInventory, xpos, ypos);
          }
        }else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED){
          if (assignedActionsMenu!=null){
            assignedActionsMenu.setVisible(false);
          }
        }
      }
    });
    htmlAssignedInventory.addExtraVelocityContextTopic("OrderManager", OrderManager.getInstance());
    
    // create popup menu for available inventory "actions" link for reserve, assign
    createAvailableMenu();
    
    htmlAvailableInventory.getSession().putValue("clear-screen",Boolean.TRUE);
    htmlAvailableInventory.getSession().putValue("account-reserve-screen",Boolean.FALSE);
    htmlReservedInventory.getSession().putValue("account-reserve-screen",Boolean.FALSE);
    this.edtInventoryId.addKeyListener(this);
    
  }
  
  private void populateAttribute() {
    IContext ctxer = ctxFinder.findContext();
    if(ctxFinder!=null) {
      IPersistentCollection persObj = (IPersistentCollection)ctxer.getObject("InvElementList","Available");
      iAtt = persObj.getAttribute("SalesChannelId");
    }
  }
  
  private String getLinkType(javax.swing.event.HyperlinkEvent evt){
    // parse the url to get the type
    url = evt.getURL().toString();
    HashMap params = parseURLtoMap(url);
    String linktype = "normal";
    if (params.get("linktype")!=null){
      linktype = (String) params.get("linktype");
    }
    return linktype;
  }
  
  private void createAvailableMenu() {
    availableActionsMenu = new JPopupMenu();
    
    // reserve
    JMenuItem menuItem = new JMenuItem(ResourceManager.getString("InventorySearch.menuitem.Reserve"));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DetailHyperlinkFilter(null,htmlAvailableInventory, url + "&type=reserve");
      }
    });
    availableActionsMenu.add(menuItem);
    
    // assign
    menuItem = new JMenuItem(ResourceManager.getString("InventorySearch.menuitem.Assign"));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DetailHyperlinkFilter(null,htmlAvailableInventory, url + "&type=reserve-and-assign");
      }
    });
    availableActionsMenu.add(menuItem);
  }
  
  private void createReservedMenu(javax.swing.event.HyperlinkEvent evt) {
    reservedActionsMenu = new JPopupMenu();
    
    url = evt.getURL().toString();
    HashMap params = parseURLtoMap(url);
    String aging = "false";
    if (params.get("aging")!=null){
      aging = (String) params.get("aging");
    }
    String customerOwned = "false";
    if (params.get("customerOwned")!=null){
      customerOwned = (String) params.get("customerOwned");
    }
    JMenuItem menuItem = null;
    
    // aging inventory we can't reserve
    if (aging.equals("false") && customerOwned.equals("false")){
      
      // unreserve
      menuItem = new JMenuItem(ResourceManager.getString("InventorySearch.menuitem.Unreserve"));
      menuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          DetailHyperlinkFilter(null,htmlReservedInventory, url + "&type=unreserve");
        }
      });
      reservedActionsMenu.add(menuItem);
    }
    
    // assign
    menuItem = new JMenuItem(ResourceManager.getString("InventorySearch.menuitem.Assign"));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DetailHyperlinkFilter(null,htmlReservedInventory, url + "&type=assign");
      }
    });
    reservedActionsMenu.add(menuItem);
  }
  
  private void createAssignedMenu(javax.swing.event.HyperlinkEvent evt) {
    assignedActionsMenu = new JPopupMenu();
    
    url = evt.getURL().toString();
    HashMap params = parseURLtoMap(url);
    int itemAction = 0;
    if (params.get("itemAction")!=null){
      itemAction = Integer.parseInt(params.get("itemAction").toString());
    }
    
    if (itemAction == Item.ACTION_ADD) {
      // cancel assignment
      JMenuItem menuItem = new JMenuItem(ResourceManager.getString("InventorySearch.menuitem.UndoAssignment"));
      menuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          DetailHyperlinkFilter(null,htmlAssignedInventory, url + "&type=cancel-assignment");
        }
      });
      assignedActionsMenu.add(menuItem);
      
    } else {
      // swap
      JMenuItem menuItem = new JMenuItem(ResourceManager.getString("InventorySearch.menuitem.Swap"));
      menuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          DetailHyperlinkFilter(null,htmlAssignedInventory, url + "&type=swap&action=service-inventory-swap");
        }
      });
      assignedActionsMenu.add(menuItem);
      
      // unassign
      menuItem = new JMenuItem(ResourceManager.getString("InventorySearch.menuitem.Unassign"));
      menuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          DetailHyperlinkFilter(null,htmlAssignedInventory, url + "&type=unassign&action=service-inventory-unassign");
        }
      });
      assignedActionsMenu.add(menuItem);
    }
    // return
        /* in fx 1.1 or 2.0
        menuItem = new JMenuItem("Return");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,htmlAssignedInventory, url + "&type=return");
            }
        });
        assignedActionsMenu.add(menuItem);
         */
  }
  
  public void contextFormStateChanged(ContextFormEvent evt){
    // *********************
    // set defaults after screen is loaded for the first time (Post_open if it is a dialog, Ready if it is a view)
    // *********************
    if (evt.getType() == ContextFormEvent.POST_OPEN){
      // set search button as default
      getRootPane().setDefaultButton(btnSearch);
      
      // select default inventory line row
      IContext ctx = ctxFinder.findContext();
      if (ctx != null) {
        IPersistentCollection inventoryLineList = (IPersistentCollection) ctx.getObject("EmfConfigurationInventoryLineList", null);
        if (inventoryLineList != null){
          int count = inventoryLineList.getCount();
          if (count >= 0){
            // select 1st row in grid
            htmlInventoryLineList.getSession().putValue("selectedObject","EmfConfigurationInventoryLine.0");
            
            // get object and add to context
            IPersistentObject line= inventoryLineList.getAt(0);
            ctx.addTopic(line);
          }
          htmlInventoryLineList.initializeControl();
          setInventoryLineId();
        }
      }
    }
  }
  
  protected void DetailHyperlinkFilter(javax.swing.event.HyperlinkEvent evt,CSGVelocityHTMLEP htmlControl, String url) {
    
    // get url from hyperlink click
    if (url == null){
      url = evt.getURL().toString();
    }
    
    // create a hashmap containing the url params
    HashMap params = parseURLtoMap(url);
    String action = (String) params.get("action");
    String object = (String) params.get("object");
    String subtype = null;
    if (params.get("subtype")!=null){
      subtype = (String) params.get("subtype");
    }
    String type = "normal";
    if (params.get("type")!=null){
      type = (String) params.get("type");
    }
    // index of selected object within collection
    String ind = (String) params.get("index");
    int index = -1;
    if (ind != null) {
      index = Integer.parseInt(ind);
    }
    
    if (type.equals("paging")) {
      // screen pagination
      String page = params.get("page").toString();
      
      // put the page selected into the session velocity context object
      htmlControl.getSession().putValue("page",Integer.valueOf(page));
      
      // refresh html detail control with new or modified adjustment list
      htmlControl.initializeControl();
      
    }else{
      
      // get this panel's context
      IContext ctx = ctxFinder.findContext();
      if (ctx != null) {
        
        // Create collection of inventory to peek inside and get selected object knowing the index
        IPersistentCollection invElementList = (IPersistentCollection) ctx.getObject(object + "List",subtype);
        IPersistentObject invElement = null;
        IPersistentObject service = ctx.getObject("Service",null);
        
        IPersistentCollection availableInventory = (IPersistentCollection)ctx.getObject("InvElementList","Available");
        
        if (index >= 0 && object!= null) {
          // we need to get the object out of a collection using the index
          
          if (invElementList != null){
            if (invElementList.getCount() > 0){
              invElement=invElementList.getAt(index);
              if (invElement != null){
                
                // add the topic
                ctx.addTopic(invElement,false);
              }
            }
          }
          
        }
        
        
        if (type.equals("select-external-id-type")) {
          ctxFinder.fireActionForObject(action, invElement);
          htmlAssignedInventory.initializeControl();
        }
        
        if (type.equals("view")) {
          ctxFinder.fireActionForObject(action, invElement);
          htmlAssignedInventory.initializeControl();
        }
        
        if (type.equals("add")) {
          // add customer owned inventory item
          IPersistentObject emfConfigurationInventoryLine = ctx.getObject("EmfConfigurationInventoryLine",null);
          Map pMap = new java.util.HashMap();
          if (service != null && emfConfigurationInventoryLine != null){
            pMap.put("Service", service);
            pMap.put("EmfConfigurationInventoryLine", emfConfigurationInventoryLine);
            ctxFinder.fireActionForObject(action, pMap);
          }
          
          // reset assigned inventory
          htmlAssignedInventory.initializeControl();
        }
        
        if (type.equals("reserve") ) {
          // reserve inventory item
          IPersistentObject account = ctx.getObject("Account",null);
          if (account != null && invElement != null){
            if (availableInventory!=null){
              // set reservation end date
              if (availableInventory.getAttributeDataAsBoolean("UseReservationEndDate")){
                invElement.setAttributeData("EndDateTime",availableInventory.getAttributeData("ReservationEndDate"));
              }
            }
            
            invElement.setAttributeData("ParamA",account.getAttributeData("AccountInternalId"));
            
            // associate the inventory to the account object which performs the reserve
            if (account.addAssociation(invElement)){
              
              // element in collection has been reserved or assigned
              if (invElement.getAttribute("TopLevelContainerId").isEmpty()) {
                invElement.setAttributeData("VirtualIsAvailable",Boolean.FALSE);
              } else {
                int containerId = invElement.getAttributeDataAsInteger("TopLevelContainerId");
                for (Iterator i = availableInventory.iterator(); i.hasNext(); ) {
                  IPersistentObject obj = (IPersistentObject)i.next();
                  if (obj.getAttributeDataAsInteger("TopLevelContainerId") == containerId){
                    obj.setAttributeData("VirtualIsAvailable",Boolean.FALSE);
                  }
                }
              }
              // refresh available inventory list velocity template to remove the actions link on the item just reserved
              htmlAvailableInventory.initializeControl();
              
              // refresh reserved inventory list velocity template
              htmlReservedInventory.initializeControl();
            }else{
              IError err = account.getError();
              if (err != null){
                ctxFinder.displayHTMLError(err);
              }
            }
          }
          
        }
        
        if (type.equals("assign") || type.equals("reserve-and-assign")) {
          // assign inventory to service
          if (service != null && invElement != null){
            // associate the inventory to the service object which performs the assign
            if (service.addAssociation(invElement)){
              
              // element in collection has been reserved or assigned
              if (invElement.getAttribute("TopLevelContainerId").isEmpty()) {
                invElement.setAttributeData("VirtualIsAvailable",Boolean.FALSE);
              } else {
                int containerId = invElement.getAttributeDataAsInteger("TopLevelContainerId");
                for (Iterator i = availableInventory.iterator(); i.hasNext(); ) {
                  IPersistentObject obj = (IPersistentObject)i.next();
                  if (obj.getAttributeDataAsInteger("TopLevelContainerId") == containerId){
                    obj.setAttributeData("VirtualIsAvailable",Boolean.FALSE);
                  }
                }
              }
              // refresh available inventory list velocity template to remove the actions link on the item just reserved
              htmlAvailableInventory.initializeControl();
              
              // if item is in the reserved collection
              if (type.equals("assign")){
                IPersistentObject account = ctx.getObject("Account",null);
                if (account != null){
                  IPersistentCollection reservedInventory = account.getCollection("InvElementList","Reserved");
                  if (reservedInventory!=null){
                    reservedInventory.reset();
                    
                    if (htmlReservedInventory.getSession().get("page") != null) {
                      int page = Integer.valueOf(htmlReservedInventory.getSession().get("page").toString()).intValue();
                      if (page > 1) {
                        if (((page-1)*reservedInventory.getAttributeDataAsInteger("DisplayRowsPerPage")+1)>reservedInventory.getCount()) {
                          htmlReservedInventory.getSession().putValue("page", new Integer(page-1));
                        }
                      }
                    }
                  }
                }
                // refresh reserved inventory list velocity template
                htmlReservedInventory.initializeControl();
              }
              // refresh assigned inv list
              htmlAssignedInventory.initializeControl();
            }else{
              IError err = service.getError();
              if (err != null){
                ctxFinder.displayHTMLError(err);
              }
            }
          }
          
        }
        
        if (type.equals("unreserve")) {
          // unreserve inventory from account
          IPersistentObject account = ctx.getObject("Account",null);
          if (account != null && invElement != null){
            // unassociate the inventory to the account object which performs the assign
            if (account.removeAssociation(invElement)){
              
              IPersistentCollection reservedInventory = account.getCollection("InvElementList","Reserved");
              if (reservedInventory!=null){
                if (htmlReservedInventory.getSession().get("page") != null) {
                  int page = Integer.valueOf(htmlReservedInventory.getSession().get("page").toString()).intValue();
                  if (page > 1) {
                    if (((page-1)*reservedInventory.getAttributeDataAsInteger("DisplayRowsPerPage")+1)>reservedInventory.getCount()) {
                      htmlReservedInventory.getSession().putValue("page", new Integer(page-1));
                    }
                  }
                }
              }
              
              // refresh reserved inventory list velocity template
              htmlReservedInventory.initializeControl();
              
              // refresh available inventory list velocity template to add the actions link
              availableInventory.reset();
              if (htmlAvailableInventory.getSession().get("page") != null) {
                int page = Integer.valueOf(htmlAvailableInventory.getSession().get("page").toString()).intValue();
                if (page > 1) {
                  if (((page-1)*availableInventory.getAttributeDataAsInteger("DisplayRowsPerPage")+1)>availableInventory.getCount()) {
                    htmlAvailableInventory.getSession().putValue("page", new Integer(1));
                  }
                }
              }
              htmlAvailableInventory.initializeControl();
              
            }else{
              IError err = account.getError();
              if (err != null){
                ctxFinder.displayHTMLError(err);
              }
            }
          }
        }
        
        if (type.equals("cancel-assignment")) {
          // unassign inventory
          if (service != null && invElement != null){
            
            IPersistentObject orderItem = OrderManager.getInstance().getOrderItem(invElement);
            if (orderItem != null && orderItem.isNewObject() == false) {
              markItemForCancellation(orderItem);
              htmlAssignedInventory.initializeControl();
            } else {
              // set subtype so localRemoveAssociation code in service knows to cancel instead of unassign
              invElement.setSubtype("cancelView");
              
              // unassociate the inventory to the service object which performs the cancel of view
              if (service.removeAssociation(invElement)){
                
                IPersistentCollection assignedInventory = service.getCollection("InvElementList","Service");
                if (assignedInventory!=null){
                  if (htmlAssignedInventory.getSession().get("page") != null) {
                    int page = Integer.valueOf(htmlAssignedInventory.getSession().get("page").toString()).intValue();
                    if (page > 1) {
                      if (((page-1)*assignedInventory.getAttributeDataAsInteger("DisplayRowsPerPage")+1)>assignedInventory.getCount()) {
                        htmlAssignedInventory.getSession().putValue("page", new Integer(page-1));
                      }
                    }
                  }
                }
                
                // refresh assigned inventory list velocity template
                htmlAssignedInventory.initializeControl();
                
                // refresh reserved inventory list velocity template
                IPersistentObject account = ctx.getObject("Account",null);
                IPersistentCollection reservedInventory = null;
                if (account != null){
                  reservedInventory = account.getCollection("InvElementList","Reserved");
                  if (reservedInventory!=null){
                    reservedInventory.reset();
                  }
                }
                htmlReservedInventory.initializeControl();
                
                
                // set back to available so actions link appears in available list
                if (availableInventory != null){
                  IPersistentObject obj = null;
                  int inventoryId = invElement.getAttributeDataAsInteger("InventoryId");
                  int inventoryIdResets = invElement.getAttributeDataAsInteger("InventoryIdResets");
                  int topLevelContainerId = invElement.getAttributeDataAsInteger("TopLevelContainerId");
                  
                  boolean bIsNowReserved = false;
                  if (reservedInventory != null) {
                    for (Iterator i = reservedInventory.iterator(); bIsNowReserved == false && i.hasNext(); ) {
                      obj = (IPersistentObject)i.next();
                      if (obj.getAttributeDataAsInteger("InventoryId") == inventoryId &&
                      obj.getAttributeDataAsInteger("InventoryIdResets") == inventoryIdResets){
                        bIsNowReserved = true;
                        break;
                      }
                    }
                  }
                  
                  if (bIsNowReserved == false) {
                    for (Iterator i = availableInventory.iterator(); i.hasNext(); ){
                      obj = (IPersistentObject)i.next();
                      if (topLevelContainerId>0) {
                        if (obj.getAttributeDataAsInteger("TopLevelContainerId") == topLevelContainerId) {
                          obj.setAttributeData("VirtualIsAvailable",Boolean.TRUE);
                        }
                      } else {
                        if (obj.getAttributeDataAsInteger("InventoryId") == inventoryId &&
                        obj.getAttributeDataAsInteger("InventoryIdResets") == inventoryIdResets) {
                          obj.setAttributeData("VirtualIsAvailable",Boolean.TRUE);
                        }
                      }
                    }
                  }
                }
                
                // refresh available inventory list velocity template to add the actions link
                htmlAvailableInventory.initializeControl();
              }else{
                IError err = service.getError();
                if (err != null){
                  ctxFinder.displayHTMLError(err);
                }
              }
            }
          }
        }
        
        if (type.equals("unassign")) {
          // unassign inventory
          if (service != null && invElement != null){
            Map pMap = new java.util.HashMap();
            pMap.put("Service", service);
            pMap.put("InvElement", invElement);
            ctxFinder.fireActionForObject("service-inventory-unassign", pMap);
            
            // refresh assigned inventory list velocity template
            htmlAssignedInventory.initializeControl();
            
            // refresh reserved inventory list velocity template
            htmlReservedInventory.initializeControl();
          }
        }
        
        if (type.equals("swap")) {
          Map pMap = new java.util.HashMap();
          if (service != null && invElement != null){
            pMap.put("Service", service);
            pMap.put("InvElement", invElement);
            pMap.put("InvElementList", availableInventory);
            ctxFinder.fireActionForObject(action, pMap);
          }
          // refresh available inventory list velocity template
          htmlAvailableInventory.initializeControl();
          
          // refresh assigned inventory list velocity template
          htmlAssignedInventory.initializeControl();
          
          // refresh reserved inventory list velocity template
          IPersistentObject account = ctx.getObject("Account",null);
          if (account != null){
            IPersistentCollection reservedInventory = account.getCollection("InvElementList","Reserved");
            if (reservedInventory!=null){
              reservedInventory.reset();
            }
          }
          htmlReservedInventory.initializeControl();
        }
        
        if (type.equals("return")) {
          // return inventory
          if (service != null && invElement != null){
            // unassociate the inventory to the service object which performs the return
            if (service.removeAssociation(invElement)){
              // refresh assigned inventory list velocity template
              htmlAssignedInventory.initializeControl();
            }else{
              IError err = service.getError();
              if (err != null){
                ctxFinder.displayHTMLError(err);
              }
            }
          }
        }
        
        if (type.equals("search")) {
          IPersistentCollection list = (IPersistentCollection) ctx.getObject(object,subtype);
          if (list != null){
            ctxFinder.fireActionForObject(action, list);
          }
          htmlReservedInventory.initializeControl();
        }
        
        
      }
    }
  }
  
  public boolean markItemForCancellation(IPersistentObject orderItem) {
    boolean success = false;
    
    if (orderItem.getAttributeDataAsBoolean("IsCancelled") == true ||
    orderItem.getAttributeDataAsBoolean("TransientToBeCancelled") == true) {
      JOptionPane.showMessageDialog(this, ResourceManager.getString("ItemCancelAction.text.ItemAlreadyCancelled"));
      return success;
    }
    success = orderItem.sendMessage("CancelValidateItem", new java.util.HashMap());
    if (success == false) {
      String errMsg = orderItem.getError().getErrorMessage();
      int col = errMsg.indexOf(":");
      errMsg = errMsg.substring(col+1);
      JOptionPane.showMessageDialog(this, errMsg);
    }
    IPersistentCollection pColl = orderItem.getCollection("ItemList","Validate");
    if (pColl != null) {
      int num = pColl.getCount();
      
      String pDesc = ResourceManager.getString("ItemCancelAction.text.ItemsAlsoToBeCanceled") + "\n\n";
      for (int i = 0;i < num; i++) {
        IPersistentObject pDom = pColl.getAt(i);
        pDesc += pDom.getAttributeData("ItemDescription");
        pDesc += "\n";
      }
      pDesc += "\n";
      pDesc += ResourceManager.getString("ItemCancelAction.text.CancelItem");
      pDesc += "\n\n";
      int cancel = JOptionPane.showConfirmDialog(this, pDesc);
      if ((cancel != JOptionPane.NO_OPTION) && (cancel != JOptionPane.CANCEL_OPTION)) {
        IPersistentObject pCurrentOrder = OrderManager.getInstance().getCurrentOrder();  // Check for Current Order
        if (pCurrentOrder != null) {
          Map pMap = new java.util.HashMap();
          pMap.put("cancelItem", orderItem);
          pMap.put("validateList", pColl);
          success = pCurrentOrder.sendMessage("markItemForCancellation", pMap);
          success = true;
        }
      }
    }
    
    return success;
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  private void initComponents() {//GEN-BEGIN:initComponents
    bgReserveInventory = new javax.swing.ButtonGroup();
    jSplitPane1 = new javax.swing.JSplitPane();
    jPanel1 = new javax.swing.JPanel();
    lblIdentifier = new com.csgsystems.igpa.controls.CSGLabel();
    edtInventoryId = new com.csgsystems.igpa.controls.CSGEdit();
    btnSearch = new com.csgsystems.igpa.controls.CSGSearchButton();
    edtQuantity = new com.csgsystems.igpa.controls.CSGNumberEdit();
    lblQuantity = new com.csgsystems.igpa.controls.CSGLabel();
    chkContiguousBlock = new com.csgsystems.igpa.controls.CSGCheckBox();
    lblContiguousBlock = new com.csgsystems.igpa.controls.CSGLabel();
    chkSetReservationEndDate = new com.csgsystems.igpa.controls.CSGCheckBox();
    edtEndDate = new com.csgsystems.igpa.controls.CSGDateEdit();
    lblEndDate = new com.csgsystems.igpa.controls.CSGLabel();
    cSGLabel4 = new com.csgsystems.igpa.controls.CSGLabel();
    lblInventoryType = new com.csgsystems.igpa.controls.CSGLabel();
    lblLocation = new com.csgsystems.igpa.controls.CSGLabel();
    lblVanity = new com.csgsystems.igpa.controls.CSGLabel();
    cbVanityCode = new com.csgsystems.igpa.controls.CSGComboBox();
    lblSalesChannel = new com.csgsystems.igpa.controls.CSGLabel();
    edtNetworkId = new com.csgsystems.igpa.controls.CSGEdit();
    edtServiceNumber = new com.csgsystems.igpa.controls.CSGEdit();
    lblNetworkId = new com.csgsystems.igpa.controls.CSGLabel();
    lblServiceNumber = new com.csgsystems.igpa.controls.CSGLabel();
    cSGLabel41 = new com.csgsystems.igpa.controls.CSGLabel();
    btnCancel = new com.csgsystems.igpa.controls.CSGButton();
    lblSerialNumber = new com.csgsystems.igpa.controls.CSGLabel();
    edtSerialNumber = new com.csgsystems.igpa.controls.CSGEdit();
    lblPrimaryCode = new com.csgsystems.igpa.controls.CSGLabel();
    edtPrimaryCode = new com.csgsystems.igpa.controls.CSGEdit();
    lblSecondaryCode = new com.csgsystems.igpa.controls.CSGLabel();
    edtSecondaryCode = new com.csgsystems.igpa.controls.CSGEdit();
    lblTertiaryCode = new com.csgsystems.igpa.controls.CSGLabel();
    edtTertiaryCode = new com.csgsystems.igpa.controls.CSGEdit();
    chkAutoReserve = new com.csgsystems.igpa.controls.CSGCheckBox();
    cbInventoryTypeId = new com.csgsystems.igpa.controls.CSGCollectionComboBox();
    btnReset = new com.csgsystems.igpa.controls.CSGButton();
    chkAutoAssign = new com.csgsystems.igpa.controls.CSGCheckBox();
    cSGLabel42 = new com.csgsystems.igpa.controls.CSGLabel();
    lblQuantity1 = new com.csgsystems.igpa.controls.CSGLabel();
    lblAssignContiguousBlock = new com.csgsystems.igpa.controls.CSGLabel();
    edtAssignQuantity = new com.csgsystems.igpa.controls.CSGNumberEdit();
    chkAssignContiguousBlock = new com.csgsystems.igpa.controls.CSGCheckBox();
    cbLocationId = new com.csgsystems.igpa.controls.CSGCollectionComboBox();
    cbSalesChannelId = new com.csgsystems.igpa.controls.CSGCollectionComboBox();
    chkCustomerOwned = new com.csgsystems.igpa.controls.CSGCheckBox();
    cbTopLevelContainerType = new com.csgsystems.igpa.controls.CSGComboBox();
    lblTopLevelContainerType = new com.csgsystems.igpa.controls.CSGLabel();
    jSplitPane3 = new javax.swing.JSplitPane();
    jSplitPane2 = new javax.swing.JSplitPane();
    htmlAvailableInventory = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
    htmlReservedInventory = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
    htmlAssignedInventory = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();
    htmlInventoryLineList = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();

    setLayout(null);

    setAlignmentX(0.0F);
    setAlignmentY(0.0F);
    setPreferredSize(new java.awt.Dimension(900, 670));
    jSplitPane1.setBorder(null);
    jSplitPane1.setDividerLocation(310);
    jPanel1.setLayout(null);

    jPanel1.setPreferredSize(new java.awt.Dimension(310, 515));
    lblIdentifier.setLocalizationKey("InventorySearch.lblIdentifier");
    jPanel1.add(lblIdentifier);
    lblIdentifier.setBounds(10, 85, 140, 20);

    edtInventoryId.setAttributeName("ExternalId");
    edtInventoryId.setDomainName("InvElementList");
    edtInventoryId.setDomainSubtype("Available");
    edtInventoryId.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        edtInventoryIdActionPerformed(evt);
      }
    });

    jPanel1.add(edtInventoryId);
    edtInventoryId.setBounds(150, 85, 150, 20);

    btnSearch.setLocalizationKey("AccountLocateFilter.Search");
    btnSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSearchActionPerformed(evt);
      }
    });

    jPanel1.add(btnSearch);
    btnSearch.setBounds(130, 515, 80, 25);

    edtQuantity.setText("cSGNumberEdit1");
    edtQuantity.setAttributeName("Quantity");
    edtQuantity.setDomainName("InvElementList");
    edtQuantity.setDomainSubtype("Available");
    jPanel1.add(edtQuantity);
    edtQuantity.setBounds(150, 390, 150, 20);

    lblQuantity.setLocalizationKey("InventorySearch.label.Quantity");
    jPanel1.add(lblQuantity);
    lblQuantity.setBounds(40, 390, 110, 20);

    chkContiguousBlock.setAttributeName("UseContiguous");
    chkContiguousBlock.setDomainName("InvElementList");
    chkContiguousBlock.setDomainSubtype("Available");
    chkContiguousBlock.setFalseValue("0");
    chkContiguousBlock.setTrueValue("1");
    jPanel1.add(chkContiguousBlock);
    chkContiguousBlock.setBounds(150, 415, 21, 20);

    lblContiguousBlock.setLocalizationKey("InventorySearch.label.ContiguousBlock");
    jPanel1.add(lblContiguousBlock);
    lblContiguousBlock.setBounds(40, 415, 110, 20);

    chkSetReservationEndDate.setAttributeName("UseReservationEndDate");
    chkSetReservationEndDate.setDomainName("InvElementList");
    chkSetReservationEndDate.setDomainSubtype("Available");
    chkSetReservationEndDate.setLocalizationKey("InventorySearch.chkSetReservationEndDate");
    chkSetReservationEndDate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        chkSetReservationEndDateActionPerformed(evt);
      }
    });

    jPanel1.add(chkSetReservationEndDate);
    chkSetReservationEndDate.setBounds(10, 325, 260, 20);

    edtEndDate.setAttributeName("ReservationEndDate");
    edtEndDate.setDomainName("InvElementList");
    edtEndDate.setDomainSubtype("Available");
    edtEndDate.setEnabled(false);
    jPanel1.add(edtEndDate);
    edtEndDate.setBounds(150, 345, 150, 20);

    lblEndDate.setLocalizationKey("InventorySearch.lblEndDate");
    jPanel1.add(lblEndDate);
    lblEndDate.setBounds(40, 345, 110, 20);

    cSGLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/csgsystems/bp/images/TreeTwoBranch.gif")));
    cSGLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    jPanel1.add(cSGLabel4);
    cSGLabel4.setBounds(30, 385, 10, 50);

    lblInventoryType.setLocalizationKey("InventorySearch.lblInventoryType");
    jPanel1.add(lblInventoryType);
    lblInventoryType.setBounds(10, 10, 140, 20);

    lblLocation.setLocalizationKey("InventorySearch.lblLocation");
    jPanel1.add(lblLocation);
    lblLocation.setBounds(10, 35, 140, 20);

    lblVanity.setLocalizationKey("InventorySearch.lblVanity");
    jPanel1.add(lblVanity);
    lblVanity.setBounds(10, 60, 140, 20);

    cbVanityCode.setAttributeName("VanityCode");
    cbVanityCode.setDomainName("InvElementList");
    cbVanityCode.setDomainSubtype("Available");
    jPanel1.add(cbVanityCode);
    cbVanityCode.setBounds(150, 60, 150, 20);

    lblSalesChannel.setLocalizationKey("InventorySearch.lblSalesChannel");
    jPanel1.add(lblSalesChannel);
    lblSalesChannel.setBounds(10, 110, 140, 20);

    edtNetworkId.setAttributeName("NetworkId");
    edtNetworkId.setDomainName("InvElementList");
    edtNetworkId.setDomainSubtype("Available");
    jPanel1.add(edtNetworkId);
    edtNetworkId.setBounds(150, 135, 150, 20);

    edtServiceNumber.setAttributeName("ServiceNumber");
    edtServiceNumber.setDomainName("InvElementList");
    edtServiceNumber.setDomainSubtype("Available");
    jPanel1.add(edtServiceNumber);
    edtServiceNumber.setBounds(150, 260, 150, 20);

    lblNetworkId.setLocalizationKey("InventorySearch.lblNetwordId");
    jPanel1.add(lblNetworkId);
    lblNetworkId.setBounds(10, 135, 140, 20);

    lblServiceNumber.setLocalizationKey("InventorySearch.lblServiceNumber");
    jPanel1.add(lblServiceNumber);
    lblServiceNumber.setBounds(10, 260, 140, 20);

    cSGLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/csgsystems/bp/images/TreeOneBranch.gif")));
    cSGLabel41.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    jPanel1.add(cSGLabel41);
    cSGLabel41.setBounds(30, 340, 10, 25);

    btnCancel.setLocalizationKey("InventorySearch.btnCancel");
    btnCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCancelActionPerformed(evt);
      }
    });

    jPanel1.add(btnCancel);
    btnCancel.setBounds(220, 515, 80, 25);

    lblSerialNumber.setLocalizationKey("InventorySearch.lblSerialNumber");
    jPanel1.add(lblSerialNumber);
    lblSerialNumber.setBounds(10, 160, 140, 20);

    edtSerialNumber.setAttributeName("SerialNumber");
    edtSerialNumber.setDomainName("InvElementList");
    edtSerialNumber.setDomainSubtype("Available");
    jPanel1.add(edtSerialNumber);
    edtSerialNumber.setBounds(150, 160, 150, 20);

    lblPrimaryCode.setLocalizationKey("InventorySearch.lblPrimaryCode");
    jPanel1.add(lblPrimaryCode);
    lblPrimaryCode.setBounds(10, 185, 140, 20);

    edtPrimaryCode.setAttributeName("PrimaryCode");
    edtPrimaryCode.setDomainName("InvElementList");
    edtPrimaryCode.setDomainSubtype("Available");
    jPanel1.add(edtPrimaryCode);
    edtPrimaryCode.setBounds(150, 185, 150, 20);

    lblSecondaryCode.setLocalizationKey("InventorySearch.lblSecondaryCode");
    jPanel1.add(lblSecondaryCode);
    lblSecondaryCode.setBounds(10, 210, 140, 20);

    edtSecondaryCode.setAttributeName("SecondaryCode");
    edtSecondaryCode.setDomainName("InvElementList");
    edtSecondaryCode.setDomainSubtype("Available");
    jPanel1.add(edtSecondaryCode);
    edtSecondaryCode.setBounds(150, 210, 150, 20);

    lblTertiaryCode.setLocalizationKey("InventorySearch.lblTertiaryCode");
    jPanel1.add(lblTertiaryCode);
    lblTertiaryCode.setBounds(10, 235, 140, 20);

    edtTertiaryCode.setAttributeName("TertiaryCode");
    edtTertiaryCode.setDomainName("InvElementList");
    edtTertiaryCode.setDomainSubtype("Available");
    jPanel1.add(edtTertiaryCode);
    edtTertiaryCode.setBounds(150, 235, 150, 20);

    chkAutoReserve.setAttributeName("AutoReserveInventory");
    chkAutoReserve.setDomainName("InvElementList");
    chkAutoReserve.setDomainSubtype("Available");
    chkAutoReserve.setLocalizationKey("InventorySearch.chkAutoReserve");
    chkAutoReserve.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        chkAutoReserveActionPerformed(evt);
      }
    });

    jPanel1.add(chkAutoReserve);
    chkAutoReserve.setBounds(10, 370, 270, 20);

    cbInventoryTypeId.setAttributeName("InventoryTypeId");
    cbInventoryTypeId.setDomainName("InvElementList");
    cbInventoryTypeId.setDomainSubtype("Available");
    cbInventoryTypeId.setEnumCollectionName("InvsTypeList");
    cbInventoryTypeId.setEnumCollectionSubtype("InvsLine");
    cbInventoryTypeId.setEnumDisplayAttributeName("DisplayValue");
    cbInventoryTypeId.setEnumKeyAttributeName("InventoryTypeId");
    cbInventoryTypeId.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cbInventoryTypeIdActionPerformed(evt);
      }
    });

    jPanel1.add(cbInventoryTypeId);
    cbInventoryTypeId.setBounds(150, 10, 150, 20);

    btnReset.setLocalizationKey("InventorySearch.btnReset");
    btnReset.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnResetActionPerformed(evt);
      }
    });

    jPanel1.add(btnReset);
    btnReset.setBounds(40, 515, 80, 25);

    chkAutoAssign.setAtomName("AutoAssignInventory");
    chkAutoAssign.setDomainName("InvElementList");
    chkAutoAssign.setDomainSubtype("Available");
    chkAutoAssign.setLocalizationKey("InventorySearch.chkAutoAssign");
    chkAutoAssign.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        chkAutoAssignActionPerformed(evt);
      }
    });

    jPanel1.add(chkAutoAssign);
    chkAutoAssign.setBounds(10, 440, 270, 20);

    cSGLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/csgsystems/bp/images/TreeTwoBranch.gif")));
    cSGLabel42.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    jPanel1.add(cSGLabel42);
    cSGLabel42.setBounds(30, 455, 10, 50);

    lblQuantity1.setLocalizationKey("InventorySearch.label.Quantity");
    jPanel1.add(lblQuantity1);
    lblQuantity1.setBounds(40, 460, 110, 20);

    lblAssignContiguousBlock.setLocalizationKey("InventorySearch.label.ContiguousBlock");
    jPanel1.add(lblAssignContiguousBlock);
    lblAssignContiguousBlock.setBounds(40, 485, 110, 20);

    edtAssignQuantity.setText("cSGNumberEdit1");
    edtAssignQuantity.setAttributeName("AssignQuantity");
    edtAssignQuantity.setDomainName("InvElementList");
    edtAssignQuantity.setDomainSubtype("Available");
    jPanel1.add(edtAssignQuantity);
    edtAssignQuantity.setBounds(150, 460, 150, 20);

    chkAssignContiguousBlock.setAttributeName("UseAssignContiguous");
    chkAssignContiguousBlock.setDomainName("InvElementList");
    chkAssignContiguousBlock.setDomainSubtype("Available");
    chkAssignContiguousBlock.setFalseValue("0");
    chkAssignContiguousBlock.setTrueValue("1");
    jPanel1.add(chkAssignContiguousBlock);
    chkAssignContiguousBlock.setBounds(150, 485, 21, 20);

    cbLocationId.setAttributeName("LocationId");
    cbLocationId.setDomainName("InvElementList");
    cbLocationId.setDomainSubtype("Available");
    cbLocationId.setEnumCollectionName("InvsLocationList");
    cbLocationId.setEnumCollectionSubtype("InvsLine");
    cbLocationId.setEnumDisplayAttributeName("DisplayValue");
    cbLocationId.setEnumKeyAttributeName("LocationId");
    jPanel1.add(cbLocationId);
    cbLocationId.setBounds(150, 35, 150, 20);

    cbSalesChannelId.setAttributeName("SalesChannelId");
    cbSalesChannelId.setDomainName("InvElementList");
    cbSalesChannelId.setDomainSubtype("Available");
    cbSalesChannelId.setEnumCollectionName("InvsSalesChannelList");
    cbSalesChannelId.setEnumCollectionSubtype("InvsLine");
    cbSalesChannelId.setEnumDisplayAttributeName("DisplayValue");
    cbSalesChannelId.setEnumKeyAttributeName("SalesChannelId");
    cbSalesChannelId.setOpaque(false);
    jPanel1.add(cbSalesChannelId);
    cbSalesChannelId.setBounds(150, 110, 150, 20);

    chkCustomerOwned.setAttributeName("IsCustomerOwned");
    chkCustomerOwned.setDomainName("InvElementList");
    chkCustomerOwned.setDomainSubtype("Available");
    chkCustomerOwned.setLocalizationKey("InventorySearch.chkCustomerOwned");
    jPanel1.add(chkCustomerOwned);
    chkCustomerOwned.setBounds(10, 305, 260, 20);

    cbTopLevelContainerType.setAttributeName("TopLevelContainerType");
    cbTopLevelContainerType.setDomainName("InvElementList");
    cbTopLevelContainerType.setDomainSubtype("Available");
    jPanel1.add(cbTopLevelContainerType);
    cbTopLevelContainerType.setBounds(150, 285, 150, 20);

    lblTopLevelContainerType.setLocalizationKey("InventorySearch.lblTopLevelContainerType");
    jPanel1.add(lblTopLevelContainerType);
    lblTopLevelContainerType.setBounds(10, 285, 140, 20);

    jSplitPane1.setLeftComponent(jPanel1);

    jSplitPane3.setDividerLocation(350);
    jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    jSplitPane2.setDividerLocation(170);
    jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    jSplitPane2.setPreferredSize(new java.awt.Dimension(352, 430));
    htmlAvailableInventory.setBorder(null);
    htmlAvailableInventory.setVelocityTemplateUri("template/InventorySearch.vm");
    jSplitPane2.setLeftComponent(htmlAvailableInventory);

    htmlReservedInventory.setBorder(null);
    htmlReservedInventory.setVelocityTemplateUri("template/InventoryReserved.vm");
    jSplitPane2.setRightComponent(htmlReservedInventory);

    jSplitPane3.setLeftComponent(jSplitPane2);

    htmlAssignedInventory.setBorder(null);
    htmlAssignedInventory.setVelocityTemplateUri("template/InventoryAssigned.vm");
    jSplitPane3.setRightComponent(htmlAssignedInventory);

    jSplitPane1.setRightComponent(jSplitPane3);

    add(jSplitPane1);
    jSplitPane1.setBounds(5, 120, 890, 540);

    htmlInventoryLineList.setBorder(null);
    htmlInventoryLineList.setPreferredSize(new java.awt.Dimension(800, 110));
    htmlInventoryLineList.setVelocityTemplateUri("template/InventorySearch_InventoryLineList.vm");
    add(htmlInventoryLineList);
    htmlInventoryLineList.setBounds(5, 5, 890, 110);

  }//GEN-END:initComponents
  
    private void chkAutoAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAutoAssignActionPerformed
      chkAutoReserve.initializeControl();
      
      chkContiguousBlock.initializeControl();
      edtQuantity.initializeControl();
      
      chkAssignContiguousBlock.initializeControl();
      edtAssignQuantity.initializeControl();
    }//GEN-LAST:event_chkAutoAssignActionPerformed
    
    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
      
      // set filter attributes to null
      IContext ctx = ctxFinder.findContext();
      if (ctx != null) {
        IPersistentCollection invElementList = (IPersistentCollection)ctx.getObject("InvElementList", "Available");
        if (invElementList != null){
          invElementList.setAttributeData("VanityCode",null);
          invElementList.setAttributeData("TertiaryCode",null);
          invElementList.setAttributeData("NetworkId",null);
          invElementList.setAttributeData("SerialNumber",null);
          invElementList.setAttributeData("LocationId",null);
          invElementList.setAttributeData("ExternalId",null);
          invElementList.setAttributeData("PrimaryCode",null);
          invElementList.setAttributeData("SalesChannelId",null);
          invElementList.setAttributeData("SecondaryCode",null);
          invElementList.setAttributeData("ServiceNumber",null);
          invElementList.setAttributeData("AutoReserveInventory",null);
          invElementList.setAttributeData("Quantity",new Integer(1));
          invElementList.setAttributeData("UseContiguous",null);
          invElementList.setAttributeData("UseReservationEndDate",null);
          invElementList.setAttributeData("ReservationEndDate",null);
          invElementList.setAttributeData("AutoAssignInventory",null);
          invElementList.setAttributeData("AssignQuantity",new Integer(1));
          invElementList.setAttributeData("UseAssignContiguous",null);
          invElementList.setAttributeData("TopLevelContainerType",null);
          invElementList.setAttributeData("IsCustomerOwned",Boolean.FALSE);
        }
      }
      
      // refresh the controls
      resetControls();
      // clear available inventory list
      htmlAvailableInventory.getSession().putValue("clear-screen",Boolean.TRUE);
      // reset paging to first page
      htmlAvailableInventory.getSession().remove("page");
      htmlAvailableInventory.initializeControl();
    }//GEN-LAST:event_btnResetActionPerformed
    
    private void resetControls(){
      cbLocationId.initializeControl();
      cbVanityCode.initializeControl();
      edtInventoryId.initializeControl();
      cbSalesChannelId.initializeControl();
      
      edtNetworkId.initializeControl();
      edtSerialNumber.initializeControl();
      edtPrimaryCode.initializeControl();
      edtSecondaryCode.initializeControl();
      edtTertiaryCode.initializeControl();
      edtServiceNumber.initializeControl();
      
      cbInventoryTypeId.initializeControl();
      chkSetReservationEndDate.initializeControl();
      edtEndDate.initializeControl();
      
      cbTopLevelContainerType.initializeControl();
      chkCustomerOwned.initializeControl();
      
      chkAutoReserve.initializeControl();
      edtQuantity.initializeControl();
      chkContiguousBlock.initializeControl();
      
      chkAutoAssign.initializeControl();
      edtAssignQuantity.initializeControl();
      chkAssignContiguousBlock.initializeControl();
      //edited
      //added by Lee Wynn, Teoh (24/5/2005)
      checkCharCount();
    }
    private void cbInventoryTypeIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbInventoryTypeIdActionPerformed
      resetControls();
      checkCharCount();
    }//GEN-LAST:event_cbInventoryTypeIdActionPerformed
    
    private void chkAutoReserveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAutoReserveActionPerformed
      chkAutoAssign.initializeControl();
      chkContiguousBlock.initializeControl();
      edtQuantity.initializeControl();
      chkAssignContiguousBlock.initializeControl();
      edtAssignQuantity.initializeControl();
    }//GEN-LAST:event_chkAutoReserveActionPerformed
    
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
      // Close down the dialog
      ctxFinder.closeDialog(IContext.OK);
    }//GEN-LAST:event_btnCancelActionPerformed
    
    private void setInventoryLineId(){
      // get this panel's context
      IContext ctx = ctxFinder.findContext();
      if (ctx != null) {
            /*
             * *****************************************************************
             * If the inventory line is being changed, reset the collection of
             * inventory types
             * *****************************************************************
             */
        IPersistentCollection invElementList = (IPersistentCollection)ctx.getObject("InvElementList", "Available");
        IPersistentObject emfConfigurationInventoryLine = ctx.getObject("EmfConfigurationInventoryLine",null);
        if (emfConfigurationInventoryLine != null && invElementList != null){
          Object inventoryLineId = emfConfigurationInventoryLine.getAttributeData("InventoryLineId");
          invElementList.setAttributeData("InventoryLineId", inventoryLineId);
          System.out.println(invElementList.getAttribute("LocationId").toString());
          this.selectedIndex= Integer.parseInt(inventoryLineId.toString());
          invElementList.reset();
          htmlAvailableInventory.initializeControl();
          cbInventoryTypeId.initializeControl();
          //edited added by Lee Wynn, Teoh 24/05/2005
        }
      }
      populateAttribute();
      checkCharCount();
    }
    
    private void chkSetReservationEndDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSetReservationEndDateActionPerformed
      // enable/disable end date
      edtEndDate.initializeControl();
    }//GEN-LAST:event_chkSetReservationEndDateActionPerformed
    
    private void edtInventoryIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtInventoryIdActionPerformed
      // Add your handling code here:
    }//GEN-LAST:event_edtInventoryIdActionPerformed
    
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
      htmlAvailableInventory.getSession().putValue("clear-screen",Boolean.FALSE);
      htmlAvailableInventory.getSession().remove("page");
      IContext ctx = ctxFinder.findContext();
      IPersistentCollection availableInventory = (IPersistentCollection)ctx.getObject("InvElementList","Available");
      IPersistentObject account = ctx.getObject("Account",null);
      IPersistentObject service = ctx.getObject("Service", null);
      //availableInventory.getAttribute("SalesChannelId").setRequired(true);
      // change reserved criteria to use new search criteria
      IPersistentCollection reservedInventory = (IPersistentCollection)ctx.getObject("InvElementList","Reserved");
      if (reservedInventory == null){
        if (account != null){
          reservedInventory = account.getCollection("InvElementList","Reserved");
        }
      }
      
      // set values to null so non AutoAssign/AutoReserve searches do not use this filter criteria
      availableInventory.setAttributeData("AccountId",null);
      availableInventory.setAttributeData("ParamA",null);
      availableInventory.setAttributeData("ParamB",null);
      
      if (chkAutoReserve.isSelected()){
        
        // set account we are going to auto reserve inventory to
        availableInventory.setAttributeData("ParamA",account.getAttributeData("AccountInternalId"));
        
        // fire autoReserve Request
        if (!availableInventory.sendMessage("autoReserve",null)){
          
          IError err = availableInventory.getError();
          if (err != null){
            ctxFinder.displayHTMLError(err);
          }
          
        }else{
          // reset reserved collection
          if (reservedInventory != null){
            reservedInventory.reset();
          }
          // refresh html control
          htmlReservedInventory.initializeControl();
        }
        availableInventory.setAttributeData("AutoReserveInventory",null);
        availableInventory.setAttributeData("ParamA",null);
        availableInventory.reset();
        htmlAvailableInventory.initializeControl();
        
      }else if (chkAutoAssign.isSelected()){
        // set service we are going to auto assign inventory to
        availableInventory.setAttributeData("AccountId",account.getAttributeData("AccountInternalId"));
        availableInventory.setAttributeData("ParamA",service.getAttributeData("ServiceInternalId"));
        availableInventory.setAttributeData("ParamB",service.getAttributeData("ServiceInternalIdResets"));
        
        Map pMap = new java.util.HashMap();
        pMap.put("Service", service);
        
        // fire autoReserve Request
        if (!availableInventory.sendMessage("autoAssign",pMap)){
          
          IError err = availableInventory.getError();
          if (err != null){
            ctxFinder.displayHTMLError(err);
          }
          
        }else{
          // reset assigned collection
          IPersistentCollection assignedInventory = (IPersistentCollection)ctx.getObject("InvElementList", "Service");
          if (assignedInventory == null){
            if (service != null){
              assignedInventory = service.getCollection("InvElementList", "Service");
            }
          }
          if (assignedInventory != null){
            assignedInventory.reset();
          }
          // refresh html controls
          htmlReservedInventory.initializeControl();
          availableInventory.setAttributeData("AutoAssignInventory",null);
          availableInventory.setAttributeData("ParamA",null);
          availableInventory.setAttributeData("ParamB",null);
          availableInventory.reset();
          htmlAvailableInventory.initializeControl();
          htmlAssignedInventory.initializeControl();
        }
      }
    }//GEN-LAST:event_btnSearchActionPerformed
    
    public void keyTyped(KeyEvent e) {
    }
    
    public void keyReleased(KeyEvent e) {
      checkCharCount();
    }
    public void keyPressed(KeyEvent e) {
    }
    
    private int getEdtInventoryLen() {
      return this.edtInventoryId.getText().length();
    }
    
    private void checkCharCount() {
      if(selectedIndex==1) {
        if(getEdtInventoryLen()==19) {
          this.cbSalesChannelId.setRequired(false);
          iAtt.setRequired(false);
          cbSalesChannelId.setSelectedIndex(-1);
        }else {
          this.cbSalesChannelId.setRequired(true);
          iAtt.setRequired(true);
        }
      }else {
        this.cbSalesChannelId.setRequired(true);
        iAtt.setRequired(true);
      }
    }
    
    
    
    
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup bgReserveInventory;
  private com.csgsystems.igpa.controls.CSGButton btnCancel;
  private com.csgsystems.igpa.controls.CSGButton btnReset;
  private com.csgsystems.igpa.controls.CSGSearchButton btnSearch;
  private com.csgsystems.igpa.controls.CSGLabel cSGLabel4;
  private com.csgsystems.igpa.controls.CSGLabel cSGLabel41;
  private com.csgsystems.igpa.controls.CSGLabel cSGLabel42;
  private com.csgsystems.igpa.controls.CSGCollectionComboBox cbInventoryTypeId;
  private com.csgsystems.igpa.controls.CSGCollectionComboBox cbLocationId;
  private com.csgsystems.igpa.controls.CSGCollectionComboBox cbSalesChannelId;
  private com.csgsystems.igpa.controls.CSGComboBox cbTopLevelContainerType;
  private com.csgsystems.igpa.controls.CSGComboBox cbVanityCode;
  private com.csgsystems.igpa.controls.CSGCheckBox chkAssignContiguousBlock;
  private com.csgsystems.igpa.controls.CSGCheckBox chkAutoAssign;
  private com.csgsystems.igpa.controls.CSGCheckBox chkAutoReserve;
  private com.csgsystems.igpa.controls.CSGCheckBox chkContiguousBlock;
  private com.csgsystems.igpa.controls.CSGCheckBox chkCustomerOwned;
  private com.csgsystems.igpa.controls.CSGCheckBox chkSetReservationEndDate;
  private com.csgsystems.igpa.controls.CSGNumberEdit edtAssignQuantity;
  private com.csgsystems.igpa.controls.CSGDateEdit edtEndDate;
  private com.csgsystems.igpa.controls.CSGEdit edtInventoryId;
  private com.csgsystems.igpa.controls.CSGEdit edtNetworkId;
  private com.csgsystems.igpa.controls.CSGEdit edtPrimaryCode;
  private com.csgsystems.igpa.controls.CSGNumberEdit edtQuantity;
  private com.csgsystems.igpa.controls.CSGEdit edtSecondaryCode;
  private com.csgsystems.igpa.controls.CSGEdit edtSerialNumber;
  private com.csgsystems.igpa.controls.CSGEdit edtServiceNumber;
  private com.csgsystems.igpa.controls.CSGEdit edtTertiaryCode;
  private com.csgsystems.igpa.controls.CSGVelocityHTMLEP htmlAssignedInventory;
  private com.csgsystems.igpa.controls.CSGVelocityHTMLEP htmlAvailableInventory;
  private com.csgsystems.igpa.controls.CSGVelocityHTMLEP htmlInventoryLineList;
  private com.csgsystems.igpa.controls.CSGVelocityHTMLEP htmlReservedInventory;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JSplitPane jSplitPane2;
  private javax.swing.JSplitPane jSplitPane3;
  private com.csgsystems.igpa.controls.CSGLabel lblAssignContiguousBlock;
  private com.csgsystems.igpa.controls.CSGLabel lblContiguousBlock;
  private com.csgsystems.igpa.controls.CSGLabel lblEndDate;
  private com.csgsystems.igpa.controls.CSGLabel lblIdentifier;
  private com.csgsystems.igpa.controls.CSGLabel lblInventoryType;
  private com.csgsystems.igpa.controls.CSGLabel lblLocation;
  private com.csgsystems.igpa.controls.CSGLabel lblNetworkId;
  private com.csgsystems.igpa.controls.CSGLabel lblPrimaryCode;
  private com.csgsystems.igpa.controls.CSGLabel lblQuantity;
  private com.csgsystems.igpa.controls.CSGLabel lblQuantity1;
  private com.csgsystems.igpa.controls.CSGLabel lblSalesChannel;
  private com.csgsystems.igpa.controls.CSGLabel lblSecondaryCode;
  private com.csgsystems.igpa.controls.CSGLabel lblSerialNumber;
  private com.csgsystems.igpa.controls.CSGLabel lblServiceNumber;
  private com.csgsystems.igpa.controls.CSGLabel lblTertiaryCode;
  private com.csgsystems.igpa.controls.CSGLabel lblTopLevelContainerType;
  private com.csgsystems.igpa.controls.CSGLabel lblVanity;
  // End of variables declaration//GEN-END:variables
}
