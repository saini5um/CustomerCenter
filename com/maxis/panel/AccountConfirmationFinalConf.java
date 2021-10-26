/*
 * OrderConfirmation.java
 *
 * Created on November 23, 2002, 11:48 AM
 */

package com.maxis.panel;
import javax.swing.event.*;
import javax.swing.text.Element;
import java.awt.event.*;
import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csgsystems.localization.ResourceManager;
import com.csgsystems.bp.utilities.TemplateUtility;
import java.util.*;

/**
 *
 * @author  prev01
 */
public class AccountConfirmationFinalConf extends AbstractHTMLPanel {
    protected JPopupMenu accountActionsMenu = null;
    protected JPopupMenu serviceActionsMenu = null;    
    protected String url = null;
    protected int xpos = 0;
    protected int ypos = 0;
    
    protected Log log = LogFactory.getLog( AccountConfirmationFinalConf.class );
    
    /** Creates new form OrderConfirmation */
    public AccountConfirmationFinalConf() {
        initComponents();
        // create Account actions menu
        createAccountActionsMenu();

        // create Account actions menu
        createServiceActionsMenu();

        velAccountConfirmation.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(java.awt.event.MouseEvent evt) {
                    int containerX = new Double(((JEditorPane)evt.getSource()).getBounds().getX()).intValue();
                    int containerY = new Double(((JEditorPane)evt.getSource()).getBounds().getY()).intValue();     
                    xpos = evt.getX() + containerX;
                    ypos = evt.getY() + containerY;
                }
            });  
                
        velAccountConfirmation.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt){
                String newURL = null;
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){  
                    // parse the url to get the type
                    url = evt.getURL().toString();
                    HashMap params = parseURLtoMap(url);    
                    String linktype = "normal";
                    if (params.get("linktype")!=null){
                        linktype = (String) params.get("linktype");
                    } 

                    String type = "normal";
                    if( params.get( "type" ) != null ) {
                        type = (String) params.get( "type" );
                    }
                    
                    //display popup menu with account detail, add charges, etc...                  
                    if (linktype.equals("account-actions-popup-menu")){
                        accountActionsMenu.show(velAccountConfirmation, xpos, ypos);
                    }else if(linktype.equals("service-actions-popup-menu")){
                        serviceActionsMenu.show(velAccountConfirmation, xpos, ypos);
                    }else if( type.equals( "remove" ) ) {
                        NewCustAcq_ServiceAdd_RemoveHyperlinkFilter( evt, velAccountConfirmation );
//                        handleRemoveLink( evt );     
//                        velAccountConfirmation.initializeControl();                   
                    }else{                    
                        DetailHyperlinkFilter(evt, velAccountConfirmation, newURL);
                    }
                }else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED){
                    Element element = evt.getSourceElement(); 
                    
                    // parse the url to get the type
                    url = evt.getURL().toString();
                    HashMap params = parseURLtoMap(url);    
                    String linktype = "normal";
                    if (params.get("linktype")!=null){
                        linktype = (String) params.get("linktype");
                    } 

                    //display popup menu with billing, orders, services menues         
                    if (linktype.equals("account-actions-popup-menu")){
                        accountActionsMenu.show(velAccountConfirmation, xpos, ypos);
                    }else if(linktype.equals("service-actions-popup-menu")){
                        serviceActionsMenu.show(velAccountConfirmation, xpos, ypos);
                    }
                }else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED){
                    serviceActionsMenu.setVisible(false);  
                    accountActionsMenu.setVisible(false);    
                } 
            }
        });

        velAccountConfirmation.addExtraVelocityContextTopic("TemplateUtility", new TemplateUtility());
    }
    
    private void createAccountActionsMenu() {
        accountActionsMenu = new JPopupMenu();
        
        // disconnect
        JMenuItem menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.AccountDetails") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&action=new-cust-acq-account-detail-dlg&object=Account");
            }
        });
        accountActionsMenu.add(menuItem);
        
        accountActionsMenu.addSeparator(); 
/*
        menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.Quote") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&object=Account&action=quote-dlg");
            }
        });
        accountActionsMenu.add(menuItem);

        accountActionsMenu.addSeparator(); 
*/        
        menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.AddExternalId") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&type=add&object=AccountId&action=newacct-externalId-dlg");
            }
        });
        accountActionsMenu.add(menuItem);        
/*        
        menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.AddDeposit") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&action=new-cust-acq-account-deposit-create-dlg&object=Account");
            }
        });
        accountActionsMenu.add(menuItem);     
*/
        menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.AddChargeRule") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&action=new-cust-acq-account-openitemidmap-create-dlg&object=Account");
            }
        });
        accountActionsMenu.add(menuItem);      
/*        
        menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.AddCorridorPlan") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&action=new-cust-acq-account-corridor-create-dlg&object=Account");
            }
        });
        accountActionsMenu.add(menuItem);   

        menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.AddPayment") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&action=new-cust-acq-account-payment-create-dlg&object=Account");
            }
        });
        accountActionsMenu.add(menuItem);   

        menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.AddPrepaidBalance") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&action=new-cust-acq-prepaid-balance-create-dlg&object=Account");
            }
        });
        accountActionsMenu.add(menuItem);   

        menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.AddPrepayment") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&action=new-cust-acq-prepayment-add-dlg&object=PrepaymentList");
            }
        });
        accountActionsMenu.add(menuItem);           */
    }        

    private void createServiceActionsMenu() {
        serviceActionsMenu = new JPopupMenu();
        
        // disconnect
        JMenuItem menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.ServiceDetails") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&action=new-cust-acq-service-detail-dlg&object=Service&subtype=Account");
            }
        });
        serviceActionsMenu.add(menuItem);
        
        serviceActionsMenu.addSeparator(); 

        menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.AddExternalId") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&action=new-cust-acq-service-externalid-create-dlg&object=Service&subtype=Account");
            }
        });
        serviceActionsMenu.add(menuItem); 

        menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.AddChargeRule") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&action=new-cust-acq-service-openitemidmap-create-dlg&object=Service&subtype=Account");
            }
        });
        serviceActionsMenu.add(menuItem);      
        
        menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.AddCorridorPlan") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&action=new-cust-acq-service-corridor-create-dlg&object=Service&subtype=Account");
            }
        });
        serviceActionsMenu.add(menuItem);    

        menuItem = new JMenuItem( ResourceManager.getString("AccountConfirmationFinalConf.menuitem.AddZone") );       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DetailHyperlinkFilter(null,velAccountConfirmation, url + "&action=new-cust-acq-service-zone-create-dlg&object=Service&subtype=Account");
            }
        });
        serviceActionsMenu.add(menuItem);           
    }        

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        velAccountConfirmation = new com.csgsystems.igpa.controls.CSGVelocityHTMLEP();

        setLayout(new java.awt.BorderLayout());

        setPreferredSize(new java.awt.Dimension(650, 530));

        velAccountConfirmation.setVelocityTemplateUri("template/NewCustAcqAccountFinalConfSummary.vm");
        add(velAccountConfirmation, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.csgsystems.igpa.controls.CSGVelocityHTMLEP velAccountConfirmation;
    // End of variables declaration//GEN-END:variables
    
    
    
    
}
