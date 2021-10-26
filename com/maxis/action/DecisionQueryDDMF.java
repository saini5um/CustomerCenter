/*
 * DecisionQueryDDMF.java
 *
 * Created on October 11, 2005, 6:13 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.action;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

import com.csgsystems.workflow.BaseWorkflowAction;
import com.csgsystems.workflow.WorkflowActionEvent;
import com.csgsystems.domain.framework.context.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.localization.ResourceManager;
import com.csgsystems.domain.arbor.order.OrderManager;

import javax.swing.JOptionPane;

/**
 * Action class for decision on whether DDMF should be queried
 * for a given situation considering user profile (security keys)
 * and customer data (# of active services, write off, collection status etc.)
 * @author Pankaj Saini
 */
public class DecisionQueryDDMF extends BaseWorkflowAction {
    
    private boolean barred = false;
    private boolean writeOff = false;
    private boolean disconnected = false;
    private int serviceCount = 0;
    
    /** Creates a new instance of DecisionQueryDDMF */
    public DecisionQueryDDMF() {
    }
    
    public final void execute (ActionEvent e, IContext context) {
        IPersistentObject newAcct = context.getObject("Account", null);
        
        if ( newAcct == null ) {
            // Clear order before creating account.
            // Account.notifyPostUnmarshal will create a new order if one does not exist.
            OrderManager.getInstance().clearCurrentOrder();
            
            newAcct = PersistentObjectFactory.getFactory().createNew("Account", null);
            context.addTopic(newAcct);
        }
        
        createExternalId(context, newAcct);        
        IPersistentCollection acctSearchResults = (IPersistentCollection)context.getObject("MXSAccountSearchList");

        int count = acctSearchResults.getCount();
        if ( count == 0 ) {
            fireEvent("query");
            return;
        }
        
        for ( int i = 0; i < count; i++ ) {
            IPersistentObject searchResult = acctSearchResults.getAt(i);
            IPersistentObject acct = searchResult.getObject("Account");
            
            if ( acct.getAttributeDataAsInteger("CollectionStatus") == 1 ) barred = true;
            else if ( acct.getAttributeDataAsInteger("CollectionStatus") == 2 ) disconnected = true;
            if ( acct.getAttributeDataAsInteger("CollectionHistory") != 0 ) writeOff = true;
            
            serviceCount = serviceCount = searchResult.getAttributeDataAsInteger("ActiveServices");
        }
        
        if ( barred ) {
            JOptionPane.showMessageDialog(null, ResourceManager.getString("NCA.Barred.text.Warning"), 
                    ResourceManager.getString("NCA.Barred.title.Warning"), JOptionPane.WARNING_MESSAGE);
        } else if ( disconnected || writeOff ) {
            JOptionPane.showMessageDialog(null, ResourceManager.getString("NCA.WriteOff.text.Warning"), 
                    ResourceManager.getString("NCA.WriteOff.title.Warning"), JOptionPane.WARNING_MESSAGE);
        }
        
        if ( serviceCount == 0 ) {
            fireEvent("query");
            return;
        } else {
            fireEvent("by-pass");
            return;
        }
    }
    
    private void createExternalId(IContext context, IPersistentObject acct) {
        IPersistentObject genericObj = context.getObject("GenericDomain", null);
        IPersistentCollection acctIdList = (IPersistentCollection) acct.getObject("AccountIdList", "Account");
        if ( acctIdList != null )
            for ( int i = 0; i < acctIdList.getCount(); i++ )
                acct.removeAssociation(acctIdList.getAt(i));
                            
        IPersistentObject acctId = PersistentObjectFactory.getFactory().createNew("AccountId", null);
        acctId.setAttributeData("AccountExternalIdType", genericObj.getAttributeData("Type"));
        acctId.setAttributeData("AccountExternalId", genericObj.getAttributeData("Value"));
        
        acct.addAssociation(acctId);
    }
    
}

