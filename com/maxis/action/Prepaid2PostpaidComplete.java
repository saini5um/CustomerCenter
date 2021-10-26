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
public class Prepaid2PostpaidComplete extends BaseWorkflowAction {
    
    private ErrorReporting err = ErrorReporting.getInstance();

    /** Creates a new instance of Prepaid2PostpaidComplete */
    public Prepaid2PostpaidComplete() {
    }
    
    public final void execute (ActionEvent e, IContext context) {
        // New Maxis code - Pankaj Saini (11-May-2005)
        // The disconnect reason is being hard coded to 1 (Pre-Post)
        IPersistentObject serviceDisconnect = context.getObject("ServiceDisconnect");
        
        boolean success = true;
        success = updateDB(serviceDisconnect);
//        CallCORBA();

        fireEvent("next");
        // End new Maxis code - Pankaj Saini (11-May-2005)
    }
    
    // New Maxis code - Pankaj Saini (11-May-2005)
    // The disconnect reason is being hard coded to 1 (Pre-Post)
    private boolean updateDB(IPersistentObject serviceDisc){
        boolean success = true;
        
        String SQL = "INSERT INTO MXS_CC_PRE_POST ";
        SQL = SQL + "(msisdn, subscr_no, amount, first_call_date, order_id) ";
        SQL = SQL + "values (?, ?, ?, ?, ?)";

        try {
            RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CUST");
            if (dbConn == null) {
                err.logErrorToFile("Error getting current Oracle Connection", getClass());
               return false;
            }
            PreparedStatement pstmt = dbConn.prepareStatement(new SQL(SQL, "insert"));
//            pstmt.setMaxRows(3);
            pstmt.setString(1, serviceDisc.getAttributeDataAsString("ServiceExternalId"));
            pstmt.setString(2, serviceDisc.getAttributeDataAsString("ServiceInternalId"));
            pstmt.setInt(3, 1);
            pstmt.setString(4, "x");
            pstmt.setInt(5, 0);
            
            pstmt.executeUpdate();
            pstmt.close();
            
        } catch (FxException fx) {
            err.logError("A Kenan Framework error has occured, please contact IT!!   "+ fx.getMessage(), "Error Message", getClass());
            err.logErrorToFile("FX Exception "+fx.getMessage(), getClass());
            fx.printStackTrace();
        } catch (java.sql.SQLException ex) {
            err.logErrorToFile("SQL Exception "+ex.getMessage(), getClass());
            err.logError("An Oracle error has occured on MXS_CC_PRE_POST table, please contact IT!! "+ ex.getMessage(), "Error Message", getClass());
        } catch (Exception e) {
            err.logErrorToFile("Exception ", getClass());
        }
        
        return success;
    }
    // End new Maxis code - Pankaj Saini (11-May-2005)
    
}
