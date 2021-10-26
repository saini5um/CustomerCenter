/*
 * CustomerIdEquipMapValidator.java
 *
 * Created on November 24, 2005, 9:41 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.xvalidation;

import com.csgsystems.validator.ValidatorBase;
import com.csgsystems.domain.framework.IFrameworkObject;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.error.ErrorFactory;
import com.csgsystems.localization.ResourceManager;
import com.csgsystems.fx.security.remote.*;
import com.csgsystems.fx.security.util.FxException;

import com.maxis.util.*;
import javax.swing.JOptionPane;
import java.sql.*;
//import java.lang.IllegalArgumentException;

/**
 *
 * @author Pankaj Saini
 */
public class CustomerIdEquipMapValidator extends ValidatorBase {
    
    /** Creates a new instance of CustomerIdEquipMapValidator */
    public CustomerIdEquipMapValidator() {
        super();
    }
    
    public boolean isValid(IFrameworkObject obj) {
        boolean valid = true;
        if ( obj instanceof IPersistentObject ) {
            IPersistentObject pCiem = (IPersistentObject) obj;
            if ( !pCiem.isNewObject() ) return valid;
            boolean check = true;
            try {
                check = pCiem.getAttributeDataAsBoolean("Check");
            } catch (IllegalArgumentException e) { }
            if ( pCiem.getAttributeDataAsInteger("ServiceExternalIdType") == Constant.SIMM_MISM_LINK &&
                    pCiem.getAttributeDataAsBoolean("IsForDisconnect") != true && check == true ) {
                IPersistentObject service = pCiem.getObject("Service");
                if ( service != null ) {
                    if ( SIMMUtil.isSecondary(service) ) {
                        if ( !SIMMUtil.canCreateLink(pCiem.getAttributeDataAsString("ServiceExternalId")) ) {
                            int option = JOptionPane.showConfirmDialog(null, ResourceManager.getString("MSA.account.link.option"), 
                                    ResourceManager.getString("Alert!"), JOptionPane.YES_NO_OPTION);
                            boolean success;
                            if ( option == JOptionPane.YES_OPTION ) {
                                success = informAMP(service.getObject("Account"), 1, pCiem);
                            }
                            valid = false;
                            obj.addError(ErrorFactory.createError("MX-1-2", null, obj.getClass()));
                        }
                    } else if ( MISMUtil.isSecondary(service) ) {
                        int acctNo = service.getObject("Account").getAttributeDataAsInteger("AccountInternalId");
                        if ( !MISMUtil.canCreateLink(pCiem.getAttributeDataAsString("ServiceExternalId"), acctNo) ) {
                            valid = false;
                            obj.addError(ErrorFactory.createError("MX-2-2", null, obj.getClass()));
                        }
                    } else {
                        valid = false;
                        obj.addError(ErrorFactory.createError("MX-1-3", null, obj.getClass()));                        
                    }
                }
            }
        }
        
        return valid;
    }
    
    private boolean informAMP(IPersistentObject sourceAcct, int targetAccountServer, IPersistentObject ciem) {
        boolean success = true;
        try {
            RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CAT");
            if (dbConn == null) {
                logError("Error getting current Oracle Connection", null);
               return success;
            }
            CallableStatement cs = dbConn.prepareCall(new SQL("MXS_ADD_AMP_ACCT", "proc"));
            cs.setString(1, sourceAcct.getAttributeDataAsString("AccountExternalId"));
            cs.setLong(2, sourceAcct.getAttributeDataAsInteger("AccountInternalId"));
            cs.setInt(3, sourceAcct.getAttributeDataAsInteger("AccountServer"));
            cs.setInt(4, targetAccountServer);
            cs.setLong(5, 0);
            cs.registerOutParameter(6, Types.NUMERIC);
            cs.registerOutParameter(7, Types.VARCHAR);

            ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cs).getParms().get(5)).registerAsOutParameter();
            ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cs).getParms().get(5)).setNullParm(false);
            ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cs).getParms().get(5)).setValue(new Integer(0));
            ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cs).getParms().get(6)).registerAsOutParameter();
            ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cs).getParms().get(6)).setNullParm(false);
            ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cs).getParms().get(6)).setValue("N/A");
            
            logDebug("calling stored proc MXS_ADD_AMP_ACCT(" + sourceAcct.getAttributeDataAsString("AccountExternalId") 
                    + ", " + sourceAcct.getAttributeDataAsInteger("AccountInternalId")
                    + ", " + sourceAcct.getAttributeDataAsInteger("AccountServer") + ", " + targetAccountServer + ", 0)");
            int result = cs.executeUpdate();
            int errcode = cs.getInt(6);
            logDebug("return code from proc MXS_ADD_AMP_ACCT is " + errcode);
            if ( errcode != 0 ) {
                String errMsg = cs.getString(7);
                logError("stored proc MXS_ADD_AMP_ACCT returns error " + errcode + ". " + errMsg, null);
                ciem.setError(errMsg, null);
                success = false;
            }
            cs.close();            
        } catch (FxException fx) {
            ciem.setError("A Kenan Framework error has occured, please contact Support."+ fx.getMessage(), null);
            fx.printStackTrace();
            success = false;
        } catch (java.sql.SQLException ex) {
            ciem.setError("Oracle SQL error during execution of proc MXS_ADD_AMP_ACCT."+ ex.getMessage(), null);
            success = false;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        
        return success;
    }

}
