/*
 * AccountLocateExtendedLogic.java
 *
 * Created on March 22, 2006, 6:10 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.xlogic;

import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.attribute.StringAttribute;
import com.csgsystems.domain.framework.businessobject.Domain;
import com.csgsystems.xlogic.ExtendedLogicBase;

/**
 *
 * @author Pankaj Saini
 */
public class AccountLocateExtendedLogic extends ExtendedLogicBase {
    
    /** Creates a new instance of AccountLocateExtendedLogic */
    public AccountLocateExtendedLogic() {
    }
    
    public void postUnmarshal(IPersistentObject target) {
        StringAttribute CheckDigitAcctNum = new StringAttribute("CheckDigitAcctNum", (Domain)target, false);
        target.setAttributeDataAsString("CheckDigitAcctNum", 
                getCheckDigitAcctNum(target.getAttributeDataAsLong("AccountInternalId")));        
    }

    private String getCheckDigitAcctNum(long accountNo) {
        int sum = 0;
        String strAcctNum = new String(new Long(accountNo).toString());
        int numDigits = strAcctNum.length();
        int parity = numDigits % 2;
        int digit = 0;
        for ( int i = 0; i < numDigits; i++ ) {
            digit = Integer.parseInt(strAcctNum.substring(i, i + 1));
            if ( ((i + 1) % 2) == parity ) digit = digit * 2;
            if ( digit > 9 ) digit = digit - 9;
            sum = sum + digit;
        }
        int checkDigit = (((sum/10) + 1)*10 - sum) % 10;
        return strAcctNum + new Integer(checkDigit).toString();
    }
}
