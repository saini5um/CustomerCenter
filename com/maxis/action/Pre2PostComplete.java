/*
 * Pre2PostComplete.java
 *
 * Created on November 7, 2005, 6:23 PM
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

import java.util.Iterator;
import java.awt.event.ActionEvent;

/**
 *
 * @author Pankaj Saini
 */
public class Pre2PostComplete extends BaseWorkflowAction {
    
    /** Creates a new instance of Pre2PostComplete */
    public Pre2PostComplete() {
    }
    
    public final void execute(ActionEvent e, IContext context) {
        String event = getSubsystemParameter("event");
        if ( "ok".equals(event) ) {
// Additional processing for executing ulock etc
// Also create new account and add external id
            IPersistentObject acct = context.getObject("Account", null);
            acct.setSubtype("Pre2Post");
//            acct.beginPopulation();
            createNewAccount(context);
        } else if ( "cancel".equals(event) ) {
            // We're exiting the wizard, show the NavigationBar
            engine.getActionFrame().showNavigationBar(true);
            // We're exiting the wizard, show the SubsystemNavigation
            engine.displaySubsystemNavigation(true);
        }
        
        removeTopic(context, "GenericDomain", null);
        removeTopic(context, "MXSAccountSearchList", null);
        fireEvent("next");
        return;
    }

    private void createNewAccount(IContext context) {
        IPersistentObject acct = context.getObject("Account", "Pre2Post");
        if ( acct == null ) context.logError("account not found!", new Exception());
        IPersistentObject newAcct = createClone(acct);
        
        newAcct.getAttribute("MktCode").setEmpty(true);
        newAcct.setAttributeDataAsInteger("AcctSegId", 2); // 2 = Convergent
//        newAcct.getAttribute("AcctSegId").setReadOnly(true);
        newAcct.setAttributeDataAsInteger("ServerCategory", 2); // 2 = Convergent Server
        newAcct.getAttribute("ServerCategory").setReadOnly(true);
        
        newAcct.setAttributeDataAsInteger("BillFmtOpt", 6); // 6 = Convergent
        newAcct.setAttributeDataAsBoolean("NoBill", false); // do not suppress billing
        newAcct.setAttributeDataAsInteger("BillFranchiseTaxCode", 1); // Taxable
        newAcct.getAttribute("RevRcvCostCtr").setEmpty(true);
        newAcct.getAttribute("VipCode").setEmpty(true);
        newAcct.setAttributeDataAsInteger("CustFranchiseTaxCode", 1); // Taxable
        context.addTopic(newAcct);
        
        createExternalId(context, newAcct);
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
            || "AccountConsideredNew".equals(attrName) || "UnbilledUsage".equals(attrName)) continue;
            try {
                if (attr.isCloneable() && !attr.isTransient()) {
                    clone.setAttributeData(attrName, attr.getData());
                }
            } catch (Exception ex) {
                clone.logError(
                    "Error setting attribute value in createClone: "
                        + attrName,
                    ex);
            }
        }
        
        ((Domain) clone).resetExtendedAttributes();
        Iterator itExtAttrs = account.getExtendedAttributes().iterator();
        while (itExtAttrs.hasNext()) {
            IAttribute extAttr = (IAttribute) itExtAttrs.next();
            attrName = extAttr.getName();
            try {
                clone.logDebug("Cloning attribute: " + attrName + " = " + extAttr.getData());
                clone.setAttributeData(attrName, extAttr.getData());
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
        IPersistentObject genericObj = context.getObject("GenericDomain", null);
        IPersistentObject acctId = PersistentObjectFactory.getFactory().createNew("AccountId", null);

        acctId.setAttributeData("AccountExternalIdType", genericObj.getAttributeData("Type"));
        acctId.setAttributeData("AccountExternalId", genericObj.getAttributeData("Value"));
        
        acct.addAssociation(acctId);
    }
     
    private void removeTopic(IContext context, String objName, String subtype) {
        IPersistentObject obj = context.getObject(objName, subtype);
        if ( obj != null ) {
            context.removeTopic(obj);
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
