// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.maxis.action;

import com.csgsystems.domain.framework.IPersistentObjectFactory;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.attribute.Attribute;
import com.csgsystems.domain.framework.attribute.IEnumeration;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.workflow.BaseWorkflowAction;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

/*
* We need to aviod loading too much Bopts, 
* 1) The Order object will cause to load the default sales channel which will be abandone later in the program
*    The sales channel bopt in the order consumes 5MB+ of ram and it takes 5-10 second to load it.
* 2) When all these Bopt finish loading, its almost full at 37 object. when search and enter an account, some of these
*    bopt will be flush and reloaded because its cached based on LRU. 
* 3) not loading extended data bopt, as its not used as frequently.
*/
public class MXSLoadBopts extends BaseWorkflowAction{

    public final void execute(ActionEvent actionevent, IContext icontext){
        System.out.println("custom load bopts called");
        IPersistentObject ipersistentobject = PersistentObjectFactory.getFactory().create("Account", null);
        loadObjectBopts(ipersistentobject);
        IPersistentObject ipersistentobject1 = PersistentObjectFactory.getFactory().create("Service", null);
        loadObjectBopts(ipersistentobject1);
        //IPersistentObject ipersistentobject2 = PersistentObjectFactory.getFactory().create("Order", null);
        //loadObjectBopts(ipersistentobject2);
        //IPersistentObject ipersistentobject3 = PersistentObjectFactory.getFactory().create("ServiceOrder", null);
        //loadObjectBopts(ipersistentobject3);
        //IPersistentObject ipersistentobject4 = PersistentObjectFactory.getFactory().create("Item", null);
        //loadObjectBopts(ipersistentobject4);
        
        java.awt.Frame frame = null;
        java.awt.Frame aframe[] = java.awt.Frame.getFrames();
        int i = 0;
        do{
            if(i >= aframe.length)
                break;
            if(aframe[i] instanceof com.csgsystems.igpa.IActionFrame)
            {
                frame = aframe[i];
                break;
            }
            i++;
        } while(true);
        if(frame!=null){
            System.out.println("found the frame!");
            WindowListener wl[] = frame.getWindowListeners();
            for ( int j = 0; j < wl.length; j++ ) {
                System.out.println("listener " + j + " " + wl[j].getClass().getName());
                frame.removeWindowListener(wl[j]);
            }
            frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent windowevent) {
                JOptionPane.showMessageDialog(null, "Please use the Exit button on the main screen to close Customer Center");
            }

            });
	        IPersistentObject LoggedInUser = com.csgsystems.domain.framework.SingletonManager.getObject("LoggedInUser", null);
	        if(LoggedInUser!=null){
		        IPersistentObject Application = LoggedInUser.getObject("Application", "LoggedInUser");
		        if(Application!=null){
			        String VersionNumber = Application.getAttributeDataAsString("VersionNumber");
			        if(VersionNumber!=null){
				        frame.setTitle(frame.getTitle()+" ("+VersionNumber+")");
			        }
		        }
	        }
        }
        
        fireEvent("next");
    }

    private void loadObjectBopts(IPersistentObject ipersistentobject){
        Iterator iterator = ipersistentobject.getAttributes().iterator();
        do{
            if(!iterator.hasNext())
                break;
            Attribute attribute = (Attribute)iterator.next();
            if(attribute.isEnumerated()&&!attribute.isExtended())
                attribute.getEnumerationInterface().getNumberOfRows();
        } while(true);
    }
}
