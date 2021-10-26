/*
 * MXSLauncherForm.java
 *
 * Created on February 13, 2006, 2:56 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.util;

import com.csgsystems.igpa.forms.CSGLauncherForm;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Frame;

/**
 *
 * @author Pankaj Saini
 */
public class MXSLauncherForm extends CSGLauncherForm {
    
    /** Creates a new instance of MXSLauncherForm */
    public MXSLauncherForm() {
        Frame f = (Frame) this.getParent();
        f.addWindowListener(new WindowAdapter () {
            public void windowClosing(WindowEvent windowevent)
            {
                System.out.println("window closing event fired");
                return;
            }  
        });
    }
    
    public boolean canClose() {
        //updateBigFoot();
        return false;
    }
}
