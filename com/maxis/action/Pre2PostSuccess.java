/*
 * Pre2PostSuccess.java
 *
 * Created on November 9, 2005, 6:32 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.action;

import com.csgsystems.workflow.BaseWorkflowAction;
import com.csgsystems.domain.framework.context.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.transport.ModelAdapterFactory;
import com.csgsystems.localization.ResourceManager;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.arbor.order.*;
import com.csgsystems.fx.security.remote.*;
import com.csgsystems.fx.security.util.FxException;

import com.maxis.util.tuxJDBCManager;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.sql.*;
import java.util.Iterator;
import com.csgsystems.error.IError;

/**
 *
 * @author Pankaj Saini
 */
public class Pre2PostSuccess extends BaseWorkflowAction {
    
    public final void execute(ActionEvent e, IContext context) {
        IPersistentObject newAcct = context.getObject("Account", null);
        String newAcctExternalId = null;
        long acctInternalId = 0;
        if ( newAcct.flush(ModelAdapterFactory.getModelAdapter()) ) {
            newAcctExternalId = newAcct.getAttributeDataAsString("AccountExternalId");
            acctInternalId = newAcct.getAttributeDataAsLong("AccountInternalId");
            context.removeTopic(newAcct);
            
            IPersistentObject acct = context.getObject("Account", "Pre2Post");
            if ( acct != null ) acct.setSubtype(null);
            
            JOptionPane.showMessageDialog(null, 
                    ResourceManager.getString("Pre2Post.NewAcct.text.success").replaceAll("\\{0\\}", newAcctExternalId),
                    ResourceManager.getString("Pre2Post.NewAcct.title.success"), JOptionPane.INFORMATION_MESSAGE);
            
            transferBalance(context, newAcct);
            
        } else {
            context.addError(newAcct.getError());
            context.logError("failed account flush!", null);
            fireEvent("error");
            return;
        }
        
        if ( !disconnectService(context) ) {
            System.out.println("failed to disconnect service!");
            fireEvent("error");
            return;
        }
        
        if ( !reserveInventory(context, acctInternalId) ) {
            System.out.println("failed to reserve inventory");
            fireEvent("error");
            return;
        }
        
        // We're exiting the wizard, show the NavigationBar
        engine.getActionFrame().showNavigationBar(true);
        // We're exiting the wizard, show the SubsystemNavigation
        engine.displaySubsystemNavigation(true);
        fireEvent("proceed-success");
    }
    
    private void transferBalance(IContext context, IPersistentObject account) {
        IPersistentObject service = context.getObject("Service", null);
        IPersistentCollection coll = service.getCollection("PrepaidINList", "Service");
        IPersistentObject inService = null;
        addNrc(inService, account);
        if ( coll != null ) {
            inService = coll.getAt(0);
            if ( inService != null ) {
                addNrc(inService, account);
            } else System.out.println("IN Service not found");
        } else System.out.println("IN list is null!");
    }
    
    private void addNrc(IPersistentObject inService, IPersistentObject account) {
//        int nrcAmount = inService.getAttributeDataAsInteger("OnPeakAccountIDBalance");
        int nrcAmount = 123;
        int accountNo = account.getAttributeDataAsInteger("AccountInternalId");
        System.out.println("Create NRC for amount = " + nrcAmount);

        IPersistentObject pNrc = PersistentObjectFactory.getFactory().createNew("Nrc", null);
        pNrc.setAttributeDataAsInteger("TypeIdNrc", 400505); // Pre to post hotlink balance transfer
        pNrc.setAttributeDataAsLong("Rate", nrcAmount);
        pNrc.setAttributeDataAsInteger("CurrencyCode", 1);
        pNrc.setAttributeDataAsInteger("BillingAccountInternalId", accountNo);
        pNrc.setAttributeDataAsInteger("BillRefNo", 0);
        pNrc.setAttributeDataAsInteger("BillRefResets", 0);

        long mil = new java.util.Date().getTime();
        java.sql.Date dt = new java.sql.Date(mil);
        pNrc.setAttributeDataAsDate("TransactDate",  dt);
        pNrc.setAttributeDataAsDate("RateDt",  dt);

        pNrc.setAttributeDataAsInteger("RequestStatus", 1);

        // required by object
        pNrc.setAttributeDataAsDate("ChgDt",  dt);  // required by object
        pNrc.setAttributeDataAsDate("CreateDt",  dt);  // required by object
        pNrc.setAttributeDataAsInteger("CurrentInstallment", 0);
        pNrc.setAttributeDataAsDate("IntendedViewEffectiveDt",  dt);  // required by object
        pNrc.setAttributeDataAsBoolean("NoBill", false);
        pNrc.setAttributeDataAsInteger("NrcCategory",  1);
        pNrc.setAttributeDataAsInteger("OpenItemId",  1);
        pNrc.setAttributeDataAsInteger("ViewStatus", 2);  //set viewid to be current so that we dont have to create orders
        pNrc.setAttributeDataAsBoolean("NrcRateErr", false);

        if ( pNrc.flush(ModelAdapterFactory.getModelAdapter()) == false ) {
            Iterator i = pNrc.getError().getRelatedErrors().iterator();
            IError e = null;
            while ( i.hasNext() ) {
                e = (IError)i.next();
                System.out.println("error = " + e.getErrorMessage());
            }
            System.out.println("NRC error = " + pNrc.getError().getErrorMessage());
            System.out.println("Flush of NRC failed");
        }
    }
    
    boolean disconnectService(IContext context) {
        boolean success = false;
        IPersistentObject serviceDisconnect = PersistentObjectFactory.getFactory().createNew("ServiceDisconnect", null);
        serviceDisconnect.setAttributeDataAsInteger("DisconnectReason", 1006);
        serviceDisconnect.setAttributeData("InactiveDate", new Date());
        
        IPersistentObject service = context.getObject("Service", null);
        if ( service != null ) serviceDisconnect.addAssociation(service);

        IPersistentObject serviceOrder = PersistentObjectFactory.getFactory().createNew("ServiceOrder", null);
        if ( serviceOrder != null )
            serviceOrder.addAssociation(serviceDisconnect);
        
        IPersistentObject order = OrderManager.getInstance().getCurrentOrder();  // Check for Current Order
        order.setAttributeData("EffectiveDateWithTimestamp", serviceDisconnect.getAttributeData("InactiveDate"));
        // add the service order to the current order
        order.addAssociation(serviceOrder);
        
        serviceOrder.setSubtype("NewLogicalOrder");                        
        service.addAssociation(serviceOrder);    
        
        success = order.flush(ModelAdapterFactory.getModelAdapter());
        
        return success;
    }

    boolean reserveInventory(IContext context, long account_no) {
        boolean success = false;

        IPersistentObject service = context.getObject("Service", null);
        try {
            RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CAT");
            if (dbConn == null) {
                context.logError("Error getting current Oracle Connection", null);
               return success;
            }
            CallableStatement cs = dbConn.prepareCall(new SQL("MXS_CONVERT_INVENTORY", "proc"));
            cs.setLong(1, service.getAttributeDataAsInteger("ServiceInternalId"));
            cs.setLong(2, service.getAttributeDataAsInteger("ServiceInternalIdResets"));
            cs.setLong(3, account_no);
            
            int result = cs.executeUpdate();
            cs.close();            
            success = true;
        } catch (FxException fx) {
            context.setError("A Kenan Framework error has occured, please contact ISD!!"+ fx.getMessage(), null);
            fx.printStackTrace();
            success = false;
        } catch (java.sql.SQLException ex) {
            context.setError("Oracle SQL error during execution of proc MXS_CONVERT_INVENTORY!!"+ ex.getMessage(), null);
            success = false;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        
        return success;
    }
}
