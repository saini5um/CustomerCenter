/*
 * MXSAccountSearchList.java
 *
 * Created on October 7, 2005, 5:13 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.businessobject;

import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.fxcommon.attribute.*;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.service.IServiceRequest;
import com.csgsystems.service.IServiceResponse;
import com.csgsystems.transport.ModelAdapterFactory;
import com.csgsystems.transport.IModelAdapter;
import com.csgsystems.domain.framework.security.SecurityManager;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

/**
 * Collection class represents list of Maxis Account Search objects
 * @author Pankaj Saini
 */

public class MXSAccountSearchList extends DomainCollection {
    
    protected Map searchParams;
    protected IPersistentObject account;
    protected IntegerAttribute MasaFraudCount;
    
    /** Creates a new instance of MXSAccountSearchList */
    public MXSAccountSearchList() {
        super(MXSAccountSearch.class);
        MasaFraudCount = new IntegerAttribute("MasaFraudCount", this, false);
        try {
	        boolean nonMasaUser = false;
            if(SecurityManager.getInstance().isAuthorized("Maxis.NonMasa", SecurityManager.SERVICE_RSRC))
                nonMasaUser = true;
            //log.info("masa auth="+SecurityManager.getInstance().isAuthorized("Maxis.NonMasa", SecurityManager.SERVICE_RSRC));
            //log.info("nonMasaUser=" + nonMasaUser);

            BooleanAttribute m_MXSMasa = new BooleanAttribute("MXSMasa", (Domain)this, false);
            m_MXSMasa.setDataAsBoolean(!nonMasaUser);
            //log.info("m_MXSMasa.getDataAsBoolean=" + m_MXSMasa.getDataAsBoolean());
        }
        catch(Exception e) {
            log.error(e.toString());
        }
    }
    
    public void setSearchParameters(Map m) {
        searchParams = m;
    }
    
    protected int listPageFault(int nStart) {
        IPersistentCollection coll = (IPersistentCollection) PersistentObjectFactory.getFactory().createNew("AccountLocateList", null);
		int fraudCount = 0;
		
        if (coll != null)
        {
            coll.setAttributeDataAsString("AccountExternalId", (String)searchParams.get("Value"));
            coll.setAttributeDataAsInteger("AccountExternalIdType", ((Integer)searchParams.get("Type")).intValue());
            coll.reset();
            
            for ( int i = 0; i < coll.getCount(); i++ ) {
                IPersistentObject al = coll.getAt(i);
                if ( al == null ) { logError("Null account locate in the list after reset!", null); }
                
                IPersistentObject as = PersistentObjectFactory.getFactory().createNew("MXSAccountSearch", null);
                if ( as == null ) { logError("Unable to create MXSAccountSearch object", null); }
                as.addAssociation(al);
                
                if ( getAccount(al) ) {
                    as.addAssociation(account);
                    as.setAttributeDataAsString("SumBalance", account.getFormattedAttributeData("SumBalance"));
                    as.setAttributeDataAsInteger("ActiveServices", getActiveServiceCount());
                } else {
//                    addError();
                }
                add(as);
                
                if(account!=null){
	                if(account.getAttributeDataAsInteger("CollectionStatus")==3){
		                fraudCount++;
	                }
                }
            }
        }
        
        MasaFraudCount.setDataAsInteger(fraudCount);
        
        return 0;
    }

    private int getActiveServiceCount() {
        int count = 0;
        IPersistentCollection serviceList = (IPersistentCollection) account.getObject("ServiceList", "Account");
        if ( serviceList != null ) {
            count = serviceList.getCount();
        } else { logDebug("Cannot determine service Count. serviceList collection is null"); }
        
        return count;
    }
    
    private boolean getAccount(IPersistentObject acctLocate) {
        account = acctLocate.getObject("Account", "AccountLocate");
        if ( account == null ) return false;
        
        return true;
    }
    
    private boolean getAccount_deprecate(IPersistentObject acctLocate) {
        boolean success = false;
        
        Map parameters = new HashMap();
        parameters.put("AccountInternalId", new Integer(acctLocate.getAttributeDataAsInteger("AccountInternalId")));
        parameters.put("Fetch", "true");
        
        // Create the request we need to make the call
        IServiceRequest request = fwkObjectFactory.createRequest("AccountGet");
        if (request != null) {
            request.setRequestParameters(parameters);

            IModelAdapter server = ModelAdapterFactory.getModelAdapter();
//            DomainFrameworkEventManager.getInstance().fireBusy(true);

            try {
                IServiceResponse response = server.executeService(request);
                if (response != null) {
                    account = PersistentObjectFactory.getFactory().create(response);
                    if (account == null) {
                        acctLocate.setError("CC-2-22", null);
                    } else { success = true; }
                } else {
                    acctLocate.setError("CC-2-23", null);
                }
            } catch (Exception ex) {
                Object[] inserts = {ex.getMessage()};
                setError("CC-2-24", inserts);
            } finally {
//                DomainFrameworkEventManager.getInstance().fireBusy(false);
            }
        } else {
            Object[] inserts = {"AccountGet"};
            setError("CC-2-25", inserts);
        }        
        
        return success;
    }
    
}
