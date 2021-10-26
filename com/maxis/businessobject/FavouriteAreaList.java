/*
 * FavouriteAreaList.java
 *
 * Created on July 6, 2005, 5:26 PM
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

/**
 *
 * @author Pankaj Saini
 */
public class FavouriteAreaList extends DomainCollection {
    
    protected IPersistentObject service = null;

    /** Creates a new instance of FriendsAndFamilyList */
    public FavouriteAreaList() {
        super(FavouriteArea.class);
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
        int faCount = prepaidIN.getAttributeDataAsInteger("FACnt");
        if (faCount == 0) return 0;
        
        for ( int i = 0; i < faCount; i++ ) {
            String attributeName = "FADestination" + i;
            if ( prepaidIN.getAttribute(attributeName).isEmpty() == false ) { // to trap bad data errors
                IPersistentObject fa = (IPersistentObject)PersistentObjectFactory.getFactory().createNew(FavouriteArea.class, null);
                fa.setAttributeDataAsString("Description", "Slot #" + ( i + 1 )); 
                fa.setAttributeDataAsInteger("Area", Integer.parseInt(prepaidIN.getAttributeDataAsString(attributeName)));
                add(fa);
            }
        }
        
        return 0;
    }
}
