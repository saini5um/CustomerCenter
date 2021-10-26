/*
 * SupervisorApprovals.java
 *
 * Created on January 6, 2006, 3:04 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.action;

import com.csgsystems.workflow.BaseWorkflowAction;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.configuration.ConfigurationManager;
import com.csgsystems.workflow.wfappconfig.ActionParameter;
import java.awt.event.ActionEvent;
import java.util.Iterator;

/**
 *
 * @author Pankaj Saini
 */
public class SupervisorApprovals extends BaseWorkflowAction {
    
    public final void execute (ActionEvent e, IContext context) {
        try {
            Runtime r = Runtime.getRuntime();
            String path = ConfigurationManager.getInstance().getString("iu-install-path");
            if ( path != null ) {
                r.exec(path + "/" + getSubsystemParameter("app-name") + ".exe"); 
                fireEvent("launched");
            }
        } catch (Exception err) {
            err.printStackTrace();
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
