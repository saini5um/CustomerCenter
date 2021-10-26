/**
 *  Project Name: Maxis R&B
 *  Developer Name: Ming Hon
 *  Module Name: Customer Center 
 *  Date Created: 20050714
 *  Description:
 *  Date Modified: 20050714
 *  Version #: v01
 *
 *
 * Date         By          PN#                                       Resolution
 * 20050920     minghon     200511/Kenan FX - Customer Center-2       1) Restructure the whole class
 * 
 *    
 *
 */


package com.maxis.subtypes;
import java.util.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.subtypes.*;
import com.csgsystems.domain.framework.criteria.*;
import com.csgsystems.domain.arbor.subtypes.*;
import com.csgsystems.domain.arbor.businessobject.*;
import com.csgsystems.fx.security.rules.*;
import com.csgsystems.domain.framework.security.SecurityManager;

/**
 *
 * @author  teoh
 */
public class MXSAccountLocateListFault extends com.csgsystems.domain.arbor.subtypes.AccountLocateFault{

	/** Creates a new instance of MXSAccountLocateListFault */
	public MXSAccountLocateListFault() {
		super();
	}
	
	protected void getAdditionalCriteria(java.util.Map criteria) {
		super.getAdditionalCriteria(criteria);
		//20050921 WMH  200511/Kenan FX - Customer Center-2 		
		QueryCriteria criteriaAscending = new QueryCriteria();
		criteriaAscending.setSortCriteria(new SortCriteria((short)0,  false));
		criteria.put("AccountInternalId", criteriaAscending);
	}
}
