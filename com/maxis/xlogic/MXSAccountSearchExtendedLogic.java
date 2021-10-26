/*
 * MXSAccountSearchExtendedLogic.java
 *
 * Created on August 10, 2005, 4:29 PM
 */

package com.maxis.xlogic;

import com.csgsystems.fx.security.rules.*;
import com.csgsystems.domain.arbor.businessobject.AccountLocate;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.xlogic.ExtendedLogicBase;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.security.SecurityManager;
import com.csgsystems.fx.security.remote.RemoteDBConnection;
import com.csgsystems.fx.security.remote.SQL;
import com.csgsystems.fx.security.util.FxException;

import java.util.*;
import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maxis.util.tuxJDBCManager;

/**
 *
 * @author  teoh
 */
public class MXSAccountSearchExtendedLogic extends ExtendedLogicBase{
    
    private static final String MASK_ATTR = "*****";
    private static final Integer MINUS_ONE = new Integer(-1);
    private static final int IN_FRAUD = 3;
    private static final int WRITE_OFF = 1;

    private static Log log;
    private static HashMap ServiceExternalIdType;
    private static ArrayList secSegIdList = null;//new ArrayList();
    private static boolean nonMasaUser = false;

    static {
        try {
            log = LogFactory.getLog(com.maxis.xlogic.MXSAccountSearchExtendedLogic.class);
        }
        catch( Exception ex ) {
        }
    }
    
    /** Creates a new instance of MXSAccountSearchExtendedLogic */
    public MXSAccountSearchExtendedLogic() {
        super();
    }
    
    public void newObject(IPersistentObject target) {
        super.newObject(target);
        target.setAttributeData("AccountExternalIdType", "9");
        target.setAttributeData("ServiceExternalIdType", "23");
        target.getAttribute("ServiceExternalIdType").setModified(false);
        
        target.setModified(false);
        
    }
    
    
    //20050914 WMH PN KenanFX - CC - General-112: Added method
    /*public static synchronized HashMap getServiceExternalIdType(){
        if(ServiceExternalIdType!=null) return ServiceExternalIdType;
        ServiceExternalIdType = new HashMap();
        try{
            RemoteDBConnection conn = tuxJDBCManager.getInstance().getCurrentConnection("ADMIN");
            if(conn==null){
                javax.swing.JOptionPane.showMessageDialog(null,"Cannot get tux JDBC connection. User not authorised to access resources",
                "Search for an Account", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
            else{
                String strQuery = "SELECT EITR.external_id_type, EITV.display_value "
                +"FROM EXTERNAL_ID_TYPE_REF EITR,EXTERNAL_ID_TYPE_VALUES EITV "
                +"WHERE EITR.external_id_type = EITV.external_id_type AND "
                +"EXISTS (SELECT 1 FROM CC_EMF_CONFIG_EXT_ID_TYPE_MAP MAP WHERE "
                +"MAP.external_id_type = EITR.external_id_type ) AND EITV.language_code "
                //+"= "+com.csgsystems.domain.arbor.utilities.DomainUtility.getLanguageCode()+" ORDER BY EITV.display_value "; //cannot get language code
                +"= 1 ORDER BY EITV.display_value ";
                try{
                    PreparedStatement cstat = conn.prepareStatement(new SQL(strQuery,"select"));
                    ResultSet rs = cstat.executeQuery();
                    while(rs.next()){
                        ServiceExternalIdType.put(new Integer(rs.getInt(1)),rs.getString(2));
                    }
                    rs.close();
                    cstat.close();
                }
                catch(SQLException ex){
                    conn.close();
                    ex.printStackTrace();
                }
                catch (FxException fx) {
                    fx.printStackTrace();
                }
                conn.close();
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        
        return ServiceExternalIdType;
    }*/
    
    public void postUnmarshal(IPersistentObject ipersistentobject) {
        super.postUnmarshal(ipersistentobject);
        //System.out.println("postUnmarshal");
        //System.out.println("count="+((IPersistentCollection)ipersistentobject).size());
        Domain acctLocate = null;
        Domain account = null;
        RemoteDBConnection conn = null; 
        PreparedStatement cstat = null;
        BooleanAttribute m_MXSMasa = null;
        int masa_fraud_count = 0;
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT COLLECTION_STATUS, COLLECTION_HISTORY ")
        .append("FROM ")
        .append("CMF ")
        .append("WHERE ")
        .append("ACCOUNT_NO = ?");
		if(secSegIdList == null)
	        chkSecProfileSegment();
        try {
            if(SecurityManager.getInstance().isAuthorized("Maxis.NonMasa", SecurityManager.SERVICE_RSRC))
                nonMasaUser = true;
            log.info("masa auth="+SecurityManager.getInstance().isAuthorized("Maxis.NonMasa", SecurityManager.SERVICE_RSRC));
            log.info("nonMasaUser=" + nonMasaUser);

            m_MXSMasa = new BooleanAttribute("MXSMasa", (Domain)ipersistentobject, false);
            m_MXSMasa.setDataAsBoolean(!nonMasaUser);
            log.info("m_MXSMasa.getDataAsBoolean=" + m_MXSMasa.getDataAsBoolean());
        }
        catch(Exception e) {
            log.error(e.toString());
        }
        
        
        
        
        for(int i=0; i<((IPersistentCollection)ipersistentobject).size(); i++){
            
            acctLocate = (Domain)(((IPersistentCollection)ipersistentobject).getAt(i));

            StringAttribute m_MXSName = new StringAttribute("MXSName", acctLocate, false);
            m_MXSName.setDataAsString(acctLocate.getAttributeDataAsString("AccountDisplayName"));
            //System.out.println("m_MXSName=" + m_MXSName);
            
            StringAttribute m_MXSAddress = new StringAttribute("MXSAddress", acctLocate, false);
            m_MXSAddress.setDataAsString(acctLocate.getAttributeDataAsString("AddressDisplayValue"));
            //System.out.println("m_MXSAddress=" + m_MXSAddress);
            
            StringAttribute m_MXSDate = new StringAttribute("MXSDate", acctLocate, false);
            m_MXSDate.setDataAsString(acctLocate.getAttributeDataAsString("DateDisplay"));
            //System.out.println("m_MXSDate=" + m_MXSDate);
            
            BooleanAttribute m_MXSAction = new BooleanAttribute("MXSAction", acctLocate, false);
            m_MXSAction.setDataAsBoolean(true);
            //System.out.println("m_MXSAction=" + m_MXSAction);
            
            if(!filterSecSegment(acctLocate.getAttributeDataAsInteger("AcctSegId"))) { // convergent
                m_MXSName.setDataAsString(MASK_ATTR);
                m_MXSAddress.setDataAsString(MASK_ATTR);
                m_MXSDate.setDataAsString(MASK_ATTR);
                m_MXSAction.setDataAsBoolean(false);
                
                //System.out.println("AccountExternalId=" + ((IPersistentCollection)ipersistentobject).getAt(i).getAttributeData("AccountExternalId"));
            }
            
            BooleanAttribute m_MXSDisplay = new BooleanAttribute("MXSDisplay", acctLocate, false);
            m_MXSDisplay.setDataAsBoolean(true);
            try{
                if(m_MXSMasa.getDataAsBoolean()){
                    //System.out.println("MASA=" + m_MXSMasa.getDataAsBoolean());
                    //System.out.println("ServerId=" + acctLocate.getAttributeDataAsInteger("ServerId"));
                    tuxJDBCManager.getInstance().setServerId(acctLocate.getAttributeDataAsInteger("ServerId"));
                    conn = tuxJDBCManager.getInstance().getCurrentConnection("CUS");
                    cstat = conn.prepareStatement(new SQL(sb.toString(),"select"));
                    cstat.setInt(1, acctLocate.getAttributeDataAsInteger("AccountInternalId"));
                    ResultSet rs = cstat.executeQuery();
                    if(rs.next()){
                        //System.out.println("rs.next");
                        if(IN_FRAUD == rs.getInt(1) || WRITE_OFF == rs.getInt(2)){
                            m_MXSDisplay.setDataAsBoolean(false);
                            //System.out.println("m_MXSDisplay false");
                            masa_fraud_count ++;
                        }
                    }
                    rs.close();
                    cstat.close();
                    conn.close();
                    
                    rs = null;
                    cstat = null;
                    conn = null;
                }
            } catch(Exception e) {
                log.error(e.toString());
            }
        }
        
        IntegerAttribute MasaFraudCount = new IntegerAttribute("MasaFraudCount", (Domain)ipersistentobject, false);
        MasaFraudCount.setDataAsInteger(masa_fraud_count);
        
    }
    
    
    private static void chkSecProfileSegment(){
/*
        RuleObject aruleobject[] = null;
        secSegIdList.clear();
        try {
            aruleobject = SecurityManager.getInstance().getResourceRules(null, "Account.AccountSegment");
        }
        catch(Exception e) {
            log.error(e.toString());
        }
        if(aruleobject != null) {
            RuleKey rulekey = new RuleKey("segment_id");
            for(int i = 0; i < aruleobject.length; i++) {
                RuleValue rulevalue = aruleobject[i].getValue(rulekey);
                if(rulevalue != null)
                    secSegIdList.add(new Integer((String)rulevalue.getValue()));
            }
        } else {
            log.warn("No rules found for AccountSegment");
        }
        log.info("Sec Segment Profile=" + secSegIdList);
*/
        log.info("chkSecProfileSegment");
        StringBuffer sb = new StringBuffer("");
        RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("ADMIN");
        if (dbConn == null) {
            log.info("No connection to database");
        } else {
            sb.append("SELECT ACCT_SEG_ID FROM CSR_ACCT_SEG WHERE upper(CSR_NAME) = ? ");
            try{
                PreparedStatement sql = dbConn.prepareStatement(new SQL(sb.toString(), "select"));
				sql.setString(1, SecurityManager.getInstance().getLoggedInUserId().toUpperCase());
                ResultSet result = sql.executeQuery();
				secSegIdList = new ArrayList();
                while(result.next()){
                    secSegIdList.add(new Integer(result.getInt(1)));
                }
                result.close();
				sql.close();
				dbConn.close();
            } catch (java.sql.SQLException e) {
                log.error("SQL Error: " + e);
                e.printStackTrace();
            }
            catch (FxException e) {
                log.error("FX Error: " + e);
                e.printStackTrace();
            }
            catch (Exception e) {
                log.error("Error: " + e);
                e.printStackTrace();
            }
        }
		log.info("Sec Segment Profile=" + secSegIdList);
	}
    
    private boolean filterSecSegment(int segId){
        int tmp = -1;
        //if(secSegIdList.size() == 1 && MINUS_ONE.equals(secSegIdList.get(0)))
		if(secSegIdList.size() == 0)
            return true;
        for(int i=0; i<secSegIdList.size(); i++){
            tmp = ((Integer)secSegIdList.get(i)).intValue();
            if(segId == tmp){
                return true;
            }
        }
        return false;
    }
}
