/*
 * Defaulter.java
 *
 * Created on October 13, 2005, 4:22 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.businessobject;

import com.csgsystems.domain.framework.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.attribute.*;

/**
 * Business object for defaulters from DDMF
 * @author Pankaj Saini
 */
public class Defaulter extends Domain {
    
    protected IntegerAttribute matchType = null;
    protected StringAttribute company = null;
    protected StringAttribute submitDate = null;
    protected StringAttribute ic_number = null;
    protected StringAttribute new_ic_number = null;
    protected StringAttribute br_number = null;
    protected StringAttribute name = null;
    protected StringAttribute account = null;
    protected StringAttribute lastEnqCompany = null;
    protected StringAttribute lastEnqDate = null;
    
    /** Creates a new instance of Defaulter */
    public Defaulter() {
        matchType = new IntegerAttribute("Status", this, false);
        company = new StringAttribute("SubmitBy", this, false);
        submitDate = new StringAttribute("InputDate", this, false);
        ic_number = new StringAttribute("OldIC", this, false);
        new_ic_number = new StringAttribute("NewIC", this, false);
        br_number = new StringAttribute("BRN", this, false);
        name = new StringAttribute("Name", this, false);
        account = new StringAttribute("Account", this, false);
        lastEnqCompany = new StringAttribute("LastEnqBy", this, false);
        lastEnqDate = new StringAttribute("LastEnqOn", this, false);
    }
    
}
