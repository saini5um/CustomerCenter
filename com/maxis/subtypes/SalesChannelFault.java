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
public class SalesChannelFault extends OwnerlessCollectionFault{
  
  /** Creates a new instance of faultClass */
  public SalesChannelFault() {
    System.out.println("*** SalesChannelFault init");
  }
  
  protected void getAdditionalCriteria(java.util.Map criteria) {
    Date dateNow = Calendar.getInstance().getTime();
    QueryCriteria criteriaInactiveDate = new QueryCriteria();
    QueryCriteria criteriaActiveDate = new QueryCriteria();
    criteriaInactiveDate.setFilterCriteria(FilterCriteria.createGreaterThan(dateNow));
    criteriaActiveDate.setFilterCriteria(FilterCriteria.createLessThan(dateNow));
    criteria.put("DateInactive", criteriaInactiveDate);
    criteria.put("DateActive", criteriaActiveDate);    
  }
}
