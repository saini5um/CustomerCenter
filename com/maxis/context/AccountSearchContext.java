package com.maxis.context;

import java.util.Map;
import java.util.HashMap;

import com.csgsystems.domain.framework.context.Context;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.arbor.order.*;


public class AccountSearchContext extends Context {
    
    // WARNING ... this context should only be used when searching for
    // a new account for the purpose of ordering.  This context will
    // clear any current order when it is opened.
    public boolean open(IContext object) {
        logDebug("AccountSearchContext.open(context)");
        boolean success = true;

        System.out.println("open by context entered");
        IPersistentCollection coll = (IPersistentCollection) poObjectFactory.createNew("AccountLocateList", null);

        if (coll != null)
        {
/*            Map param = new HashMap();
            param.put("Fetch", "true");
            Map p = new HashMap();
            p.put("AccountExternalId", "1176");
            p.put("AccountExternalIdType", "1");
            p.put("Fetch", "true");
            param.put("AccountLocate", p);
            coll.setSearchParameters(param);
            coll.reset();
*/
            coll.setAttributeDataAsInteger("AccountExternalIdType", 9);
            coll.setAttributeDataAsString("AccountExternalId", "830827106093");
            coll.reset();
            
            addTopic((IPersistentObject) coll); 
            
            // Selection of an account is meant to create a new order, so clear out the current order.
            // Account.notifyPostUnmarshal will create a new order if one does not exist.
            OrderManager.getInstance().clearCurrentOrder();
        } else {
            success = false;
            setError("CC-2-20", null);
        }
        
        return success;
    }
    
    public boolean open(Map parameters) {
        logDebug("AccountSearchContext.open(map)");
        boolean success = true;

        System.out.println("open by param entered");
        // Need to fetch the data at the highest level...
        parameters.put("Fetch", "true");

        IPersistentCollection coll = (IPersistentCollection) poObjectFactory.createNew("AccountLocateList", null);
        coll.setSearchParameters(parameters);
        
        // Make sure we reset the collection so that we'll fault it again.
        coll.reset();
        
        addTopic((IPersistentObject) coll);
        
        // Selection of an account is meant to create a new order, so clear out the current order.
        // Account.notifyPostUnmarshal will create a new order if one does not exist.
        OrderManager.getInstance().clearCurrentOrder();
        
        return success;
    }

    public void reset() {
        logDebug("AccountSearchContext.reset()");
        // Make sure that the Account object is cleared as a topic from this context.
        IPersistentObject obj = getObject("AccountLocate", null);
        
        if (obj != null) {
            // Remove object from local topic list
            removeTopic(obj);
        }
    }

    public boolean isValid() {
        logDebug("AccountSearchContext.isValid()");
        // This context is valid if it has an AccountLocate object as one of it's topics.
        boolean bSuccess = true;

        IPersistentObject obj = getObject("AccountLocate", null);
        if (obj == null) {
            bSuccess = false;
            setError("CC-2-21", null);
        }
        return bSuccess;
    }
}
