/*
 * UpdateBirthDate.java
 *
 * Created on October 25, 2005, 5:04 PM
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

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import com.maxis.util.SqlDate;

/**
 * Action class created to set the birth date extended data on account object
 * for NCA if new IC was searched for
 * @author Pankaj Saini
 */
public class UpdateBirthDate extends BaseWorkflowAction {
    
    /** Creates a new instance of UpdateBirthDate */
    public UpdateBirthDate() {
    }
    
    public final void execute (ActionEvent e, IContext context) {
        IPersistentObject newAcct = context.getObject("Account", null);
        IPersistentObject go = context.getObject("GenericDomain", null);
        if ( newAcct != null && go != null ) {
            if ( go.getAttributeDataAsInteger("Type") == 9 ) // New IC
                setBirthDate(newAcct, go.getAttributeDataAsString("Value"));
        }
        
        fireEvent("next");
        return;
    }
    
    private void setBirthDate(IPersistentObject acct, String icNumber) { // format yymmdd
        SimpleDateFormat icDateFormat = new SimpleDateFormat("yyMMdd");
        icDateFormat.setCalendar(new GregorianCalendar());
        icDateFormat.setLenient(false);
        
        Date birthDay = null;
        try {
            birthDay = icDateFormat.parse(icNumber.substring(0, 6));
            if ( birthDay != null ) acct.setAttributeDataAsDate("Birth Date_3", birthDay);
        } catch (Exception e) {
            System.out.println("parse err:" + e.getMessage());
        }
    }
}
