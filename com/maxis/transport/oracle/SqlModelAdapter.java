/*
 * SqlModelAdapter.java
 *
 * Created on November 3, 2005, 11:06 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.transport.oracle;

import com.csgsystems.service.*;
import com.csgsystems.transport.*;
import com.csgsystems.service.python.PythonServiceResponse;
import com.csgsystems.marshal.python.DictionaryPrettyPrinter;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.sql.Connection;

/**
 *
 * @author Pankaj Saini
 */
public class SqlModelAdapter extends BaseModelAdapter {
    
    /** Creates a new instance of SqlModelAdapter */
    public SqlModelAdapter() {
    }
    
    private static Log log = null;
    protected Connection dbConn;
    protected String name;
    protected String sql;

    static  {
        try {
            log = LogFactory.getLog(com.csgsystems.transport.tuxedo.PythonTuxedoModelAdapter.class);
        }
        catch(Exception ex) { }
    }
    
    public void openConnection(URL connURL) throws MalformedURLException, IOException {
        
    }
    
    public void closeConnection() {
        
    }
    
    public IServiceResponse executeService(IServiceRequest request) throws ServiceException {
        IServiceResponse sr = null;
        String serviceName = request.getServiceName();
        logDebug("Making tuxedo service call: " + serviceName);
        byte ba[] = request.getByteArray();

        if(log != null && log.isDebugEnabled())
            try {
                logDebug("Length of the data is: " + ba.length);
                StringWriter sw = new StringWriter();
                DictionaryPrettyPrinter dpp = new DictionaryPrettyPrinter(ba);
                dpp.prettyPrint(new PrintWriter(sw));
                sw.close();
                logDebug(sw.toString());
            }
            catch(Exception ex) {
                logError("An error while executing the service. ", ex);
            }

//        sr = new PythonServiceResponse(request.getByteArray());
        byte resp[] = new FileAdapter(serviceName).getResponse();
        if ( resp == null ) sr = new PythonServiceResponse(request.getByteArray());
        else sr = new PythonServiceResponse(resp);
/*        if(log != null && log.isDebugEnabled())
            try {
                logDebug("Length of the data is: " + ba.length);
                StringWriter sw = new StringWriter();
                DictionaryPrettyPrinter dpp = new DictionaryPrettyPrinter(resp);
                dpp.prettyPrint(new PrintWriter(sw));
                sw.close();
                logDebug(sw.toString());
            }
            catch(Exception ex) {
                logError("An error while executing the service. ", ex);
            }
*/
        return sr;
    }    

    private static void logDebug(String msg) {
        if(log != null)
            log.debug(msg);
    }

    private static void logInfo(String msg) {
        if(log != null)
            log.info(msg);
    }

    private static void logWarn(String msg, Throwable ex) {
        if(log != null)
            log.warn(msg, ex);
    }

    private static void logError(String msg, Throwable ex) {
        if(log != null)
            log.error(msg, ex);
    }

}
