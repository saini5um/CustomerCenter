/*
 * DefaultersResult.java
 *
 * Created on October 14, 2005, 2:41 PM
 */

package com.maxis.panel;

import com.csgsystems.bp.utilities.TemplateUtility;
import com.csgsystems.igpa.utils.ContextFinder;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.igpa.forms.*;
import com.csgsystems.domain.framework.security.SecurityManager;

/**
 * Panel class to display the DDMF query results
 * @author  Pankaj Saini
 */
public class DefaultersResult extends javax.swing.JPanel implements ContextFormListener {
    private ContextFinder ctxFinder = new ContextFinder(this);
    private boolean fraud = false;
    
    /** Creates new form DefaultersResult */
    public DefaultersResult() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        velDDMFQueryResults = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();

        setLayout(new java.awt.BorderLayout());

        setPreferredSize(new java.awt.Dimension(900, 620));

        velDDMFQueryResults.setVelocityTemplateUri("template/MXSDDMFQueryResults.vm");
        add(velDDMFQueryResults, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents
    
    public void contextFormStateChanged(ContextFormEvent evt){
        if (evt.getType() == ContextFormEvent.READY){       
            IContext ctx = ctxFinder.findContext();
            IPersistentCollection ddmfResults = (IPersistentCollection)ctx.getObject("DefaulterList");
            
            SecurityManager securityManager = SecurityManager.getInstance();
            boolean nonMasa = false;
            try {
                if (securityManager.isAuthorized("Maxis.NonMasa", SecurityManager.SERVICE_RSRC)) nonMasa = true;
            } catch (Exception e) {
                    System.out.println("Error checking authorization");
            }
            if ( ddmfResults != null ) {
                int defaulterCount = ddmfResults.getCount();
                if ( defaulterCount > 0 && nonMasa == false ) 
                    ctxFinder.enableButton(ICSGContextForm.NEXT_BUTTON, false);
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velDDMFQueryResults;
    // End of variables declaration//GEN-END:variables
    
}
