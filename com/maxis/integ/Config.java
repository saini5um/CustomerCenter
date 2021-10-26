/**
 *  Project Name: Maxis R&B
 *  Developer Name: Ming Hon
 *  Module Name: Customer Center - Integrator
 *  Date Created: 20050525
 *  Description:
 *  Date Modified: 20050525
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
public class Config{
    private static Config config;
    private Properties prop;
    protected static Properties initialProperties = null;
    //No more hard code config
    //public static boolean useHardCodedConfig = false;
    public static final String IntegratorVersion = "1.0";

    private static Log log = null;
    static{
        try{
            log = LogFactory.getLog(com.maxis.integ.Config.class);
        }
        catch(Exception ex) { }
    }

    protected Config()throws Exception{
        prop = new Properties();
        reload();
    }

    public static Config getInstance() throws Exception{
        if(config==null){
            config = new Config();
            //config.printAll();
        }
        //System.out.println("Config getInstance()");
        return config;
    }

    public Properties getProperties(){
	    return prop;
    }
    
    public void printAll(){
        if(prop!=null){
            System.out.println(prop);
        }
        if(initialProperties!=null){
            System.out.println(initialProperties);
        }
    }

    public String getProperty(String key){
        return prop.getProperty(key);
    }

    public void setProperty(String key, String value){
        prop.setProperty(key,value);
    }

    public Enumeration propertyNames(){
        return prop.propertyNames();
    }
    public void reload() throws Exception{
        //if running in CC load the configuration via tuxJDBCManager
        //if running outside CC load the configuration via oracle JDBC
        //No more hard code config
        //if(!useHardCodedConfig){
            if(isRunningInCC()){
                prop = configLoadViaTuxJDBC.load();
            }
            else{
                if(initialProperties!=null){
                    prop = configLoadViaOracleJDBC.load();
                }
                else{
                    System.out.println();

                    throw new Exception("Integrator.setInitialProperties() is not call for the first time to pass in the initial db connection setting.");
                }
            }
        //No more hard code config
        /*}    
        else{
            prop = new Properties();
            prop.setProperty("IN_DBG_LVL", "5");
            prop.setProperty("IN_OP_MODE", "CorbaSimulator");
            prop.setProperty("IN_SVR_1", "10.200.76.110");
            prop.setProperty("IN_SVR_2", "10.200.76.110");
            prop.setProperty("IN_SVR_3", "10.200.76.110");
            prop.setProperty("IN_SVR_CNT", "3");
            prop.setProperty("MaxAdjustmentAmount", "1000");
            prop.setProperty("MinAdjustmentAmount", "-1000");
            prop.setProperty("MaxBalance", "100000");
            prop.setProperty("MinBalance", "0");
            prop.setProperty("MaxVoucherlessReloadAmount", "99999");

            log.debug("Using Hard coded values for "+getClass().getName());
        }*/
    }

    public static boolean isRunningInCC(){
        //Assuming can find the Domain class means its running inside CC
        try{
            Class.forName("com.csgsystems.domain.framework.businessobject.Domain");
            return true;
        }
        catch(ClassNotFoundException ex){

        }
        return false;
    }

    public void print(){
        Enumeration e = prop.propertyNames();
        while(e.hasMoreElements()){
            String key = (String)e.nextElement();
            log.debug(key+"="+prop.getProperty(key));
        }

    }

    private static Properties _ExecCode;
    public static Properties getExecCode(){
        if(_ExecCode==null){
            //load the IN field type mapping
            try{
                java.io.InputStream in = ClassLoader.getSystemResourceAsStream("com/maxis/integ/ExecCode.properties");
                _ExecCode = new Properties();
                _ExecCode.load(in);
                in.close();
            }
            catch(java.io.IOException ex){
                ex.printStackTrace();
            }
        }
        return _ExecCode;
    }

}
