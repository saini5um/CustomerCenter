/*
 * PackageDef.java
 *
 * Created on October 21, 2005, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.xlogic;

import com.csgsystems.domain.framework.attribute.StringAttribute;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.xlogic.ExtendedLogicBase;
import com.csgsystems.domain.framework.businessobject.Domain;
import com.csgsystems.domain.framework.businessobject.IRelationship;
import java.util.Iterator;

/**
 *
 * @author Pankaj Saini
 */
public class PackageDef extends ExtendedLogicBase {
    
    /** Creates a new instance of PackageDef */
    public PackageDef() {
        super();
    }
    
    public void newObject(IPersistentObject target) {
	StringAttribute packageDisplay = new StringAttribute("PackageDisplay", (Domain)target, false);        
    }
    
    public void postUnmarshal(IPersistentObject target) {
        System.out.println("object = " + target.getObjectDescription());
        System.out.println("subtype = " + target.getSubtype());
        Iterator it = ((Domain)target).getRelationships().iterator();
        while ( it.hasNext() ) {
            System.out.println("new relationship...");
            IRelationship re = (IRelationship) it.next();
            System.out.println("subtype = " + re.getSubtype());
            System.out.println("related object = " + re.getRelatedClass().getName());
            System.out.println("Has a = " + re.isHasa());
            System.out.println("is modified = " + re.isModified());
            System.out.println("is required = " + re.isRequired());
        }
//        PackageComponentDefPackageDefinitionFind, ProductCatalog;
//        PackageComponentDefList.PackageDefinition        
        IPersistentObject packageDef = 
                target.getObject("com.csgsystems.domain.arbor.businessobject.PackageDefinition", 
                "PackageComponentDef");
        if ( packageDef != null ) {
            target.setAttributeDataAsString("PackageDisplay", packageDef.getAttribute("PackageId").getFormattedData());
        } else System.out.println("Package def not found!");
    }

}
