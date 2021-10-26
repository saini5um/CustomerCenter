/*
 * AccountExtendedLogic.java
 *
 * Copyright (C) 2003 CSG Systems
 * www.csgsystem.com
 *
 * Created on Feb 11, 2004
 */
package com.maxis.xlogic;

import java.util.Map;
import java.util.Date;
import com.csgsystems.domain.framework.attribute.IAttribute;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.xlogic.ExtendedLogicBase;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.fxcommon.attribute.*;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.domain.framework.*;

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
public class AccountExtendedLogic extends ExtendedLogicBase {
   
   /**
    * Required public no-argument constructor. See the
    * FXExtendedLogicFactory for details on instantiating
    * this object.
    */
   public AccountExtendedLogic() {
      super();
   }
      
   public void newObject(IPersistentObject target) {
        //for MSA compliant
        com.maxis.util.tuxJDBCManager.getInstance().setServerId(-1);
        
	StringEnumeratedAttribute title = new StringEnumeratedAttribute("Title", (Domain)target, 
                "GuiIndicator.FieldName=title", "IntegerValue", "DisplayValue", false);
	IntegerEnumeratedAttribute writeOffStatus = new IntegerEnumeratedAttribute("WriteOffStatus", (Domain)target, 
                "GuiIndicator.FieldName=write_off_status", "IntegerValue", "DisplayValue", false);        
        target.getAttribute("BillLname").setMaxLength(40);
        target.getAttribute("BillAddress1").setMaxLength(40);
        target.getAttribute("BillAddress2").setMaxLength(40);
        target.getAttribute("BillAddress3").setMaxLength(40);
        target.getAttribute("CustAddress1").setMaxLength(40);
        target.getAttribute("CustAddress2").setMaxLength(40);
        target.getAttribute("CustAddress3").setMaxLength(40);
   }
   
   public void postUnmarshal(IPersistentObject target) {
        //for MSA compliant
        //log.debug("MXSAccountExtendedLogic postUnmarshal");
        String temp = target.getAttribute("AccountServer").toString();
        int as = (new Integer(temp)).intValue();
        com.maxis.util.tuxJDBCManager.getInstance().setServerId(as);
        
	IntegerEnumeratedAttribute writeOffStatus = new IntegerEnumeratedAttribute("WriteOffStatus", (Domain)target, 
                "GuiIndicator.FieldName=write_off_status", "IntegerValue", "DisplayValue", false);        
       target.setAttributeDataAsInteger("WriteOffStatus", target.getAttributeDataAsInteger("CollectionHistory"));
      return;
   }
   
   public void preMarshal(IPersistentObject target) {
      target.setAttributeDataAsInteger("BillTitle", target.getAttributeDataAsInteger("Title"));
      return;
   }
   
   public void attributeChange(IPersistentObject target, IAttribute attrib) {
       if ( attrib.getName() == "BillCountryCode" && attrib.getDataAsInteger() == 458 ) { // Malaysia
           target.getAttribute("BillState").setReadOnly(true);
           target.getAttribute("BillCity").setReadOnly(true);
       } else if ( attrib.getName() == "CustCountryCode" && attrib.getDataAsInteger() == 458 ) { // Malaysia
           target.getAttribute("CustState").setReadOnly(true);
           target.getAttribute("CustCity").setReadOnly(true);
       }
   }
  
}
