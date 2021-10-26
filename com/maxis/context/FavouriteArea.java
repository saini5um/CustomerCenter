/*
 * FavouriteArea.java
 *
 * Created on July 6, 2005, 1:59 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import com.csgsystems.domain.framework.context.Context;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.maxis.businessobject.PrepaidNotes;
import com.csgsystems.domain.framework.IPersistentObjectFactory;
import com.csgsystems.domain.framework.security.SecurityManager;
import com.csgsystems.domain.framework.attribute.IntegerEnumeratedAttribute;

import com.csgsystems.fx.security.remote.*;
import com.csgsystems.fx.security.util.FxException;
import java.sql.*;
import com.maxis.util.*;
import com.maxis.xlogic.*;
import com.csgsystems.error.IError;
import com.csgsystems.error.ErrorFactory;
import java.util.HashMap;
import java.util.Map;
import java.lang.Long;
import com.csgsystems.cache.*;
import com.csgsystems.xlogic.*;
import com.csgsystems.domain.arbor.businessobject.Service;
import com.csgsystems.util.service.ServiceFinder;
import com.csgsystems.transport.IModelAdapter;
import com.csgsystems.transport.ModelAdapterFactory;

import com.maxis.integ.*;
import com.maxis.integ.IntegException;

/**
 *
 * @author Pankaj Saini
 */
public class FavouriteArea extends Context {

    protected int origArea = 0;
    protected ErrorReporting err = null;
    private String message = null;
    private String scpid = null;
    private double charge = 0;
    
    /** Creates a new instance of FriendsAndFamily */
    public FavouriteArea() {
        err = ErrorReporting.getInstance();
    }
    
    public boolean open(IContext context) {
        IPersistentObject service = context.getObject("Service", null);
        if (service != null) {
            addTopic(service, true);
            IPersistentCollection pColl = 
                    (IPersistentCollection)service.getObject("com.maxis.businessobject.PrepaidINList", "Service");
            if ( pColl == null )
                System.out.println("PrepaidINList collection is null...");
            IPersistentObject prepaidIN = pColl.getAt(0);
            if ( prepaidIN == null )
                System.out.println("PrepaidIN object is null...");
            
            scpid = prepaidIN.getAttributeDataAsString("SCPID");
            System.out.println("scpid = " + scpid);
            addTopic(prepaidIN, true);
            
            IPersistentObject FA = getObject("FavouriteArea", null);
            if ( prepaidIN.getAttributeDataAsString("INFetchStatus") != "Success" ) {
                FA.setReadOnly(true);
                err.logWarn("Connection to IN is currently unavailable", "Warning: IN connectivity", getClass());
//                setError(ErrorFactory.createError("Connection to IN is currently unavailable", 
//                        null, com.maxis.businessobject.FriendsAndFamily.class));
            } else {
                if ( prepaidIN.getAttributeDataAsInteger("CounterChangeFAnoForFree") == 0 ) {
                    charge = getCharge(service);
                    if ( prepaidIN.getAttributeDataAsInteger("OnPeakAccountIDBalance") - charge < 0 ) {
                        FA.setReadOnly(true);
                        err.logInfo("Insufficient funds to make change", "Message", getClass());                        
                    }
                }
            }
        }
        return true;
    }

    private int getCharge(IPersistentObject service) {
        //get the emf_config_id of the current service
        int emf_config_id = service.getAttribute("EmfConfigId").getDataAsInteger();
        Cache prepaidProviderInfoCache = null;
        
        //Get the Prepaid provider info cache
        if (prepaidProviderInfoCache == null) {
            // The cache is stored in FXExtendedLogicFactory class

            IExtendedLogicFactory factory = (IExtendedLogicFactory)
            ServiceFinder.findServiceProvider(com.csgsystems.xlogic.IExtendedLogicFactory.class, null);

            // Only try to extract cache if we are using FXExtenedLogicFactory otherwise
            // no cache would be present
            if (factory!=null && factory instanceof FXExtendedLogicFactory) {
                prepaidProviderInfoCache = ((FXExtendedLogicFactory)factory).getPrepaidProviderInfoCache();
            }
        }
        //get the provider info related to the current service
        Map providerInfo = prepaidProviderInfoCache.getCache();
        com.maxis.cache.PrepaidProviderInfo prepaidProviderInfo 
        = (com.maxis.cache.PrepaidProviderInfo)prepaidProviderInfoCache.get(new Long(emf_config_id));
        
        Map m = prepaidProviderInfo.getValue();
        PrintMap.printHashMap(m);
        Long FACharge = (Long)m.get("FA_CHARGE");
     
        return FACharge.intValue();
    }
    
    public boolean open(IPersistentObject object) {
        boolean success = false;

        if (object != null) {
            addTopic(object, true);
            origArea = object.getAttributeDataAsInteger("Area");
            success = true;
        }
        return success;
    }
    
    public boolean processShutdown(int shutdownType) {
        boolean success = true;
        
        if (shutdownType == CANCELLED) {
        } else if (shutdownType == OK) {
        
            IPersistentObject FA = getObject("FavouriteArea", null);
            IPersistentObject service = getObject("Service", null);
            int area = FA.getAttributeDataAsInteger("Area");
            int emfConfigId = service.getAttributeDataAsInteger("EmfConfigId");
            
            if ( area == 0 ) return success;
            if ( area == origArea ) return success;
            
            if ( success ) {
                // flush transaction to IN
                success = updateIN(service, FA);
            }
            if ( success ) {
                success = createNote(service, FA, origArea);
                // show message box with update transaction
                err.logInfo(message, "Message", getClass());
            } else {
                FA.setAttributeDataAsInteger("Area", origArea);
                addError(ErrorFactory.createError("Update to IN failed.", 
                        null, com.maxis.businessobject.FriendsAndFamily.class));
                return success;
            }
        }
        
        return success;
    }
    
    private boolean createNote(IPersistentObject service, IPersistentObject FA, int origArea) {
        boolean success = false;

        String userId = SecurityManager.getInstance().getLoggedInUserId();
        // *******************************
        // Create new Prepaid Notes object
        // *******************************           
        IPersistentObjectFactory poObjectFactory = PersistentObjectFactory.getFactory();
        IPersistentObject note = poObjectFactory.createNew("PrepaidNotes", null);
        if (note == null) {
            if (log != null) {log.debug("CreateNew of PrepaidNote failed");}
            setError("Failed to create Note", null);               
            return success;
        }

        note.setAttributeDataAsInteger("NoteId", 0);
        note.setAttributeDataAsInteger("SubscrNo", service.getAttributeDataAsInteger("ServiceInternalId"));
        note.setAttributeDataAsInteger("SubscrNoResets", service.getAttributeDataAsInteger("ServiceInternalIdResets"));
        note.setAttributeDataAsInteger("NoteCode", 7); // GUI_INDICATOR_REF.integer_value where field_name = note_code
        note.setAttributeDataAsString("ChangeWho", userId);
        note.setAttributeDataAsString("Remarks", " ");
        note.setAttributeDataAsInteger("Reason", 0);
        
        IntegerEnumeratedAttribute attr = (IntegerEnumeratedAttribute)FA.getAttribute("Area");
        
        String text = "Old " + FA.getAttributeDataAsString("Description") + ": ";
        text = text + attr.getDescription(attr.getRowIndex(Integer.toString(origArea))) + " ";
        text = text + "New " + FA.getAttributeDataAsString("Description") + ": ";
        text = text + attr.getFormattedData() + " ";
        if ( message == "Update Favourite Area Free of Charge")
            text = text + "Free Of Charge";
        else
            text = text + "RM " + (charge/100) + " charge";

        note.setAttributeDataAsString("NoteText", text);
        // flush note manually
//        System.out.println("Text = " + text);
        return note.flush(com.csgsystems.transport.ModelAdapterFactory.getModelAdapter());
    }

    private boolean updateIN(IPersistentObject service, IPersistentObject FA) {
        boolean success = false;
        try {
            Integrator t = Integrator.getInstance();
            HashMap input = new HashMap();
            HashMap header = new HashMap();
            HashMap body = new HashMap();
            input.put("Header", header);
            // pass in the current log in user id.
            // pass in TransType as FnFUpd
            header.put("TransType", "FAUpd");
            header.put("MSISDN", service.getAttributeDataAsString("ServiceExternalId"));
            input.put("Body", body);
            String descr = FA.getAttributeDataAsString("Description");
            int slot = Integer.parseInt(descr.substring(6));
            Integer a = new Integer(FA.getAttributeDataAsInteger("Area"));
            body.put("FADestination[" + (slot - 1) + "]", a.toString()); // IN integ expects destination as string
            IPersistentObject prepaidIN = getObject("PrepaidIN", null);
            if ( prepaidIN == null) {
                System.out.println("prepaidIN not found");
                return success;
            }
            body.put("SCPID", prepaidIN.getAttributeDataAsString("SCPID"));
            HashMap output = t.execute("FAUpd", input);
            HashMap outputHeader = (HashMap)output.get("Header");
            if (((Integer)outputHeader.get("ExecCode")).intValue() > 0) {
                success = true;
                HashMap outputBody = (HashMap)output.get("Body");
                Boolean balanceUpdated = (Boolean)outputBody.get("BalanceUpdated");
                if ( balanceUpdated.booleanValue() == true ) {
                    message = "Update FA with RM" + (charge/100) + " charge";
                    prepaidIN.setAttributeDataAsInteger("OnPeakAccountIDBalance", 
                            ((Integer)outputBody.get("OnPeakAccountID.Balance")).intValue());
                } else {
                    message = "Update Favourite Area Free of Charge";
                }
                prepaidIN.setAttributeDataAsInteger("CounterChangeFAnoForFree", 
                        ((Integer)outputBody.get("CounterChangeFAnoForFree")).intValue());

                //20050718 MH: Added
                prepaidIN.setAttributeDataAsString("FADestination"+(slot-1), FA.getAttributeDataAsString("Area"));
                // flush the new prepaid IN object
                IModelAdapter ma = ModelAdapterFactory.getModelAdapter();
                success = ((com.maxis.businessobject.PrepaidIN)prepaidIN).flush(ma, service);                
            }
        } catch (IntegException ie) {
            ie.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return success;
    }

}
