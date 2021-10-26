/*
 * FavouriteArea.java
 *
 * Created on July 6, 2005, 1:59 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import com.csgsystems.domain.framework.context.Context;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.maxis.businessobject.PrepaidNotes;
import com.csgsystems.domain.framework.IPersistentObjectFactory;
import com.csgsystems.domain.framework.security.SecurityManager;
import com.csgsystems.domain.framework.attribute.*;

import com.csgsystems.fx.security.remote.*;
import com.csgsystems.fx.security.util.FxException;
import java.sql.*;
import com.maxis.util.*;
import com.maxis.xlogic.*;
import com.csgsystems.error.IError;
import com.csgsystems.error.ErrorFactory;
import java.util.HashMap;
import java.util.Map;
import java.lang.Long;
import com.csgsystems.cache.*;
import com.csgsystems.xlogic.*;
import com.csgsystems.domain.arbor.businessobject.Service;
import com.csgsystems.util.service.ServiceFinder;
import com.csgsystems.transport.IModelAdapter;
import com.csgsystems.transport.ModelAdapterFactory;

import com.maxis.integ.*;
import com.maxis.integ.IntegException;

/**
 *
 * @author Pankaj Saini
 */
public class FnF extends Context {

    protected ErrorReporting err = null;
    protected IPersistentObject pGenericObject = null;
    protected IntegerEnumeratedAttribute m_Type = null;
    protected StringAttribute m_Value = null;
    
    /** Creates a new instance of FriendsAndFamily */
    public FnF() {
        err = ErrorReporting.getInstance();
        System.out.println("context instantiated");
    }
    
    public boolean open(IContext context) {
        System.out.println("open by context called...");
        pGenericObject = PersistentObjectFactory.getFactory().createNew("GenericDomain",null);
        if (pGenericObject == null) {
            return false;
        }

        m_Value = new StringAttribute("Destination",  (Domain)pGenericObject, true);
        m_Type = new IntegerEnumeratedAttribute("Zone", (Domain)pGenericObject,
                "GenericEnumeration.EnumerationKey=maxis_home_zone", "Value", "DisplayValue", true);

        addTopic(pGenericObject, true); // read only
/*        addError(ErrorFactory.createError("Type is required", 
                null, com.maxis.businessobject.FriendsAndFamily.class));
        addError(ErrorFactory.createError("Value is required", 
                null, com.maxis.businessobject.FriendsAndFamily.class)); */
        return true;
    }

    public boolean open(IPersistentObject object) {
        boolean success = false;
        System.out.println("open by Persistent Object called");
        
        return success;
    }
    
    public boolean processShutdown(int shutdownType) {
        boolean success = true;
        System.out.println("called shutdown");
        if (shutdownType == CANCELLED) {
            System.out.println("shutdown type is cancel");
        } else if (shutdownType == OK) {
        } else {
            System.out.println("ShutdownType is not OK!");
//            success = false;
        }
        return success;
    }
    
}
