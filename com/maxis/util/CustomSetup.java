package com.maxis.util;

import com.csgsystems.fxframe.setup.*;
import com.csgsystems.configuration.ConfigurationManager;
import com.csgsystems.igpa.IActionFrame;
import com.csgsystems.transport.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

public class CustomSetup extends FrameSetupBase implements ServiceCallListener {
    Calendar lastCall;
    String timeoutParam;
    long timeoutLimit;

    public CustomSetup(){
    }

    public void initialize(IActionFrame iactionframe) throws FrameSetupException {
        timeoutParam = ConfigurationManager.getInstance().getString("session-timeout");
        if ( timeoutParam == null ) return;
        
        lastCall = Calendar.getInstance();
        System.out.println("initialize custom setup!");
        try {
            //System.out.println("CustomSetup started");
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
        System.out.println("service call interrupt by custom code");
        lastCall = Calendar.getInstance();
    }
    
    public void doDisp() {
        System.out.println("started new thread");
        
        while ( true ) {
            System.out.println("thread sleeping");
            try { Thread.sleep(10000); }
            catch(Exception ex) {}
            System.out.println("thread wokeup");
            Calendar now = Calendar.getInstance();
            long timeElapsed =  now.getTimeInMillis() - lastCall.getTimeInMillis();
            if ( timeElapsed  > timeoutLimit*1000 ) {
                System.out.println("time difference = " + timeElapsed );
                JOptionPane.showMessageDialog(null, "Your session will timeout!", "Alert!", JOptionPane.WARNING_MESSAGE);
                System.out.println("timeout exceeded!");
            }
        }
    }	    
}