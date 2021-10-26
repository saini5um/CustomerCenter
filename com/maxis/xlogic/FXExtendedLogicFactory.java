/*
 * FXExtendedLogicFactory.java
 *
 * Copyright (C) 2003 CSG Systems
 * www.csgsystem.com
 *
 * Created on Feb 11, 2004
 */
package com.maxis.xlogic;

import java.util.HashMap;
import java.util.Map;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.DateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.xlogic.DefaultExtendedLogicFactory;
import com.csgsystems.xlogic.IExtendedLogic;
import com.csgsystems.cache.CacheManager;
import com.csgsystems.cache.Cache;
import com.csgsystems.fx.security.remote.RemoteDBConnection;
import com.csgsystems.fx.security.remote.SQL;
import com.csgsystems.fx.security.util.FxException;

import com.maxis.util.tuxJDBCManager;
import com.maxis.cache.PrepaidProviderInfo;


/**
 * An implementation of the IExtendedLogicFactory interface used
 * to provide non-core business logic to application domain objects.<p>
 *
 * This implementation uses a java.util.Map to identify the extended
 * logic for particular application domain objects. It has been provided
 * as an example of how one <em>might</em> implement extended logic for
 * an application.<p>
 *
 * This example derives from DefaultExtendedLogicFactory since it provides
 * implementations of the appropriate interfaces that can be delegated to
 * when an application domain is specified that has no extended logic.
 *
 * @author djs
 */
public class FXExtendedLogicFactory extends DefaultExtendedLogicFactory {
    
    /**
     * A map of the extended logic implementations. The keys to the map are
     * the unqualified class names of the application domain object for
     * which extended logic is being provided; the values of the map entries
     * are instances of the IExtendedLogic interface.
     */
    private static Log log;
    private Map mapExtendedLogic;
    private Map mapOrderExtendedLogic;
    private static Cache prepaidProviderInfoCache;
    
    static {
        try {
            log = LogFactory.getLog(com.maxis.xlogic.FXExtendedLogicFactory.class);
        }
        catch (Exception ex) {
        }
    }
    
    /**
     * Public no-argument constructor required. A factory mechanism is used
     * to instantiate the extended logic factory.
     */
    public FXExtendedLogicFactory() {
        super();
        
        /*
         * For this example, we will set up the map of extended logic
         * entries here. For this example, we are extending only the
         * Account application domain object. Follow the pattern to
         * add more supported objects.
         */
        
        //System.out.println("Start FXExtendedLogicFactory");
        
        mapExtendedLogic = new HashMap();
        mapOrderExtendedLogic = new HashMap();
        mapExtendedLogic.put("Service", new ServiceExtendedLogic());
        mapExtendedLogic.put("Account", new AccountExtendedLogic());
        mapExtendedLogic.put("PackageComponentDef", new PackageDef());
//        mapExtendedLogic.put("Order", new MXSOrderExtendedLogic());
        mapExtendedLogic.put("AccountId", new MXSAccountIdExtendedLogic());
        mapExtendedLogic.put("AccountLocateList" ,new MXSAccountSearchExtendedLogic());
        mapExtendedLogic.put("AccountLocate", new AccountLocateExtendedLogic());
        mapExtendedLogic.put("ServiceOrder" ,new MXSServiceOrderExtendedLogic());
        mapExtendedLogic.put("ProductRateOverride" ,new ProductRateOverride());
        mapExtendedLogic.put("CustomerIdEquipMap" ,new CustomerIdEquipMapExtendedLogic());
        //mapExtendedLogic.put("InvElementList", new MXSInvSearchExtendedLogic());
        //      System.out.println("Extended logic created!");
        
        //Ming Hon: Added for IN testing
        //new InTestLogic();
    }
    
    /**
     * Implementation of the createExtendedLogic() method. This
     * method determines which implementation of the IExtendedLogic
     * interface should be returned for a specified application
     * domain object.
     *
     * @param appDomain the application domain object for which
     * extended logic is requested.
     *
     * @see com.csgsystems.xlogic.DefaultExtendedLogicFactory#createExtendedLogic(com.csgsystems.domain.framework.businessobject.IPersistentObject)
     */
    public IExtendedLogic createExtendedLogic(IPersistentObject appDomain) {
     /*
      * Get the extended logic implementation from the
      * map of supported objects.
      */
        IExtendedLogic exLogic = (IExtendedLogic) mapExtendedLogic.get(getUnqualifiedClassName(appDomain));
        
        if (exLogic == null) {
         /*
          * If no extended logic implementation was found in the
          * map, let the base class create a stub implementation
          * to use.
          */
            exLogic = super.createExtendedLogic(appDomain);
        }
        return exLogic;
    }
    
    /**
     * Returns the unqualified class name of the specified
     * instance of an object.
     *
     * @param obj reference to the object for which the unqualified
     * class name is desired.
     * @return the unqualified class name of the object. If the specified
     * object is null, then the string <em>(null)</em> is returned.
     */
    protected static String getUnqualifiedClassName(Object obj) {
        String className = "(null)";
        if (obj != null) {
            className = obj.getClass().getName();
            int pos = className.lastIndexOf('.');
            if (pos >= 0) {
                className = className.substring(pos + 1);
            }
        }
        return className;
    }
    
    /**
     * This method returns the Cache that represents contents of the
     * MXS_PREPAID_INFO table
     *
     * @return Cache object that contains the list of values obtained
     * so far
     */
    public static Cache getPrepaidProviderInfoCache() {
        if (prepaidProviderInfoCache == null) {
            prepaidProviderInfoCache = CacheManager.getInstance().createCache();
            
            RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("ADMIN");
            String strQuery = "SELECT PROVIDER_ID, D_OFFSET, O_OFFSET, ";
            strQuery = strQuery + "E_OFFSET, D_GRACE, E_GRACE, DELETE_OFFSET, FNF_SLOT, FA_SLOT, FA_LIST_ID, ";
            strQuery = strQuery + "EMF_CONFIG_ID, FNF_CHARGE, FNF_FREE_COUNTER, FA_CHARGE, FA_FREE_COUNTER, ";
            strQuery = strQuery + "MAX_BALANCE, HZ_SLOT, HZ_FREE_COUNTER, SPC_DAY_FREE_COUNTER, " +
            "BOLT_ON_FREE_COUNTER FROM MXS_PROVIDER_INFO";
            
            if (dbConn == null) {
                System.out.println("No connection to database");
            }
            
            try {
                PreparedStatement sql = dbConn.prepareStatement(new SQL(strQuery, "select"));
                sql.setMaxRows(100);
                ResultSet result = sql.executeQuery();
                
                for (int j=0; result.next() ; j++) { // while (result.next() )
                    HashMap hmValue = new HashMap();
                    long emfConfigId = result.getLong(11);
                    
                    hmValue.put("D_OFFSET", new Long(result.getLong(2)));
                    hmValue.put("O_OFFSET", new Long(result.getLong(3)));
                    hmValue.put("E_OFFSET", new Long(result.getLong(4)));
                    hmValue.put("D_GRACE", new Long(result.getLong(5)));
                    hmValue.put("E_GRACE", new Long(result.getLong(6)));
                    hmValue.put("DELETE_OFFSET", new Long(result.getLong(7)));
                    hmValue.put("FNF_SLOT", new Long(result.getLong(8)));
                    hmValue.put("FA_SLOT", new Long(result.getLong(9)));
                    hmValue.put("FA_LIST_ID", result.getString(10));
                    hmValue.put("PROVIDER_ID", new Long(result.getLong(1)));
                    hmValue.put("FNF_CHARGE", new Long(result.getLong(12)));
                    hmValue.put("FNF_FREE_COUNTER", new Long(result.getLong(13)));
                    hmValue.put("FA_CHARGE", new Long(result.getLong(14)));
                    hmValue.put("FA_FREE_COUNTER", new Long(result.getLong(15)));
                    hmValue.put("MAX_BALANCE", new Long(result.getLong(16)));
                    hmValue.put("HZ_SLOT", new Long(result.getLong(17)));
                    hmValue.put("HZ_FREE_COUNTER", new Long(result.getLong(18)));
                    hmValue.put("SPC_DAY_FREE_COUNTER", new Long(result.getLong(19)));
                    hmValue.put("BOLT_ON_FREE_COUNTER", new Long(result.getLong(20)));
                    PrepaidProviderInfo prepaidProviderInfo = new PrepaidProviderInfo(emfConfigId, hmValue);
                    prepaidProviderInfoCache.set(prepaidProviderInfo);
                }
                result.close();
            } catch (java.sql.SQLException ex) {
                System.out.println("SQL Exception "+ex.getMessage());
            } catch (FxException fx) {
                fx.printStackTrace();
            }
        }
        
        return prepaidProviderInfoCache;
    }
    
    private static String MsgSeqIdUser;
    public static String getMsgSeqIdUser(){
        if(MsgSeqIdUser==null){
            MsgSeqIdUser = "Still Blank";
            RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("ADMIN");
            
            if (dbConn == null) {
                System.out.println("No connection to database");
            }
            else{
                try{
                    String userId = com.csgsystems.domain.framework.security.SecurityManager.getInstance().getLoggedInUserId();
                    CallableStatement cstmt = dbConn.prepareCall(new SQL("get_msg_seq_id","proc"));
                    cstmt.setString(1, userId);
                    cstmt.registerOutParameter(2, Types.VARCHAR);
                    
                    ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cstmt).getParms().get(1)).registerAsOutParameter();
                    ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cstmt).getParms().get(1)).setNullParm(false);
                    ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cstmt).getParms().get(1)).setValue("eeee");
                    
                    int result = cstmt.executeUpdate();
                    MsgSeqIdUser = cstmt.getString(2);
                }
                catch (java.sql.SQLException ex) {
                    ex.printStackTrace();
                }
                catch (FxException fx) {
                    fx.printStackTrace();
                }
                catch (Exception fx) {
                    fx.printStackTrace();
                }
            }
        }
        return MsgSeqIdUser;
    }
    
    private static long activeDays;
    public static long getActiveDays(long providerId, long amount){
        log.info("getActiveDays(" + providerId + ")");
        StringBuffer sb = new StringBuffer("");
        RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("ADMIN");
        if (dbConn == null) {
            log.info("No connection to database");
        } else {
            sb.append("SELECT active_days FROM mxs_prepaid_reload_period WHERE provider_id = ? ")
            //.append(providerId).append(" ")
            .append("AND amount_low <= ? ")//.append(amount).append(" ")
            .append("AND amount_high > ? ");//.append(amount).append(" ");
            try{
                PreparedStatement sql = dbConn.prepareStatement(new SQL(sb.toString(), "select"));
                sql.setLong(1, providerId);
                sql.setLong(2, amount);
                sql.setLong(3, amount);
                ResultSet result = sql.executeQuery();
                if(result.next()){
                    activeDays = result.getLong(1);
                }
                result.close();
            } catch (java.sql.SQLException e) {
                activeDays = 0;
                log.error("SQL Error: " + e);
                e.printStackTrace();
            }
            catch (FxException e) {
                activeDays = 0;
                log.error("FX Error: " + e);
                e.printStackTrace();
            }
            catch (Exception e) {
                activeDays = 0;
                log.error("Error: " + e);
                e.printStackTrace();
            }
            
        }
        return activeDays;
    }
    
    public static void chkBalForfeitedDt(IPersistentObject prepaidService, Map providerInfoMap) throws Exception {
        log.info("chkBalForfeitedDt");
        log.info("prepaidService="+prepaidService);
        log.info("providerInfoMap="+providerInfoMap);
        if(prepaidService == null)
            throw new Exception("Prepaid Service is NULL");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        Date deActDt = prepaidService.getAttributeDataAsDate("DeactivationDate");
        log.info("DeactivationDate = " + dateFormat.format(deActDt));
        if(deActDt == null){
            prepaidService.getAttribute("BalanceForfeitedDate").setDataAsDate(null);
        } else {
            long offSet = ((Long)providerInfoMap.get("O_OFFSET")).longValue();
            log.info("offSet = " + offSet);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(deActDt);
            log.info("resolved date time = " + dateFormat.format(cal.getTime()));
            cal.add(GregorianCalendar.DAY_OF_MONTH, (int)offSet);
            log.info("modified date time offSet = " + dateFormat.format(cal.getTime()));
            prepaidService.getAttribute("BalanceForfeitedDate").setDataAsDate(cal.getTime());
        }
    }

    public static void chkExpDt(IPersistentObject prepaidService, Map providerInfoMap) throws Exception {
        log.info("chkExpDt");
        log.info("prepaidService="+prepaidService);
        log.info("providerInfoMap="+providerInfoMap);
        if(prepaidService == null)
            throw new Exception("Prepaid Service is NULL");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        Date deActDt = prepaidService.getAttributeDataAsDate("DeactivationDate");
        log.info("DeactivationDate = " + dateFormat.format(deActDt));
        if(deActDt == null){
            prepaidService.getAttribute("ExpirationDate").setDataAsDate(null);
        } else {
            long eOffSet = ((Long)providerInfoMap.get("E_OFFSET")).longValue();
            long eGrace = ((Long)providerInfoMap.get("E_GRACE")).longValue();
            log.info("eOffSet = " + eOffSet);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(deActDt);
            log.info("resolved date time = " + dateFormat.format(cal.getTime()));
            cal.add(GregorianCalendar.DAY_OF_MONTH, (int)eOffSet);
            log.info("modified date time with eOffSet = " + dateFormat.format(cal.getTime()));
            cal.add(GregorianCalendar.DAY_OF_MONTH, (int)eGrace);
            log.info("modified date time with eGrace = " + dateFormat.format(cal.getTime()));
            prepaidService.getAttribute("ExpirationDate").setDataAsDate(cal.getTime());
        }
    }

    public static String chkSubscriberSts(IPersistentObject prepaidService, IPersistentObject m_pPrepaidIN) throws Exception {
        String sts = "A"; // Active
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        Date expAt = prepaidService.getAttributeDataAsDate("ExpiredAt");
        Date fisrtCallDt = prepaidService.getAttributeDataAsDate("FirstCallDate");
        Date deactivatedAt = prepaidService.getAttributeDataAsDate("DeactivatedAt");
        Date expDt = m_pPrepaidIN.getAttributeDataAsDate("OnPeakAccountIDExpiryDate");
        Date deactivaionDt = prepaidService.getAttributeDataAsDate("DeactivationDate");

        int bal = m_pPrepaidIN.getAttributeDataAsInteger("OnPeakAccountIDBalance");
        
        log.info("balance = " + bal);
        log.info("current date time = " + (cal.getTime()));
        log.info("expired at date time = " + (expAt));
        log.info("first call date time = " + (fisrtCallDt));
        log.info("deactivated date time = " + (deactivatedAt));
        log.info("expiration date time = " + (expDt));
        log.info("deactivation date time = " + (deactivaionDt));
        
        if(expAt != null){
            sts = "X"; // Expired
        } else if(fisrtCallDt == null && deactivatedAt == null){
            sts = "I"; // Installed
        } else if(cal.getTime().after(expDt)){
            sts = "X"; // Expired
        } else if(cal.getTime().after(deactivaionDt) && cal.getTime().before(expDt)){
            if(bal > 0){
                sts = "D"; // Deactivated with balance
            } else {
                sts = "W"; // Deactivated without balance
            }
        } else {
            if(bal > 0){
                sts = "A"; // Active
            } else {
                sts = "I"; // Inactive
            }
        }
        log.info("final sts = " + sts);
        return sts;
    }
}