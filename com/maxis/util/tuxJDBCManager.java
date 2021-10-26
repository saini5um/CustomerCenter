/*
 * theJDBCManager.java
 *
 * Created on May 11, 2004, 11:39 PM
 */

package com.maxis.util;

//
import com.csgsystems.fx.security.entity.NamedResource;
import com.csgsystems.fx.security.remote.RemoteDBConnection;
import com.csgsystems.fx.security.util.FxException;
import com.csgsystems.fx.security.util.AuthenticationException;
import com.csgsystems.domain.framework.security.SecurityManager;
import com.csgsystems.bopt.*;
/**
 *
 * @author  Unknown
 */
public class tuxJDBCManager implements ItuxJDBCManager {
    
    private static com.csgsystems.fx.security.SecurityManager sm = null;
    private static RemoteDBConnection dbConn = null;
    private static NamedResource resource    = null;
    private static ItuxJDBCManager jdbcManager = null;
    private static int m_serverId = -1;

    /** Creates a new instance of theJDBCManager */
    protected tuxJDBCManager() {
        System.out.println("Instance of tuxJDBCManager Created");
    }
    
    public static ItuxJDBCManager getInstance() {
        if (jdbcManager == null) {
            resource = new NamedResource("CustomData", "CustomTables");
            //
            try {
                sm = com.csgsystems.fx.security.SecurityManagerFactory.createSecurityManager();
            } catch (FxException e) {
                e.printStackTrace();
            }
            //
            jdbcManager = new tuxJDBCManager();
        }
        return jdbcManager;
    }
    
    public RemoteDBConnection getCurrentConnection(String DAO) {
        if (m_serverId == -1) {
            BoptSet bopt = null;
            int nRowIndex = -1;
            try {
                bopt = BoptFactory.getBoptSet("Server");
            } 
            catch (BoptException ex) {
                ex.printStackTrace();
            }
            nRowIndex = bopt.getRowIndex("ServerType", "3");
            String temp = bopt.getDataValue(nRowIndex, "ServerId");
            int as = new Integer(temp).intValue();
            setServerId(as);
        }
        
        String daoName = "BPCustomerData" + m_serverId; 
        if (DAO.equals("CAT")) {
            daoName = "BPCatalogData";
        } 
        if (DAO.equals("ADMIN")) {
            daoName = "BPAdminData";
        }
        try {
            dbConn = sm.getRemoteObjectConnection(resource, daoName); 
        } catch (com.csgsystems.fx.security.util.AuthenticationException ex) {
            ex.printStackTrace();
            dbConn = null; // Hope Sombody Catches this
        } catch (FxException fex) {
            fex.printStackTrace();
        }
        return dbConn;
    }
    
    public void setServerId(int i)
    {
        m_serverId = i;
    }

	public int getServerId()
	{
		return m_serverId;
	}
}
