/*
 * InternalIcSearch.java
 *
 * Created on October 6, 2005, 1:59 PM
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
import com.csgsystems.domain.framework.IPersistentObjectFactory;
import com.csgsystems.domain.framework.attribute.*;
import java.util.ArrayList;

/**
 * Context class for searching by Account level External Id Type
 * @author Pankaj Saini
 */
public class InternalIcSearch extends Context {

//    protected IPersistentObject pGenericObject = null;
    protected IntegerEnumeratedAttribute m_Type = null;
    protected StringAttribute m_Value = null;
    
    /** Creates a new instance of InternalIcSearch */
    public InternalIcSearch() {
    }
    
    public boolean open(IContext context) {
        boolean success = false;

        IPersistentObject pGenericObject = context.getObject("GenericDomain", null);
        if ( pGenericObject == null ) {
            pGenericObject = PersistentObjectFactory.getFactory().createNew("GenericDomain",null);
        
            if ( pGenericObject == null ) {
                return success;
            }

            m_Type = new IntegerEnumeratedAttribute("Type", (Domain)pGenericObject,
                    "GuiIndicator.FieldName=maxis_ic_check", "IntegerValue", "DisplayValue", true);
            m_Value = new StringAttribute("Value",  (Domain)pGenericObject, true);
        }
        
        addTopic(pGenericObject, true); // read only
//        addTopic(pGenericObject);
        
        success = true;
        
        return success;
    }

    public boolean open(IPersistentObject object) {
        boolean success = true;
        
        return success;
    }
    
    public boolean processShutdown(int shutdownType) {
        boolean success = true;
        
        if (shutdownType == NEXT) {
            IPersistentObject pGenericObject = getObject("GenericDomain", null);
            if (!pGenericObject.valid()) {
/*                String externalId = pGenericObject.getAttributeDataAsString("Value");
                if ( externalId == null ) System.out.println("found null external id string");
                else System.out.println("external id = " + externalId);
*/                setError(pGenericObject.getError());

                success = false;
            } else {
                int externalIdType = pGenericObject.getAttributeDataAsInteger("Type");
                String externalId = pGenericObject.getAttributeDataAsString("Value");
                if ( externalId == null ) System.out.println("found null external id string");
                else System.out.println("external id = " + externalId);
                if ( externalIdType == 9 ) { // NEW IC
                    ArrayList errors = com.maxis.util.NRICValidation.newNricValidation(externalId);
                    if( errors.size() > 0 ) {
                        setError("NRIC Validation Error", errors.toArray());
                        success = false;
                    }
                } else if ( externalIdType == 10 ) { // OLD IC / OTHER IC
                    if( !com.maxis.util.NRICValidation.oldNricValidation(externalId) ) {
                        String[] err = new String[1];
                        err[0] = "No space allowed between characters";
                        setError("Old IC Validation Error", err);
                        success = false;
                    }
                }
            }
        }
        
        return success;
    }
    
}
