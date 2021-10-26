/*
 * InternalSearchServiceDetails.java
 *
 * Created on October 10, 2005, 4:04 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import java.util.Map;
import java.util.HashMap;

import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.criteria.FilterCriteria;
import com.csgsystems.domain.framework.criteria.QueryCriteria;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.context.Context;

/**
 * Class to show the service details
 * @author Pankaj Saini
 */
public class InternalSearchServiceDetails extends Context {
    
    /** Creates a new instance of InternalSearch */
    public InternalSearchServiceDetails() {
    }
    
    public boolean open(IContext context) {
        logDebug("InternalSearchServiceDetails Context.open(context)");
        boolean success = true;
        
        return success;
    }

    public boolean open(IPersistentObject object) {
        logDebug("InternalSearchServiceDetails Context.open(persistentObject)");
        boolean success = true;
        Map parameters = new HashMap();

        IPersistentObject account = object.getObject("Account");
        
        IPersistentCollection coll = (IPersistentCollection) account.getObject("ServiceList", "Account");
        if (coll != null)
        {
            addTopic((IPersistentObject) coll, true);
            success = true;
        } else {
            logDebug("Unable to obtain ServiceList from Account");
            setError("CC-2-20", null);
        }
        
        if (account != null) {
            QueryCriteria qc = new QueryCriteria();
            qc.setFilterCriteria(FilterCriteria.createEquals(account.getAttributeData("AccountInternalId")));
            qc.setFetch(true);
            parameters.put("ParentAccountInternalId", qc);
        }
        
        IPersistentCollection resultSet = 
                (IPersistentCollection) PersistentObjectFactory.getFactory().createNew("ServiceList", "ResultSet");
        if ( resultSet != null )
        {
            resultSet.setDefaultCriteria(parameters);
//            resultSet.getAttribute("ServiceInactiveDt").setEmpty(true);
            resultSet.setAttributeDataAsBoolean("ServiceInactiveDt", true);
            resultSet.setFaulted(false);
            addTopic((IPersistentObject) resultSet, true);
            success = true;
        } else {
            logDebug("Unable to obtain ServiceList from Account");
            setError("CC-2-20", null);
        }
        return success;
    }
}
