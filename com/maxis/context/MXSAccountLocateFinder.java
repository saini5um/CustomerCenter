/*
 * MXSAccountLocateFinder.java
 *
 * Created on April 24, 2006, 11:47 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import com.csgsystems.bp.context.AccountLocateFinder;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.localization.ResourceManager;
import com.csgsystems.fx.security.remote.*;
import com.csgsystems.fx.security.util.FxException;
import com.csgsystems.transport.ModelAdapterFactory;
import com.csgsystems.domain.arbor.businessobject.Account;
import com.maxis.util.tuxJDBCManager;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Pankaj Saini
 */
public class MXSAccountLocateFinder extends AccountLocateFinder {
    protected String childAccountExternalId;
    protected long childAccountInternalId;
    protected int childAccountServer;
    protected int childCount;
    protected IPersistentObject m_target;
    
    public boolean open(IPersistentObject target) {
        logInfo("open adding target = " + target.getAttributeDataAsInteger("AccountInternalId"));
        if ( target != null && target instanceof Account ) {
            m_target = target;
            childAccountExternalId = target.getAttributeDataAsString("AccountExternalId");
            childAccountInternalId = target.getAttributeDataAsLong("AccountInternalId");
            childAccountServer = target.getAttributeDataAsInteger("AccountServer");
            childCount = target.getAttributeDataAsInteger("ChildCount");
        }
        
        return super.open(target);
    }
    
    public boolean processShutdown(int shutdownType) {
        if ( m_target == null ) return super.processShutdown(shutdownType);        
        if ( shutdownType != OK ) return true;

        boolean success = false;
        IPersistentObject acctLocate = getObject("AccountLocate", null);
        if ( acctLocate == null ) logInfo("found null accountLocate object in context!");
        
        int parentAccountServer = acctLocate.getAttributeDataAsInteger("ServerId");
        if ( childAccountServer == parentAccountServer )
            return super.processShutdown(shutdownType);
        else {
            logInfo("child account on server " + childAccountServer 
            + " parent account on server " + acctLocate.getAttributeDataAsInteger("ServerId"));
        }
        
        int parentAcctSegId = acctLocate.getAttributeDataAsInteger("AcctSegId");
        int childAcctSegId = m_target.getAttributeDataAsInteger("AcctSegId");
        if ( childCount != 0 ) {
            logDebug("child account is not stand alone. ChildCount = " + childCount);
            setError("MX-4-1", null);
            success = false;
        } else if ( parentAcctSegId != childAcctSegId ) {
            logDebug("Parent account seg id = " + parentAcctSegId + ", Child account seg id = " + childAcctSegId);
            setError("CC-1-1-32", null);
            success = false;            
        } else {
            int option = JOptionPane.showConfirmDialog(null, ResourceManager.getString("MSA.account.link.option"), 
                    ResourceManager.getString("Alert!"), JOptionPane.YES_NO_OPTION);
            if ( option == JOptionPane.YES_OPTION ) {
                success = informAMP(acctLocate.getAttributeDataAsLong("AccountInternalId"), parentAccountServer);
                setParentAccountData(acctLocate);
            } else success = true;
        }
        
        logDebug("Parent Account External Id = " + m_target.getAttributeDataAsString("ParentAccountExternalId"));
        setServerIdBack();
        m_target.getAttribute("ParentAccountExternalId").setModified(false);
        return success;
    }
    
    private boolean informAMP(long parentAccountNo, int parentAccountServer) {
        boolean success = true;
        try {
            RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CAT");
            if (dbConn == null) {
                logError("Error getting current Oracle Connection", null);
               return success;
            }
            CallableStatement cs = dbConn.prepareCall(new SQL("MXS_ADD_AMP_ACCT", "proc"));
            cs.setString(1, childAccountExternalId);
            cs.setLong(2, childAccountInternalId);
            cs.setInt(3, childAccountServer);
            cs.setInt(4, parentAccountServer);
            cs.setLong(5, parentAccountNo);
            cs.registerOutParameter(6, Types.NUMERIC);
            cs.registerOutParameter(7, Types.VARCHAR);

            ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cs).getParms().get(5)).registerAsOutParameter();
            ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cs).getParms().get(5)).setNullParm(false);
            ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cs).getParms().get(5)).setValue(new Integer(0));
            ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cs).getParms().get(6)).registerAsOutParameter();
            ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cs).getParms().get(6)).setNullParm(false);
            ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cs).getParms().get(6)).setValue("N/A");
            
            logDebug("calling stored proc MXS_ADD_AMP_ACCT(" + childAccountExternalId + ", " + childAccountInternalId
                    + ", " + childAccountServer + ", " + parentAccountServer + ", " + parentAccountNo + ")");
            int result = cs.executeUpdate();
            int errcode = cs.getInt(6);
            logDebug("return code from proc MXS_ADD_AMP_ACCT is " + errcode);
            if ( errcode != 0 ) {
                String errMsg = cs.getString(7);
                logError("stored proc MXS_ADD_AMP_ACCT returns error " + errcode + ". " + errMsg, null);
                setError(errMsg, null);
                success = false;
            }
            cs.close();            
        } catch (FxException fx) {
            setError("A Kenan Framework error has occured, please contact Support."+ fx.getMessage(), null);
            fx.printStackTrace();
            success = false;
        } catch (java.sql.SQLException ex) {
            setError("Oracle SQL error during execution of proc MXS_ADD_AMP_ACCT."+ ex.getMessage(), null);
            success = false;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        
        return success;
    }
    
    private void setServerIdBack() {
        ModelAdapterFactory.getModelAdapter().setEnvelopeParameter("AccountServer", new Integer(childAccountServer));
    }
    
    private void setParentAccountData(IPersistentObject acctLocate) {
        m_target.beginPopulation();
        m_target.setAttributeData("ParentId", acctLocate.getAttributeData("AccountInternalId"));
        m_target.setAttributeData("ParentAccountExternalId", acctLocate.getAttributeData("AccountExternalId"));
        m_target.setAttributeData("ParentAccountExternalIdType", acctLocate.getAttributeData("AccountExternalIdType"));
//        m_target.setAttributeData("HierarchyId", acctLocate.getAttributeData("HierarchyId"));
        m_target.setAttributeData("PreviousParentAccountExternalId", acctLocate.getAttributeData("AccountExternalId"));
        m_target.endPopulation();
    }
}
