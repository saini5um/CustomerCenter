/*
 * Pre2PostNewAccount.java
 *
 * Created on November 9, 2005, 4:12 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csgsystems.domain.framework.context.Context;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.context.IContext;

/**
 *
 * @author Pankaj Saini
 */
public class Pre2PostNewAccount extends Context {
    
    private static Log log = null;

    static {
        try {
            log = LogFactory.getLog(Pre2PostNewAccount.class);
        }
        catch (Exception ex) {
        }
    }
    
    public boolean open(IContext context) {
        
        // For this context, regardless of what comes in the context we will create a new account
        IPersistentObject account = context.getObject("Account", null);
        if (account == null) {
            account = poObjectFactory.createNew("Account", null);
        }
        if (account != null) {
            addTopic(account);
        } else {
            logError("Could not Create Account", null);
            return false;
        }

        return true;
    }
           
    /**
     * Allows for the testing of the context before being opened by the workflow to see
     * if the context can be opened.  Used by BeginWizard to see if a topicmark needs to
     * be set on the application context.
     * 
     * @param map
     * @return
     *  true if the context will open when the workflow opens it.
     */
    public boolean canBeOpened(Map map) {
        boolean success = true;        
        
        return success;
    }
    
    /**
     *  Description of the Method
     *
     * @param  resp  Description of Parameter
     * @return       Description of the Returned Value
     */
    public boolean processShutdown(int shutdownType) {
        boolean success = true;
        
        if (shutdownType == NEXT ||
            shutdownType == FINISH) {
    		IPersistentObject account = getObject("Account",null);            
            if (!account.sendMessage("validateParentAccount", null)) {            	
                addError(account.getError());
                success = false;
            }
        } 
	
		return success;
    }      
}
