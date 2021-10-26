/*
 * Prepaid2PostpaidContext.java
 *
 * Created on May 11, 2005, 9:54 AM
 * Date         By          PN#                      Resolution
 * 20050826     kinyip      KenanFX-CC-General-89    To manually check for the Dealers/CSD mandatory field.
                                                     To eliminate null pointer when click on finish button,
                                                     as the wizard don't know how to show the error message if there is any.
 * 20050829     kinyip      KenanFX-CC-General-94    Change prompt messge to "Dealer/Center is required, but no value has been set."
 */

package com.maxis.context;

import com.csgsystems.domain.framework.context.Context;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.IPersistentObjectFactory;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.domain.framework.criteria.DataUtils;
import com.csgsystems.domain.arbor.order.*;
import com.csgsystems.util.net.http.CalendarUtils;
import java.util.Calendar;
import org.apache.commons.logging.*;
import com.csgsystems.fx.security.remote.*;
import com.csgsystems.fx.security.util.FxException;
import com.csgsystems.domain.framework.security.SecurityManager;
import com.csgsystems.transport.IModelAdapter;
import com.csgsystems.transport.ModelAdapterFactory;

import javax.swing.JOptionPane;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.maxis.util.*;

import com.maxis.integ.*;
import com.maxis.integ.IntegException;

/**
 *
 * @author  Pankaj Saini
 */
public class Prepaid2PostpaidContext extends Context {
  
  /** Creates a new instance of Prepaid2PostpaidContext */
  public Prepaid2PostpaidContext() {
  }
  
  private static Log log = null;
  
  // New Maxis code - Pankaj Saini (12-May-2005)
  private ErrorReporting err = ErrorReporting.getInstance();
  // End new Maxis code - Pankaj Saini (12-May-2005)
  
  static {
    try {
      log = LogFactory.getLog(Prepaid2PostpaidContext.class);
    }
    catch (Exception ex) {
    }
  }
  
  public boolean open(IPersistentObject obj){
    return false;
  }
  
  public boolean open(IContext context) {
    
    boolean success = false;
    if (context != null) {
      IPersistentObject service = context.getObject("Service",null);
      addTopic(service, true);    // non flushable
      
      //20050902 WMH: added, default the order fields
      IPersistentObject order = OrderManager.getInstance().getCurrentOrder();
      order.getAttribute("ContactCompany").setRequired(true);
      
      int emfConfigId = service.getAttributeDataAsInteger("EmfConfigId");
      
      // Check for prepaid SI. If not prepaid service then cannot open wizard
      if ( emfConfigId < 1000 || emfConfigId > 2000 ) {
        setError("Prepaid service is required for this wizard.", null);
        return false;
      }
      
      IPersistentCollection pColl =
      (IPersistentCollection)service.getObject("com.maxis.businessobject.PrepaidINList", "Service");
      if ( pColl == null )
        System.out.println("PrepaidINList collection is null...");
      IPersistentObject prepaidIN = pColl.getAt(0);
      if ( prepaidIN == null )
        System.out.println("PrepaidIN object is null...");
      
      addTopic(prepaidIN, true);
      
      IPersistentCollection pColl1 = (IPersistentCollection)service.getObject("PrepaidServiceList", "Service");
      if ( pColl1 != null ) {
        IPersistentObject prepaidService = (IPersistentObject)pColl1.getAt(0);
        addTopic(prepaidService, true);
      } else {
        log.error("PrepaidServiceList IPersistentCollection was null.");
        setError("PrepaidServiceList IPersistentCollection was null.", (Object[])null);
        return false;
      }
      
      IPersistentObject serviceDisconnect = context.getObject("ServiceDisconnect", null);
      
      if (serviceDisconnect == null){
        if (log != null) {log.info("Entering Context's 'Open by IContext' Creating new ServiceDisconnect");}
        // check if we can disconnect this service
        if (!canDisconnect(service)){
          return false;
        }
        
        // create new service
        success = createServiceDisconnect(service, context);
      }else{
        if (service != null){
          int internalId = service.getAttributeDataAsInteger("ServiceInternalId");
          int disconnectInternalId = serviceDisconnect.getAttributeDataAsInteger("ServiceInternalId");
          // in case an old disconnect is in the context when we started the wizard
          // verify that it is for the current service
          if (internalId == disconnectInternalId){
            // use disconnect object because it matches the service
            addTopic(serviceDisconnect,true );             // none flushable
            success = true;
          } else {
            // check if we can disconnect this service
            if (!canDisconnect(service)){
              return false;
            }
            // create new service
            success = createServiceDisconnect(service, context);
          }
          // New Maxis code - Pankaj Saini (17-May-2005)
          // The disconnect reason is being hard coded to 1006 (Pre-Post)
          serviceDisconnect.setAttributeDataAsInteger("DisconnectReason", 1006);
          serviceDisconnect.getAttribute("DisconnectReason").setReadOnly(true);
          if(serviceDisconnect.getAttribute("InactiveDate").isEmpty()){
            serviceDisconnect.setAttributeData("InactiveDate", new Date());
          }
          // End new Maxis code - Pankaj Saini (17-May-2005)
          
        } else {
          if (log != null) {log.debug("No Service object found in context.");}
          setError("ServiceDisconnectContext.open.service-not-found", null);
          success = false;
        }
      }
      
      //20050902 WMH KenanFX – CC – General-100: added, set the order date if the service disconnect date is not empty
      if(serviceDisconnect!=null && !serviceDisconnect.getAttribute("InactiveDate").isEmpty()){
        order.getAttribute("EffectiveDateWithTimestamp").setDataAsDate(serviceDisconnect.getAttribute("InactiveDate").getDataAsDate());
      }
    }else{
      if (log != null) {log.error("Context is null.");}
    }
    return success;
  }
  
  public boolean canDisconnect(IPersistentObject service){
    boolean success = true;
    // check if we can disconnect this service
    if (service != null){
      if (!service.sendMessage("canDisconnectService",null)){
        error = service.getError();
        success = false;
      }
    }
    return success;
  }
  
  public boolean createServiceDisconnect(IPersistentObject service, IContext context){
    boolean success = false;
    // *****************************
    // Create new ServiceDisconnect object
    // *****************************
    IPersistentObjectFactory poObjectFactory = PersistentObjectFactory.getFactory();
    IPersistentObject serviceDisconnect = poObjectFactory.createNew("ServiceDisconnect", null);
    if (serviceDisconnect != null) {
      addTopic(serviceDisconnect,true); // none flushable
    }
    else {
      if (log != null) {log.debug("CreateNew of serviceDisconnect failed");}
      setError("ServiceDisconnectContext.open.create-failed", null);
      return false;
    }
    
    // New Maxis code - Pankaj Saini (17-May-2005)
    // The disconnect reason is being hard coded to 1006 (Pre-Post)
    serviceDisconnect.setAttributeDataAsInteger("DisconnectReason", 1006);
    serviceDisconnect.getAttribute("DisconnectReason").setReadOnly(true);
    serviceDisconnect.setAttributeData("InactiveDate", new Date());
    // End new Maxis code - Pankaj Saini (17-May-2005)
    
    // *****************************
    // Get the account object from bigfoot
    // *****************************
    IPersistentObject account = context.getObject("Account",null);
    if (account != null){
      if (log != null) {log.debug("Found object on bigfoot.  Adding object '" + account.getClass() + "' to this context");}
      addTopic(account, true); // none flushabel
    } else {
      if (log != null) {log.error("Unable to find account object on Bigfoot context.");}
    }
    
    // *****************************
    // Get the service object from bigfoot
    // *****************************
    if (service != null) {
      if (log != null) {log.debug("Found object on bigfoot.  Adding object '" + service.getClass() + "' to this context");}
      
      addTopic(service, true); // none flushabel
      
      //Associate the service to the serviceDisconnect
      if (serviceDisconnect.addAssociation(service)) {
        if (log != null) {log.debug("Successfully associated service to serviceDisconnect");}
        success = true;
      }
      else {
        if (log != null) {log.error("Failed to associate service to serviceDisconnect");}
        setError("ServiceDisconnectContext.open.associate-service-failed", null);
        success = false;
      }
    }
    else {
      if (log != null) {log.error("Unable to find service object on Bigfoot context.");}
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
    boolean success = true;
    
    // get the ServiceDisconnect objects off of the context
    IPersistentObject serviceDisconnect = getObject("ServiceDisconnect", null);
    if (serviceDisconnect == null){
      if (log != null) {log.error("Unable to find serviceDisconnect object on context.");}
      return false;
    }
    
    //20050907 WMH KenanFX - CC - General-108:
    if(shutdownType == NEXT){
      if(serviceDisconnect.getAttribute("InactiveDate").isEmpty()){
        setError("Disconnect Date is not set.", null);
        return false;
      }
      else{
        Date InactiveDate = serviceDisconnect.getAttributeDataAsDate("InactiveDate");
        long InactiveDateMillis = InactiveDate.getTime();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        long TodayMillis = cal.getTime().getTime();
        System.out.println("InactiveDate="+InactiveDate);
        System.out.println("cal="+cal);
        if(InactiveDateMillis<TodayMillis){
          setError("Prepaid to post Disconnect Date cannot be backdated.", null);
          return false;
        }
      }
    }
    
    // user clicked Finish button in Service Disconnect wizard
    if (shutdownType == OK || shutdownType == FINISH) {
      
      //**********************************
      // set service order
      //**********************************
      // get the current order object
      
      IPersistentObject order = OrderManager.getInstance().getCurrentOrder();  // Check for Current Order
      if (order == null) {
        if (log != null) {log.error("No current order.");}
        return false;
      }
      
      //20050906 WMH PN KenanFX - CC - General-108: Added: check for back dated order
      if(serviceDisconnect.getAttribute("InactiveDate").isEmpty()){
        setError("Disconnect Date is not set.", null);
        return false;
      }
      else{
        Date InactiveDate = serviceDisconnect.getAttributeDataAsDate("InactiveDate");
        long InactiveDateMillis = InactiveDate.getTime();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        long TodayMillis = cal.getTime().getTime();
        System.out.println("InactiveDate="+InactiveDate);
        System.out.println("cal="+cal);
        if(InactiveDateMillis<TodayMillis){
          setError("Prepaid to post Disconnect Date cannot be backdated.", null);
          return false;
        }
      }
      
      // KenanFX–CC–General-89
      // 20050826 Modified by KY
      if(order.getAttributeDataAsString("ContactCompany") == null ||
      order.getAttribute("ContactCompany").isEmpty()) {
        // KenanFX–CC–General-94
        setError("Dealer/Center is required, but no value has been set.", null);
        return false;
      }
      
      IPersistentObject service = serviceDisconnect.getObject("Service",null);
      
      if( service==null){
        setError("Fail to get Service Object.", null);
        return false;
      }
     
      
      if(!updateIN(service)){
        setError("Call to IN fail.", null);
        return false;
      }
      
      // ********************************
      // create a new SERVICE DISCONNECT service order
      // *******************************
      IPersistentObjectFactory poObjectFactory = PersistentObjectFactory.getFactory();
      IPersistentObject serviceOrder = poObjectFactory.createNew("ServiceOrder", null);
      if (serviceOrder != null) {
        // the addAssociation logic in the ServiceOrder object sets the attributes on the ServiceOrder from the serviceDisconnect object.
        serviceOrder.addAssociation(serviceDisconnect);
        addTopic(serviceOrder, true );  // should be handled by Order flush
        
      } else {
        if (log != null) {log.error("CreateNew of ServiceOrder failed");}
        return false;
      }
      
      
      
      
      // Set effective date on order which intern sets the "CustDesireDate"
      // on the service order when the service order is associated to the order
      order.setAttributeData("EffectiveDateWithTimestamp", serviceDisconnect.getAttributeData("InactiveDate"));
      
      // add the service order to the current order
      order.addAssociation(serviceOrder);
      
      // add the service order to the service so it updates the list of service orders off of service
      //20050902 wmh move to above
      /*IPersistentObject service = serviceDisconnect.getObject("Service",null);*/
      if (service != null){
        serviceOrder.setSubtype("NewLogicalOrder");
        service.addAssociation(serviceOrder);
      }else{
        if (log != null) {log.error("failed to find service off of serviceDisconnect");}
      }
      
      addTopic(order);
      
      success = this.flush();
      
      if (!success){
        clearOrder(false);
      }else{
        // New Maxis code - Pankaj Saini (12-May-2005)
        // If order created successfully then call IN
        //20050902 WMH : move to high above.
        //updateIN(service);
        
        
        // If corba call is successful then database insert into custom table
        //                getLastCallDate(service); // May be is available by default as part of the new object
        updateDB(service, order);
        // End new Maxis code - Pankaj Saini (12-May-2005)
        //PN 200511/Interface - Number Retention-4
        //set value to E in mxs_service_ext
         updateMXSServiceExt(service);
        
        ////////////////////////////////////////////
        removeTopic(serviceDisconnect);
        
      }
      
    } else if (shutdownType == CANCELLED) {
      clearOrder(true);
      
      // New Maxis code - Pankaj Saini (17-May-2005)
      removeTopic(serviceDisconnect);
      // End new Maxis code - Pankaj Saini (17-May-2005)
    }
    
    return success;
  }
  
  private void updateMXSServiceExt(IPersistentObject service) {
  	//TLW 25.11.2005: changed sql query to include subscr_no_resets 
    String sql = "UPDATE MXS_SERVICE_EXT SET STATUS = 'E' WHERE SUBSCR_NO = ? AND SUBSCR_NO_RESETS = ?";
    
    try {
      RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CUST");
      if (dbConn == null) {
        err.logErrorToFile("Error getting current Oracle Connection", getClass());
      }
      PreparedStatement pstmt = dbConn.prepareStatement(new SQL(sql, "update"));
      pstmt.setInt(1, service.getAttributeDataAsInteger("ServiceInternalId"));
      pstmt.setInt(2, service.getAttributeDataAsInteger("ServiceInternalIdResets"));
      //End Change
      int res = pstmt.executeUpdate();
      pstmt.close();
      
    } catch (FxException fx) {
      setError("A Kenan Framework error has occured, please contact IT!!   "+ fx.getMessage(), null);
      err.logErrorToFile("FX Exception "+fx.getMessage(), getClass());
      fx.printStackTrace();
    } catch (java.sql.SQLException ex) {
      err.logErrorToFile("SQL Exception "+ex.getMessage(), getClass());
      setError("An Oracle error has occured on MXS_CC_PRE_POST table, please contact IT!! "+ ex.getMessage(), null);
      
    } catch (Exception e) {
      err.logErrorToFile("Exception ", getClass());
      e.printStackTrace();
    }
    
  }
  // New Maxis code - Pankaj Saini (21-Jul-2005)
  private boolean updateIN(IPersistentObject service) {
    boolean success = false;
    try {
      Integrator t = Integrator.getInstance();
      HashMap input = new HashMap();
      HashMap header = new HashMap();
      HashMap body = new HashMap();
      input.put("Header", header);
      // pass in the current log in user id.
      input.put("KenanUserID", SecurityManager.getInstance().getLoggedInUserId());
      
      // pass in TransType as CallBarring
      header.put("TransType", "CallBarring");
      header.put("MSISDN", service.getAttributeDataAsString("ServiceExternalId"));
      
      input.put("Body", body);
      IPersistentObject prepaidIN = getObject("PrepaidIN", null);
      if ( prepaidIN == null) {
        System.out.println("prepaidIN not found");
        return success;
      }
      body.put("SCPID", prepaidIN.getAttributeDataAsString("SCPID"));
      body.put("IsLocked", new Boolean(true));
      
      HashMap output = t.execute("CallBarring", input);
      HashMap outputHeader = (HashMap)output.get("Header");
      
      if (((Integer)outputHeader.get("ExecCode")).intValue() > 0) {
        success = true;
        HashMap outputBody = (HashMap)output.get("Body");
        if( ((Boolean)outputBody.get("IsLocked")).booleanValue() )
          prepaidIN.getAttribute("IsLocked").setDataAsInteger(1);
        else
          prepaidIN.getAttribute("IsLocked").setDataAsInteger(0);
        
        IPersistentObject prepaidService = getObject("PrepaidService", null);
        prepaidService.setAttributeDataAsInteger("CallBarStatus", 1);
        // flush the new prepaid IN object
        IModelAdapter ma = ModelAdapterFactory.getModelAdapter();
        success = ((com.maxis.businessobject.PrepaidIN)prepaidIN).flush(ma, service);
        success = prepaidService.flush(ma);
      }
    } catch (IntegException ie) {
      ie.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return success;
  }
  // End new Maxis code - Pankaj Saini (21-Jul-2005)
  
  // New Maxis code - Pankaj Saini (12-May-2005)
  private boolean updateDB(IPersistentObject service, IPersistentObject order) {
    boolean success = false;
    
    String SQL = "INSERT INTO MXS_PRE_POST ";
    SQL = SQL + "(msisdn, subscr_no, subscr_no_resets, amount, first_call_date, order_id) ";
    SQL = SQL + "values (?, ?, ?, ?, ?, ?)";
    
    try {
      RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CUST");
      if (dbConn == null) {
        err.logErrorToFile("Error getting current Oracle Connection", getClass());
        return success;
      }
      PreparedStatement pstmt = dbConn.prepareStatement(new SQL(SQL, "insert"));
      pstmt.setString(1, service.getAttributeDataAsString("ServiceExternalId"));
      pstmt.setInt(2, service.getAttributeDataAsInteger("ServiceInternalId"));
      pstmt.setInt(3, service.getAttributeDataAsInteger("ServiceInternalIdResets"));
      
      IPersistentObject prepaidIN = getObject("PrepaidIN", null);
      IPersistentObject prepaidService = getObject("PrepaidService", null);
      
      pstmt.setInt(4, prepaidIN.getAttributeDataAsInteger("OnPeakAccountIDBalance"));
      
      Date d = prepaidService.getAttributeDataAsDate("FirstCallDate");
      if ( d == null )
        pstmt.setTimestamp(5, SqlDate.Now());
      else pstmt.setTimestamp(5, new java.sql.Timestamp(d.getTime()));
      
      pstmt.setLong(6, order.getAttributeDataAsLong("OrderId"));
      
      int res = pstmt.executeUpdate();
      pstmt.close();
      
      success = true;
    } catch (FxException fx) {
      setError("A Kenan Framework error has occured, please contact IT!!   "+ fx.getMessage(), null);
      err.logErrorToFile("FX Exception "+fx.getMessage(), getClass());
      fx.printStackTrace();
      success = false;
    } catch (java.sql.SQLException ex) {
      err.logErrorToFile("SQL Exception "+ex.getMessage(), getClass());
      setError("An Oracle error has occured on MXS_CC_PRE_POST table, please contact IT!! "+ ex.getMessage(), null);
      success = false;
    } catch (Exception e) {
      err.logErrorToFile("Exception ", getClass());
      e.printStackTrace();
      success = false;
    }
    
    return success;
  }
  // End new Maxis code - Pankaj Saini (12-May-2005)
  
  private void clearOrder(boolean cancel){
      System.out.println("called cancel order with flag = " + cancel);
    // cleanup when shutdown/cancel even if error occured
    // if service order is on topic then remove it
      IPersistentObject order = OrderManager.getInstance().getCurrentOrder();  // Check for Current Order
      order.getAttribute("ContactCompany").setRequired(false); // fixing PN
      System.out.println("contactcompany has been set to not required attribute");
      
    IPersistentObject serviceOrder = getObject("ServiceOrder", null);
    if (serviceOrder != null){
      IPersistentObject service = getObject("Service",null);
      if (service != null){
        // remove from service
        service.removeAssociation(serviceOrder);
        if (cancel){
          removeTopic(service);
        }
      }
      
//      OrderManager.getInstance().clearCurrentOrder();
//      OrderManager.getInstance().
      if (order != null) {
        // remove from order
        order.removeAssociation(serviceOrder);
        
        removeTopic(order);
      }
      removeTopic(serviceOrder);
    }
  }
  
}
