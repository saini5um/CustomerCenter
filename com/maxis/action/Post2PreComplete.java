/*
 * Pre2PostComplete.java
 *
 * Created on November 15, 2005, 4:56 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.action;

import com.csgsystems.workflow.BaseWorkflowAction;
import com.csgsystems.workflow.WorkflowActionEvent;
import com.csgsystems.domain.framework.context.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.workflow.wfappconfig.ActionParameter;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.attribute.IAttribute;
import com.csgsystems.transport.ModelAdapterFactory;
import com.csgsystems.localization.ResourceManager;
import com.csgsystems.domain.arbor.order.*;
import com.csgsystems.fx.security.remote.*;
import com.csgsystems.fx.security.util.FxException;
import com.csgsystems.error.IError;

import com.maxis.util.tuxJDBCManager;

import java.util.Iterator;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.util.Date;
import java.sql.*;

/**
 *
 * @author Pankaj Saini
 */
public class Post2PreComplete extends BaseWorkflowAction {
        
    private long acctInternalId = 0;
        
    public final void execute(ActionEvent e, IContext context) {
        String event = getSubsystemParameter("event");
        if ( "ok".equals(event) ) {
            if ( !createNewAccount(context) ) {
                System.out.println("account create failed");
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
        }

        // We're exiting the wizard, show the NavigationBar
        engine.getActionFrame().showNavigationBar(true);
        // We're exiting the wizard, show the SubsystemNavigation
        engine.displaySubsystemNavigation(true);
        
        fireEvent("proceed-success");
        return;
    }

    private boolean createNewAccount(IContext context) {
        IPersistentObject acct = context.getObject("Account", null);
        if ( acct == null ) context.logError("account not found!", new Exception());
        IPersistentObject newAcct = createClone(acct);

        newAcct.setAttributeDataAsInteger("MktCode", 31); // 31 = Prepaid
        newAcct.setAttributeDataAsInteger("AcctSegId", 1); // 1 = Prepaid
        newAcct.getAttribute("AcctSegId").setReadOnly(true);
        newAcct.setAttributeDataAsInteger("ServerCategory", 1); // 1 = Prepaid Server
        newAcct.getAttribute("ServerCategory").setReadOnly(true);
        newAcct.setAttributeDataAsInteger("BillFmtOpt", 5); // 5 = Prepaid - No Bill
        newAcct.setAttributeDataAsInteger("BillFranchiseTaxCode", 2); // 2 = Non Taxable
        newAcct.setAttributeDataAsInteger("CustFranchiseTaxCode", 2); // 2 = Non Taxable
        newAcct.setAttributeDataAsInteger("VipCode", 0); // 0 = NOT VIP (Prepaid)
        
        createExternalId(context, newAcct);

        String newAcctExternalId = null;
        if ( newAcct.flush(ModelAdapterFactory.getModelAdapter()) ) {
            newAcctExternalId = newAcct.getAttributeDataAsString("AccountExternalId");
            acctInternalId = newAcct.getAttributeDataAsLong("AccountInternalId");
            
            JOptionPane.showMessageDialog(null, 
                    ResourceManager.getString("Post2Pre.NewAcct.text.success").replaceAll("\\{0\\}", newAcctExternalId),
                    ResourceManager.getString("Post2Pre.NewAcct.title.success"), JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            engine.getActionFrame().displayError(newAcct.getError(), true);
            context.logError("failed account flush!", null);
            return false;
        }
    }
    
    boolean disconnectService(IContext context) {
        boolean success = false;
        IPersistentObject serviceDisconnect = PersistentObjectFactory.getFactory().createNew("ServiceDisconnect", null);
        serviceDisconnect.setAttributeDataAsInteger("DisconnectReason", 2052);
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
            cs.setString(4, "2");
            
            int result = cs.executeUpdate();
            cs.close();            
            success = true;
        } catch (FxException fx) {
            engine.getActionFrame().displayMessageBox("SQL Error", 
                    "A Kenan Framework error has occured, please contact ISD!!"+ fx.getMessage(), 0);
            fx.printStackTrace();
            success = false;
        } catch (java.sql.SQLException ex) {
            engine.getActionFrame().displayMessageBox("SQL Error", 
                    "Oracle SQL error during execution of proc MXS_CONVERT_INVENTORY!!"+ ex.getMessage(), 0);
            success = false;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        
        return success;
    }
    
    private IPersistentObject createClone(IPersistentObject account) {
        IPersistentObject clone = PersistentObjectFactory.getFactory().createNew("Account", null);
        Iterator itAttrs = account.getAttributes().iterator();
        String attrName = null;
        while (itAttrs.hasNext()) {
            IAttribute attr = (IAttribute) itAttrs.next();
            attrName = attr.getName();
            if (attr.isExtended()) continue;
            if ("AccountInternalId".equals(attrName) || "ParentAccountExternalId".equals(attrName) 
            || "AccountExternalId".equals(attrName) || "AccountExternalIdType".equals(attrName)
            || "ParentAccountExternalIdType".equals(attrName) || "HierarchyId".equals(attrName)
            || "ChildCount".equals(attrName) || "DateActive".equals(attrName) || "DateCreated".equals(attrName)
            || "ChgDate".equals(attrName) || "ChgWho".equals(attrName) || "AccountStatus".equals(attrName)
            || "AccountServer".equals(attrName) || attrName.startsWith("PastDue")
            || "AccountConsideredNew".equals(attrName) || "UnbilledUsage".equals(attrName)
            || "PayMethod".equals(attrName) || "CreditThresh".equals(attrName)) continue;
            try {
                if (attr.isCloneable() && !attr.isTransient() && clone.isAttribute(attrName)) {
                    clone.setAttributeData(attrName, attr.getData());
                }
            } catch (Exception ex) {
                clone.logError(
                    "Error setting attribute value in createClone: "
                        + attrName,
                    ex);
            }
        }
        
        //default some values                                   
        clone.setAttributeData("PrevBillRefno", null);    
        clone.setAttributeData("PrevBillRefResets", null);
        
        ((Domain) clone).resetExtendedAttributes();
        Iterator itExtAttrs = account.getExtendedAttributes().iterator();
        while (itExtAttrs.hasNext()) {
            IAttribute extAttr = (IAttribute) itExtAttrs.next();
            attrName = extAttr.getName();
            try {
                if ( "*Birth Date_3".equals(attrName) || "*Gender_2".equals(attrName) || "*Nationality_21".equals(attrName) ||
                    "*Non-Malaysian/Country_13".equals(attrName) || "*Race_1".equals(attrName) || "Hotlink Club_4".equals(attrName) ||
                    "Interest - Entertainment_8".equals(attrName) || "Interest - Travel_9".equals(attrName) ||
                    "Interest - Fashion_10".equals(attrName) || "Interest - Sport_11".equals(attrName) ||
                    "Job Description_6".equals(attrName)) {
                    clone.logDebug("Cloning attribute: " + attrName + " = " + extAttr.getData());
                    clone.setAttributeData(attrName, extAttr.getData());
                }
            } catch (Exception ex) {
                clone.logError(
                         "Error setting extended attribute value in createClone: "
                         + attrName,
                         ex);
            }
        }

        return clone;
    }
    
    private void createExternalId(IContext context, IPersistentObject acct) {
        IPersistentObject obj = context.getObject("Account");
        IPersistentCollection coll = (IPersistentCollection) obj.getObject("AccountIdList", "Account");
        
        for ( int i = 0; i < coll.getCount(); i++ ) {
            int extIdType = coll.getAt(i).getAttributeDataAsInteger("AccountExternalIdType");
            if (  extIdType == 8 || extIdType == 9 || extIdType == 10 || extIdType == 11 ) {
                if ( coll.getAt(i).getAttribute("InactiveDate").isEmpty() ) {
                    IPersistentObject acctId = PersistentObjectFactory.getFactory().createNew("AccountId", null);

                    acctId.setAttributeDataAsInteger("AccountExternalIdType", extIdType);
                    acctId.setAttributeData("AccountExternalId", coll.getAt(i).getAttributeData("AccountExternalId"));

                    acct.addAssociation(acctId);
                }
            }
        }
    }
     
    private String getSubsystemParameter(String parameterName) {
        if (parameterName == null) {
            throw new IllegalArgumentException("parameter name was null");
        }
        
        String parameterValue = null;
        
        Iterator it = actionClass.getActionParameters().iterator();
        while (it.hasNext()) {
            ActionParameter ap = (ActionParameter) it.next();
            if (parameterName.equals(ap.getName())) {
                parameterValue = ap.getValue();
                break;
            }
        }

        return parameterValue;
    }
}
