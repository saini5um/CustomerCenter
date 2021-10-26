/*
 * MXSNewAcct.java
 *
 * Created on October 25, 2005, 4:03 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import com.csgsystems.bp.context.NewCustAcqAccount;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created new context for Screen 1 of NCA - Account Information
 * Done to set the birth date if new IC is searched for
 * @author Pankaj Saini
 */
public class MXSNewAcct extends NewCustAcqAccount {
    
    /** Creates a new instance of MXSNewAcct */
    public MXSNewAcct() {
    }
    
    public boolean processShutdown(int shutdownType) {
        System.out.println("new context shutting down...");
        if ( shutdownType == NEXT ) {
            IPersistentObject acct = getObject("Account");
            IPersistentObject go = getObject("GenericDomain", null);
            if ( go == null ) System.out.println("generic domain not found");
            if ( go.getAttributeDataAsInteger("Type") == 9 ) // New IC
                setBirthDate(acct, go.getAttributeDataAsString("Value"));
        }
        return super.processShutdown(shutdownType);
    }
    
    private void setBirthDate(IPersistentObject acct, String icNumber) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        Date birthDay = null;
        try {
            birthDay = df.parse(icNumber.substring(4, 6) + "/" + 
                    icNumber.substring(2, 4) + "/" + icNumber.substring(0, 2));            
            acct.setAttributeDataAsDate("Birth Date_3", birthDay);
        } catch (Exception e) {
            System.out.println("parse err:" + e.getMessage());
        }
    }
}
