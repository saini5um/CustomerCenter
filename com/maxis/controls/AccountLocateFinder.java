// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) nonlb 

package com.maxis.controls;

import com.csgsystems.domain.framework.context.*;
import com.csgsystems.igpa.controls.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class AccountLocateFinder extends JPanel
    implements ICSGDataBoundControl, ContextListener {

    public AccountLocateFinder() {
        canEnableEdit = false;
        initComponents();
    }

    private void initComponents() {
        btnLocateAccount = new CSGEventButton();
        edtAccount = new CSGEdit();
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(50, 20));
        setPreferredSize(new Dimension(150, 20));
        btnLocateAccount.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Find16.gif")));
        btnLocateAccount.setEventAction("account-locate-finder-dlg");
        btnLocateAccount.setMaximumSize(new Dimension(20, 20));
        btnLocateAccount.setMinimumSize(new Dimension(20, 20));
        btnLocateAccount.setPreferredSize(new Dimension(20, 20));
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridx = 1;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.fill = 3;
        gridbagconstraints.insets = new Insets(0, 0, 0, 0);
        gridbagconstraints.weighty = 1.0D;
        add(btnLocateAccount, gridbagconstraints);
        gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.fill = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        add(edtAccount, gridbagconstraints);
    }

    public IContext getContext() {
        return edtAccount.getContext();
    }

    public void setContext(IContext icontext) {
        edtAccount.setContext(icontext);
    }

    public String getDomainName() {
        return edtAccount.getDomainName();
    }

    public void setDomainName(String s) {
        edtAccount.setDomainName(s);
    }

    public String getDomainSubtype() {
        return edtAccount.getDomainSubtype();
    }

    public void setDomainSubtype(String s) {
        edtAccount.setDomainSubtype(s);
    }

    public String getAttributeName() {
        return edtAccount.getAttributeName();
    }

    public void setAttributeName(String s) {
        edtAccount.setAttributeName(s);
    }

    public boolean isContextObserver() {
        return edtAccount.isContextObserver();
    }

    public void setContextObserver(boolean flag) {
        edtAccount.setContextObserver(flag);
    }

    public void initializeControl() {
        edtAccount.initializeControl();
        edtAccount.setEnabled(false);
    }

    public void contextChanged(ContextEvent contextevent) {
        edtAccount.contextChanged(contextevent);
    }

    public void setEventAction(String s) {
        btnLocateAccount.setEventAction(s);
    }

    public String getEventAction() {
        return btnLocateAccount.getEventAction();
    }

    public void setBigfootObject(String s) {
        btnLocateAccount.setBigfootObject(s);
    }

    public String getBigfootObject() {
        return btnLocateAccount.getBigfootObject();
    }

    public void setBigfootObjectSubtype(String s) {
        btnLocateAccount.setBigfootObjectSubtype(s);
    }

    public String getBigfootObjectSubtype() {
        return btnLocateAccount.getBigfootObjectSubtype();
    }

    public void addActionListener(ActionListener actionlistener) {
        btnLocateAccount.addActionListener(actionlistener);
    }

    public ActionListener[] getActionListeners() {
        return btnLocateAccount.getActionListeners();
    }

    public void removeActionListener(ActionListener actionlistener) {
        btnLocateAccount.removeActionListener(actionlistener);
    }

    public void addBoundDataChangeListener(BoundDataChangeListener bounddatachangelistener) {
        edtAccount.addBoundDataChangeListener(bounddatachangelistener);
    }

    public void setEditModifyable() {
        canEnableEdit = true;
        edtAccount.setEnabled(btnLocateAccount.isEnabled());
    }

    public void setEnabled(boolean flag) {
        btnLocateAccount.setEnabled(flag);
        if(canEnableEdit)
            edtAccount.setEnabled(flag);
        else
            edtAccount.setEnabled(false);
    }

    private boolean canEnableEdit;
    private CSGEdit edtAccount;
    private CSGEventButton btnLocateAccount;
}
