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

package com.maxis.util;

import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.arbor.order.OrderManager;
import com.csgsystems.domain.framework.attribute.IAttribute;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;

/**
 *
 * @author  Ming Hon
 */
public class MultiSingleProfile {
    
    public static final int FaxRCElementId[] = {400027,400182}; //Mobile - Fax Service, Mobile - Fax Service
    public static final int FaxInvTypeId[] = {104,602};   //Prepaid - Fax Number, Postpaid - Fax Number
    public static final int DataRCElementId[] = {400026,400095,400181};  //Mobile - Data Service,Mobile - Corp Unlimited Mobile Data,Mobile - Data Service
    public static final int DataInvTypeId[] = {103,601};   //Prepaid - Data Number, Postpaid - Data Number
    
    public static String[] validateMultiSingleProfileOrder(){
        //create a container to store the error messages
        Vector errorMessages = new Vector();
        
        //get current order
        //-----------------
        IPersistentObject pOrder = OrderManager.getInstance().getCurrentOrder();
        
        //get the ServiceOrder list
        //-------------------------
        IPersistentCollection colServiceOrder = pOrder.getCollection("ServiceOrderList","Order");
        if(colServiceOrder!=null){
            //process each serviceOrder object, normally its one entry, but can have more
            //---------------------------------------------------------------------------
            for(int i=0; i<colServiceOrder.getCount(); i++){
                IPersistentObject pServiceOrder = colServiceOrder.getAt(i);
                //only process if its a service level service order
                //true means service level
                if(pServiceOrder.getAttributeDataAsBoolean("IsServiceLevel")){
                    //check the serviceorder type
                    //errorMessages.add("this is service level stuff");
                    
                    //make sure its a prepaid or postpaid service
                    //-------------------------------------------
                    //find the emf config id
                    int AccountInternalId = pServiceOrder.getAttributeDataAsInteger("AccountInternalId");
                    int ServiceInternalId = pServiceOrder.getAttributeDataAsInteger("ServiceInternalId");
                    int ServiceInternalIdResets = pServiceOrder.getAttributeDataAsInteger("ServiceInternalIdResets");
                    
                    //get the account object
                    IPersistentObject pAccount = pOrder.getObject("Account","Order");
                    IPersistentCollection colService = pAccount.getCollection("ServiceList","Account");
                    boolean servicefound = false;
                    int EmfConfigId = 0;
                    IPersistentObject pCurrentService = null;
                    for(int j=0; j<colService.size(); j++){
                        IPersistentObject pService = colService.getAt(j);
                        if(pService.getAttributeDataAsInteger("ParentAccountInternalId")==AccountInternalId
                        && pService.getAttributeDataAsInteger("ServiceInternalId")==ServiceInternalId
                        && pService.getAttributeDataAsInteger("ServiceInternalIdResets")==ServiceInternalIdResets
                        ){
                            EmfConfigId = pService.getAttributeDataAsInteger("EmfConfigId");
                            servicefound = true;
                            pCurrentService = pService;
                        }
                    }
                    
                    if(!servicefound){
                        errorMessages.add("Service not found, you should never ever see this message.");
                    }
                    //not (isprepaid or ispostpaid)
                    else if (!((EmfConfigId>=1001 && EmfConfigId<2000) || (EmfConfigId>=4001 && EmfConfigId<6000))){
                        //do nothing: nothing should be done if its not a prepaid or postpaid service
                    }
                    else{
                        //bool to store the state of item to be check
                        //-------------------------------------------
                        int connectDataInvFound = 0;
                        int connectDataRcFound = 0;
                        int connectFaxInvFound = 0;
                        int connectFaxRcFound = 0;
                        
                        //loop through every order item
                        //-----------------------------
                        IPersistentCollection colItem = pServiceOrder.getCollection("ItemList", "ServiceOrder");
                        for(int j=0; j<colItem.size(); j++){
                            IPersistentObject pItem = colItem.getAt(j);
                            int membertype = pItem.getAttributeDataAsInteger("MemberType");
                            int itemactionid = pItem.getAttributeDataAsInteger("ItemActionId");
                            int memberid = pItem.getAttributeDataAsInteger("MemberId");
                            
                            //check for existent of inv
                            //(Add && Inventory)
                            if(itemactionid==10&&membertype==70){
                                //look for data number
                                for(int k=0; k<DataInvTypeId.length; k++){
                                    if(memberid==DataInvTypeId[k]){
                                        connectDataInvFound++;
                                    }
                                }
                                //look for fax number
                                for(int k=0; k<FaxInvTypeId.length; k++){
                                    if(memberid==FaxInvTypeId[k]){
                                        connectFaxInvFound++;
                                    }
                                }
                            }
                            
                            //check for existent of rc
                            //(Add && Product)
                            if(itemactionid==10&&membertype==10){
                                //look for data rc
                                for(int k=0; k<DataRCElementId.length; k++){
                                    if(memberid==DataRCElementId[k]){
                                        connectDataRcFound++;
                                    }
                                }
                                //look for fax rc
                                for(int k=0; k<FaxRCElementId.length; k++){
                                    if(memberid==FaxRCElementId[k]){
                                        connectFaxRcFound++;
                                    }
                                }
                            }
                        }
                        
                        //do the checking based on HLD Multi / Single profile Validation rules
                        //--------------------------------------------------------------------
                        
                        //HLD validation 1
                        if(connectFaxInvFound>0 && connectFaxRcFound==0){
                            errorMessages.add("Please assign Fax VAS to the subscriber before finishing the wizard");
                        }
                        //HLD validation 2
                        if(connectFaxInvFound==0 && connectFaxRcFound>0){
                            errorMessages.add("Please assign Fax number in the inventory screen before finishing the wizard");
                        }
                        //HLD validation 3
                        if(connectFaxInvFound>0 && connectFaxRcFound>0 && connectDataRcFound>0 && connectDataInvFound==0){
                            errorMessages.add("Please assign a Data number in the inventory screen before finishing the wizard");
                        }
                        //HLD validation 4
                        if(connectDataInvFound>0 && connectDataRcFound==0){
                            errorMessages.add("Please assign Data VAS to the subscriber before finishing the wizard");
                        }
                        
                        //perform profile changes if necessary
                        //------------------------------------
                        //do the profile changes if any of the items in view has been added
                        if((connectDataInvFound+connectDataRcFound+connectFaxInvFound+connectFaxRcFound)>0){
                            //the IxcProviderId field in service object is used to deterimine if the service
                            //is single profile or multi profile
                            //value = 0 = single profile
                            //value = 1 = multi profile
                            
                            //initialize to 0 default single
                            int IxcProviderId = 0;
                            
                            //Detected Data RC with separate number => Multi
                            if(connectDataRcFound>0 && connectDataInvFound>0){
                                IxcProviderId = 1;
                            }
                            //Detected Fax (always with number) => Multi
                            if(connectFaxRcFound>0){
                                IxcProviderId = 1;
                            }
                            
                            //Update the service object
                            //-------------------------
                            //Update if its new service
                            //If IsServiceLevel==true(check far above) && ServiceOrderTypeId==10 => its a new sevice
                            if(pServiceOrder.getAttributeDataAsInteger("ServiceOrderTypeId")==10){
                                pCurrentService.setAttributeDataAsInteger("IxcProviderId", IxcProviderId);
                            }
                            //If IsServiceLevel==true(check far above) && ServiceOrderTypeId==20 => its a sevice change, add/remove vas,inventory, etc
                            else if(pServiceOrder.getAttributeDataAsInteger("ServiceOrderTypeId")==20){
                                //get the pending service object
                                IPersistentObject pPendingService = pCurrentService.getObject("Service", "Pending");
                                //if there is no pending service, create one
                                if(pPendingService==null){
                                    pCurrentService.sendMessage("CreatePendingView", new HashMap());
                                    pPendingService = pCurrentService.getObject("Service", "Pending");
                                }
                                pPendingService.setAttributeDataAsInteger("IxcProviderId", IxcProviderId);
                                
                            }
                            //else no requirement to do other checking
                        }
                    }
                }
            }
        }
        
        //convert the vector to array
        if(errorMessages.size()>0){
            String rntstr[] = new String[errorMessages.size()];
            for(int i=0; i<errorMessages.size(); i++){
                rntstr[i] = (String)errorMessages.get(i);
            }
            return rntstr;
        }
        return null;
    }
    
}




//Notes section
//============

//Sql to find the related package/component to test in CC
/*
select package_components.package_id, package_components.component_id, package_definition_values.display_value, component_definition_values.display_value
from package_component_members, package_components, package_definition_values, component_definition_values
where member_type = 1 and member_id in (400026,400095,400181,400027,400182)
and package_component_members.component_id = package_components.component_id
and package_components.package_id = package_definition_values.package_id
and package_components.component_id = component_definition_values.component_id
 */

