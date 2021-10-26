/*
 * ServiceExtendedLogic.java
 *
 * Copyright (C) 2003 CSG Systems
 * www.csgsystem.com
 *
 * Created on Feb 11, 2004
 */
package com.maxis.xlogic;

import java.util.Map;
import com.csgsystems.domain.framework.attribute.IAttribute;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.xlogic.ExtendedLogicBase;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.fxcommon.attribute.*;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.domain.framework.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.maxis.businessobject.PrepaidINList;

/**
 * This class is an example implementation of the IExtendedLogic
 * interface and illustrates how to extend the logic for an
 * application domain.<p>
 *
 * This class provides an example implementation of each method
 * on the IExtendedLogic interface. However, in the real world,
 * it is unlikely that each method of the interface will require
 * implementation for a particular application domain. Use the
 * ExtendedLogicBase base to provide the default implementation
 * of methods that do not require specific overrides.
 *
 * @author djs
 */
public class ServiceExtendedLogic extends ExtendedLogicBase {
    
    ReferenceElement m_prepaidServiceList = null;
    ReferenceElement m_prepaidNotesList = null;
    ReferenceElement PrepaidINList = null;
    ReferenceElement m_FnFList = null;
    ReferenceElement m_FAList = null;

    //added by ming hon
    private static Log log = null;
    static{
        try{
            log = LogFactory.getLog(com.maxis.xlogic.ServiceExtendedLogic.class);
        }
        catch(Exception ex) { }
    }
    
    /**
     * Required public no-argument constructor. See the
     * FXExtendedLogicFactory for details on instantiating
     * this object.
     */
    public ServiceExtendedLogic() {
        super();
    }
    
    /**public boolean addAssociation(IPersistentObject target, IPersistentObject objToAssociate) {
     *boolean success = true;
     *System.out.println("AddAssociation");
     *System.out.println("Source "+objToAssociate);
     *System.out.println("Target "+target);
     *return success;
     *
     *}
     */
    
   public void attributeChange(IPersistentObject target, IAttribute attrib) {
       if ( attrib.getName() == "ServiceCountryCode" && attrib.getDataAsInteger() == 458 ) { // Malaysia
           target.getAttribute("ServiceState").setReadOnly(true);
           target.getAttribute("ServiceCity").setReadOnly(true);
       } else if ( attrib.getName() == "BServiceCountryCode" && attrib.getDataAsInteger() == 458 ) { // Malaysia
           target.getAttribute("BServiceState").setReadOnly(true);
           target.getAttribute("BServiceCity").setReadOnly(true);
       }
   }

    public void newObject(IPersistentObject target) {
        System.out.println("In newObject()");
        target.getAttribute("ServicePhone").setRequired(true);
        return;
    }
    
    
    public void postUnmarshal(IPersistentObject target) {
        
        if (target.getObject("PrepaidServiceList", "Service") == null) {
            m_prepaidServiceList = new ReferenceElement("PrepaidServiceList", "Service", (Domain)target, true, false);
            m_prepaidServiceList.createNew();
            
            IPersistentCollection pColl = (IPersistentCollection)target.getObject("PrepaidServiceList","Service");
            if (pColl != null){
                pColl.addAssociation(target);
                pColl.setFaulted(false);
            }
        }
        
        if (target.getObject("PrepaidNotesList", "Service") == null) {
            m_prepaidNotesList = new ReferenceElement("PrepaidNotesList", "Service", (Domain)target, false, false);
            m_prepaidNotesList.createNew();
            
            IPersistentCollection pColl = (IPersistentCollection)target.getObject("PrepaidNotesList", "Service");
            if (pColl != null){
                pColl.addAssociation(target);
            }
        }
        
        if (target.getObject("FriendsAndFamilyList", "Service") == null) {
            m_FnFList = new ReferenceElement("FriendsAndFamilyList", "Service", (Domain)target, true, false);
            m_FnFList.createNew();
            
            IPersistentCollection pColl = (IPersistentCollection)target.getObject("FriendsAndFamilyList", "Service");
            if (pColl != null){
                pColl.addAssociation(target);
                pColl.setFaulted(false);
            }
        }

        if (target.getObject("FavouriteAreaList", "Service") == null) {
            m_FAList = new ReferenceElement("FavouriteAreaList", "Service", (Domain)target, true, false);
            m_FAList.createNew();
            
            IPersistentCollection pColl = (IPersistentCollection)target.getObject("FavouriteAreaList", "Service");
            if (pColl != null){
                pColl.addAssociation(target);
                pColl.setFaulted(false);
            }
        }

        //Ming Hon: Added
       if (target.getObject("PrepaidINList", "Service") == null) {
            PrepaidINList = new ReferenceElement("PrepaidINList", "Service", (Domain)target, true, false);
            PrepaidINList.createNew();
            
            IPersistentCollection pColl = (IPersistentCollection)target.getObject("com.maxis.businessobject.PrepaidINList", 
                    "Service");
            if (pColl != null){
                pColl.addAssociation(target);
                pColl.setFaulted(false);
                log.debug("ServiceExtendedLogic.postUnmarshal().PrepaidINList");
            }
        }        
        
        return;
        
    }
    
}
