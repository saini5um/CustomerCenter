/*
 * IJDBCManager.java
 *
 * Created on May 11, 2005, 12:00 AM
 */

package com.maxis.util;

import com.csgsystems.fx.security.remote.RemoteDBConnection;

/**
 *
 * @author  Unknown
 */
public interface ItuxJDBCManager {
    /*
     *Returns the current Connection to the JDBC - Database
     */
    RemoteDBConnection getCurrentConnection(String DAO);
    void setServerId(int i);
	int getServerId();
}
