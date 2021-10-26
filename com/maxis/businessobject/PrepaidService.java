/*
 * PrepaidService.java
 *
 * Created on May 19, 2005, 4:14 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.businessobject;

import com.csgsystems.domain.framework.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.fxcommon.attribute.*;
import java.lang.*;
import java.util.HashMap;
import com.csgsystems.transport.IModelAdapter;
import com.csgsystems.fx.security.remote.*;
import com.csgsystems.fx.security.util.FxException;

import java.sql.*;
import com.maxis.util.*;

/**
 *
 * @author Pankaj Saini
 */
public class PrepaidService extends Domain {

    protected TimestampAttribute lastUpdateDate = null;
    protected TimestampAttribute firstCallDate = null;
    protected TimestampAttribute activationDate = null;
    protected TimestampAttribute deactivationDate = null;
    protected TimestampAttribute deactivatedAt = null;
    protected TimestampAttribute balanceForfeitedDate = null;
    protected TimestampAttribute balanceForfeitedAt = null;
    protected TimestampAttribute expirationDate = null;
    protected TimestampAttribute expiredAtDate = null;
    protected IntegerAttribute noVoucherBar = null;
    protected IntegerEnumeratedAttribute voucherBarStatus = null;
    protected IntegerEnumeratedAttribute callBarStatus = null;
    protected BPCurrencyAttribute balance1 = null;
    protected BPCurrencyAttribute balance2 = null;
    protected BPCurrencyAttribute balance3 = null;
    protected IntegerEnumeratedAttribute ipLanguage = null;
    protected StringCustomEnumeratedAttribute subscriberStatus = null;
    protected TimestampAttribute installDate = null;
    protected IntegerAttribute subscrNo = null;
    protected IntegerAttribute subscrNoResets = null;
    private HashMap subStatusTable = null;
    private ErrorReporting err = ErrorReporting.getInstance();

    /** Creates a new instance of PrepaidService */
    public PrepaidService() {
        subStatusTable = new HashMap();
        subStatusTable.put("N", "Inactive");
        subStatusTable.put("X", "Expired");
        subStatusTable.put("E", "Expired");
        subStatusTable.put("A", "Active");
        subStatusTable.put("I", "Installed");
        subStatusTable.put("D", "Deactivated with balance");
        subStatusTable.put("W", "Deactivated without balance");
        
        lastUpdateDate = new TimestampAttribute("LastUpdateDate", this, false);
        lastUpdateDate.setDateTimeInclude(IDateTime.DATETIME);
        
        firstCallDate = new TimestampAttribute("FirstCallDate", this, false);
        firstCallDate.setDateTimeInclude(IDateTime.DATETIME);

        activationDate = new TimestampAttribute("ActivationDate", this, false);
        activationDate.setDateTimeInclude(IDateTime.DATETIME);

        deactivationDate = new TimestampAttribute("DeactivationDate", this, false);
        deactivationDate.setDateTimeInclude(IDateTime.DATETIME);
        
        deactivatedAt = new TimestampAttribute("DeactivatedAt", this, false);
        deactivatedAt.setDateTimeInclude(IDateTime.DATETIME);
        
        balanceForfeitedDate = new TimestampAttribute("BalanceForfeitedDate", this, false);
        balanceForfeitedDate.setDateTimeInclude(IDateTime.DATETIME);

        balanceForfeitedAt = new TimestampAttribute("BalanceForfeitedAt", this, false);
        balanceForfeitedAt.setDateTimeInclude(IDateTime.DATETIME);

        expirationDate = new TimestampAttribute("ExpirationDate", this, false);
        expirationDate.setDateTimeInclude(IDateTime.DATETIME);

        expiredAtDate = new TimestampAttribute("ExpiredAt", this, false);
        expiredAtDate.setDateTimeInclude(IDateTime.DATETIME);

        noVoucherBar = new IntegerAttribute("NoVoucherBar", this, false);
        voucherBarStatus = new IntegerEnumeratedAttribute("VoucherBarStatus", this, 
                "GuiIndicator.FieldName=bar_status", "IntegerValue", "DisplayValue", false);
        callBarStatus = new IntegerEnumeratedAttribute("CallBarStatus", this, 
                "GuiIndicator.FieldName=bar_status", "IntegerValue", "DisplayValue", false);
        subscriberStatus = new StringCustomEnumeratedAttribute("SubscriberStatus", this, 
                subStatusTable, "A", false);
        ipLanguage = new IntegerEnumeratedAttribute("IPLanguage", this, 
                "GenericEnumeration.EnumerationKey=maxis_vm_language", "Value", "DisplayValue", false);
        
        balance1 = new BPCurrencyAttribute("Balance1", this, false);
        balance1.setCurrencyCodeAccessors(null, null, "CurrencyCode");
        balance2 = new BPCurrencyAttribute("Balance2", this, false);
        balance2.setCurrencyCodeAccessors(null, null, "CurrencyCode");
        balance3 = new BPCurrencyAttribute("Balance3", this, false);
        balance3.setCurrencyCodeAccessors(null, null, "CurrencyCode");

        installDate = new TimestampAttribute("InstallDate", this, false);
        installDate.setDateTimeInclude(IDateTime.DATETIME);
        
        subscrNo = new IntegerAttribute("ServiceInternalId", this, false);
        subscrNoResets = new IntegerAttribute("ServiceInternalIdResets", this, false);
    }
    
    /* 7-Jul-2005 Pankaj Saini
     Implements basic updates. Extended as required */
    public boolean flush(IModelAdapter ma) {
        boolean success = true;
        
        String SQL = "UPDATE MXS_SERVICE_EXT SET NUM_VOUCHER_BAR = ?, VOUCHER_BAR = ?, CALL_BAR = ?, ";
        SQL += "IP_LANGUAGE = ?, STATUS = ? ";
        SQL += "WHERE SUBSCR_NO = ? ";
        SQL += "AND SUBSCR_NO_RESETS = ?";

        try {
            RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CUST");
            if (dbConn == null) {
                err.logErrorToFile("Error getting current Oracle Connection", getClass());
               return false;
            }
            PreparedStatement pstmt = dbConn.prepareStatement(new SQL(SQL, "update"));
            pstmt.setInt(1, noVoucherBar.getDataAsInteger());
            // 20050714 Ming Hon - change. voucherBarStatus and callBarStatus values is [0|1] on MXS_SERVICE_EXT instead of [1|2] on Gui_indicator
            //pstmt.setInt(2, voucherBarStatus.getDataAsInteger());
            //pstmt.setInt(3, callBarStatus.getDataAsInteger());
            if(voucherBarStatus.getDataAsInteger()==1) pstmt.setInt(2, 1);
            else pstmt.setInt(2, 0);
            if(callBarStatus.getDataAsInteger()==1) pstmt.setInt(3, 1);
            else pstmt.setInt(3, 0);
            pstmt.setInt(4, ipLanguage.getDataAsInteger());
            pstmt.setString(5, subscriberStatus.getDataAsString());
            pstmt.setInt(6, subscrNo.getDataAsInteger());
            pstmt.setInt(7, subscrNoResets.getDataAsInteger());

            pstmt.executeUpdate();
            pstmt.close();
            
        } catch (FxException fx) {
            err.logError("A Kenan Framework error has occured, please contact ISD!!   "+ fx.getMessage(), "Error Message", getClass());
            err.logErrorToFile("FX Exception "+fx.getMessage(), getClass());
            fx.printStackTrace();
        } catch (java.sql.SQLException ex) {
            err.logErrorToFile("SQL Exception "+ex.getMessage(), getClass());
            err.logError("An Oracle error has occured on MXS_SERVICE_EXT table, please contact ISD!! "+ ex.getMessage(), "Error Message", getClass());
        } catch (Exception e) {
            err.logErrorToFile("Exception ", getClass());
            e.printStackTrace();
        }
        
        return success;
    }
}
