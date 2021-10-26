/*
 * Pre2PostInit.java
 *
 * Created on November 3, 2005, 2:09 PM
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
 * Action class for pre-wizard validations during Prepaid to Postpaid conversion
 * @author Pankaj Saini
 */
public class Pre2PostInit extends BaseWorkflowAction {
    
    /** Creates a new instance of Pre2PostInit */
    public Pre2PostInit() {
    }
    
    public final void execute (ActionEvent e, IContext context) {
        context.resetError();
        
        IPersistentObject service = context.getObject("Service", null);
        int emfConfigId = 0;
        
        if ( service != null ) emfConfigId = service.getAttributeDataAsInteger("EmfConfigId");
        else context.logError("Service not found!", new Exception());
        
        IPersistentObject acct = context.getObject("Account", null);
        if ( acct == null ) context.logError("Account not found!", new Exception());
        
        IPersistentCollection prepaid = (IPersistentCollection) service.getObject("PrepaidINList", "Service");
        if ( prepaid == null ) context.logError("Prepaid service not found!", new Exception());
        
        if ( emfConfigId < 1000 || emfConfigId > 2000 ) {
            engine.getActionFrame().displayMessageBox(ResourceManager.getString("Pre2Post.error.Title"), 
                    ResourceManager.getString("Pre2Post.error.PrepaidServiceRequired"), 0);
            fireEvent("stop"); 
            return;
        } else if ( emfConfigId == 1003 ) { // Traveller's pack
            engine.getActionFrame().displayMessageBox(ResourceManager.getString("Pre2Post.error.Title"), 
                    ResourceManager.getString("Pre2Post.error.TravellersPack"), 0);
            fireEvent("stop"); 
            return;
        } else if ( acct.getAttributeDataAsInteger("CollectionStatus") == 3 )  { // Fraud
            engine.getActionFrame().displayMessageBox(ResourceManager.getString("Pre2Post.error.Title"), 
                    ResourceManager.getString("Pre2Post.error.Fraud"), 0);
            fireEvent("stop");
            return;
        } else if ( service.getAttributeDataAsInteger("StatusId") != 1 ) { // Inactive service
            engine.getActionFrame().displayMessageBox(ResourceManager.getString("Pre2Post.error.Title"), 
                    ResourceManager.getString("Pre2Post.error.InactiveService"), 0);
            fireEvent("stop");
            return;
        } else if (!canDisconnect(service)) {
            engine.getActionFrame().displayError(service.getError(), true);
            fireEvent("stop");
            return;
        } else {
            if ( prepaid.getCount() == 0 ) context.logError("Couldn't query network for prepaid status", null);
            else { 
                IPersistentObject inService = prepaid.getAt(0);
                if ( "Success".equals(inService.getAttributeDataAsString("INFetchStatus")) == false ) {
                    engine.getActionFrame().displayMessageBox(ResourceManager.getString("Pre2Post.error.Title"), 
                            ResourceManager.getString("Pre2Post.error.INdown"), 0);
                    fireEvent("stop");
                    return;
                } else if ( "I".equals(inService.getAttributeDataAsString("SubscriberStatus")) ) {
                    engine.getActionFrame().displayMessageBox(ResourceManager.getString("Pre2Post.error.Title"), 
                            ResourceManager.getString("Pre2Post.error.ServiceInstalled"), 0);
                    fireEvent("stop");
                    return;
                } else if ( "X".equals(inService.getAttributeDataAsString("SubscriberStatus")) || 
                        "E".equals(inService.getAttributeDataAsString("SubscriberStatus")) ) {
                    engine.getActionFrame().displayMessageBox(ResourceManager.getString("Pre2Post.error.Title"), 
                            ResourceManager.getString("Pre2Post.error.ServiceExpired"), 0);
                    fireEvent("stop");
                    return;
                } else {
                    int tryAgain = JOptionPane.YES_OPTION;
                    while ( !lockSub(inService) && tryAgain == JOptionPane.YES_OPTION ) {
                        tryAgain = JOptionPane.showConfirmDialog(null, 
                                ResourceManager.getString("Pre2Post.error.LockFail"), 
                                ResourceManager.getString("Pre2Post.error.Title"), 
                                JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    }
                    if ( tryAgain == JOptionPane.NO_OPTION ) {
                        fireEvent("stop");
                        return;
                    } else { 
                        // We're starting a wizard, don't show the NavigationBar
                        engine.getActionFrame().showNavigationBar(false);
                        // We're starting a wizard, don't show the SubsystemNavigation
                        engine.displaySubsystemNavigation(false);
                        fireEvent("go");
                        return;
                    }
                }
            }
        }
        
        // We're starting a wizard, don't show the NavigationBar
        engine.getActionFrame().showNavigationBar(false);
        // We're starting a wizard, don't show the SubsystemNavigation
        engine.displaySubsystemNavigation(false);
        fireEvent("go");
    }
    
    private boolean canDisconnect(IPersistentObject service){
        boolean success = true;
        // check if we can disconnect this service
        if (service != null)
            if (!service.sendMessage("canDisconnectService",null)) success = false;
        
        return success;
    }
    
    private boolean lockSub(IPersistentObject service) {
        boolean success = false;
        try {
            Integrator t = Integrator.getInstance();
            HashMap input = new HashMap();
            HashMap header = new HashMap();
            HashMap body = new HashMap();
            input.put("Header", header);
            // pass in the current log in user id.
            input.put("KenanUserID", SecurityManager.getInstance().getLoggedInUserId());
            
            // pass in TransType as CallBarring
            header.put("TransType", "CallBarring");
            header.put("MSISDN", service.getAttributeDataAsString("ServiceExternalId"));

            input.put("Body", body);
            
            IPersistentCollection pColl =
                    (IPersistentCollection)service.getObject("com.maxis.businessobject.PrepaidINList", "Service");
            if ( pColl == null )
                System.out.println("PrepaidINList collection is null...");
            IPersistentObject prepaidIN = pColl.getAt(0);            
            if ( prepaidIN == null) {
                System.out.println("prepaidIN not found");
                return success;
            }
            
            body.put("SCPID", prepaidIN.getAttributeDataAsString("SCPID"));
            body.put("IsLocked", new Boolean(true));            
            
            HashMap output = t.execute("CallBarring", input);
            HashMap outputHeader = (HashMap)output.get("Header");
            
            if (((Integer)outputHeader.get("ExecCode")).intValue() > 0) {
                success = true;
                HashMap outputBody = (HashMap)output.get("Body");
                if( ((Boolean)outputBody.get("IsLocked")).booleanValue() )
                    prepaidIN.getAttribute("IsLocked").setDataAsInteger(1);
                else
                    prepaidIN.getAttribute("IsLocked").setDataAsInteger(0);
                
                IPersistentCollection pColl1 = (IPersistentCollection)service.getObject("PrepaidServiceList", "Service");
                if ( pColl1 == null ) System.out.println("cannot find prepaid service list");
                IPersistentObject prepaidService = (IPersistentObject)pColl1.getAt(0);
                
                prepaidService.setAttributeDataAsInteger("CallBarStatus", 1);
                // flush the new prepaid IN object
                success = ((com.maxis.businessobject.PrepaidIN)prepaidIN).flush(ModelAdapterFactory.getModelAdapter(), service);
                success = prepaidService.flush(ModelAdapterFactory.getModelAdapter());
            }
        } catch (IntegException ie) {
            ie.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }
}
