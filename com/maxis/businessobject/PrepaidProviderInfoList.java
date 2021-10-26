/*
 * PrepaidNotesList.java
 *
 * Created on May 25, 2005, 7:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.businessobject;

import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.fxcommon.attribute.*;
import com.csgsystems.domain.arbor.businessobject.Account;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.arbor.businessobject.Service;
import com.csgsystems.fx.security.remote.RemoteDBConnection;
import com.csgsystems.fx.security.remote.SQL;
import com.csgsystems.fx.security.util.FxException;
import com.csgsystems.transport.tuxJDBC.tuxJDBCManager;

import com.maxis.util.ErrorReporting;
import java.sql.*;

/**
 *
 * @author Pankaj Saini
 */
public class PrepaidProviderInfoList extends DomainCollection {
    
    protected int subscr_no = 0;
    protected int subscr_no_resets = 0;
    private ErrorReporting err = ErrorReporting.getInstance();
    
    /** Creates a new instance of PrepaidNotesList */
    public PrepaidProviderInfoList() {
        super(PrepaidNotes.class);
    }
    
    protected boolean localAddAssociation(IPersistentObject relObject) {
        boolean success = false;
        
        subscr_no = relObject.getAttributeDataAsInteger("ServiceInternalId");
        subscr_no_resets = relObject.getAttributeDataAsInteger("ServiceInternalIdResets");
        success = true;

        return success;
    }
    
    protected int listPageFault(int nStart) {
        String SQL = "SELECT NOTE_CODE, NOTE_DATE, NOTE_TEXT, REASON_ID, ";
        SQL += "REMARKS, CHG_WHO FROM MXS_PREPAID_NOTES ";
        SQL += "WHERE SUBSCR_NO = ? ";
        SQL += "AND SUBSCR_NO_RESETS = ?";
                
        RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CUST");
        if (dbConn == null) {
            err.logErrorToFile("Error getting current Oracle Connection.", getClass());
           return 0; // prevent windows from shutting down
        }

        PreparedStatement pstmt = null;
        try {
            pstmt = dbConn.prepareStatement (new SQL(SQL, "select"));
            pstmt.setMaxRows(100);
            pstmt.setInt(1, subscr_no);
            pstmt.setInt(2, subscr_no_resets);
        } catch (FxException fx) {
            fx.printStackTrace();
            err.logErrorToFile("SQL Exception "+fx.getMessage(), PrepaidServiceList.class);
            return 0;
        } catch (SQLException sEx) {
            sEx.printStackTrace();
            err.logErrorToFile("SQL Exception "+sEx.getMessage(), PrepaidServiceList.class);
            return 0;
        }
        
        try {
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()) {
                IPersistentObject prepaidNote = (IPersistentObject)PersistentObjectFactory.getFactory().createNew(PrepaidNotes.class, null);
                if (prepaidNote == null) {
                    err.logErrorToFile("Couldn't create PrepaidNote.", PrepaidNotesList.class);
                } else {
                    prepaidNote.getAttribute("NoteCode").setDataAsString(rset.getString(1)); 
                    prepaidNote.getAttribute("NoteDate").setDataAsDate(rset.getDate(2));
                    prepaidNote.getAttribute("NoteText").setDataAsString(rset.getString(3)); 
                    prepaidNote.getAttribute("Reason").setDataAsInteger(rset.getInt(4));
                    prepaidNote.getAttribute("Remarks").setDataAsString(rset.getString(5)); 
                    prepaidNote.getAttribute("ChangeWho").setDataAsString(rset.getString(6));

                    // Insert New Domain into Collection
                    add(prepaidNote);
                }
            }
            rset.close();
            pstmt.close();
        } catch (SQLException sEx) {
            sEx.printStackTrace();
            err.logErrorToFile("SQL Exception "+sEx.getMessage(), PrepaidNotesList.class);
            return 0;
        }
        
        return 0;
    }
}
