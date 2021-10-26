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
public class MXSInvSearchExtendedLogic extends ExtendedLogicBase {
  
  /**
   * Required public no-argument constructor. See the
   * FXExtendedLogicFactory for details on instantiating
   * this object.
   */
  public MXSInvSearchExtendedLogic() {
    super();
    System.out.println("MXSInvSearchExtendedLogic Created");
  }
  
  public void newObject(IPersistentObject target) {
    System.out.println("In MXSInvSearchExtendedLogic.newObject()");
    //target.getAttribute("SalesChannelId").setRequired(true);
    //target.getAttribute("ExternalId").setRequired(true);
  }
  
  public void postUnmarshal(IPersistentObject target) {
    //valide when an account is sent back from DB to the sytem
    //target.getAttribute("SalesChannelId").setRequired(true);
    return;
  }
  
  public void preMarshal(IPersistentObject target) {
    return;
  }
  
  private void setButtonOFF(IPersistentObject target) {
    target.getAttribute("SalesChannelId").setEmpty(true);
  }
  
  
}
