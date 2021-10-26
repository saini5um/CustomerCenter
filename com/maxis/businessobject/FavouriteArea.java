/*
 * FavouriteArea.java
 *
 * Created on July 6, 2005, 5:20 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.businessobject;

import com.csgsystems.domain.framework.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.attribute.*;

/**
 *
 * @author Pankaj Saini
 */
public class FavouriteArea extends Domain {
    
    protected IntegerEnumeratedAttribute area = null;
    protected StringAttribute paramName = null;

    /** Creates a new instance of FriendsAndFamily */
    public FavouriteArea() {
        paramName = new StringAttribute("Description", this, false);
        paramName.setReadOnly(true);
        
        area = new IntegerEnumeratedAttribute("Area", this, 
                "GenericEnumeration.EnumerationKey=maxis_fa_country", "Value", "DisplayValue", false);
    }
    
}
