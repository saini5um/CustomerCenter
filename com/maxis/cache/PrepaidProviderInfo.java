/*
 * PrepaidProviderInfo.java
 *
 * Created on May 30, 2005, 5:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.cache;

import com.csgsystems.cache.Cacheable;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Pankaj Saini
 */
public class PrepaidProviderInfo implements Cacheable {
    protected Long m_emfConfigId;
    protected HashMap m_providerInfo;
    
    /** Creates a new instance of PrepaidProviderInfo */
    public PrepaidProviderInfo(long key, HashMap value) {
        m_emfConfigId = new Long(key);
        m_providerInfo = value;
    }

    public Object getKey() {
        return m_emfConfigId;
    }
    
    public Map getValue() {
        return m_providerInfo;
    }
    
    public boolean isStale() {
        return false;
    }
    
    //added by ming hon
    public String toString(){
	    return m_emfConfigId.toString()+"\n"+com.maxis.util.PrintMap.toMapString(m_providerInfo,2);
    }
}
