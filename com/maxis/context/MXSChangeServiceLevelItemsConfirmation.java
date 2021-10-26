/**
 *  Project Name: Maxis R&B
 *  Developer Name: Ming Hon
 *  Module Name: Customer Center
 *  Date Created: 20051026
 *  Description: 
 *      this file was created for P1 sim change no product addition checking
 *      the checking was conflicting with P2 Multi / Single profile
 *      therefore the entire file is replace, and new checking code was written
 *      this new file inherits from the core context instead of replacing it
 *  Date Modified: 20051026
 *  Version #: v01
 *
 *
 */

package com.maxis.context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Vector;
import com.csgsystems.domain.framework.context.Context;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.arbor.businessobject.UtilityOrderableObjects;
import com.csgsystems.domain.arbor.order.*;
import com.csgsystems.domain.arbor.utilities.DomainUtility;
import com.csgsystems.error.ErrorFactory;
import org.apache.commons.logging.*;
import com.maxis.util.MultiSingleProfile;
import com.maxis.util.SIMMUtil;
import com.maxis.util.MISMUtil;
import com.maxis.util.DataRatePlans;
import javax.swing.JOptionPane;

public class MXSChangeServiceLevelItemsConfirmation extends com.csgsystems.bp.context.ChangeServiceLevelItemsConfirmation  {
    
    private static Log log = null;
    
    static {
        try {
            log = LogFactory.getLog(MXSChangeServiceLevelItemsConfirmation.class);
        } catch (Exception ex) {}
    }
    
    
    public boolean processShutdown(int shutdownType) {
        //runs the core method first
        if (!super.processShutdown(shutdownType)) return false;
        
        if(shutdownType==OK){
            //Check for sim replacement, when perfroming sim replacement RC is not allowed
            String mesg1[] = checkSimReplacement();
            //print all error message from checkSimReplacement if any
            if(mesg1!=null){
                if(mesg1.length > 0){
                    for(int i=0; i<mesg1.length; i++){
                        addError(ErrorFactory.createError(mesg1[i],  null, com.maxis.context.ServiceAddConfirm.class));
                    }
                    return false;
                }
            }
            
/*            //Added for  Multi / Single profile module
            //call the validateMultiSingleProfileOrder
            String mesg2[] = MultiSingleProfile.validateMultiSingleProfileOrder();
            //print all error message from validateMultiSingleProfileOrder if any
            if(mesg2!=null){
                if(mesg2.length > 0){
                    for(int i=0; i<mesg2.length; i++){
                        addError(ErrorFactory.createError(mesg2[i],  null, com.maxis.context.ServiceAddConfirm.class));
                    }
                    return false;
                }
            }
            
            //check for contact company on the order
            IPersistentObject order = OrderManager.getInstance().getCurrentOrder();
            if(order!=null){
                String contactCompany = order.getAttributeDataAsString("ContactCompany");
                if(contactCompany==null) {
                    String[] errstr = new String[1];
                    errstr[0]="Dealer/CSD is required, but no value has been set";
                    error = ErrorFactory.createError("Order Error", errstr, getClass());
                    return false;
                }
            }*/
            
            // SIMM-MISM Checks
            IPersistentObject service = getObject("Service");
            IPersistentObject order = OrderManager.getInstance().getCurrentOrder();
            if ( SIMMUtil.wasSecondary(service) ) SIMMUtil.disconnectLink(order, service);
            
            if ( SIMMUtil.isSecondary(service) && !SIMMUtil.hasLink(service) ) {
                addError(ErrorFactory.createError("MX-1-1", 
                        null, com.maxis.context.ServiceAddConfirm.class));
                return false;
            } else if ( SIMMUtil.isPrimary(service) || MISMUtil.isPrimary(service) ) SIMMUtil.changeLinks(order, service);
            else if ( MISMUtil.isSecondary(service) && !SIMMUtil.hasLink(service) ) {
                addError(ErrorFactory.createError("MX-2-1", 
                        null, com.maxis.context.ServiceAddConfirm.class));
                return false;                
            }
            
            // Data rate plans check
            String dataChecks[] = DataRatePlans.validateChange(service);
            if ( dataChecks != null && dataChecks.length > 0 ) {
                String errors = "";
                for ( int i = 0; i < dataChecks.length; i++ )
                    errors = errors + dataChecks[i];
//                    addError(ErrorFactory.createError(dataChecks[i], null, getClass()));
                JOptionPane.showMessageDialog(null, errors, "Alert!", JOptionPane.WARNING_MESSAGE);
            }
            
        }
        return true;
    }
    
    private String[] checkSimReplacement(){
        Vector errorMessages = new Vector();
        
        IPersistentObject order = OrderManager.getInstance().getCurrentOrder();
        if(order!=null){
            //get ipersistent collection serviceoerderlist
            IPersistentCollection serviceOrderList = order.getCollection("ServiceOrderList",  "Order");
            if(serviceOrderList!=null) {
                for (int ixa = 0 ;ixa<serviceOrderList.getCount(); ixa++) {
                    IPersistentObject so = serviceOrderList.getAt(ixa);
                    //only perform checking if its service level
                    if(so.getAttributeDataAsBoolean("IsServiceLevel")) {
                        IPersistentCollection itemList  = so.getCollection("ItemList",  "ServiceOrder");
                        if(itemList !=null ){
                            boolean hasInventory=false;
                            boolean hasProducts = false;
                            for (int i = 0;i<itemList.getCount();i++) {
                                /*if(itemList.getAt(i).getAttributeDataAsInteger("MemberType")==70) {
                                    hasInventory=true;
                                    //IPersistentObject service = getObject("Service", null);
                                    //IPersistentCollection invElementList = service.getCollection("InvElementList",  "Service");
                                    //System.out.println("mmm**** Items in InvElementList"+invElementList.size());
                                    //1) find the related inventory object from the list
                                    //2) check the inventory by the InventoryLineId (a SIM line id can have more than one inventory type)
                                }*/
                                // can shortcut a bit, check by the sim identifier instead of by inventory
                                //Order item type SI external ID && Si External ID Type = 12 = SIM Card Number
                                if(itemList.getAt(i).getAttributeDataAsInteger("MemberType")==60&&itemList.getAt(i).getAttributeDataAsInteger("MemberId")==12){
                                    hasInventory=true;
                                }
                                else if(itemList.getAt(i).getAttributeDataAsInteger("MemberType")==10) {
                                    hasProducts = true;
                                }
                                //System.out.println("*** Hasinventory? "+hasInventory+" has Products? "+hasProducts);
                                if(hasInventory&&hasProducts) {
                                    errorMessages.add("No product is allowed if there is a SIM replacement. Please remove it and add it in another order.");
                                }
                            }
                        }
                    }
                }
            }
        }
        
        //convert the vector to array
        if(errorMessages.size()>0){
            String rntstr[] = new String[errorMessages.size()];
            for(int i=0; i<errorMessages.size(); i++){
                rntstr[i] = (String)errorMessages.get(i);
            }
            return rntstr;
        }
        return null;
    }
    
    public boolean processShutdown2(int shutdownType) {
        boolean success = true;
        if (shutdownType == OK) {
            IPersistentObject service = getObject("Service", null);
            if (service != null) {
                //************************
                // check services to make sure required inventory has been selected
                //************************
                // only if inventory is turned on
                if (DomainUtility.isInventoryUsed() == true) {
                    Map parms = new HashMap();
                    if (!service.sendMessage("checkServiceInventory",parms)){
                        error = service.getError();
                        success = false;
                    }
                }
                
                // check for processed service orders to see if we have stale data.
                Date faultDate = service.getAttributeDataAsDate("TransientFaultDate");
                if (faultDate != null) {
                    long numCompletedOrders = UtilityOrderableObjects.getNumProcessedServiceOrders(service, faultDate, false);
                    if (numCompletedOrders != 0) {
                        error = ErrorFactory.createError("CC-2-161", new Object[] {}, getClass());
                        return false;
                    }
                }
            }else{
                log.debug("Missing service.");
            }
            if (success == true) {
                // Attempt to flush Item Cancel operations first.
                // This is to satisfy the following scenario:
                //   - service add + inventory that generates external id
                //   - finish wizard
                //   - modify service order ... disconnect inventory which results
                //     in a cancel order item ... add new inventory + external id
                //   - failure to send the item cancel down before the external id add
                //     would result in a trigger error.
                IPersistentObject order = OrderManager.getInstance().getCurrentOrder();
                if (order != null) {
                    if (order.sendMessage("flushCancelOrderItems", null) == false) {
                        error = order.getError();
                        success = false;
                    }
                    //added by lee wynn, teoh 5/7/2005
                    
                    else {
                        
             /*
              if(service!=null&&serviceOrderList!=null) {
                System.out.println("*** Proceeding to check for disconnects");
                IPersistentCollection invElementList = service.getCollection("InvElementList",  "Service");
                int elementListCount = invElementList.getCount();
                for (int c = 0;c<elementListCount;c++) {
                  IPersistentObject invElement = invElementList.getAt(c);
                  if(invElement!=null) {
                    System.out.println("*** inventory id = "+invElement.getAttributeDataAsString("ExternalId"));
                    System.out.println("*** InventoryElementenddatetime "+ invElement.getAttributeDataAsDate("EndDateTime"));
                    System.out.println("*** State = "+invElement.getAttributeDataAsInteger("State"));
                    if(invElement.getAttributeDataAsInteger("State")==1) {
                      System.out.println("*** Found pending State!");
                      System.out.println("*** Current user = "+SecurityManager.getInstance().getLoggedInUserId());
                    }
                  }
                }
              }*/
                        
                        
                        
                        //foreach element of collection, get Ipersistentcollection itemlist
                        //foreach elemtn of itemlist collection, check attribute membertype (refer kenan doc :item)
                        //differentiate from there
                        
                    }
                    //////////////////////////////////
                }else {
                }
                
            }
        }
        return success;
    }
    
    
    //service contains the invelementlist where we get the element number
    //order is where we get the reasonID
    private void checkForDisconnects(IPersistentObject order, IPersistentObject service) {
        if(order!=null&&service!=null) {
            //get the inventoryList
        }
    }
    
    //finds the InvIdResets number from the invelement in the list
    private void  findItem(IPersistentCollection serviceOrderList) {
        for (int ix = 0;ix<serviceOrderList.getCount();ix++) {
            IPersistentObject serviceOrder = serviceOrderList.getAt(ix);
            IPersistentCollection itemList = serviceOrder.getCollection("ItemList", "ServiceOrder");
            if(itemList!=null ){
                for (int i = 0;i< itemList.getCount();i++) {
                    IPersistentObject item = itemList.getAt(i);
                    if(item.getAttributeDataAsInteger("ItemActionId")==30&&item.getAttributeDataAsInteger("MemberType")==70) {
                        
                    }
                    //com.maxis.util.CommonUtil.objectToString(item);
                }
            }
        }
    }
    
    private void findInv(IPersistentObject service, IPersistentCollection serviceOrderList, int inventoryId) {
        if(service!=null&&serviceOrderList!=null) {
            IPersistentCollection invElementList = service.getCollection("InvElementList",  "Service");
            int elementListCount = invElementList.getCount();
            for (int c = 0;c<elementListCount;c++) {
                IPersistentObject invElement = invElementList.getAt(c);
                if(invElement!=null) {
                    if(invElement.getAttributeDataAsInteger("InventoryId")==inventoryId) {
                        System.out.println("*** Setting inventoryid = "+invElement.getAttributeDataAsString("ExternalId"));
                        System.out.println("*** State = "+invElement.getAttributeDataAsInteger("State"));
                        //invElement.setAttributeDataAsInteger("Annotation3",
                        //com.maxis.util.CommonUtil.objectToString(invElement);
                        IPersistentObject invUnassign = invElement.getObject("InventoryUnassign",null);
                        if(invUnassign==null) {
                            System.out.println("*** Inventory Unasign is null");
                            invUnassign = invElementList.getObject("InventoryUnassign", null);
                        }else {
                            System.out.println("*** INV NOT NULL");
                        }
                        
                        if(invUnassign==null) {
                            System.out.println("*** Inv still null");
                        }
                        
                    }
                }
            }
            for (int i = 0;i< serviceOrderList.getCount();i++) {
                IPersistentObject serviceOrder = serviceOrderList.getAt(i);
                com.maxis.util.CommonUtil.objectToString(serviceOrder);
            }
        }
    }
}
