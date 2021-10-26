/*
 * DefaultersResult.java
 *
 * Created on October 14, 2005, 10:37 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.context;

import com.csgsystems.domain.framework.context.Context;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.IPersistentObjectFactory;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.bopt.*;
import com.csgsystems.localization.ResourceManager;
import com.maxis.integ.GenericUtil;

import javax.swing.JOptionPane;

/**
 * Context class for showing the DDMF query results
 * @author Pankaj Saini
 */
public class DefaultersResult extends Context {
    
    /** Creates a new instance of DefaultersResult */
    public DefaultersResult() {
    }
    
    public boolean open(IContext context) {
        boolean success = false;
        IPersistentObject pGenericObject = context.getObject("GenericDomain",null);
        if ( pGenericObject == null ) { 
            System.out.println("generic domain not found");
            return success; 
        }

        IPersistentObject acctSearch = context.getObject("MXSAccountSearchList",null);
        if ( acctSearch != null ) context.removeTopic(acctSearch);
        else System.out.println("MXSAccountSearchList not removed");
        
        IPersistentObject defaulters = PersistentObjectFactory.getFactory().createNew("DefaulterList",null);
        if ( defaulters == null ) { 
            System.out.println("defaulter list not found");
            return success; 
        }
        
        MultiKeyParams mkp = BoptFactory.getSystemParameters();
        String ddmfUrl = (String) mkp.get("CSR.DDMF_URL");
        String ddmfProtocol = (String) mkp.get("CSR.DDMF_PROTOCOL");
        String ddmfUserid = (String)mkp.get("CSR.DDMF_USERNAME");
        String ddmfPassword = (String)mkp.get("CSR.DDMF_PASSWORD");

        if ( ddmfUrl == null || ddmfUrl.length() == 0 ) { 
            logError("System parameter DDMF_URL module CSR not configured", null);
            setError("System parameter DDMF_URL module CSR not configured", new Object[0]);
            return success;
        }
        if ( ddmfProtocol == null || ddmfProtocol.length() == 0 ) { 
            logError("System parameter DDMF_PROTOCOL module CSR not configured", null);
            setError("System parameter DDMF_PROTOCOL module CSR not configured", new Object[0]);
            return success;
        } else if ( !(ddmfProtocol.toLowerCase().equals("http") || ddmfProtocol.toLowerCase().equals("https")) ) {
            logError("System parameter DDMF_PROTOCOL module CSR allowed values are http or https", null);
            setError("System parameter DDMF_PROTOCOL module CSR incorrectly configured", new Object[0]);
            return success;
        }
        if ( ddmfUserid == null || ddmfUserid.length() == 0 ) { 
            logError("System parameter DDMF_USERNAME module CSR not configured", null);
            setError("System parameter DDMF_USERNAME module CSR not configured", new Object[0]);
            return success;
        }
        if ( ddmfPassword == null || ddmfPassword.length() == 0 ) {
            logError("System parameter DDMF_PASSWORD module CSR not configured", null);
            setError("System parameter DDMF_PASSWORD module CSR not configured", new Object[0]);
            return success;
        }
        
        defaulters.setAttributeDataAsString("URL", ddmfUrl);
        defaulters.setAttributeDataAsString("Protocol", ddmfProtocol);
        defaulters.setAttributeDataAsString("Username", ddmfUserid);
        defaulters.setAttributeDataAsString("Password", GenericUtil.decode(ddmfPassword));
//        defaulters.setAttributeDataAsString("Password", ddmfPassword);
        
        if ( defaulters.addAssociation(pGenericObject) ) {
            ((IPersistentCollection)defaulters).setFaulted(false);
            addTopic(defaulters, true); // read only
        } else return success;

            ((IPersistentCollection)defaulters).setFaulted(false);
            addTopic(defaulters, true); // read only
        success = true;
        return success;
    }
    
    public boolean processShutdown(int shutdownType) {
        boolean success = true;
        if ( shutdownType == IContext.NEXT ) {
            IPersistentCollection ddmfResults = (IPersistentCollection)getObject("DefaulterList");
            if ( ddmfResults != null && ddmfResults.getCount() > 0 ) {
                JOptionPane.showMessageDialog(null, ResourceManager.getString("DDMF.checkfail.text.Warning"), 
                        ResourceManager.getString("DDMF.checkfail.title.Warning"), JOptionPane.WARNING_MESSAGE);
            }
        }
        
        return success;
    }
}
