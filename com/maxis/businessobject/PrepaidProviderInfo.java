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

/**
 *
 * @author Lee Wynn, Teoh
 *
 */
public class PrepaidProviderInfo extends Domain {

    protected IntegerAttribute subscrNo = null;
    protected IntegerAttribute subscrNoResets = null;
    protected TimestampAttribute noteDate = null;
    protected StringEnumeratedAttribute noteCode = null;
    protected IntegerEnumeratedAttribute reasonId = null;
    protected StringAttribute noteText = null;
    protected StringAttribute remarks = null;
    protected StringAttribute changeWho = null;

    /** Creates a new instance of PrepaidNotes */
    public PrepaidProviderInfo() {
        subscrNo = new IntegerAttribute("SubscrNo", this, false);
        subscrNoResets = new IntegerAttribute("SubscrNoResets", this, false);
        
        noteDate = new TimestampAttribute("NoteDate", this, false);
        noteDate.setDateTimeInclude(IDateTime.DATETIME);
        noteDate.setReadOnly(true);
        
        noteCode = new StringEnumeratedAttribute("NoteCode", this, 
                "NoteCode", "Code", "Code", true);
        noteCode.setMaxLength(4);
        noteCode.setReadOnly(true);
        
        reasonId = new IntegerEnumeratedAttribute("Reason", this, 
                "GuiIndicator.FieldName=bar_status", "IntegerValue", "DisplayValue", false);
        
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
    }
    
}
