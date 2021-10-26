/*
 * FriendsAndFamily.java
 *
 * Created on June 17, 2005, 5:20 PM
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
 *
 * @author Pankaj Saini
 */
public class FriendsAndFamily extends Domain {
    
    protected LongAttribute msisdn = null;
    protected StringAttribute paramName = null;

    /** Creates a new instance of FriendsAndFamily */
    public FriendsAndFamily() {
        paramName = new StringAttribute("Description", this, false);
        paramName.setReadOnly(true);
        
        msisdn = new LongAttribute("Number", this, false);
        msisdn.setMaxLength(11);
    }
    
}
