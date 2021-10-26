/*
 * MXSServiceExternalId.java
 *
 * Created on November 23, 2005, 5:39 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import com.csgsystems.bp.context.ServiceExternalId;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.localization.ResourceManager;

import com.maxis.util.*;
import javax.swing.JOptionPane;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Pankaj Saini
 */
public class MXSServiceExternalId extends ServiceExternalId {
    
    public boolean addCustomerIdEquipMap(Map map) {
        return super.addCustomerIdEquipMap(map);
    }
    
    public boolean processShutdown(int shutdownType) {
        boolean success = false;
        if ( shutdownType == OK ) {
            boolean simm_mism = false;
            IPersistentCollection ciemList = getCollection("CustomerIdEquipMapList", "ServiceExternalIdAdd");
            for ( int i = 0; i < ciemList.getCount(); i++ ) {
                if ( ciemList.getAt(i).getAttributeDataAsInteger("ServiceExternalIdType") == Constant.SIMM_MISM_LINK ) {
                    simm_mism = true;
                    break;
                }
            }
            if ( simm_mism == true ) {
                IPersistentObject service = getObject("Service");
                if ( !SIMMUtil.isSecondary(service) ) {
                    JOptionPane.showMessageDialog(null, ResourceManager.getString("SIMM.link.warning.message"),
                            ResourceManager.getString("SIMM.link.warning.title"), JOptionPane.WARNING_MESSAGE);
                    return success;
                } else if ( !MISMUtil.isSecondary(service) ) {
                    JOptionPane.showMessageDialog(null, ResourceManager.getString("MISM.link.warning.message"),
                            ResourceManager.getString("MISM.link.warning.title"), JOptionPane.WARNING_MESSAGE);                    
                }
            }
        }
        
        return super.processShutdown(shutdownType);
    }
}
