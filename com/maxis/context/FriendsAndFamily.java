/*
 * FriendsAndFamily.java
 *
 * Created on June 15, 2005, 1:59 PM
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
import com.csgsystems.domain.framework.IPersistentObjectFactory;
import com.maxis.businessobject.PrepaidNotes;
import com.csgsystems.domain.framework.criteria.DataUtils;
import com.csgsystems.domain.framework.security.SecurityManager;

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
public class FriendsAndFamily extends Context {

    protected String origNumber = null;
    protected ErrorReporting err = null;
    private String message = null;
    private String scpid = null;
    private double charge = 0;

    /** Creates a new instance of FriendsAndFamily */
    public FriendsAndFamily() {
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

            IPersistentObject FnF = getObject("FriendsAndFamily", null);
            if ( prepaidIN.getAttributeDataAsString("INFetchStatus") != "Success" ) {
                FnF.setReadOnly(true);
                err.logWarn("Connection to IN is currently unavailable", "Warning: IN connectivity", getClass());
//                setError(ErrorFactory.createError("Connection to IN is currently unavailable",
//                        null, com.maxis.businessobject.FriendsAndFamily.class));
            } else {
                if ( prepaidIN.getAttributeDataAsInteger("CounterChangeFnFnoForFree") == 0 ) {
                    charge = getCharge(service);
                    if ( prepaidIN.getAttributeDataAsInteger("OnPeakAccountIDBalance") - charge < 0 ) {
                        FnF.setReadOnly(true);
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
        Long FnFCharge = (Long)m.get("FNF_CHARGE");

        return FnFCharge.intValue();
    }

    public boolean open(IPersistentObject object) {
        boolean success = false;

        if (object != null) {
            addTopic(object, true);
            origNumber = object.getAttributeDataAsString("Number");
            success = true;
        }
        return success;
    }

    public boolean processShutdown(int shutdownType) {
        boolean success = true;

        if (shutdownType == CANCELLED) {
            IPersistentObject FnF = getObject("FriendsAndFamily", null);
            FnF.setAttributeDataAsString("Number",origNumber);
        } else if (shutdownType == OK) {

            IPersistentObject FnF = getObject("FriendsAndFamily", null);

            //20050718 MH: Added
            if(FnF.isReadOnly()){
                //IF the object is readonly, there is nothing to do, just exit the form
                return true;
            }

            IPersistentObject service = getObject("Service", null);
            String number = FnF.getAttributeDataAsString("Number");
            int emfConfigId = service.getAttributeDataAsInteger("EmfConfigId");

            if ( number == null || number.length() == 0 ) return success;
            if ( number == origNumber ) return success;

            success = checkWhitelist(emfConfigId, number);
            if ( success ) {
                success = checkBlacklist(emfConfigId, number);
            } else {
                FnF.setAttributeDataAsString("Number", origNumber);
                addError(ErrorFactory.createError("The number you have entered is not valid.",
                        null, com.maxis.businessobject.FriendsAndFamily.class));
                return success;
            }
            if ( success ) {
                // flush transaction to IN
                success = updateIN(service, FnF);
            } else {
                FnF.setAttributeDataAsString("Number", origNumber);
                addError(ErrorFactory.createError("The number you have entered is not valid.",
                        null, com.maxis.businessobject.FriendsAndFamily.class));
                return success;
            }
            if ( success ) {
                success = createNote(service, FnF, origNumber);
                // show message box with update transaction
                err.logInfo(message, "Message", getClass());
            } else {
                FnF.setAttributeDataAsString("Number", origNumber);
                addError(ErrorFactory.createError("Update to IN failed.",
                        null, com.maxis.businessobject.FriendsAndFamily.class));
                return success;
            }
        }

        return success;
    }
    
    private boolean createNote(IPersistentObject service, IPersistentObject FnF, String origNumber) {
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
        note.setAttributeDataAsInteger("NoteCode", 6); // GUI_INDICATOR_REF.integer_value where field_name = note_code
        note.setAttributeDataAsString("ChangeWho", userId);
        note.setAttributeDataAsString("Remarks", " ");
        note.setAttributeDataAsInteger("Reason", 0);
        
        String text = "Old " + FnF.getAttributeDataAsString("Description") + ": ";
        text = text + origNumber + " ";
        text = text + "New " + FnF.getAttributeDataAsString("Description") + ": ";
        text = text + FnF.getAttributeDataAsString("Number") + " ";
        if ( message == "Update FnF Free Of Charge")
            text = text + "Free Of Charge";
        else
            text = text + "RM " + (charge/100) + " charge";

        note.setAttributeDataAsString("NoteText", text);
        // flush note manually
//        System.out.println("Text = " + text);
        return note.flush(com.csgsystems.transport.ModelAdapterFactory.getModelAdapter());
    }

    private boolean updateIN(IPersistentObject service, IPersistentObject FnF) {
        boolean success = false;
        try {
            Integrator t = Integrator.getInstance();
            HashMap input = new HashMap();
            HashMap header = new HashMap();
            HashMap body = new HashMap();
            input.put("Header", header);
            // pass in the current log in user id.
            String userId = SecurityManager.getInstance().getLoggedInUserId();
            input.put("UserId", userId);
            
            // pass in TransType as FnFUpd
            header.put("TransType", "FnFUpd");
            header.put("MSISDN", service.getAttributeDataAsString("ServiceExternalId"));

            input.put("Body", body);
            String descr = FnF.getAttributeDataAsString("Description");
            int slot = Integer.parseInt(descr.substring(6));
            body.put("FnFNumber[" + (slot - 1) + "]", FnF.getAttributeDataAsString("Number"));
            IPersistentObject prepaidIN = getObject("PrepaidIN", null);
            if ( prepaidIN == null) {
                System.out.println("prepaidIN not found");
                return success;
            }
            body.put("SCPID", prepaidIN.getAttributeDataAsString("SCPID"));
            
            
            HashMap output = t.execute("FnFUpd", input);
            HashMap outputHeader = (HashMap)output.get("Header");
            
            if (((Integer)outputHeader.get("ExecCode")).intValue() > 0) {
                success = true;
                HashMap outputBody = (HashMap)output.get("Body");
                //13-Jul-2005 MH: change
                //Boolean balanceUpdated = (Boolean)output.get("BalanceUpdated");
                Boolean balanceUpdated = (Boolean)outputBody.get("BalanceUpdated");
                //13-Jul-2005 MH: removed
                //balanceUpdated = new Boolean(false);
                if ( balanceUpdated.booleanValue() == true ) {
                    message = "Update FnF with RM" + (charge/100) + " charge";
                    prepaidIN.setAttributeDataAsInteger("OnPeakAccountIDBalance",
                            ((Integer)outputBody.get("OnPeakAccountID.Balance")).intValue());
                } else {
                    message = "Update FnF Free Of Charge";
                }
                //20050718 MH: Added
                prepaidIN.setAttributeDataAsString("FnFNumber" + (slot - 1), FnF.getAttributeDataAsString("Number"));
                prepaidIN.setAttributeDataAsInteger("CounterChangeFnFnoForFree",
                        ((Integer)outputBody.get("CounterChangeFnFnoForFree")).intValue());
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

    private boolean checkBlacklist(int emfConfigId, String number) {
        boolean success = true;
        String SQL = "SELECT COUNT(1) FROM MXS_BLACKLIST ";
        SQL = SQL + "WHERE EMF_CONFIG_ID = " + emfConfigId + " ";
        SQL = SQL + "WHERE '" + number + "' LIKE MSISDN||'%'";

        try {
            RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("ADMIN");
            if (dbConn == null) {
                logError("Error getting current Oracle Connection", null);
               return false;
            }
            PreparedStatement pstmt = dbConn.prepareStatement(new SQL(SQL, "select"));
            pstmt.setMaxRows(100);
            ResultSet result = pstmt.executeQuery();

            int count = 0;
            for (int j=0; result.next() ; j++) {
                count = result.getInt(1);
            }

            if ( count > 0 ) success = false;
            pstmt.close();

        } catch (FxException fx) {
            logError("FX Exception "+fx.getMessage(), null);
            fx.printStackTrace();
        } catch (java.sql.SQLException ex) {
            logError("SQL Exception "+ex.getMessage(), null);
        } catch (Exception e) {
            logError("Exception ", null);
            e.printStackTrace();
        }
        return success;
    }

    private boolean checkWhitelist(int emfConfigId, String number) {
        boolean success = true;

        String SQL = "SELECT COUNT(1) FROM MXS_WHITELIST ";
        SQL = SQL + "WHERE EMF_CONFIG_ID = " + emfConfigId + " ";
        SQL = SQL + "AND '" + number + "' LIKE MSISDN||'%'";

        try {
            RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("ADMIN");
            if (dbConn == null) {
                logError("Error getting current Oracle Connection", null);
               return false;
            }
            PreparedStatement pstmt = dbConn.prepareStatement(new SQL(SQL, "select"));
            pstmt.setMaxRows(100);
            ResultSet result = pstmt.executeQuery();

            int count = 0;
            for (int j=0; result.next() ; j++) {
                count = result.getInt(1);
            }

            if ( count == 0 ) success = false;
            pstmt.close();

        } catch (FxException fx) {
            logError("FX Exception "+fx.getMessage(), null);
            fx.printStackTrace();
        } catch (java.sql.SQLException ex) {
            logError("SQL Exception "+ex.getMessage(), null);
        } catch (Exception e) {
            logError("Exception ", null);
            e.printStackTrace();
        }

        return success;
    }
}
