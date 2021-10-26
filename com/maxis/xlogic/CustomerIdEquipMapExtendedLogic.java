/*
 * CustomerIdEquipMapExtendedLogic.java
 *
 * Created on November 23, 2005, 12:20 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.xlogic;

import com.csgsystems.xlogic.ExtendedLogicBase;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.attribute.IAttribute;
import com.csgsystems.fx.security.remote.RemoteDBConnection;
import com.csgsystems.fx.security.remote.SQL;
import com.csgsystems.fx.security.util.FxException;
import java.util.*;
import java.sql.*;
import com.maxis.util.tuxJDBCManager;


/**
 *
 * @author Pankaj Saini
 */
public class CustomerIdEquipMapExtendedLogic extends ExtendedLogicBase {
    
    /** Creates a new instance of CustomerIdEquipMapExtendedLogic */
    public CustomerIdEquipMapExtendedLogic() {
        super();
    }
    
/*    public void newObject(IPersistentObject target) {
        if ( target.getAttribute("InactiveDate").isEmpty() && 
                target.getAttributeDataAsInteger("ServiceExternalIdType") == 15) {
           target.setAttributeDataAsString("ServiceExternalId", getNextModemId());
           target.getAttribute("ServiceExternalId").setReadOnly(true);            
        }
    }
*/    
    public void attributeChange(IPersistentObject target, IAttribute attrib) {
        System.out.println("called attribute change for " + attrib.getName());
       if ( target.isNewObject() == true && target.getAttributeDataAsBoolean("IsForDisconnect") == false ) {
           if ( attrib.getName() == "ServiceExternalIdType" && attrib.getDataAsInteger() == 15 ) { // Modem Id
               target.setAttributeDataAsString("ServiceExternalId", getNextModemId());
               target.getAttribute("ServiceExternalId").setReadOnly(true);
           }
       }
    }
    
    private String getNextModemId() {
        String modemId = null;
        RemoteDBConnection conn = null;
        PreparedStatement cstat = null;
        ResultSet rs = null;
        try {
            conn = tuxJDBCManager.getInstance().getCurrentConnection("CAT");
            String strQuery ="SELECT MXS_MODEM_ID_SEQ.NEXTVAL FROM DUAL";
            cstat = conn.prepareStatement( new SQL( strQuery, "select") );
            rs = cstat.executeQuery();
            while ( rs.next() ) modemId = new Long(rs.getLong(1)).toString();
        
            rs.close();
            cstat.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
        } catch (FxException fx) {
            fx.printStackTrace();
        } 
        
        return modemId;
    }
}
