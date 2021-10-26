/*
 * ErrorReporting.java
 *
 * Created on May 12, 2005, 9:25 AM
 * Edited Date          By              Purpose
 * 2005-06-03           kinyip          change to singleton pattern
 *                                      simplify method calling
 */

package com.maxis.util;

import javax.swing.JOptionPane;
import com.csgsystems.domain.framework.context.IContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  Pankaj Saini
 */
public class ErrorReporting {
    
    private static Log log;
    private static ErrorReporting errReport;

    /** Creates a new instance of ErrorReporting */
    private ErrorReporting() {
    }
    
    public static ErrorReporting getInstance() {
        if (errReport != null) {
            return errReport;
        } else {
            errReport = new ErrorReporting();
            return errReport;
        }
    }
    
    /////////////////////////////////////////////////
    //    GENERAL LOGGING - no dialog box for user
    /////////////////////////////////////////////////
    public static void logFatalToFile(String msg, Class errClass) {
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.fatal(msg);
        }
    }
    
    public static void logErrorToFile(String msg, Class errClass) {
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.error(msg);
        }
    }
    
    public static void logInfoToFile(String msg, Class errClass) {
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.info(msg);
            if (log.isInfoEnabled() == true)
                System.out.println(msg);            
        }
    }
    
    public static void logWarnToFile(String msg, Class errClass) {
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.warn(msg);
            if (log.isWarnEnabled() == true)
                System.out.println(msg);
        }
    }
    
    public static void logDebugToFile(String msg, Class errClass) {
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.debug(msg);
            if (log.isDebugEnabled() == true)
                System.out.println(msg);
        }
    }
    

    //////////////////////////////////////////////////////
    //    ACTIONS LOGGING - includes dialog box for user
    //////////////////////////////////////////////////////
    
    public static void logError(String msg, String title, Class errClass) {
        JOptionPane.showMessageDialog(null,msg,title,JOptionPane.ERROR_MESSAGE);
        logErrorToFile(msg, errClass);
        /*
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.error(msg);
        }
         */
    }
    
    public static void logWarn(String msg, String title, Class errClass) {
        JOptionPane.showMessageDialog(null,msg,title,JOptionPane.WARNING_MESSAGE);
        logWarnToFile(msg, errClass);
        /*
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.warn(msg);
            if (log.isWarnEnabled() == true)
                System.out.println(msg);            
        }
         */
    }
    
    public static void logInfo(String msg, String title, Class errClass) {
        JOptionPane.showMessageDialog(null,msg,title,JOptionPane.INFORMATION_MESSAGE);
        logInfoToFile(msg, errClass);
        /*
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.info(msg);
            if (log.isInfoEnabled() == true)
                System.out.println(msg);
        }
         */
    }
    
    public static void logFatal(String msg, String title, Class errClass) {
        JOptionPane.showMessageDialog(null,msg,title,JOptionPane.ERROR_MESSAGE);
        logFatalToFile(msg, errClass);
        /*
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.fatal(msg);
        }
         */
    }
    
    public static void logDebug(String msg, String title, Class errClass) {
        JOptionPane.showMessageDialog(null,"DEBUG Message: " + msg,title,JOptionPane.PLAIN_MESSAGE);
        logDebugToFile(msg, errClass);
        /*
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.debug(msg);
            if (log.isDebugEnabled() == true)
                System.out.println(msg);
        }
         */
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    //    PANEL LOGGING - includes HTML dialog box for user (if context picks up error)
    /////////////////////////////////////////////////////////////////////////////////////
    
    public static void logPanelError(String msg, IContext ctx, Class errClass) {
        ctx.setError("<HTML>" + msg + "</HTML>",null);
        logErrorToFile(msg, errClass);
        /*
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.error(msg);
        }
         */
    }
    
    public static void logPanelFatal(String msg, IContext ctx, Class errClass) {
        ctx.setError("<HTML>" + msg + "</HTML>",null);
        logFatalToFile(msg, errClass);
        /*
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.fatal(msg);
        }
         */
    }
    
    public static void logPanelWarn(String msg, IContext ctx, Class errClass) {
        ctx.setError("<HTML>" + msg + "</HTML>",null);
        logWarnToFile(msg, errClass);
        /*
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.warn(msg);
            if (log.isWarnEnabled() == true)
                System.out.println(msg);
        }
         */
    }
    
    public static void logPanelInfo(String msg, IContext ctx, Class errClass) {
        ctx.setError("<HTML>" + msg + "</HTML>",null);
        logInfoToFile(msg, errClass);
        /*
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.info(msg);
            if (log.isInfoEnabled() == true)
                System.out.println(msg);
        }
         */
    }
    
    public static void logPanelDebug(String msg, IContext ctx, Class errClass) {
        ctx.setError("<HTML>" + msg + "</HTML>",null);
        logDebugToFile(msg, errClass);
        /*
        logGeneral(msg, errClass);
        if ( log != null ) { 
            log.debug(msg);
            if (log.isDebugEnabled() == true)
                System.out.println(msg);
        }
         */
    }

    ////////////////////////////////////////////////////
    private static void logGeneral(String msg, Class errClass) {
        try {
            log = LogFactory.getLog(errClass);
        } catch (Exception ex) {
            System.err.println("Failed to get Log");
        }
    }
}
