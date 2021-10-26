/*
 * PrepaidNoteSearch.java
 *
 * Created on June 13, 2005, 2:42 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import com.csgsystems.domain.framework.context.Context;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.arbor.businessobject.Service;
import java.util.*;
import com.maxis.util.SqlDate;

/**
 *
 * @author Pankaj Saini
 */
public class PrepaidNoteSearch extends Context {
    
    /** Creates a new instance of PrepaidNoteSearch */
    public PrepaidNoteSearch() {
    }

    public boolean open(IPersistentObject obj) {
        boolean success = false;
        this.addTopic(obj, true);

        IPersistentObject noteList = obj.getObject("PrepaidNotesList", "Service");
        if (noteList != null) {
            this.addTopic(noteList, true);
            success = true;
        }
            
        return success;
    }
    
    public boolean processShutdown(int shutdownType) {
        boolean success = true;
        
        if (shutdownType == CANCELLED) {
        } else if (shutdownType == OK) {
            IPersistentCollection noteList = (IPersistentCollection)this.getObject("PrepaidNotesList", "Service");
            
            Map sc = new HashMap();
            if (!noteList.getAttribute("NoteCode").isEmpty()) {
                sc.put("NoteCode", new Integer(noteList.getAttributeDataAsInteger("NoteCode")));
            }
            if (!noteList.getAttribute("NoteText").isEmpty()) {
                sc.put("NoteText", noteList.getAttributeDataAsString("NoteText"));
            }
            if (!noteList.getAttribute("Reason").isEmpty()) {
                sc.put("Reason", new Integer(noteList.getAttributeDataAsInteger("Reason")));
            }
            if (!noteList.getAttribute("Remarks").isEmpty()) {
                sc.put("Remarks", noteList.getAttributeDataAsString("Remarks"));
            }            
            if (!noteList.getAttribute("ChangeWho").isEmpty()) {
                sc.put("ChangeWho", noteList.getAttributeDataAsString("ChangeWho"));
            }            
            if (!noteList.getAttribute("NoteDate").isEmpty()) {
                Date d = noteList.getAttributeDataAsDate("NoteDate");
                sc.put("Date", SqlDate.Format(d));
            }            
            
            noteList.setSearchParameters(sc); // will take map of search criteria
//            (PrepaidNotesList(noteList)).setFilterCriteria();
        }
        
        return success;
    }   
}
