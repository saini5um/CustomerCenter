/*
 * MXSOrderExtendedLogic.java
 *
 * Created on June 29, 2005, 5:31 PM
 * Date         By          PN#                      Resolution          
 * 20050826     kinyip      KenanFX–CC–General-89    To make the ContactCompany attribute shown mandatory.
 */

package com.maxis.xlogic;
import com.csgsystems.domain.framework.IPersistentObjectFactory;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.xlogic.ExtendedLogicBase;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.fxcommon.attribute.*;
import com.csgsystems.domain.framework.*;
import com.csgsystems.fx.security.util.FxException;
import com.csgsystems.fx.security.remote.RemoteDBConnection;
import java.util.*;
import com.maxis.util.*;
import com.csgsystems.fx.security.remote.SQL;
import java.sql.*;
import java.lang.*;
import com.csgsystems.fx.security.remote.RemoteDBPreparedStatement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 *
 * @author  maxis
 */
public class MXSOrderExtendedLogic extends ExtendedLogicBase
{
    //com.csgsystems.domain.framework.context.Context ctx = new com.csgsystems.domain.framework.context.Context();
    
    private static Log log;
    
    static {
        try {
            log = LogFactory.getLog(com.maxis.xlogic.MXSOrderExtendedLogic.class);
        }
        catch (Exception ex) {
        }
    }
    
    
    /** Creates a new instance of MXSOrderExtendedLogic */
    public MXSOrderExtendedLogic() 
    {
        super();
    }
    
    public void newObject(IPersistentObject target) 
    {
        IntegerEnumeratedAttribute  m_SalesChannelId = new IntegerEnumeratedAttribute("SalesChannelId", (Domain)target, "SalesChannel.SalesChannelId<-20000.IsInternal=0", "SalesChannelId", "DisplayValue", false);
        IntegerEnumeratedAttribute  m_ContactCompany = new IntegerEnumeratedAttribute("ContactCompany", (Domain)target, "SalesChannel.SalesChannelId>=-20000.IsInternal=0", "SalesChannelId", "DisplayValue", false);
        //m_ContactCompany.setRequired(true);
        IntegerEnumeratedAttribute  m_DisconnectReason = new IntegerEnumeratedAttribute("DisconnectReason", (Domain)target, "DiscReason.DisconnectReason<1000.IsInternal=0", "DisconnectReason", "DisplayValue", false);
        m_DisconnectReason.setNillable(true);
        m_DisconnectReason.setUsingDefault(false);
        
        //target.getAttribute("ContactCompany").setRequired(true);
        return;
    }
    
    public void postUnmarshal(IPersistentObject target) 
    {
        if(!(target.getAttribute("ContactCompany") instanceof IntegerEnumeratedAttribute)){
            String contactCompany = target.getAttribute("ContactCompany").getDataAsString();
            IntegerEnumeratedAttribute  m_ContactCompany = new IntegerEnumeratedAttribute("ContactCompany", (Domain)target, "SalesChannel.SalesChannelId>=-21000.IsInternal=0", "SalesChannelId", "DisplayValue", false);
            target.getAttribute("ContactCompany").setDataAsString(contactCompany);
            target.getAttribute("ContactCompany").setModified(false);
        }
        target.getAttribute("ContactCompany").setRequired(true);
        
        if(!(target.getAttribute("SalesChannelId") instanceof IntegerEnumeratedAttribute)){
            String salesChannel = target.getAttribute("SalesChannelId").getDataAsString();
            IntegerEnumeratedAttribute  m_SalesChannelId = new IntegerEnumeratedAttribute("SalesChannelId", (Domain)target, "SalesChannel.SalesChannelId<10000.IsInternal=0", "SalesChannelId", "DisplayValue", false);
            target.getAttribute("SalesChannelId").setDataAsString(salesChannel);
            target.getAttribute("SalesChannelId").setModified(false);
        }
        
        //if((target.getAttribute("DisconnectReason") instanceof IntegerEnumeratedAttribute)){
            int  DisconnectReason = target.getAttribute("DisconnectReason").getDataAsInteger();
            IntegerEnumeratedAttribute  m_DisconnectReason = new IntegerEnumeratedAttribute("DisconnectReason", (Domain)target, "DiscReason.DisconnectReason<1000.IsInternal=0", "DisconnectReason", "DisplayValue", false);
	        m_DisconnectReason.setNillable(true);
	        m_DisconnectReason.setUsingDefault(false);
	        target.getAttribute("DisconnectReason").setDataAsInteger(DisconnectReason);
	        target.getAttribute("DisconnectReason").setModified(false);
        //}
        
        target.setModified(false);
        return;
    }
    
}
