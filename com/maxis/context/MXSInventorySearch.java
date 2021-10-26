/*
 * MXSInventorySearch.java
 *
 * Created on November 23, 2005, 2:02 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import com.csgsystems.bp.context.InventorySearchContext;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.localization.ResourceManager;

import com.maxis.util.Constant;

import javax.swing.JOptionPane;

/**
 *
 * @author Pankaj Saini
 */
public class MXSInventorySearch extends InventorySearchContext {
    
    public boolean processShutdown(int shutdownType) {
        boolean success = super.processShutdown(shutdownType);
        if ( success == true ) {
            IPersistentCollection assignedInventory = getCollection("InvElementList", "Service");
            boolean simmSwapout = false;
            for ( int i = 0; i < assignedInventory.getCount(); i++ ) {
                IPersistentObject invElement = assignedInventory.getAt(i);
                if ( !invElement.getAttribute("EndDateTime").isEmpty() ) {
                    int invType = invElement.getAttributeDataAsInteger("InventoryTypeId");
                    if ( invType == Constant.SIMM_PRIMARY_INV_TYPE || invType == Constant.SIMM_PRIMARY_USIM_INV_TYPE )
                        simmSwapout = true;
                }
            }
            boolean simmSwapin = false;
            if ( simmSwapout == true ) {
                for ( int i = 0; i < assignedInventory.getCount(); i++ ) {
                    IPersistentObject invElement = assignedInventory.getAt(i);
                    if ( invElement.getAttribute("EndDateTime").isEmpty() ) {
                        int invType = invElement.getAttributeDataAsInteger("InventoryTypeId");
                        if ( invType == Constant.SIMM_PRIMARY_INV_TYPE || invType == Constant.SIMM_PRIMARY_USIM_INV_TYPE )
                            simmSwapin = true;
                    }
                }
            }
            if ( simmSwapout == true && simmSwapin == false ) {
                JOptionPane.showMessageDialog(null, ResourceManager.getString("SIMM.swapout.warning.text"), 
                        ResourceManager.getString("SIMM.swapout.warning.title"), JOptionPane.WARNING_MESSAGE);
            }
        }
        
        return success;
    }
}
