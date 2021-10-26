/*
 * PrepaidNotes.java
 *
 * Created on May 25, 2005, 4:14 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.businessobject;

import com.csgsystems.domain.framework.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.fxcommon.attribute.*;
import java.lang.*;
import java.util.*;
import com.csgsystems.transport.IModelAdapter;
import com.csgsystems.fx.security.remote.*;
import com.csgsystems.fx.security.util.FxException;
import com.csgsystems.error.IError;
import com.csgsystems.error.ErrorFactory;

import java.sql.*;
import com.maxis.util.*;

/**
 *
 * @author Pankaj Saini
 */
public class PrepaidNotes extends Domain {

    protected IntegerAttribute subscrNo = null;
    protected IntegerAttribute subscrNoResets = null;
    protected TimestampAttribute noteDate = null;
    protected IntegerEnumeratedAttribute noteCode = null;
    protected IntegerEnumeratedAttribute adjReason = null;
    protected IntegerEnumeratedAttribute reloadReason = null;
    protected IntegerEnumeratedAttribute callBarReason = null;
    protected IntegerEnumeratedAttribute voucherBarReason = null;
    protected IntegerCustomEnumeratedAttribute reason = null;
    protected StringAttribute noteText = null;
    protected StringAttribute remarks = null;
    protected StringAttribute changeWho = null;
    protected IntegerAttribute noteId = null;

    private ErrorReporting err = ErrorReporting.getInstance();
    
    /** Creates a new instance of PrepaidNotes */
    public PrepaidNotes() {
        subscrNo = new IntegerAttribute("SubscrNo", this, false);
        subscrNoResets = new IntegerAttribute("SubscrNoResets", this, false);
        
        noteDate = new TimestampAttribute("NoteDate", this, false);
        noteDate.setDateTimeInclude(IDateTime.DATETIME);
        noteDate.setReadOnly(true);
        
        noteCode = new IntegerEnumeratedAttribute("NoteCode", this, 
                "GuiIndicator.FieldName=note_code", "IntegerValue", "DisplayValue", true);
        noteCode.setMaxLength(4);
        noteCode.setReadOnly(true);
        
        adjReason = new IntegerEnumeratedAttribute("AdjReason", this, 
                "GuiIndicator.FieldName=adj_reason", "IntegerValue", "DisplayValue", false);
        
        reloadReason = new IntegerEnumeratedAttribute("ReloadReason", this, 
                "GuiIndicator.FieldName=rld_reason", "IntegerValue", "DisplayValue", false);

        callBarReason = new IntegerEnumeratedAttribute("CallBarReason", this, 
                "GuiIndicator.FieldName=call_bar_reason", "IntegerValue", "DisplayValue", false);

        voucherBarReason = new IntegerEnumeratedAttribute("VoucherBarReason", this, 
                "GuiIndicator.FieldName=voucher_bar_reason", "IntegerValue", "DisplayValue", false);
        
        Map reasonMap = new HashMap();
        int i = 0;
        for ( i = 0; i < adjReason.getNumberOfRows(); i++ )
            reasonMap.put(adjReason.getKey(i), adjReason.getDescription(i));
        for ( i = 0; i < reloadReason.getNumberOfRows(); i++ )
            reasonMap.put(reloadReason.getKey(i), reloadReason.getDescription(i));
        for ( i = 0; i < callBarReason.getNumberOfRows(); i++ )
            reasonMap.put(callBarReason.getKey(i), callBarReason.getDescription(i));
        for ( i = 0; i < voucherBarReason.getNumberOfRows(); i++ )
            reasonMap.put(voucherBarReason.getKey(i), voucherBarReason.getDescription(i));

        reason = new IntegerCustomEnumeratedAttribute("Reason", this, reasonMap, "1", false);
        reason.setReadOnly(true);
        
        noteText = new StringAttribute("NoteText", this, false);
        noteText.setMaxLength(80);
        noteText.setNillable(true);
        noteText.setReadOnly(true);
        
        remarks = new StringAttribute("Remarks", this, false);
        remarks.setMaxLength(85);
        remarks.setRequired(true);
        
        changeWho = new StringAttribute("ChangeWho", this, false);
        changeWho.setMaxLength(30);
        changeWho.setReadOnly(true);
        
        noteId = new IntegerAttribute("NoteId", this, false);
        noteId.setReadOnly(true);
    }
    
    public boolean flush(IModelAdapter ma) {
        boolean success = true;
        
        String SQL = "INSERT INTO MXS_PREPAID_NOTES ";
        SQL = SQL + "(note_id, note_code, note_text, reason_id, remarks, chg_who, note_date, ";
        SQL = SQL + "subscr_no, subscr_no_resets) ";
        SQL = SQL + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CUST");
            if (dbConn == null) {
                err.logErrorToFile("Error getting current Oracle Connection", getClass());
               return false;
            }
            PreparedStatement pstmt = dbConn.prepareStatement(new SQL(SQL, "insert"));
            System.out.println(SQL);
            System.out.println("Note ID = " + noteId.getDataAsInteger());
            System.out.println("Note Code = " + noteCode.getDataAsInteger());
            pstmt.setInt(1, noteId.getDataAsInteger());
            pstmt.setInt(2, noteCode.getDataAsInteger());
            pstmt.setString(3, noteText.getDataAsString());
            pstmt.setInt(4, reason.getDataAsInteger());
            pstmt.setString(5, remarks.getDataAsString());
            pstmt.setString(6, changeWho.getDataAsString());
            pstmt.setTimestamp(7, SqlDate.Now());
            pstmt.setInt(8, subscrNo.getDataAsInteger());
            pstmt.setInt(9, subscrNoResets.getDataAsInteger());

            pstmt.executeUpdate();
            pstmt.close();
            
        } catch (FxException fx) {
            addError(ErrorFactory.createError("Kenan Framework Error " + fx.getMessage(),
                    null, com.maxis.businessobject.PrepaidNotes.class));
            err.logErrorToFile("FX Exception "+fx.getMessage(), getClass());
            fx.printStackTrace();
            success = false;
        } catch (java.sql.SQLException ex) {
            addError(ErrorFactory.createError("SQL error on table MXS_PREPAID_NOTES " + ex.getMessage(),
                    null, com.maxis.businessobject.PrepaidNotes.class));
            err.logErrorToFile("SQL Exception "+ex.getMessage(), getClass());
            success = false;
        } catch (Exception e) {
            err.logErrorToFile("Exception ", getClass());
            e.printStackTrace();
            success = false;
        }
        
        return success;
    }
}
