/*
 * FriendsAndFamilyList.java
 *
 * Created on June 17, 2005, 5:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.businessobject;

import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.fxcommon.attribute.*;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.arbor.businessobject.Service;

/**
 *
 * @author Pankaj Saini
 */
public class FriendsAndFamilyList extends DomainCollection {
    
    protected IPersistentObject service = null;

    /** Creates a new instance of FriendsAndFamilyList */
    public FriendsAndFamilyList() {
        super(FriendsAndFamily.class);
    }
    
    protected boolean localAddAssociation(IPersistentObject relObject) {
        boolean success = false;
//        if (relObject instanceof Service) { -- assuming that the relObject is always a service
            service = relObject;
            success = true;
//        }

        return success;
    }

    protected int listPageFault(int nStart) {

        // Get the prepaid IN collection from service
        IPersistentCollection prepaidINList = (IPersistentCollection)service.getObject("com.maxis.businessobject.PrepaidINList", 
                "Service");
        if ( prepaidINList == null ) {
            System.out.println("received null list object from service!");
            return 0;
        }
        IPersistentObject prepaidIN = prepaidINList.getAt(0);
        if ( prepaidIN == null ) {
            System.out.println("received null object from IN list!");
            return 0;
        }        
        
        // Get number of slots to show from the prepaid IN object
        int fnfCount = prepaidIN.getAttributeDataAsInteger("FnFCnt");
        if (fnfCount == 0) return 0;
        
        for ( int i = 0; i < fnfCount; i++ ) {
            String attributeName = "FnFNumber" + i;
            System.out.println("Number = |" + prepaidIN.getAttributeDataAsString(attributeName) + "|");
            if ( prepaidIN.getAttribute(attributeName).isEmpty() == false ) { // to trap bad data errors
                    IPersistentObject fnf = (IPersistentObject)PersistentObjectFactory.getFactory().createNew(FriendsAndFamily.class, null);
                    fnf.setAttributeDataAsString("Description", "Slot #" + ( i + 1 ));
                    String attributeValue = prepaidIN.getAttributeDataAsString(attributeName);
                    if ( attributeValue.equals("null") )
                        fnf.getAttribute("Number").setEmpty(true);
                    else
                        fnf.setAttributeDataAsString("Number", attributeValue);
                    add(fnf);
            }
        }
        
        return 0;
    }
}
