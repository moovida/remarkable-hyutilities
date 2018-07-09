package com.hydrologis.remarkable.progress;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** 
 * MySwing: Advanced Swing Utilites 
 * Copyright (C) 2005  Santhosh Kumar T 
 * <p/> 
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version. 
 * <p/> 
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
 * Lesser General Public License for more details. 
 */ 
public class ProgressDialog extends JDialog implements ChangeListener{ 
    JLabel statusLabel = new JLabel(); 
    JProgressBar progressBar = new JProgressBar(); 
    ProgressMonitor monitor; 
 
    public ProgressDialog(Frame owner, ProgressMonitor monitor) throws HeadlessException{ 
        super(owner, "Progress", true); 
        init(monitor); 
    } 
 
    public ProgressDialog(Dialog owner, ProgressMonitor monitor) throws HeadlessException{ 
        super(owner); 
        init(monitor); 
    } 
 
    private void init(ProgressMonitor monitor){ 
        this.monitor = monitor; 
 
        progressBar = new JProgressBar(0, monitor.getTotal()); 
        if(monitor.isIndeterminate()) 
            progressBar.setIndeterminate(true); 
        else 
            progressBar.setValue(monitor.getCurrent()); 
        statusLabel.setText(monitor.getStatus()); 
 
        JPanel contents = (JPanel)getContentPane(); 
        contents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        contents.add(statusLabel, BorderLayout.NORTH); 
        contents.add(progressBar); 
 
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
        monitor.addChangeListener(this); 
    } 
 
    public void stateChanged(final ChangeEvent ce){ 
        // to ensure EDT thread 
        if(!SwingUtilities.isEventDispatchThread()){ 
            SwingUtilities.invokeLater(new Runnable(){ 
                public void run(){ 
                    stateChanged(ce); 
                } 
            }); 
            return; 
        } 
 
        if(monitor.getCurrent()!=monitor.getTotal()){ 
            statusLabel.setText(monitor.getStatus()); 
            if(!monitor.isIndeterminate()) 
                progressBar.setValue(monitor.getCurrent()); 
        }else 
            dispose(); 
    } 
} 
