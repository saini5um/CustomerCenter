/*
 * faultClass.java
 *
 * Created on June 30, 2005, 12:12 PM
 */

package com.maxis.subtypes;

import java.util.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.subtypes.*;
import com.csgsystems.domain.framework.criteria.*;
//import com.csgsystems.domain.arbor.subtypes.*;
import com.csgsystems.domain.framework.attribute.*;
/**
 *
 * @author  teoh
 */
public class MaxisPackageDefFault extends OwnerlessCollectionFault{
  
  /** Creates a new instance of faultClass */
  public MaxisPackageDefFault() {
      System.out.println("new sorting instantiated...");
  }
  
  protected void getAdditionalCriteria(java.util.Map criteria) {
    Date dateNow = Calendar.getInstance().getTime();
    QueryCriteria criteriaShortDisplay = new QueryCriteria();
    criteriaShortDisplay.setSortCriteria(new SortCriteria((short)0, true));
    criteria.put("ShortDisplay", criteriaShortDisplay);
  }
}
