/*
 * MXSAccountExtendedLogic.java
 *
 * Created on July 11, 2005, 2:46 PM
 */

package com.maxis.xlogic;
import com.csgsystems.xlogic.ExtendedLogicBase;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;

import java.text.DateFormat;
import java.util.Date;

/**
 *
 * @author  teoh
 */
public class MXSAccountIdExtendedLogic extends ExtendedLogicBase{
  
  /** Creates a new instance of MXSAccountExtendedLogic */
  public MXSAccountIdExtendedLogic() {
    super();
    //System.out.println("*** In MXSAccountIDExtendedLogic()");
  }
  
  //public void newObject(IPersistentObject target) {
  //}
  
  public void preMarshal(IPersistentObject target) {
      System.out.println("acct external id premarshal ... ");
      if ( target.isNewObject() && target.getAttributeDataAsInteger("AccountExternalIdType") == 9 ) { // New IC
          IPersistentObject acct = target.getObject("Account");
          if ( acct != null )
              setBirthDate(acct, target.getAttributeDataAsString("AccountExternalId"));
          else System.out.println("account not found!");
      }
  }
  
    private void setBirthDate(IPersistentObject acct, String icNumber) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        Date birthDay = null;
        try {
            birthDay = df.parse(icNumber.substring(4, 6) + "/" + 
                    icNumber.substring(2, 4) + "/" + icNumber.substring(0, 2));            
            acct.setAttributeDataAsDate("Birth Date_3", birthDay);
        } catch (Exception e) {
            System.out.println("parse err:" + e.getMessage());
        }
    }
    
  public void postUnmarshal(IPersistentObject target) {
    //System.out.println("*** AccountExternalIdType = "+target.getAttributeDataAsString("AccountExternalIdType"));
    if(target.getAttributeDataAsInteger("AccountExternalIdType")==1||
    target.getAttributeDataAsInteger("AccountExternalIdType")==2) {
      target.getAttribute("InactiveDate").setReadOnly(true);
    }
  }
  
}
