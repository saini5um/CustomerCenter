/*
 * MXSNCAFinalConf.java
 *
 * Created on December 19, 2005, 3:42 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import com.csgsystems.bp.context.NewCustomerAcquisitionFinalConf;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.arbor.order.OrderManager;
import com.csgsystems.error.ErrorFactory;

/**
 *
 * @author Pankaj Saini
 */
public class MXSNCAFinalConf extends NewCustomerAcquisitionFinalConf {
    
    public boolean processShutdown(int shutdownType) {
        if ( shutdownType == NEXT || shutdownType == OK ) {
            IPersistentObject order = OrderManager.getInstance().getCurrentOrder();
            if ( order != null && order.getAttribute("ContactCompany").isEmpty() ) {
                String[] errstr = new String[1];
                errstr[0]="Dealer/CSD is required, but no value has been set";
                error = ErrorFactory.createError("Order Error", errstr, getClass());
                return false;
            }
            
            IPersistentObject acct = getObject("Account", null);
            if ( acct == null ) logError("could not find account!", null);
            else {
                int accountCat = acct.getAttributeDataAsInteger("AccountCategory");
                int serverCat = 2; // Postpaid Consumer Server
                if ( accountCat == 1 ) serverCat = 3; // Consumer --> Postpaid Corporate Server
                acct.setAttributeDataAsInteger("ServerCategory", serverCat);
                logDebug("Setting server category " + serverCat + " as account category is " + accountCat);
            }
        }
        
        System.out.println("order is valid!");
        return super.processShutdown(shutdownType);
    }
    
}
