/*
 * MXSServiceTransferAcctLoc.java
 *
 * Created on January 5, 2006, 4:29 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import com.csgsystems.bp.context.ServiceTransferAccountLocateContext;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.error.ErrorFactory;

import com.maxis.util.*;

/**
 *
 * @author Pankaj Saini
 */
public class MXSServiceTransferAcctLoc extends ServiceTransferAccountLocateContext {
    
    public boolean processShutdown(int shutdownType) {
        if (shutdownType == OK) {
            IPersistentObject account = getObject("Account", "AccountLocate");
            if ( DomainUtil.isAccountWrittenOff(account) ) { // Written off
                addError(ErrorFactory.createError("Cannot transfer this Service as parent account is written-off", 
                        null, MXSServiceTransferAcctLoc.class));
                return false;
            } else if ( DomainUtil.isAccountInFraud(account) ) { // Fraud
                addError(ErrorFactory.createError("Cannot transfer this Service as parent account is being investigated for fraud", 
                        null, MXSServiceTransferAcctLoc.class));
                return false;
            }
            IPersistentObject service = getObject("Service");
            if ( service == null ) System.out.println("service not found!!!!");
            int emfConfigId = service.getAttributeDataAsInteger("EmfConfigId");
            if ( emfConfigId > Constant.SERVICE_TYPE_PREPAID_MOBILE_LOW && emfConfigId < Constant.SERVICE_TYPE_PREPAID_MOBILE_HIGH ) {
                if ( account.getAttributeDataAsInteger("MktCode") != 31 ) {
                    addError(ErrorFactory.createError("Cannot transfer this Prepaid Service as market code is not prepaid", 
                            null, MXSServiceTransferAcctLoc.class));
                    return false;
                }
            }
        }
        
        return super.processShutdown(shutdownType);
    }
    
}
