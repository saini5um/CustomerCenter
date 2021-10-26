package com.maxis.context;

import java.util.Map;

import com.csgsystems.domain.framework.context.Context;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.arbor.order.*;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.IPersistentObjectFactory;


public class MXSAccountSearchContext extends Context {
    
    // WARNING ... this context should only be used when searching for
    // a new account for the purpose of ordering.  This context will
    // clear any current order when it is opened.
    public boolean open(IContext object) {
        logDebug("AccountSearchContext.open(context)");
        boolean success = true;
        IPersistentCollection coll = (IPersistentCollection) poObjectFactory.createNew("AccountLocateList", null);
        createNewAccountId();

        if (coll != null)
        {
            addTopic((IPersistentObject) coll); 

            // Alex: GEN Req. 2.1.1.1 
            // Teoh moved to xlogic
            //coll.setAttributeData("AccountExternalIdType", "9");
            //coll.setAttributeData("ServiceExternalIdType", "23");
            coll.reset();
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
        System.out.println("*** reset() called");
        // Make sure that the Account object is cleared as a topic from this context.
        IPersistentObject obj = getObject("AccountLocate", null);        
        if (obj != null) {
            // Remove object from local topic list
            removeTopic(obj);
        }
    }

    public boolean isValid() {
        logDebug("AccountSearchContext.isValid()");
        System.out.println("*** isValid() Called");
        // This context is valid if it has an AccountLocate object as one of it's topics.
        boolean bSuccess = true;
        IPersistentObject obj = getObject("AccountLocate", null);
        if (obj == null) {
            bSuccess = false;
            setError("CC-2-21", null);
        }
        return bSuccess;
    }

    private void createNewAccountId() {        
        IPersistentObjectFactory poObjectFactory = PersistentObjectFactory.getFactory();
        // Regardless of what is in the context, create a new object and add it as a topic
        IPersistentObject newObj = poObjectFactory.createNew("AccountId", null);
        addTopic(newObj, true); // set to not flush. It will be sent in the collection

        IPersistentObject newObj1 = poObjectFactory.createNew("CustomerIdEquipMap", null);
        addTopic(newObj1, true); // set to not flush. It will be sent in the collection
        IPersistentObject newAcct = poObjectFactory.createNew("Service", null);
        newObj1.addAssociation(newAcct);        
    }

    private void createCustomerIdEquipMap() {        
        IPersistentObjectFactory poObjectFactory = PersistentObjectFactory.getFactory();
        // Regardless of what is in the context, create a new object and add it as a topic
        IPersistentObject newObj = poObjectFactory.createNew("CustomerIdEquipMap", null);
        addTopic(newObj, true); // set to not flush. It will be sent in the collection
    }
}
