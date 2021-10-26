/**
 *  Project Name: Maxis R&B
 *  Developer Name: Pankaj Saini
 *  Module Name: Customer Center
 *  Date Created: 20060215
 *  Description: for Data Only Rate Plans CR1037
 *  Date Modified: 20060215
 *  Version #: v01
 */

package com.maxis.util;

import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.arbor.order.OrderManager;
import com.csgsystems.domain.framework.attribute.IAttribute;
import com.csgsystems.domain.arbor.businessobject.Item;
import com.csgsystems.localization.ResourceManager;
import com.csgsystems.bopt.BoptFactory;
import com.csgsystems.bopt.MultiKeyParams;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;

/**
 *
 * @author  Pankaj Saini
 */
public class DataRatePlans {
    
//    public static final int dataRCElementId[] = {400110, 400111}; // Mobile - Telephony Barring, Mobile - Video Telephony Barring
    public static int dataRCElementId[] = null;
    public static boolean configFetched = false;
    
    static {
        MultiKeyParams mkp = BoptFactory.getSystemParameters();
        String dataRCCountParam  = (String) mkp.get("CC.DATA_RATE_PLAN_RC_COUNT");

        try {
            int dataRCCount = Integer.parseInt(dataRCCountParam);
            dataRCElementId = new int[dataRCCount];
            for ( int i = 0; i < dataRCCount; i++ ) 
                dataRCElementId[i] = Integer.parseInt((String) mkp.get("CC.DATA_RATE_PLAN_RC_" + ( i + 1 )));
            configFetched = true;
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }	    
    }
    
    public static String[] validateChange(IPersistentObject service) {
	if ( !configFetched ) {
            javax.swing.JOptionPane.showMessageDialog(null, 
                    "Data only profile elements not found in SYSTEM_PARAMETERS. No checking will be done.");
            return null;
    	}
            
        Vector errorMessages = new Vector();
        int dataRCFound = 0;
        
        IPersistentCollection componentList = service.getCollection("ComponentList", "Service");
        IPersistentCollection productAlaCarteList = service.getCollection("ProductList", "ServiceAlaCarte");
        
        if ( componentList != null ) {
            for ( int i = 0; i < componentList.getCount(); i++ ) {
                IPersistentObject component = componentList.getAt(i);
                IPersistentCollection componentElementList = component.getCollection("ComponentElementList", "Component");
                for ( int j = 0; j < componentElementList.getCount(); j++ ) {
                    IPersistentObject componentElement = componentElementList.getAt(j);
                    int elementType = componentElement.getAttributeDataAsInteger("VirtualElementType");
                    if ( elementType == 0 || elementType == 1 ) {
                        IPersistentObject product = componentElement.getObject("Product", "ComponentElement");
                        IPersistentObject pendingProduct = product.getObject("Product", "Pending");
                            if ( pendingProduct != null ) product = pendingProduct;

                        if ( product.getAttributeData("ProductInactiveDt") == null ) {
                            int elementId = product.getAttributeDataAsInteger("ElementId");
                            for ( int k = 0; k < dataRCElementId.length; k++ )
                                if ( elementId == dataRCElementId[k] ) dataRCFound++;

                        }
                    }
                }
            }
        }
        if ( productAlaCarteList != null ) {
            for ( int i = 0; i < productAlaCarteList.getCount(); i++ ) {
                IPersistentObject product = productAlaCarteList.getAt(i);
                IPersistentObject pendingProduct = product.getObject("Product", "Pending");
                if ( pendingProduct != null ) product = pendingProduct;

                if ( product.getAttributeData("ProductInactiveDt") == null ) {
                    int elementId = product.getAttributeDataAsInteger("ElementId");
                    for ( int j = 0; j < dataRCElementId.length; j++ )
                        if ( elementId == dataRCElementId[j] ) dataRCFound++;
                }
            }
        }
        
        IPersistentObject pOrder = OrderManager.getInstance().getCurrentOrder();
        IPersistentCollection colServiceOrder = pOrder.getCollection("ServiceOrderList", "Order");
        if ( colServiceOrder != null ) {
            for ( int i = 0; i < colServiceOrder.getCount(); i++ ) {
                IPersistentObject pServiceOrder = colServiceOrder.getAt(i);
                if ( pServiceOrder.getAttributeDataAsBoolean("IsServiceLevel") ) {
                    int dataExtIdDisconnected = 0;
                    int dataExtIdConnected = 0;

                    IPersistentCollection colItem = pServiceOrder.getCollection("ItemList", "ServiceOrder");
                    for ( int j = 0; j < colItem.size(); j++ ) {
                        IPersistentObject pItem = colItem.getAt(j);
                        int memberType = pItem.getAttributeDataAsInteger("MemberType");
                        int itemActionId = pItem.getAttributeDataAsInteger("ItemActionId");
                        int memberId = pItem.getAttributeDataAsInteger("MemberId");

                        if ( itemActionId == Item.ACTION_DISCONNECT && memberType == Item.TYPE_SERVICEID && memberId == Constant.MSISDN_EXT_ID ) {
                            IPersistentObject ciem = pItem.getObject("CustomerIdEquipMap", "Item");
                            if ( ciem != null ) {
                                String externalId = ciem.getAttributeDataAsString("ServiceExternalId");
                                if ( externalId.startsWith(Constant.DATA_NUMBER_PREFIX) ) dataExtIdDisconnected++;
                            }
                        }

                        if ( itemActionId == Item.ACTION_ADD && memberType == Item.TYPE_SERVICEID && memberId == Constant.MSISDN_EXT_ID ) {
                            IPersistentObject ciem = pItem.getObject("CustomerIdEquipMap", "Item");
                            if ( ciem != null ) {
                                String externalId = ciem.getAttributeDataAsString("ServiceExternalId");
                                if ( externalId.startsWith(Constant.DATA_NUMBER_PREFIX) ) dataExtIdConnected++;
                            }
                        }

                        if ( itemActionId == Item.ACTION_ADD && memberType == Item.TYPE_PRODUCT )
                            for ( int k = 0; k < dataRCElementId.length; k++ )
                                if ( memberId == dataRCElementId[k] )
                                    dataRCFound++;
                    }
                        
                    if ( dataRCFound > 0 && dataExtIdDisconnected > 0 && dataExtIdConnected == 0 )
                        errorMessages.add(ResourceManager.getString("DataRatePlans.NumberChange.Error"));
                }
            }
        }
        
        if ( errorMessages.size() > 0 ) {
            String rntstr[] = new String[errorMessages.size()];
            for ( int i = 0; i < errorMessages.size(); i++ )
                rntstr[i] = (String)errorMessages.get(i);
            return rntstr;
        }
        
        return null;        
    }
    
    public static String[] validate() {
	if ( !configFetched ) {
            javax.swing.JOptionPane.showMessageDialog(null, 
                    "Data only profile elements not found in SYSTEM_PARAMETERS. No checking will be done.");
            return null;
    	}
            
        Vector errorMessages = new Vector();
        
        IPersistentObject pOrder = OrderManager.getInstance().getCurrentOrder();
        IPersistentCollection colServiceOrder = pOrder.getCollection("ServiceOrderList", "Order");
        if ( colServiceOrder != null ) {
            for ( int i = 0; i < colServiceOrder.getCount(); i++ ) {
                IPersistentObject pServiceOrder = colServiceOrder.getAt(i);
                if ( pServiceOrder.getAttributeDataAsBoolean("IsServiceLevel") ) {
                    int dataRCFound = 0;
                    int dataExternalIdFound = 0;

                    IPersistentCollection colItem = pServiceOrder.getCollection("ItemList", "ServiceOrder");
                    for ( int j = 0; j < colItem.size(); j++ ) {
                        IPersistentObject pItem = colItem.getAt(j);
                        int memberType = pItem.getAttributeDataAsInteger("MemberType");
                        int itemActionId = pItem.getAttributeDataAsInteger("ItemActionId");
                        int memberId = pItem.getAttributeDataAsInteger("MemberId");

                        if ( itemActionId == Item.ACTION_ADD && memberType == Item.TYPE_SERVICEID && memberId == Constant.MSISDN_EXT_ID ) {
                            IPersistentObject ciem = pItem.getObject("CustomerIdEquipMap", "Item");
                            if ( ciem != null ) {
                                String externalId = ciem.getAttributeDataAsString("ServiceExternalId");
                                if ( externalId.startsWith(Constant.DATA_NUMBER_PREFIX) ) dataExternalIdFound++;
                            }
                        }

                        if ( itemActionId == Item.ACTION_ADD && memberType == Item.TYPE_PRODUCT )
                            for ( int k = 0; k < dataRCElementId.length; k++ )
                                if ( memberId == dataRCElementId[k] )
                                    dataRCFound++;
                    }
                        
                    if ( dataRCFound > 0 && dataExternalIdFound == 0 )
                        errorMessages.add(ResourceManager.getString("DataRatePlans.RequiredNumber.Error"));
                }
            }
        }
        
        if ( errorMessages.size() > 0 ) {
            String rntstr[] = new String[errorMessages.size()];
            for ( int i = 0; i < errorMessages.size(); i++ )
                rntstr[i] = (String)errorMessages.get(i);
            return rntstr;
        }
        
        return null;
    }    
}