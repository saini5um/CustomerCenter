/*
 * Post2PreInit.java
 *
 * Created on November 15, 2005, 3:26 PM
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
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.localization.ResourceManager;
import com.csgsystems.error.ErrorFactory;
import com.csgsystems.transport.ModelAdapterFactory;
import com.csgsystems.domain.framework.security.SecurityManager;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import com.maxis.integ.*;
import com.maxis.integ.IntegException;

/**
 * Action class for pre-wizard validations during Postpaid to Prepaid conversion
 * @author Pankaj Saini
 */
public class Post2PreInit extends BaseWorkflowAction {
        
    public final void execute (ActionEvent e, IContext context) {        
        if ( JOptionPane.showConfirmDialog(null, ResourceManager.getString("Post2Pre.confirm.text"),
                ResourceManager.getString("Post2Pre.confirm.title"), JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION ) {
            IPersistentObject service = context.getObject("Service", null);
            int emfConfigId = 0;

            if ( service != null ) emfConfigId = service.getAttributeDataAsInteger("EmfConfigId");
            else context.logError("Service not found!", new Exception());

            IPersistentObject acct = context.getObject("Account", null);
            if ( acct == null ) context.logError("Account not found!", new Exception());

            IPersistentCollection serviceList = (IPersistentCollection) acct.getObject("ServiceList", "Account");
            if ( serviceList == null ) context.logDebug("Cannot determine service Count. serviceList collection is null");
                        
            if ( emfConfigId < 4000 || emfConfigId > 6000 ) { // Postpaid emf_config_id range 4001-6000
                engine.getActionFrame().displayMessageBox(ResourceManager.getString("Post2Pre.error.Title"),
                        ResourceManager.getString("Post2Pre.error.PostpaidServiceRequired"), 0);
                fireEvent("stop"); 
                return;
            }
            if ( !(service.getAttributeDataAsInteger("StatusId") == 1  || service.getAttributeDataAsInteger("StatusId") == 5)  ) { // not active and not transfer in service
                engine.getActionFrame().displayMessageBox(ResourceManager.getString("Post2Pre.error.Title"),
                        ResourceManager.getString("Post2Pre.error.InactiveService"), 0);
                fireEvent("stop");
                return;
            }

            IPersistentCollection serviceIdList = 
                    (IPersistentCollection) service.getObject("CustomerIdEquipMapList", "Service");
            if ( serviceIdList == null ) context.logDebug("Cannot find service external id list.");
            boolean mvpn = false;
            for ( int i = 0; i < serviceIdList.getCount(); i++ ) {
                IPersistentObject serviceExtId = serviceIdList.getAt(i);
                if ( serviceExtId.getAttribute("InactiveDate").isEmpty() && 
                    serviceExtId.getAttributeDataAsInteger("ServiceExternalIdType") == 34 ) mvpn = true;
            }
            if ( mvpn ) {
                engine.getActionFrame().displayMessageBox(ResourceManager.getString("Post2Pre.error.Title"),
                        ResourceManager.getString("Post2Pre.error.MVPN"), 0);
                fireEvent("stop");
                return;
            }

            IPersistentCollection invElementList = 
                    (IPersistentCollection) service.getObject("InvElementList", "Service");
            if ( invElementList == null ) context.logDebug("Cannot find inventory elements");
            boolean mism = false;
            boolean simm = false;
            for ( int i = 0; i < invElementList.getCount(); i++ ) {
                int inventoryType = invElementList.getAt(i).getAttributeDataAsInteger("InventoryTypeId");
                if ( inventoryType == 424 || inventoryType == 650 || inventoryType == 680 ) mism = true;
                if ( inventoryType == 414 || inventoryType == 415 || 
                        inventoryType == 421 || inventoryType == 422 ) simm = true;
            }
            if ( simm ) {
                engine.getActionFrame().displayMessageBox(ResourceManager.getString("Post2Pre.error.Title"),
                        ResourceManager.getString("Post2Pre.error.SIMM"), 0);
                fireEvent("stop");
                return;
            }
            if ( mism ) {
                engine.getActionFrame().displayMessageBox(ResourceManager.getString("Post2Pre.error.Title"),
                        ResourceManager.getString("Post2Pre.error.MISM"), 0);
                fireEvent("stop");
                return;
            }
            
/*            if ( serviceList.getCount() > 1 ) { // service count > 1
                engine.getActionFrame().displayMessageBox(ResourceManager.getString("Post2Pre.error.Title"),
                        ResourceManager.getString("Post2Pre.error.MultipleSI"), 0);
                fireEvent("stop");
                return;
            }
*/            if ( acct.getAttributeDataAsInteger("CollectionStatus") == 3 )  { // Fraud
                engine.getActionFrame().displayMessageBox(ResourceManager.getString("Post2Pre.error.Title"),
                        ResourceManager.getString("Post2Pre.error.Fraud"), 0);
                fireEvent("stop");
                return;
            }
            if (!canDisconnect(service)) {
                engine.getActionFrame().displayError(service.getError(), true);
                fireEvent("stop");
                return;
            }
            
            // We're starting a wizard, don't show the NavigationBar
            engine.getActionFrame().showNavigationBar(false);
            // We're starting a wizard, don't show the SubsystemNavigation
            engine.displaySubsystemNavigation(false);
            fireEvent("go");
            return;
        } else {
            fireEvent("stop");
            return;
        }
    }
    
    private boolean canDisconnect(IPersistentObject service){
        boolean success = true;
        // check if we can disconnect this service
        if (service != null)
            if (!service.sendMessage("canDisconnectService",null)) success = false;
        
        return success;
    }
}
