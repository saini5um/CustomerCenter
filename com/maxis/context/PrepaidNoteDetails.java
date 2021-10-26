/*
 * PrepaidNoteDetails.java
 *
 * Created on June 8, 2005, 4:21 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import com.csgsystems.domain.framework.context.Context;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.fx.security.*;
import com.csgsystems.fx.security.entity.*;
import com.csgsystems.fx.security.util.*;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.maxis.businessobject.PrepaidNotes;
import com.csgsystems.domain.framework.security.SecurityManager;

/**
 *
 * @author Pankaj Saini
 */
public class PrepaidNoteDetails extends Context {
    
    /** Creates a new instance of PrepaidNoteDetails */
    public PrepaidNoteDetails() {
//        domainName = "Note";
        
//        parentDomainName = "Account";
    }
    
    public boolean open(IContext context) {
        IPersistentObject service = context.getObject("Service", null);
        if (service != null) {
            addTopic(service, true);
        }
        
        return true;
    }

    public boolean open(IPersistentObject object) {
        boolean success = false;

        if (object != null) {
            object.getAttribute("Remarks").setEmpty(true);
            String userId = SecurityManager.getInstance().getLoggedInUserId();
            object.setAttributeDataAsString("ChangeWho", userId);
            addTopic(object, false);
            success = true;
        }
        return success;
    }
    
    public boolean processShutdown(int shutdownType) {
        boolean success = true;
        
        if (shutdownType == CANCELLED) {
        } else if (shutdownType == OK) {
            success = this.flush();
        }
        
        return success;
    }
}
