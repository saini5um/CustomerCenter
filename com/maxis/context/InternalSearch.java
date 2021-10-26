/*
 * InternalSearch.java
 *
 * Created on October 7, 2005, 2:25 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import java.util.Map;
import java.util.HashMap;

import com.csgsystems.bp.context.AccountSearchContext;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.context.IContext;

/**
 *
 * @author Pankaj Saini
 */
public class InternalSearch extends AccountSearchContext {
    
    /** Creates a new instance of InternalSearch */
    public InternalSearch() {
    }
    
    public boolean open(IContext context) {
        logDebug("InternalSearch Context.open(context)");
        boolean success = true;

        IPersistentObject go = context.getObject("GenericDomain");
        if ( go == null ) { setError("CC-2-20", null); return false; }
        
        IPersistentCollection coll = (IPersistentCollection) poObjectFactory.createNew("MXSAccountSearchList", null);

        if (coll != null) {
            Map param = new HashMap();
            param.put("Type", new Integer(go.getAttributeDataAsInteger("Type")));
            param.put("Value", (String)go.getAttributeDataAsString("Value"));
            coll.setSearchParameters(param);
            coll.reset();
            
            addTopic((IPersistentObject) coll, true); 
        } else {
            System.out.println("unable to create MXSAccountSearchList");
            success = false;
            setError("CC-2-20", null);
        }
        
        return success;
    }

}
