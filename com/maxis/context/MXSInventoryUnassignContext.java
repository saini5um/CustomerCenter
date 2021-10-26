package com.maxis.context;
import com.maxis.util.tuxJDBCManager;
import com.maxis.util.ErrorReporting;

import com.csgsystems.fx.security.remote.RemoteDBConnection;
import com.csgsystems.fx.security.remote.SQL;

import com.csgsystems.fx.security.util.FxException;
import com.csgsystems.domain.framework.context.Context;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.error.IError;
import com.csgsystems.error.ErrorFactory;
import com.csgsystems.domain.arbor.order.*;

import java.util.*;
import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MXSInventoryUnassignContext extends Context {
  
  private static Log log = null;
  private static ErrorReporting err;
  static {
    try {
      log = LogFactory.getLog(MXSInventoryUnassignContext.class);
      err = ErrorReporting.getInstance();
    }
    catch (Exception ex) {
    }
  }
  
  public boolean open(Map parms){
    boolean success = true;
    System.out.println("*** InventoryUnassignComtext OPEN");
    // **********************
    // service we are unassigning inventory
    // **********************
    if (parms == null){
      setError("CC-2-62", null);
      return false;
    }
    
    IPersistentObject service = (IPersistentObject)parms.get("Service");
    if (service != null) {
      addTopic(service,true);
    } else {
      setError("CC-2-63", null);
      return false;
    }
    
    // create new inventory swap object
    IPersistentObject inventoryUnassign = (IPersistentObject) poObjectFactory.createNew("InventoryUnassign", null);
    if (inventoryUnassign != null) {
      
      //get the inventory item
      IPersistentObject invElement = (IPersistentObject)parms.get("InvElement");
      if (invElement != null){
        inventoryUnassign.addAssociation(invElement);
      }else{
        success = false;
        setError("CC-2-64", null);
      }
      
      addTopic(inventoryUnassign,true);   // non flushable
    } else {
      success = false;
      setError("CC-2-65", null);
    }
    return success;
  }
  
  public boolean open(IContext context) {
    boolean success = true;
    System.out.println("*** InventoryUnassignComtext OPEN");
    // **********************
    // account this inventory is assigned to
    // **********************
    IPersistentObject account = context.getObject("Account", null);
    if (account != null) {
      addTopic(account,true);
    } else {
      logError("Could not find account", null);
    }
    return success;
  }
  
  /**
   *  If OK is signaled from the closing dialog, perform the addAssociation
   *  from the source object to the target object
   *
   *@param  shutdownType  Description of the Parameter
   *@return               Description of the Return Value
   */
  public boolean processShutdown(int shutdownType) {
    
    // user clicked ok
    if (shutdownType == OK) {
      
      IPersistentObject inventoryUnassign = getObject("InventoryUnassign", null);
      if (inventoryUnassign == null){
        if (log != null) {log.error("Unable to find inventoryUnassign object on context.");}
        return false;
      }
      
      // ********************************
      // fire unassign request
      // ********************************
      IPersistentObject invElement = inventoryUnassign.getObject("InvElement",null);
      if (invElement == null){
        if (log != null) {log.error("Unable to find invElement object on context.");}
        return false;
      }
      
      IPersistentObject service = getObject("Service",null);
      if (service != null){
        Map parms = new HashMap();
        parms.put("InvElement",invElement);
        if (!inventoryUnassign.getAttribute("Reason").isReadOnly()){
          parms.put("DisconnectReason",inventoryUnassign.getAttributeData("Reason"));
        }
        parms.put("StartDateTime",inventoryUnassign.getAttributeData("StartDateTime"));
        parms.put("IsPrimaryDisconnect",Boolean.TRUE);
        //tlw 2005/08/04
        String updateInvdMain = "UPDATE INVD_MAIN SET Annotation_3 = ? WHERE INVENTORY_ID = ? and INVENTORY_ID_RESETS = ?";
        RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CAT");
        int inventoryId = invElement.getAttributeDataAsInteger("InventoryId");
        int inventoryIdResets = invElement.getAttributeDataAsInteger("InventoryIdResets");
        if (dbConn == null) {
          logError("Error getting current Oracle Connection.", null);
          return false; // prevent windows from shutting down
        }
        
        PreparedStatement pstmt = null;
        try {
          pstmt = dbConn.prepareStatement(new SQL(updateInvdMain, "update"));
          pstmt.setMaxRows(10);
          pstmt.setString(1,  com.csgsystems.domain.framework.security.SecurityManager.getInstance().getLoggedInUserId()+
          " |"+inventoryUnassign.getFormattedAttributeData("Reason"));
          pstmt.setInt(2, inventoryId);
          pstmt.setInt(3, inventoryIdResets);
        } catch (FxException fx) {
          fx.printStackTrace();
          logError("SQL Exception "+fx.getMessage(), null);
          return false;
        } catch (SQLException sEx) {
          sEx.printStackTrace();
          logError("SQL Exception "+sEx.getMessage(), null);
          return false;
        }
        try {
          int updateState = pstmt.executeUpdate();
           if(updateState <1 ) {
             logError("Record was not updated", null);
             return false;
           }
        }catch (SQLException e ) {
          e.printStackTrace();
          logError("SQL Exception "+e.getMessage(), null);
          return false;
        }
       
        ////////////////
        if (!service.sendMessage("unassignInvElement", parms)){
          
          if (log != null) {
              System.out.println("failed unassign inventory element for service");
              IPersistentObject order = OrderManager.getInstance().getCurrentOrder();  // Check for Current Order
              Iterator it = order.getError().getRelatedErrors().iterator();
              while ( it.hasNext() ) {
                  System.out.println("error = " + ((IError)it.next()).getErrorMessage());
              }
              
              log.error("Failed firing unassign request.");
          }
          
          IError err = service.getError();
          if (err != null){
            error = err;
          } else {
              System.out.println("no error found on service object!");
              error = ErrorFactory.createError("Error during unassign of inventory!", null, MXSInventoryUnassignContext.class);
          }
          
          return false;
        }
      } else {
        if (log != null) {log.error("Could not find service.");}
        return false;
      }
      
      removeTopic(inventoryUnassign);
      
    }
    return true;
  }
}

/**
 *  If OK is signaled from the closing dialog, perform the addAssociation
 *  from the source object to the target object
 *
 *@param  shutdownType  Description of the Parameter
 *@return               Description of the Return Value
 */
 /*  public boolean processShutdown(int shutdownType) {
  
    // user clicked ok
    if (shutdownType == OK) {
  
      IPersistentObject inventoryUnassign = getObject("InventoryUnassign", null);
      if (inventoryUnassign == null){
        if (log != null) {log.error("Unable to find inventoryUnassign object on context.");}
        return false;
      }
  
      // ********************************
      // fire unassign request
      // ********************************
      IPersistentObject invElement = inventoryUnassign.getObject("InvElement",null);
      if (invElement == null){
        if (log != null) {log.error("Unable to find invElement object on context.");}
        return false;
      }
      IPersistentObject service = getObject("Service",null);
      if (service != null){
        Map parms = new HashMap();
        //invElement.setAttributeDataAsString("Annotation3", "arbor123"+inventoryUnassign.getAttributeData("Reason"));
        parms.put("InvElement",invElement);
        if (!inventoryUnassign.getAttribute("Reason").isReadOnly()){
          parms.put("DisconnectReason","arbor123"+inventoryUnassign.getAttributeData("Reason"));
        }
        parms.put("StartDateTime",inventoryUnassign.getAttributeData("StartDateTime"));
        parms.put("IsPrimaryDisconnect",Boolean.TRUE);
  
        if (!service.sendMessage("unassignInvElement", parms)){
  
          if (log != null) {log.error("Failed firing unassign request.");}
  
          IError err = service.getError();
          if (err != null){
            error = err;
          }
  
          return false;
        }
      } else {
        if (log != null) {log.error("Could not find service.");}
        return false;
      }
      removeTopic(inventoryUnassign);
  
    }
  
    System.out.println("*** Annotation3 Added for InventoryUnassign");
    return true;
  }*/