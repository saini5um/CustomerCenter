/*
 * ProductRateOverride.java
 *
 * Created on February 13, 2006, 10:40 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.xlogic;

import com.csgsystems.xlogic.ExtendedLogicBase;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.PersistentObjectFactory;

/**
 *
 * @author Pankaj Saini
 */
public class ProductRateOverride extends ExtendedLogicBase {
    
    /** Creates a new instance of ProductRateOverride */
    public ProductRateOverride() {
        super();
    }
    
    public void preMarshal(IPersistentObject target) {
        System.out.println("called product rate override premarshal");
        System.out.println("VERB = " + target.getVerb().toString());
        IPersistentObject note = PersistentObjectFactory.getFactory().create("Note", null);
//        note.flush();
        return;
    }
}
