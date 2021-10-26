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
import com.maxis.util.tuxJDBCManager;

import com.maxis.util.ErrorReporting;
import com.maxis.util.IntegerCustomEnumeratedFilterAttribute;

import java.sql.*;
import java.util.*;

/**
 *
 * @author Pankaj Saini
 */
public class PrepaidNotesList extends DomainCollection {
    
    protected int subscr_no = 0;
    protected int subscr_no_resets = 0;
    private ErrorReporting err = ErrorReporting.getInstance();
    
    protected TimestampFilterAttribute noteDate = null;
    protected IntegerEnumeratedFilterAttribute noteCode = null;
    protected IntegerCustomEnumeratedFilterAttribute reason = null;
    protected StringFilterAttribute noteText = null;
    protected StringFilterAttribute remarks = null;
    protected StringFilterAttribute changeWho = null;
    protected IntegerEnumeratedAttribute adjReason = null;
    protected IntegerEnumeratedAttribute reloadReason = null;
    protected IntegerEnumeratedAttribute callBarReason = null;
    protected IntegerEnumeratedAttribute voucherBarReason = null;
    protected IntegerAttribute displayRowsPerPage = null;
    protected IntegerAttribute displayMaxPages = null;
    
    protected String filter = null;
    protected String additionalFilter = null;
    protected String sort = null;
    
    /** Creates a new instance of PrepaidNotesList */
    public PrepaidNotesList() {
        super(PrepaidNotes.class);
        noteDate = new TimestampFilterAttribute("NoteDate", this, false);
        noteCode = new IntegerEnumeratedFilterAttribute("NoteCode", this, 
                "GuiIndicator.FieldName=note_code", "IntegerValue", "DisplayValue", false);
        noteText = new StringFilterAttribute("NoteText", this, false);
        noteText.setMaxLength(80);
        
        remarks = new StringFilterAttribute("Remarks", this, false);
        remarks.setMaxLength(85);
        
        changeWho = new StringFilterAttribute("ChangeWho", this, false);
        adjReason = new IntegerEnumeratedAttribute("AdjReason", this, 
                "GuiIndicator.FieldName=adj_reason", "IntegerValue", "DisplayValue", true);
        
        reloadReason = new IntegerEnumeratedAttribute("ReloadReason", this, 
                "GuiIndicator.FieldName=rld_reason", "IntegerValue", "DisplayValue", true);

        callBarReason = new IntegerEnumeratedAttribute("CallBarReason", this, 
                "GuiIndicator.FieldName=call_bar_reason", "IntegerValue", "DisplayValue", true);

        voucherBarReason = new IntegerEnumeratedAttribute("VoucherBarReason", this, 
                "GuiIndicator.FieldName=voucher_bar_reason", "IntegerValue", "DisplayValue", true);
        
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

        reason = new IntegerCustomEnumeratedFilterAttribute("Reason", this, reasonMap, null, false);
        reason.setReadOnly(false);
        
        displayRowsPerPage = new IntegerAttribute("DisplayRowsPerPage", this, true);
        this.setAttributeDataAsInteger("DisplayRowsPerPage", 10);
        displayMaxPages = new IntegerAttribute("DisplayPages", this, true);
        this.setAttributeDataAsInteger("DisplayPages", 10);
        
        additionalFilter = "";
        sort = "ORDER BY NOTE_ID DESC, NOTE_DATE DESC";
    }
    
    protected boolean localAddAssociation(IPersistentObject relObject) {
        boolean success = false;
        
        subscr_no = relObject.getAttributeDataAsInteger("ServiceInternalId");
        subscr_no_resets = relObject.getAttributeDataAsInteger("ServiceInternalIdResets");
        filter = "WHERE SUBSCR_NO = " + Integer.toString(subscr_no) + " ";
        filter += "AND SUBSCR_NO_RESETS = " + Integer.toString(subscr_no_resets) + " ";
        success = true;

        return success;
    }

    public void setSearchParameters(Map m) {
        additionalFilter = "";
        if (m.containsKey("NoteCode"))
            additionalFilter += "AND NOTE_CODE = " + m.get("NoteCode") + " ";
        if (m.containsKey("NoteText"))
            additionalFilter += "AND NOTE_TEXT = '" + m.get("NoteText") + "' ";
        if (m.containsKey("Reason"))
            additionalFilter += "AND REASON_ID = " + m.get("Reason") + " ";
        if (m.containsKey("Remarks"))
            additionalFilter += "AND REMARKS = '" + m.get("Remarks") + "' ";
        if (m.containsKey("ChangeWho"))
            additionalFilter += "AND CHG_WHO = '" + m.get("ChangeWho") + "' ";
        if (m.containsKey("Date"))
            additionalFilter += "AND TO_CHAR(NOTE_DATE, 'DD-MM-YYYY') = '" + m.get("Date") + "' ";
    }
    
    protected int listPageFault(int nStart) {
        if (nStart < 0) {
            logError("listPageFault() called with an invalid value: " + nStart, null);
            throw new IllegalArgumentException("Only positive integers may be specified");
        }
        
        int foundCount = 0;
        
        String SQL = "SELECT NOTE_CODE, NOTE_DATE, NOTE_TEXT, REASON_ID, ";
        SQL += "REMARKS, CHG_WHO, NOTE_ID FROM MXS_PREPAID_NOTES ";
        SQL += filter;
        SQL += additionalFilter;
        SQL += sort;

        RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CUST");
        if (dbConn == null) {
            err.logErrorToFile("Error getting current Oracle Connection.", getClass());
           return 0; // prevent windows from shutting down
        }

        PreparedStatement pstmt = null;
        try {
            pstmt = dbConn.prepareStatement (new SQL(SQL, "select"));
            pstmt.setMaxRows(100);
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
            int rset_index = 0;
            while (rset.next()) {
                IPersistentObject prepaidNote = (IPersistentObject)PersistentObjectFactory.getFactory().createNew(PrepaidNotes.class, null);
                if (prepaidNote == null) {
                    err.logErrorToFile("Couldn't create PrepaidNote.", PrepaidNotesList.class);
                } else {
                    prepaidNote.getAttribute("NoteCode").setDataAsInteger(rset.getInt(1)); 
                    prepaidNote.getAttribute("NoteDate").setDataAsDate(rset.getDate(2));
                    prepaidNote.getAttribute("NoteText").setDataAsString(rset.getString(3)); 
                    prepaidNote.getAttribute("Reason").setDataAsInteger(rset.getInt(4));
                    prepaidNote.getAttribute("Remarks").setDataAsString(rset.getString(5)); 
                    prepaidNote.getAttribute("ChangeWho").setDataAsString(rset.getString(6));
                    prepaidNote.getAttribute("NoteId").setDataAsInteger(rset.getInt(7));

                    prepaidNote.setAttributeDataAsInteger("SubscrNo", subscr_no);
                    prepaidNote.setAttributeDataAsInteger("SubscrNoResets", subscr_no_resets);
                    
                    prepaidNote.setReadOnly(true);
                    // Insert New Domain into Collection
                    add(prepaidNote);
                } 
            }
            rset.close();
            pstmt.close();
        } catch (SQLException sEx) {
            sEx.printStackTrace();
            err.logErrorToFile("SQL Exception "+sEx.getMessage(), PrepaidNotesList.class);
            System.out.println(sEx.getMessage());
            return 0;
        }

        return 0;
    }
    
}
