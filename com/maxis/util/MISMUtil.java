/*
 * MISMUtil.java
 *
 * Created on November 27, 2005, 3:48 PM
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
public class MISMUtil {
    
    private static Log log = null;
//    public static final int PRIMARY_INV_TYPE = 424;
    public static final int PRIMARY_INV_TYPE = 650;
    public static final int SECONDARY_INV_TYPE = 680;
    public static final int SECONDARY_EMF_CONFIG = 4003;
    
    /** Creates a new instance of SIMMUtil */
    public MISMUtil() {
    }

    static  {
        try {
            log = LogFactory.getLog(com.maxis.util.MISMUtil.class);
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
    
    private static Map getFilters(String MSISDN) { // review
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
    
    private static Map getLinkFilters(String MSISDN) { // review
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
    
    public static boolean canCreateLink(String MSISDN, int accountNo) {
        boolean success = false;
        IPersistentCollection serviceList = null;
        System.out.println("now in can create link of util class");
        
        Map parameters = getFilters(MSISDN);

        // Create the request we need to make the call
        IServiceRequest request = FrameworkObjectFactory.getFactory().createRequest("ServiceExternalFind");
        if (request != null) {
            request.setRequestParameters(parameters);

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
                IPersistentObject service = serviceList.getAt(i);
                if ( isPrimary(service) && service.getAttributeDataAsInteger("ParentAccountInternalId") == accountNo )
                    success = true;
                System.out.println("service " + i + " is primary = " + success);
            }
        } else System.out.println("failed to create service list");
        
        if ( success == true ) {
            // check for inactive or barred service
        }
       
        return success;
    }
    
    public static boolean isSecondary(IPersistentObject service) {
        return ( service.getAttributeDataAsInteger("EmfConfigId") == SECONDARY_EMF_CONFIG );
    }
    
    public static boolean isPrimary(IPersistentObject service) {
        boolean result = false;
        
        if ( !SIMMUtil.isPostpaidGSM(service) ) return result;        
        IPersistentCollection assignedInventory = service.getCollection("InvElementList", "Service");
        
        for ( int i = 0; i < assignedInventory.getCount(); i++ ) {
            IPersistentObject invElement = assignedInventory.getAt(i);
            if ( invElement.getAttribute("EndDateTime").isEmpty() ) {
                int invType = invElement.getAttributeDataAsInteger("InventoryTypeId");
                System.out.println("inventory type = " + invType);
                if ( invType == PRIMARY_INV_TYPE )
                    result = true;                
            }
        }
        
        return result;
    }
    
    public static boolean hasSecondary(IPersistentObject account) {
        boolean result = false;
        IPersistentCollection serviceList = (IPersistentCollection)account.getObject("ServiceList", "Account");
        for ( int i = 0; i < serviceList.getCount(); i++ ) {
            IPersistentObject service = serviceList.getAt(i);
            boolean isSecondary = isSecondary(service);
            if ( isSecondary ) return isSecondary;
        }
        
        return result;
    }
    
    public static boolean hasPrimary(IPersistentObject account) {
        boolean result = false;
        IPersistentCollection serviceList = (IPersistentCollection)account.getObject("ServiceList", "Account");
        for ( int i = 0; i < serviceList.getCount(); i++ ) {
            IPersistentObject service = serviceList.getAt(i);
            boolean isPrimary = isPrimary(service);
            if ( isPrimary ) return isPrimary;
        }
        
        return result;
    }
}
