/**
 *  Project Name: Maxis R&B
 *  Developer Name: Ming Hon
 *  Module Name: Customer Center
 *  Date Created: 20051018
 *  Description: for Multi / Single profile
 *  Date Modified: 20051018
 *  Version #: v01
 *
 *
 */

package com.maxis.context;

import com.csgsystems.domain.framework.context.IContext;
import com.maxis.util.MultiSingleProfile;
import com.csgsystems.error.IError;
import com.csgsystems.error.ErrorFactory;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.arbor.order.*;
import com.maxis.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author  Ming Hon
 */
public class ServiceAddConfirm extends com.csgsystems.bp.context.ServiceAddContext{
    
    /** Creates a new instance of ServiceAddConfirm */
    public ServiceAddConfirm() {
        super();
    }

    public boolean processShutdown(int shutdownType) {
        super.resetError();
        if(!super.processShutdown(shutdownType)){
            return false;
        }
        else{
            if(shutdownType==OK){
/*                //call the validateMultiSingleProfileOrder
                String mesg[] = MultiSingleProfile.validateMultiSingleProfileOrder();
                //print all error message from validateMultiSingleProfileOrder if any
                if(mesg!=null){
                    if(mesg.length > 0){
                        for(int i=0; i<mesg.length; i++){
                            addError(ErrorFactory.createError(mesg[i],  null, com.maxis.context.ServiceAddConfirm.class));
                        }
                        return false;
                    }
                }
 */
                // Data only rate plan checks
                String dataChecks[] = DataRatePlans.validate();
                if ( dataChecks != null && dataChecks.length > 0 ) {
                    String errors = "";
                    for ( int i = 0; i < dataChecks.length; i++ )
                        errors = errors + dataChecks[i];
//                        addError(ErrorFactory.createError(dataChecks[i], null, getClass()));
                    JOptionPane.showMessageDialog(null, errors, "Alert!", JOptionPane.WARNING_MESSAGE);
                }
                // SIMM - MISM Checks
                IPersistentObject acct = getObject("Account");
                IPersistentObject order = OrderManager.getInstance().getCurrentOrder();
                IPersistentCollection serviceList = acct.getCollection("ServiceList", "Account");
                if ( serviceList == null ) return true;
                for ( int i = 0; i < serviceList.getCount(); i++ ) {
                    IPersistentObject service = serviceList.getAt(i);
                    if ( service.isNewObject() ) {
                        if ( SIMMUtil.wasSecondary(service) ) SIMMUtil.disconnectLink(order, service);

                        if ( SIMMUtil.isSecondary(service) && !SIMMUtil.hasLink(service) ) {
                            addError(ErrorFactory.createError("MX-1-1", 
                                    null, com.maxis.context.ServiceAddConfirm.class));
                            return false;
                        } else if ( SIMMUtil.isPrimary(service) || MISMUtil.isPrimary(service) ) SIMMUtil.changeLinks(order, service);
                        else if ( MISMUtil.isSecondary(service) && !SIMMUtil.hasLink(service) ) {
                            addError(ErrorFactory.createError("MX-2-1", 
                                    null, com.maxis.context.ServiceAddConfirm.class));
                            return false;                
                        }
                    }
                }
            }
        }
        return true;
    }
}
