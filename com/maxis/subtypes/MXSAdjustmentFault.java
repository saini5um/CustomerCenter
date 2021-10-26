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
import com.csgsystems.service.IServiceRequest;

/**
 *
 * @author  Ming Hon
 */
public class MXSAdjustmentFault extends OwnerlessCollectionFault {
  	public MXSAdjustmentFault() {
		super();
	}

	protected void getAdditionalCriteria(java.util.Map criteria) {
/*		QueryCriteria criteriaChgWho = new QueryCriteria();
		QueryCriteria criteriaChgDate = new QueryCriteria();
		criteriaChgWho.setUseFetch(true);
		criteriaChgWho.setFetch(true);
		criteriaChgDate.setUseFetch(true);
		criteria.put("ChgWho",criteriaChgWho);
//		criteria.put("ChgDate",criteriaChgDate);
*/		logDebug("MXSAdjustmentFault");
		System.out.println("MXSAdjustmentFault");
		
	}
	/*
	public IServiceRequest createServiceRequest(int i, Class class1, IDomainIdentifier idomainidentifier, Map map){
		System.out.println("createServiceRequest");
	    getAdditionalCriteria(map);
        return super.createServiceRequest(i, class1, idomainidentifier, map, true);
    }

    public IServiceRequest createServiceRequest(int i, Class class1, IDomainIdentifier idomainidentifier, Map map, boolean flag){
	    System.out.println("createServiceRequest2");
	    getAdditionalCriteria(map);
	    return super.createServiceRequest(i, class1, idomainidentifier, map, flag);
    }*/
}
