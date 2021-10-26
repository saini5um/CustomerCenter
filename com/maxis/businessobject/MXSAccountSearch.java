/*
 * MXSAccountSearch.java
 *
 * Created on October 7, 2005, 4:09 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.businessobject;

import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.Domain;
import com.csgsystems.domain.framework.businessobject.ReferenceElement;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.domain.arbor.businessobject.*;

import java.util.Iterator;

/**
 * Class to extend accountLocate object for the purposes of Internal Search
 * @author Pankaj Saini
 */
public class MXSAccountSearch extends Domain {

    protected ReferenceElement account;
    protected IntegerAttribute serviceCount;
    protected ReferenceElement accountLocate;
    protected StringAttribute sumBalance;
    
    /** Creates a new instance of MXSAccountSearch */
    public MXSAccountSearch() {
        serviceCount = new IntegerAttribute("ActiveServices", this, false);
        sumBalance = new StringAttribute("SumBalance", this, false);
        serviceCount.setReadOnly(true);        
    }    
    
    protected boolean localAddAssociation(IPersistentObject relObject) {
        boolean success = true;
        
        if ( relObject instanceof Account ) {
            account = new ReferenceElement("Account", null, this, true, false);
            account.setRelatedObject(relObject);
        } else if ( relObject instanceof AccountLocate ) {
            accountLocate = new ReferenceElement("AccountLocate", null, this, true, false);
            accountLocate.setRelatedObject(relObject);
        }
        
        return success;
    }
}
