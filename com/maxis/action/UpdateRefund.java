/*
 * UpdateRefund.java
 *
 * Created on March 10, 2006, 10:48 AM
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
import com.csgsystems.fx.security.remote.*;
import com.csgsystems.fx.security.util.FxException;
import com.maxis.util.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

/**
 *
 * @author Pankaj Saini
 */
public class UpdateRefund extends BaseWorkflowAction {
    
    public final void execute (ActionEvent e, IContext context) {
        System.out.println("context = " + context.getClass().getName());
        
        IPersistentObject refund = context.getObject("Refund", null);
        if ( refund == null ) System.out.println("null refund object!");
                
        if ( updateRefundStatus(refund) == false )
            JOptionPane.showMessageDialog(null, "Failed to update check number", "Error!", JOptionPane.ERROR_MESSAGE);

        engine.getContext().removeTopic(refund);
        
/*        IPersistentObject acct = context.getObject("Account", null);
        context.clearTopics();
        if ( acct == null ) System.out.println("account is null after clearing topics!");
        context.addTopic(acct);
        context.removeTopic(refund);
*/        
        fireEvent("ok");
    }
    
    public boolean updateRefundStatus(IPersistentObject obj) {
        boolean success = false;
        
        String strQuery = "UPDATE REFUND SET check_num =  ?" ;
               strQuery = strQuery + "WHERE tracking_id = ? and tracking_id_serv = ?";
               
        RemoteDBConnection conn = tuxJDBCManager.getInstance().getCurrentConnection("CUST");
        if ( conn == null )  return success;
        
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(new SQL(strQuery, "update"));
            stmt.setString(1, obj.getAttributeDataAsString("CheckNum"));
            stmt.setString(2, obj.getAttributeDataAsString("TrackingId"));
            stmt.setString(3, obj.getAttributeDataAsString("TrackingIdServ"));
        } catch (FxException fx) {
            fx.printStackTrace();
            success = false;
        } catch (SQLException sEx) {
            sEx.printStackTrace();
            success = false;
        }

        try {
            int retval = stmt.executeUpdate();
            stmt.close();
            success = true;
        } catch (SQLException sEx) {
            sEx.printStackTrace();
            success = false;
        }
        
        return success;
     }
}
