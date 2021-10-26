/*
 * PrepaidServiceList.java
 *
 * Created on May 19, 2005, 7:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.businessobject;

import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.fxcommon.attribute.*;
import com.csgsystems.domain.arbor.businessobject.Account;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.arbor.businessobject.Service;
import com.csgsystems.fx.security.remote.RemoteDBConnection;
import com.csgsystems.fx.security.remote.SQL;
import com.csgsystems.fx.security.util.FxException;
import com.maxis.util.tuxJDBCManager;
import com.csgsystems.cache.CacheManager;
import com.csgsystems.cache.Cache;
import com.maxis.cache.PrepaidProviderInfo;
import com.csgsystems.xlogic.IExtendedLogicFactory;
import com.csgsystems.util.service.ServiceFinder;

import com.maxis.util.ErrorReporting;
import java.sql.*;
import com.maxis.xlogic.*;

/**
 *
 * @author Pankaj Saini
 */
public class PrepaidServiceList extends DomainCollection {
    
    protected int subscr_no = 0;
    protected int subscr_no_resets = 0;
    private Cache prepaidProviderInfoCache = null;
    private ErrorReporting err = ErrorReporting.getInstance();
    
    /** Creates a new instance of PrepaidServiceList */
    public PrepaidServiceList() {
        super(PrepaidService.class);
    }
    
    protected boolean localAddAssociation(IPersistentObject relObject) {
        boolean success = false;
        
//        if (relObject instanceof Service) {
            subscr_no = relObject.getAttributeDataAsInteger("ServiceInternalId");
            subscr_no_resets = relObject.getAttributeDataAsInteger("ServiceInternalIdResets");
            success = true;
//        } else {
//            success = super.localAddAssociation(relObject);
//        }
        
        return success;
    }
    
    protected int listPageFault(int nStart) {
        // First get data from IN.
        // If IN connection is down fetch from DB
        // If service status is expired fetch from DB. Values from DB override.
        // 
        //Ming Hon: Added, Column name is different
        //Table is own by TP, CC have to follow
        String SQL = "SELECT LAST_MODIFY_DATE, FIRST_CALL_DATE, ACTIVE_DATE, DEACTIVATION_DATE, ";
        SQL += "DEACTIVATED_AT, FORFEIT_DATE, FORFEITED_AT, EXPIRY_DATE, EXPIRED_AT, ";
        SQL += "NUM_VOUCHER_BAR, VOUCHER_BAR, CALL_BAR, BALANCE1, BALANCE2, BALANCE3, ";
        SQL += "IP_LANGUAGE, STATUS, INSTALL_DATE FROM MXS_SERVICE_EXT ";
        SQL += "WHERE SUBSCR_NO = ? ";
        SQL += "AND SUBSCR_NO_RESETS = ?";
        
		//Ming Hon: Removed
        /*String SQL = "SELECT LAST_UPDATE_DATE, FIRST_CALL_DATE, ACTIVATION_DATE, DEACTIVATION_DATE, ";
        SQL += "DEACTIVATED_AT, BALANCE_FORFEITED_DATE, BALANCE_FORFEITED_AT, EXPIRATION_DATE, EXPIRED_AT_DATE, ";
        SQL += "NO_VOUCHER_BAR, VOUCHER_BAR_STATUS, CALL_BAR_STATUS, BALANCE_1, BALANCE_2, BALANCE_3, ";
        SQL += "IP_LANGUAGE, SUBSCRIBER_STATUS FROM MXS_SERVICE_EXT ";
        SQL += "WHERE SUBSCR_NO = ? ";
        SQL += "AND SUBSCR_NO_RESETS = ?";*/
                
        RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CUST");
        if (dbConn == null) {
            err.logErrorToFile("Error getting current Oracle Connection.", getClass());
           return 0; // prevent windows from shutting down
        }

        PreparedStatement pstmt = null;
        try {
            pstmt = dbConn.prepareStatement (new SQL(SQL, "select"));
            pstmt.setMaxRows(10);
            pstmt.setInt(1, subscr_no);
            pstmt.setInt(2, subscr_no_resets);
        } catch (FxException fx) {
            fx.printStackTrace();
            err.logErrorToFile("SQL Exception "+fx.getMessage(), PrepaidServiceList.class);
            return 0;
        } catch (SQLException sEx) {
            sEx.printStackTrace();
            err.logErrorToFile("SQL Exception "+sEx.getMessage(), PrepaidServiceList.class);
            return 0;
        }
        
        try {
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()) {
                IPersistentObject prepaidService = (IPersistentObject)PersistentObjectFactory.getFactory().createNew(PrepaidService.class, null);
                if (prepaidService == null) {
                    err.logErrorToFile("Couldn't create PrepaidService.", PrepaidServiceList.class);
                } else {
                    prepaidService.getAttribute("LastUpdateDate").setDataAsDate(rset.getDate(1)); 
                    prepaidService.getAttribute("FirstCallDate").setDataAsDate(rset.getDate(2));
                    prepaidService.getAttribute("ActivationDate").setDataAsDate(rset.getDate(3)); 
                    prepaidService.getAttribute("DeactivationDate").setDataAsDate(rset.getDate(4));
                    prepaidService.getAttribute("DeactivatedAt").setDataAsDate(rset.getDate(5)); 
                    prepaidService.getAttribute("BalanceForfeitedDate").setDataAsDate(rset.getDate(6));
                    prepaidService.getAttribute("BalanceForfeitedAt").setDataAsDate(rset.getDate(7)); 
                    prepaidService.getAttribute("ExpirationDate").setDataAsDate(rset.getDate(8));
                    prepaidService.getAttribute("ExpiredAt").setDataAsDate(rset.getDate(9));
                    prepaidService.getAttribute("NoVoucherBar").setDataAsInteger(rset.getInt(10));
                    //prepaidService.getAttribute("VoucherBarStatus").setDataAsInteger(rset.getInt(11));
                    //Ming Hon have to follow the integer_value in the gui_indicator_values
                    if(rset.getInt(11)==1) 
                        prepaidService.getAttribute("VoucherBarStatus").setDataAsInteger(1);
                    else 
                        prepaidService.getAttribute("VoucherBarStatus").setDataAsInteger(0);
                        //prepaidService.getAttribute("VoucherBarStatus").setDataAsInteger(2);
                    //prepaidService.getAttribute("CallBarStatus").setDataAsInteger(rset.getInt(12));
                    //Ming Hon have to follow the integer_value in the gui_indicator_values
                    if(rset.getInt(12)==1) 
                        prepaidService.getAttribute("CallBarStatus").setDataAsInteger(1);
                    else 
                        prepaidService.getAttribute("CallBarStatus").setDataAsInteger(0);
                        //prepaidService.getAttribute("CallBarStatus").setDataAsInteger(2);
                    prepaidService.getAttribute("Balance1").setDataAsDouble(rset.getDouble(13));
                    prepaidService.getAttribute("Balance2").setDataAsDouble(rset.getDouble(14));
                    prepaidService.getAttribute("Balance3").setDataAsDouble(rset.getDouble(15));
                    prepaidService.getAttribute("IPLanguage").setDataAsInteger(rset.getInt(16));
                    prepaidService.getAttribute("SubscriberStatus").setDataAsString(rset.getString(17));
                    prepaidService.getAttribute("InstallDate").setDataAsDate(rset.getDate(18));
                    prepaidService.getAttribute("ServiceInternalId").setDataAsInteger(subscr_no);
                    prepaidService.getAttribute("ServiceInternalIdResets").setDataAsInteger(subscr_no_resets);

                    // Insert New Domain into Collection
                    add(prepaidService);

                    if (prepaidProviderInfoCache == null) {
                        // The cache is stored in FXExtendedLogicFactory class

                        IExtendedLogicFactory factory = (IExtendedLogicFactory)
                            ServiceFinder.findServiceProvider(com.csgsystems.xlogic.IExtendedLogicFactory.class, null);

                        // Only try to extract cache if we are using FXExtenedLogicFactory otherwise
                        // no cache would be present
                        if (factory!=null && factory instanceof FXExtendedLogicFactory) {
                            prepaidProviderInfoCache = ((FXExtendedLogicFactory)factory).getPrepaidProviderInfoCache();
                        }
                    }
                    
                }
            }
            rset.close();
            pstmt.close();
        } catch (SQLException sEx) {
            sEx.printStackTrace();
            err.logErrorToFile("SQL Exception "+sEx.getMessage(), PrepaidServiceList.class);
            return 0;
        }
        
        return 0;
    }
}
