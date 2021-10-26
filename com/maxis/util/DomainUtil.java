/*
 * DomainUtil.java
 *
 * Created on 24 November 2005, 18:51
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.util;

import com.csgsystems.domain.framework.businessobject.*;

/**
 *
 * @author shoa01
 */
public class DomainUtil {
    
    /** Creates a new instance of DomainUtil */
    public DomainUtil() {
    }
    
    public static boolean isAccountWrittenOff(IPersistentObject accountObject){
        return accountObject.getAttributeDataAsInteger("CollectionHistory")!=0?true:false;
    }
    
    public static boolean isAccountInFraud(IPersistentObject accountObject){
        return accountObject.getAttributeDataAsInteger("CollectionStatus")==3?true:false;
    }
}
