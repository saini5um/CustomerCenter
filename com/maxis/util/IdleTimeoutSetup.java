package com.maxis.util;

import com.csgsystems.fxframe.setup.*;
import com.csgsystems.configuration.ConfigurationManager;
import com.csgsystems.igpa.IActionFrame;
import com.csgsystems.transport.*;
import com.csgsystems.localization.ResourceManager;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class IdleTimeoutSetup extends FrameSetupBase implements ServiceCallListener {
    Calendar lastCall;
    String timeoutParam;
    long timeoutLimit;
    final long threadWakeupInterval = 30000;
    IActionFrame topFrame = null;

    public IdleTimeoutSetup(){
    }

    public void initialize(IActionFrame iactionframe) throws FrameSetupException {
        topFrame = iactionframe;
        timeoutParam = ConfigurationManager.getInstance().getString("session-timeout");
        if ( timeoutParam == null ) return;
        
        lastCall = Calendar.getInstance();
        try {
            Thread t1 = new Thread() {
                public void run(){
                    doDisp();
                }
            };
            t1.start();
        } catch(Exception exception) {
            logError("Error initializing CustomSetup", exception);
            throw new FrameSetupException("Error initializing CustomSetup", exception);
        }
        
        try {
            timeoutLimit = Long.parseLong(timeoutParam);
            if ( timeoutLimit > 0 )
                ModelAdapterFactory.getModelAdapter().addServiceCallListener(this);
        } catch (NumberFormatException e) {
            logError("parameter session-timeout incorrectly set", e);
            throw new FrameSetupException("session-timeout should be a positive number!", e);            
        }
    }
    
    public void serviceCalled(ServiceCallEvent servicecallevent) {
        lastCall = Calendar.getInstance();
    }
    
    public void doDisp() {
//        boolean timedout = false;
        while ( true ) {
            try { Thread.sleep(threadWakeupInterval); }
            catch (Exception ex) {}
            Calendar now = Calendar.getInstance();
            long timeElapsed =  now.getTimeInMillis() - lastCall.getTimeInMillis();
//            if ( timeElapsed  > timeoutLimit*1000 && timedout == false ) {
            if ( timeElapsed  > timeoutLimit*1000 ) {
//                timedout = true;
                JFrame jf = new JFrame("Alert!");
                JPanel jp = new JPanel();
                final JOptionPane msgbox = new JOptionPane(ResourceManager.getString("idletimeout.warning.message"), 
                        JOptionPane.WARNING_MESSAGE);
                JLabel jl = new JLabel();
                jp.setLayout(null);
                jp.setPreferredSize(new java.awt.Dimension(200, 100));
                jp.setSize(new java.awt.Dimension(200, 100));
                jl.setText(ResourceManager.getString("idletimeout.warning.message"));
                jp.add(jl);
                jf.getContentPane().add(msgbox);
                jf.pack();
                jf.setSize(msgbox.getSize());
                jf.setLocationRelativeTo(topFrame.getApplicationFrame());
                jf.setVisible(true);
//                jf.setDefaultCloseOperation(jf.DO_NOTHING_ON_CLOSE);
/*                jf.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
//                        timedout = false;
                    }
                }); */
//                topFrame.getApplicationFrame().setEnabled(true);
//                topFrame.displayMessageBox(ResourceManager.getString("idletimeout.warning.message"), null, IActionFrame.WARNING_MESSAGE);
//                JOptionPane.showMessageDialog(null, ResourceManager.getString("idletimeout.warning.message"), 
//                        "Alert!", JOptionPane.WARNING_MESSAGE);
            }
        }
    }	    
}