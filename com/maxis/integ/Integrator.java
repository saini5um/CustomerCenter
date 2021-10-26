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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.*;
import java.lang.reflect.Method;
import com.maxis.util.PrintMap;
import com.csgsystems.domain.framework.DomainFrameworkEventManager;

/**
 * If this class is to be used inside Customer Center, it can directly call getInstance.
 * If this class is to be used outside customer center, there need to call setInitialProerties
 * to set the initial oracle login properties. It need to retrieve the modules configuration setting
 * from table SYSTEM_PARAMETERS@FXADM
 *
 * Valid values for IN_OP_MODE
 * AlwaysCache = Always fail each call by returning ExecCode=-2 (Connectivity Failure)
 * CorbaSimulator = Connects to corba simulator to fetch data.
 * Production = Runs on production Mode. Connects to production server.
 *
 * @author Ming Hon
 */
public class Integrator{
    private static Integrator integrator;
    private static Properties transactionMapping;
    private static Config config;

    private static Log log = null;
    static{
        try{
            log = LogFactory.getLog(com.maxis.integ.Integrator.class);
        }
        catch(Exception ex) { }
    }

    protected Integrator()throws Exception{
        config = Config.getInstance();
        //Load the transaction mapping
        java.io.InputStream in = ClassLoader.getSystemResourceAsStream("com/maxis/integ/TransMapping.properties");
        transactionMapping = new Properties();
        transactionMapping.load(in);
        in.close();
    }

    /**
     * This method is used to set the database connection conifuration if its used
     * outside Customer Center. Customer center would already have an oracle connection
     * to fetch all other integ configuration.
     * The module will only use the arguments from initialProperties if its running outside Customer Center
     * This method is expecting the following values
     * DB URL = jdbc:oracle:thin:@10.200.76.109:1521:FXADM
     * Driver = oracle.jdbc.driver.OracleDriver
     * User Name = arbor6
     * Encoded Password = front6 (in encyrpted format)
     */
    public static void setInitialProperties(Properties prop){
        Config.initialProperties = prop;
    }


    public static Integrator getInstance()throws Exception{
        if(integrator==null){
            integrator = new Integrator();
        }
        if(!config.getProperty("IntegratorVersion").equals(Config.IntegratorVersion)){
            throw new Exception("You are running a version of Integrator which does not match the configured number. Package version number: "+Config.IntegratorVersion+". Configured version number: "+config.getProperty("IntegratorVersion")+". Please contact Admin/Developer to get the correct libraries.");
        }
        return integrator;
    }

    /**
     * Valid transaction mapping is stored in file TransactionMapping.properties
     *
     */
    public HashMap execute(String transactionType, HashMap input) throws IntegException{
        DomainFrameworkEventManager.getInstance().fireBusy(true);
        try{
            long startTime = System.currentTimeMillis();
            HashMap inputHeader = (HashMap)input.get("Header");
            HashMap inputBody = (HashMap)input.get("Body");
            //config.print();
            log.debug("Integrator.execute() input hashmap\n"+PrintMap.toMapString(input,2));
            if(!((String)config.getProperty("IN_OP_MODE")).equals("AlwaysCache")){
                //execute the transaction
                //the transaction name maps to a class name
                //get that class
                //get the instance of that class and then invoke execrans
                //and return the output of the execution
                //if mapping is not found, throw some errors
                String transClassName = transactionMapping.getProperty(transactionType);
                log.debug("transClassName="+transClassName);
                Class transClass = Class.forName(transClassName);
                //Class transClass = ClassLoader.getSystemClassLoader().loadClass(transClassName);
                Method method = transClass.getMethod("getInstance", null);
                Transaction trans = (Transaction)method.invoke(null, null);

                HashMap output = trans.execTrans(input);
                log.debug("Integrator.execute() output hashmap\n"+PrintMap.toMapString(output,2));
                log.info("Transaction "+transactionType+" executed in "+(System.currentTimeMillis()-startTime)+" miliseconds");
                return output;
            }
            else{
                HashMap output = new HashMap();
                HashMap header = new HashMap();
                HashMap body = new HashMap();
                output.put("Header", header);
                output.put("Body", body);

                header.put("ExecCode", new Integer(-2));
                header.put("ExecMesg", "Connectivity Failure");
                header.put("TransType", inputHeader.get("TransType"));
                log.debug("Force Transaction To fail="+inputHeader.get("TransType")+" IN_OP_MODE set to AlwaysCache.");
                log.debug("Integrator.execute() output hashmap\n"+PrintMap.toMapString(output,2));
                return output;
            }
        }
        catch(ClassNotFoundException ex){
            String errMesg = "Class mapping not found for transaction type = " + transactionType;
            log.error(errMesg,ex);
            throw new IntegException(errMesg);
        }
        catch(NoSuchMethodException ex){
            String errMesg = "Class mapping not found for transaction type = " + transactionType+" doesnot have such method";
            log.error(errMesg,ex);
            throw new IntegException(errMesg);
        }
        catch(IllegalAccessException ex){
            String errMesg = ex.getMessage();
            log.error(errMesg,ex);
            throw new IntegException(errMesg);
        }
        catch(java.lang.reflect.InvocationTargetException ex){
            String errMesg = ex.getMessage();
            log.error(errMesg,ex);
            throw new IntegException(errMesg);
        } finally {
            DomainFrameworkEventManager.getInstance().fireBusy(false);
        }
    }

}
