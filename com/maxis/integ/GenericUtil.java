/**
 *  Project Name: Maxis R&B
 *  Developer Name: Ming Hon
 *  Module Name: Customer Center - Integrator
 *  Date Created: 20050630
 *  Description:
 *  Date Modified: 20050630
 *  Version #: v01
 *
 *
 */
package com.maxis.integ;

import java.util.*;
import java.sql.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author Ming Hon
 */
public class GenericUtil{
	private static String standardKey = "A$CDE^GHI&K>MN*P;R(TUV)XYZa!cde@f<h#jkl'nop-rs}u`wx~z0123[56789+";
    public static boolean enableDebug = false;
    //20050819 WMH KenanFX - CC - IN-29: CUSTOMERID prefix should be dynamic
    //public static String CUSTOMERID_prefix = "1-1-00";
    
    private static Log log = null;
    static{
        try{
            log = LogFactory.getLog(com.maxis.integ.GenericUtil.class);
        }
        catch(Exception ex) { }
    }
    
    private static Properties MsgSeqIdUser;
/*    public static String getMsgSeqIdUser(String userId){
        if(MsgSeqIdUser==null){
            MsgSeqIdUser = new Properties();
        }
        String cacheMsgSeqIdUser = MsgSeqIdUser.getProperty(userId);
        if(cacheMsgSeqIdUser==null){
            String id;
            if(Config.isRunningInCC()){
                id = tuxJDBCUtil.getMsgSeqIdUser(userId);
            }
            else{
                id = oraJDBCUtil.getMsgSeqIdUser(userId);
            }
            MsgSeqIdUser.setProperty(userId, id);
            return id;
        }
        else{
            return cacheMsgSeqIdUser;
        }
    }
    
    public static void processMsgSeqId(HashMap input, String transCode){
        HashMap inputHeader = (HashMap)input.get("Header");
        HashMap inputBody = (HashMap)input.get("Body");
        //try to findout the msg_seq_id
        String MsgSeqIDOverride = (String)inputHeader.get("MsgSeqIDOverride");
        String MsgSeqIDUserOverride = (String)inputHeader.get("MsgSeqIDUserOverride");
        String MsgSeqIDTransOverride = (String)inputHeader.get("MsgSeqIDTransOverride");
        if(MsgSeqIDOverride==null){
            if(MsgSeqIDUserOverride==null){
                String KenanUserID = (String)inputHeader.get("KenanUserID");
                if(KenanUserID==null){
                    log.warn("KenanUserID must be specified when MsgSeqIDUserOverride or MsgSeqIDOverride is null. Default to arborsv");
                    KenanUserID = "arborsv";
                }
                MsgSeqIDUserOverride=GenericUtil.getMsgSeqIdUser(KenanUserID);
                inputHeader.put("MsgSeqIDUserOverride", MsgSeqIDUserOverride);
            }
            if(MsgSeqIDTransOverride==null){
                MsgSeqIDTransOverride = getTicketTypeMap().getProperty(transCode);
                if(MsgSeqIDTransOverride==null){
                    log.warn("Ticket type not found for "+inputHeader.get("TransType"));
                    MsgSeqIDTransOverride="999";
                }
                if(MsgSeqIDTransOverride.length()==1) MsgSeqIDTransOverride = "00"+MsgSeqIDTransOverride;
                if(MsgSeqIDTransOverride.length()==2) MsgSeqIDTransOverride = "0"+MsgSeqIDTransOverride;
            }
            inputHeader.put("MsgSeqIDOverride", MsgSeqIDTransOverride+MsgSeqIDUserOverride);
        }
    }
    
    private static Properties TicketTypeMap;
    private static boolean TicketTypeMapIsRunning = false;
    public static Properties getTicketTypeMap(){
        if(TicketTypeMap==null){
            if(TicketTypeMapIsRunning){
                for(int i=0;i<25;i++){
                    try{Thread.sleep(100);}catch(Exception ex){}
                    if(TicketTypeMapIsRunning==false){
                        i = 50; //just set it to more than the end value
                    }
                }
            }
            if(TicketTypeMap==null){
                TicketTypeMapIsRunning = true;
                if(Config.isRunningInCC()){
                    TicketTypeMap = tuxJDBCUtil.getTicketTypeMap();
                }
                else{
                    TicketTypeMap = oraJDBCUtil.getTicketTypeMap();
                }
                TicketTypeMapIsRunning = false;
            }
        }
        return TicketTypeMap;
    }

    private static Vector PrepaidReloadPeriod;
    private static boolean PrepaidReloadPeriodIsRunning = false;
    public static Vector getPrepaidReloadPeriod(){
        if(PrepaidReloadPeriod==null){
            if(PrepaidReloadPeriodIsRunning){
                for(int i=0;i<25;i++){
                    try{Thread.sleep(100);}catch(Exception ex){}
                    if(PrepaidReloadPeriodIsRunning==false){
                        i = 50; //just set it to more than the end value
                    }
                }
            }
            if(PrepaidReloadPeriod==null){
                PrepaidReloadPeriodIsRunning = true;
                if(Config.isRunningInCC()){
                    PrepaidReloadPeriod = tuxJDBCUtil.getPrepaidReloadPeriod();
                }
                else{
                    PrepaidReloadPeriod = oraJDBCUtil.getPrepaidReloadPeriod();
                }
                PrepaidReloadPeriodIsRunning = false;
            }
        }
        return PrepaidReloadPeriod;
    }
    
    private static Vector PrepaidProviderInfo;
    private static boolean PrepaidProviderInfoIsRunning = false;
    public static Vector getPrepaidProviderInfo(){
        if(PrepaidProviderInfo==null){
            if(PrepaidProviderInfoIsRunning){
                for(int i=0;i<25;i++){
                    try{Thread.sleep(100);}catch(Exception ex){}
                    if(PrepaidReloadPeriodIsRunning==false){
                        i = 50; //just set it to more than the end value
                    }
                }
            }
            if(PrepaidProviderInfo==null){
                PrepaidProviderInfoIsRunning = true;
                if(Config.isRunningInCC()){
                    PrepaidProviderInfo = tuxJDBCUtil.getPrepaidProviderInfo();
                }
                else{
                    PrepaidProviderInfo = oraJDBCUtil.getPrepaidProviderInfo();
                }
                PrepaidProviderInfoIsRunning = false;
            }
        }
        return PrepaidProviderInfo;
    }    
    
    public static String processPassword(String value){
	    if(value==null) return null;
	    try{
		    if("true".equals(Config.getInstance().getProperty("integ.enable_encyrption"))){
			    return decode(value);
		    }
	    }
	    catch(Exception ex){
		    log.error("Password not decoded."+ex.getMessage());
	    }
	    return value;
    }
*/    
    public static String encode(String value){
        byte keys[] = standardKey.getBytes();
        byte src[] = value.getBytes();
        byte dst[] = new byte[(src.length+2)/3 * 4];
        
        int start = 0;
        int length = src.length;
        
        int x = 0;
        int dstIndex = 0;
        int state = 0;	// which char in pattern
        int old = 0;	// previous byte
        int max = src.length;
        for (int srcIndex = start; srcIndex<max; srcIndex++) {
            x = src[srcIndex];
            switch (++state) {
                case 1:
                    dst[dstIndex++] = keys[(x>>2) & 0x3f];
                    break;
                case 2:
                    dst[dstIndex++] = keys[((old<<4)&0x30) | ((x>>4)&0xf)];
                    break;
                case 3:
                    dst[dstIndex++] = keys[((old<<2)&0x3C) | ((x>>6)&0x3)];
                    dst[dstIndex++] = keys[x&0x3F];
                    state = 0;
                    break;
            }
            old = x;
        }
        
        switch (state) {
            case 1: dst[dstIndex++] = keys[(old<<4) & 0x30];
            dst[dstIndex++] = (byte) '=';
            dst[dstIndex++] = (byte) '=';
            break;
            case 2: dst[dstIndex++] = keys[(old<<2) & 0x3c];
            dst[dstIndex++] = (byte) '=';
            break;
        }
        return new String(dst);
    }
    
    public static String decode( String value){
        int end = 0;
        if (value.endsWith("=")) end++;
        if (value.endsWith("==")) end++;
        int len = (value.length() + 3)/4 * 3 - end;
        byte[] result = new byte[len+1];
        int dst = 0;
        try {
            for(int src = 0; src< value.length(); src++) {
                int code =  standardKey.indexOf(value.charAt(src));
                if (code == -1) break;
                switch (src%4) {
                    case 0:
                        result[dst] = (byte) (code<<2);
                        break;
                    case 1:
                        result[dst++] |= (byte) ((code>>4) & 0x3);
                        result[dst] = (byte) (code<<4);
                        break;
                    case 2:
                        result[dst++] |= (byte) ((code>>2) & 0xf);
                        result[dst] = (byte) (code<<6);
                        break;
                    case 3:
                        result[dst++] |= (byte) (code & 0x3f);
                        break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) { if(enableDebug)ex.printStackTrace(); return "decode fail";}
        
        return new String(result, 0, result.length-1);
    }
/*    
    public static void printSmafException(de.siemens.advantage.in.sm.smaf.corba.servant.intf.CorbaSmafException ex, Log log){
	    String errorParams = "";
	    for(int i=0; i<ex.errorMsg.errorParams.length; i++){
		    errorParams = errorParams + "ex.errorMesg.errorParams["+i+"]="+ex.errorMsg.errorParams[i]+"\n";
	    }
	    String errMesg = "Smf errorMesg\n"
	    +"errorCategory="+ex.errorMsg.errorCategory+"\n"
	    +"errorCode="+ex.errorMsg.errorCode+"\n"
	    +"fullTraceMessage="+ex.errorMsg.fullTraceMessage+"\n"
	    +"errorParams="+errorParams;
	    log.error(errMesg);
    }
    
    //20050819 WMH KenanFX - CC - IN-29: CUSTOMERID prefix should be dynamic
    public static String resolveINCustomerID(String msisdn){
	    String INCustomerID = null;
	    if(Config.isRunningInCC()){
            INCustomerID = tuxJDBCUtil.resolveINCustomerID(msisdn);
        }
        else{
            INCustomerID = oraJDBCUtil.resolveINCustomerID(msisdn);
        }
        if(INCustomerID==null){
	        log.warn("Customer ID Map not found for MSISDN "+msisdn+" proceeding by sending "+msisdn+" to in ");
	        INCustomerID=msisdn;
        }
        return INCustomerID;
    }
*/
}

