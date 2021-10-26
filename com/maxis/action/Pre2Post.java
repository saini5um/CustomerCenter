/*
 * Prepaid2PostpaidComplete.java
 *
 * Created on May 11, 2005, 1:53 PM
 */

package com.maxis.action;

import java.awt.event.ActionEvent;
import com.csgsystems.workflow.BaseWorkflowAction;
import com.csgsystems.workflow.WorkflowActionEvent;
import com.csgsystems.domain.framework.context.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.fx.security.remote.*;
import com.csgsystems.fx.security.util.FxException;

import javax.swing.JOptionPane;
import java.sql.*;

import com.maxis.util.*;

/**
 *
 * @author  Pankaj Saini
 */
public class Pre2Post extends BaseWorkflowAction {
    
    private ErrorReporting err = ErrorReporting.getInstance();

    /** Creates a new instance of Prepaid2PostpaidComplete */
    public Pre2Post() {
    }
    
    public final void execute (ActionEvent e, IContext context) {
        
        JOptionPane.showConfirmDialog(null, "Unable to perform LOCK! Retry?", "IN Error", 
                JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        
        fireEvent("next");
    }
}