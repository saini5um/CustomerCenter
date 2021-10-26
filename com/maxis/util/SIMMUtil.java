/*
 * SIMMUtil.java
 *
 * Created on November 23, 2005, 3:48 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.util;

import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.service.IServiceRequest;
import com.csgsystems.domain.framework.businessobject.Verb;
import com.csgsystems.transport.IModelAdapter;
import com.csgsystems.domain.framework.FrameworkObjectFactory;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.transport.ModelAdapterFactory;
import com.csgsystems.service.IServiceResponse;
import com.csgsystems.domain.framework.criteria.FilterCriteria;
import com.csgsystems.domain.framework.criteria.QueryCriteria;
import com.csgsystems.service.python.PythonServiceResponse;
import com.csgsystems.marshal.python.DictionaryPrettyPrinter;
import com.csgsystems.igpa.utils.ContextFinder;
import com.csgsystems.domain.framework.attribute.BooleanAttribute;
import com.csgsystems.domain.framework.businessobject.Domain;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Pankaj Saini
 */
public class SIMMUtil {
    
    private static Log log = null;
    
    /** Creates a new instance of SIMMUtil */
    public SIMMUtil() {
    }

    static  {
        try {
            log = LogFactory.getLog(com.maxis.util.SIMMUtil.class);
        } catch(Exception ex) { }
    }
    
    public static void printResponse(IServiceResponse sr, IPersistentObject po) {
        try {
            StringWriter sw = new StringWriter();
            DictionaryPrettyPrinter dpp = new DictionaryPrettyPrinter(sr.getByteArray());
            dpp.prettyPrint(new PrintWriter(sw));
            sw.close();
            if ( po != null ) log.debug(sw.toString());
            System.out.println(sw.toString());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static Map getFilters(String MSISDN) {
        HashMap parameters = new HashMap();
        parameters.put("Fetch", new Boolean(true));
        
        QueryCriteria extId = new QueryCriteria();
        extId.setFilterCriteria(FilterCriteria.createEquals(MSISDN));
        extId.setFetch(true);
        parameters.put("ServiceExternalId", extId);
        
        QueryCriteria extIdType = new QueryCriteria();
        extIdType.addFilterCriteria(FilterCriteria.createEquals(new Integer(23)));
        extIdType.setFetch(true);
        parameters.put("ServiceExternalIdType", extIdType);
        
        QueryCriteria viewStatus = new QueryCriteria();
        viewStatus.addFilterCriteria(FilterCriteria.createEquals(new Integer(2)));
        viewStatus.setFetch(true);
        parameters.put("ViewStatus", viewStatus);
        
        QueryCriteria status = new QueryCriteria();
        status.addFilterCriteria(FilterCriteria.createEquals(new Integer(1)));
        status.setFetch(true);
        parameters.put("StatusId", status);
        
        QueryCriteria inactiveDate = new QueryCriteria();
        inactiveDate.addFilterCriteria(FilterCriteria.createIsNull());
        inactiveDate.setFetch(true);
        parameters.put("ServiceInactiveDt", inactiveDate);
        
        return parameters;
    }
    
    private static Map getLinkFilters(String MSISDN) {
        HashMap parameters = new HashMap();
        parameters.put("Fetch", new Boolean(true));
        
        QueryCriteria extId = new QueryCriteria();
        extId.setFilterCriteria(FilterCriteria.createEquals(MSISDN));
        extId.setFetch(true);
        parameters.put("ServiceExternalId", extId);
        
        QueryCriteria extIdType = new QueryCriteria();
        extIdType.addFilterCriteria(FilterCriteria.createEquals(new Integer(Constant.SIMM_MISM_LINK)));
        extIdType.setFetch(true);
        parameters.put("ServiceExternalIdType", extIdType);
        
        QueryCriteria viewStatus = new QueryCriteria();
        viewStatus.addFilterCriteria(FilterCriteria.createEquals(new Integer(2)));
        viewStatus.setFetch(true);
        parameters.put("ViewStatus", viewStatus);
        
        QueryCriteria status = new QueryCriteria();
        status.addFilterCriteria(FilterCriteria.createEquals(new Integer(1)));
        status.setFetch(true);
        parameters.put("StatusId", status);
        
        QueryCriteria inactiveDate = new QueryCriteria();
        inactiveDate.addFilterCriteria(FilterCriteria.createIsNull());
        inactiveDate.setFetch(true);
        parameters.put("ServiceInactiveDt", inactiveDate);
        
        return parameters;
    }
    
    public static IPersistentCollection searchSecondaryServices(String MSISDN) {
        System.out.println("searching for secondary services of " + MSISDN);
        IPersistentCollection serviceList = null;
        
        Map parameters = getLinkFilters(MSISDN);

        // Create the request we need to make the call
        IServiceRequest request = FrameworkObjectFactory.getFactory().createRequest("ServiceExternalFindAll");
        if (request != null) {
            request.setRequestParameters(parameters);
            request.addEnvelopeParameter("AccountServer", new Integer(4));

            IModelAdapter server = ModelAdapterFactory.getModelAdapter();
            try {
                IServiceResponse response = server.executeService(request);
//                printResponse(response, serviceList);
                if (response != null) {
                    serviceList = (IPersistentCollection) PersistentObjectFactory.getFactory().create(response);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return serviceList;
    }
    
    public static boolean canCreateLink(String MSISDN) {
        boolean success = false;
        IPersistentCollection serviceList = null;
        System.out.println("now in can create link of util class");
        
        Map parameters = getFilters(MSISDN);

        // Create the request we need to make the call
        IServiceRequest request = FrameworkObjectFactory.getFactory().createRequest("ServiceExternalFindAll");
        if (request != null) {
            request.setRequestParameters(parameters);
            request.addEnvelopeParameter("AccountServer", new Integer(4));

            IModelAdapter server = ModelAdapterFactory.getModelAdapter();
            try {
                IServiceResponse response = server.executeService(request);
                printResponse(response, serviceList);
                if (response != null) {
                    serviceList = (IPersistentCollection) PersistentObjectFactory.getFactory().create(response);
                    if ( serviceList != null && serviceList.getCount() > 0 ) success = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        
        if ( serviceList != null ) {
            System.out.println("number of services in the list = " + serviceList.getCount());
            for ( int i = 0; i < serviceList.getCount(); i++ ) {
                success = isPrimary(serviceList.getAt(i));
                System.out.println("service " + i + " is primary = " + success);
            }
        } else System.out.println("failed to create service list");
        
        if ( success == true ) {
            // check for inactive or barred service
        }
       
        return success;
    }
    
    public static boolean isSecondaryValid(IPersistentObject service) {
        boolean valid = true;
        if ( isSecondary(service) )
            if ( !hasLink(service) ) valid = false;
        
        return valid;
    }
    
/*    public static void disconnectLink(IPersistentObject service) {
        if ( wasSecondary(service) && !isSecondary(service) ) {
            fireEvent();
        }
    }*/
    
    public static boolean isPostpaidGSM(IPersistentObject service) {
        return ( service.getAttributeDataAsInteger("EmfConfigId") == Constant.POSTPAID_GSM_EMF_CONFIG_ID );        
    }
    
    public static boolean wasSecondary(IPersistentObject service) {
        boolean result = false;

        if ( !isPostpaidGSM(service) ) return result;        
        IPersistentCollection assignedInventory = service.getCollection("InvElementList", "Service");
        
        for ( int i = 0; i < assignedInventory.getCount(); i++ ) {
            IPersistentObject invElement = assignedInventory.getAt(i);
            if ( !invElement.getAttribute("EndDateTime").isEmpty() ) {
                int invType = invElement.getAttributeDataAsInteger("InventoryTypeId");
                if ( invType == Constant.SIMM_SECONDARY_INV_TYPE || invType == Constant.SIMM_SECONDARY_USIM_INV_TYPE )
                    result = true;                
            }
        }
        
        return result;
    }
    
    public static boolean isSecondary(IPersistentObject service) {
        boolean result = false;

        if ( !isPostpaidGSM(service) ) return result;
        IPersistentCollection assignedInventory = service.getCollection("InvElementList", "Service");
        
        for ( int i = 0; i < assignedInventory.getCount(); i++ ) {
            IPersistentObject invElement = assignedInventory.getAt(i);
            if ( invElement.getAttribute("EndDateTime").isEmpty() ) {
                int invType = invElement.getAttributeDataAsInteger("InventoryTypeId");
                if ( invType == Constant.SIMM_SECONDARY_INV_TYPE || invType == Constant.SIMM_SECONDARY_USIM_INV_TYPE )
                    result = true;                
            }
        }
        
        return result;
    }
    
    public static boolean isPrimary(IPersistentObject service) {
        boolean result = false;
        
        if ( !isPostpaidGSM(service) ) return result;        
        IPersistentCollection assignedInventory = service.getCollection("InvElementList", "Service");
        
        for ( int i = 0; i < assignedInventory.getCount(); i++ ) {
            IPersistentObject invElement = assignedInventory.getAt(i);
            if ( invElement.getAttribute("EndDateTime").isEmpty() ) {
                int invType = invElement.getAttributeDataAsInteger("InventoryTypeId");
                if ( invType == Constant.SIMM_PRIMARY_INV_TYPE || invType == Constant.SIMM_PRIMARY_USIM_INV_TYPE )
                    result = true;                
            }
        }
        
        return result;
    }
    
    public static boolean hasLink(IPersistentObject service) {
        boolean result = false;
        IPersistentCollection ciemList = service.getCollection("CustomerIdEquipMapList", "Service");
        
        for ( int i = 0; i < ciemList.getCount(); i++ ) {
            IPersistentObject ciem = ciemList.getAt(i);
            if ( ciem.getAttribute("InactiveDate").isEmpty() && 
                    ciem.getAttributeDataAsInteger("ServiceExternalIdType") == Constant.SIMM_MISM_LINK )
                    result = true;
        }
        
        return result;        
    }
    
    public static void disconnectLink(IPersistentObject order, IPersistentObject service) {
        System.out.println("disconnect link called");
        IPersistentCollection soList = order.getCollection("ServiceOrderList", "Order");
        boolean isSimmDisconnect = false;
        boolean isSimmConnect = false;
        for ( int i = 0; i < soList.getCount(); i++ ) {
            IPersistentCollection iList = soList.getAt(i).getCollection("ItemList", "ServiceOrder");
            for ( int j = 0; j < iList.getCount(); j++ ) {
                IPersistentObject item = iList.getAt(j);
                if ( item.getAttributeDataAsInteger("MemberType") == Constant.INV_MEMBER_TYPE && 
                        ( item.getAttributeDataAsInteger("MemberId") == Constant.SIMM_SECONDARY_INV_TYPE ||
                        item.getAttributeDataAsInteger("MemberId") == Constant.SIMM_SECONDARY_USIM_INV_TYPE ) &&
                        item.getAttributeDataAsInteger("ItemActionId") == Constant.DISCONNECT_ACTION &&
                        item.getAttributeDataAsBoolean("IsCancelled") == false &&
                        item.getAttributeDataAsBoolean("TransientToBeCancelled") == false ) isSimmDisconnect = true;
                else if ( item.getAttributeDataAsInteger("MemberType") == Constant.INV_MEMBER_TYPE && 
                        ( item.getAttributeDataAsInteger("MemberId") == Constant.SIMM_SECONDARY_INV_TYPE ||
                        item.getAttributeDataAsInteger("MemberId") == Constant.SIMM_SECONDARY_USIM_INV_TYPE ) &&
                        item.getAttributeDataAsInteger("ItemActionId") == Constant.ADD_ACTION &&
                        item.getAttributeDataAsBoolean("IsCancelled") == false &&
                        item.getAttributeDataAsBoolean("TransientToBeCancelled") == false ) isSimmConnect = true;
            }
        }
        if ( isSimmDisconnect == true && isSimmConnect == false ) doDisconnect(service);
    }
    
    public static void changeLinks(IPersistentObject order, IPersistentObject service) {
        System.out.println("called change links");
        IPersistentCollection soList = order.getCollection("ServiceOrderList", "Order");
        boolean isMsisdnDisconnect = false;
        boolean isMsisdnConnect = false;
        String oldMsisdn = null;
        String newMsisdn = null;
        for ( int i = 0; i < soList.getCount(); i++ ) {
            IPersistentCollection iList = soList.getAt(i).getCollection("ItemList", "ServiceOrder");
            for ( int j = 0; j < iList.getCount(); j++ ) {
                IPersistentObject item = iList.getAt(j);
                if ( item.getAttributeDataAsInteger("MemberType") == Constant.INV_MEMBER_TYPE && 
                        ( item.getAttributeDataAsInteger("MemberId") == Constant.POSTPAID_REGULAR_INV_TYPE ||
                        item.getAttributeDataAsInteger("MemberId") == Constant.POSTPAID_SPECIAL_INV_TYPE ||
                        item.getAttributeDataAsInteger("MemberId") == Constant.POSTPAID_GOLDEN_INV_TYPE ) &&
                        item.getAttributeDataAsInteger("ItemActionId") == Constant.DISCONNECT_ACTION &&
                        item.getAttributeDataAsBoolean("IsCancelled") == false &&
                        item.getAttributeDataAsBoolean("TransientToBeCancelled") == false ) 
                    oldMsisdn = item.getObject("InvElement", "Item").getAttributeDataAsString("ExternalId");
                else if ( item.getAttributeDataAsInteger("MemberType") == Constant.INV_MEMBER_TYPE && 
                        ( item.getAttributeDataAsInteger("MemberId") == Constant.POSTPAID_REGULAR_INV_TYPE ||
                        item.getAttributeDataAsInteger("MemberId") == Constant.POSTPAID_SPECIAL_INV_TYPE ||
                        item.getAttributeDataAsInteger("MemberId") == Constant.POSTPAID_GOLDEN_INV_TYPE ) &&
                        item.getAttributeDataAsInteger("ItemActionId") == Constant.ADD_ACTION &&
                        item.getAttributeDataAsBoolean("IsCancelled") == false &&
                        item.getAttributeDataAsBoolean("TransientToBeCancelled") == false ) 
                    newMsisdn = item.getObject("InvElement", "Item").getAttributeDataAsString("ExternalId");
            }
        }
        if ( oldMsisdn != null && newMsisdn != null ) findAndChangeLinks(order, oldMsisdn, newMsisdn);
    }
    
    private static void findAndChangeLinks(IPersistentObject order, String oldLink, String newLink) {
        System.out.println("changing link " + oldLink + " to " + newLink);
        if ( oldLink != null ) {
            IPersistentCollection serviceList = searchSecondaryServices(oldLink);
            if ( serviceList == null ) return;
            HashMap services = new HashMap();
            for ( int i = 0; i < serviceList.getCount(); i++ ) {
                IPersistentObject service = serviceList.getAt(i);
                if ( services.get(service.getAttributeData("ServiceInternalId")) == null ) {
                    changeLink(service, order, newLink);
                    services.put(service.getAttributeData("ServiceInternalId"), "1");
                }
            }
        }
    }
    
    private static String[] findLink(IPersistentObject service) {
        String link[] = {null, null};
        IPersistentCollection ciemList = service.getCollection("CustomerIdEquipMapList", "Service");
        
        for ( int i = 0; i < ciemList.getCount(); i++ ) {
            IPersistentObject ciem = ciemList.getAt(i);
            if ( ciem.getAttributeDataAsInteger("ServiceExternalIdType") == 23 )
                 if ( ciem.getAttribute("InactiveDate").isEmpty() ) 
                     link[0] = ciem.getAttributeDataAsString("ServiceExternalId");
                 else link[1] = ciem.getAttributeDataAsString("ServiceExternalId");
        }
        
        return link;
    }
    
    private static void changeLink(IPersistentObject service, IPersistentObject order, String newLink) {
        System.out.println("changing link for service " + service.getAttributeDataAsInteger("ServiceInternalId") + " to " + newLink + " now!");
        doDisconnect(service);
        addLink(service, order, newLink);
        boolean result = service.flush(ModelAdapterFactory.getModelAdapter());
        System.out.println("flush service response is " + result);
    }
    
    private static void addLink(IPersistentObject service, IPersistentObject order, String link) {
        IPersistentObject newObj = PersistentObjectFactory.getFactory().createNew("CustomerIdEquipMap", null);
        newObj.setAttributeData("ActiveDate", order.getAttributeData("EffectiveDateWithTimestamp"));
        newObj.setAttributeDataAsString("ServiceExternalId", link);
        newObj.setAttributeDataAsInteger("ServiceExternalIdType", Constant.SIMM_MISM_LINK);
        BooleanAttribute dontCheck = new BooleanAttribute("Check", (Domain)newObj, false);
        newObj.setAttributeDataAsBoolean("Check", false);
        service.addAssociation(newObj);
    }
    
    private static void doDisconnect(IPersistentObject service) {
        System.out.println("do disconnect called for service " + service.getAttributeDataAsString("ServiceInternalId"));
        IPersistentCollection ciemList = service.getCollection("CustomerIdEquipMapList", "Service");
        
        for ( int i = 0; i < ciemList.getCount(); i++ ) {
            IPersistentObject ciem = ciemList.getAt(i);
            if ( ciem.getAttribute("InactiveDate").isEmpty() && 
                    ciem.getAttributeDataAsInteger("ServiceExternalIdType") == Constant.SIMM_MISM_LINK ) {
                System.out.println("disconnect external id = " + ciem.getAttributeDataAsString("ServiceExternalId"));
                if ( ciem.isNewObject() ) service.removeAssociation(ciem);
                else {
                    HashMap params = new HashMap();
                    params.put("CustomerIdEquipMap", ciem);
                    System.out.println("calling disconnectExternalId on " + service.getAttributeDataAsInteger("ServiceInternalId"));
                    boolean result = service.sendMessage("disconnectExternalId", params);
                    System.out.println("sent disconnect message to service. response is " + result);
                }
            }
        }
        
    }
}
