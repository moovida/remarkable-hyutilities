package com.hydrologis.remarkable.progress;

import java.util.Iterator;
import java.util.Vector;

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
public class ProgressMonitor{ 
    int total, current=-1; 
    boolean indeterminate; 
    int milliSecondsToWait = 500; // half second 
    String status; 
 
    public ProgressMonitor(int total, boolean indeterminate, int milliSecondsToWait){ 
        this.total = total; 
        this.indeterminate = indeterminate; 
        this.milliSecondsToWait = milliSecondsToWait; 
    } 
 
    public ProgressMonitor(int total, boolean indeterminate){ 
        this.total = total; 
        this.indeterminate = indeterminate; 
    } 
 
    public int getTotal(){ 
        return total; 
    } 
 
    public void start(String status){ 
        if(current!=-1) 
            throw new IllegalStateException("not started yet"); 
        this.status = status; 
        current = 0; 
        fireChangeEvent(); 
    } 
 
    public int getMilliSecondsToWait(){ 
        return milliSecondsToWait; 
    } 
 
    public int getCurrent(){ 
        return current; 
    } 
 
    public String getStatus(){ 
        return status; 
    } 
 
    public boolean isIndeterminate(){ 
        return indeterminate; 
    } 
 
    public void setCurrent(String status, int current){ 
        if(current==-1) 
            throw new IllegalStateException("not started yet"); 
        this.current = current; 
        if(status!=null) 
            this.status = status; 
        fireChangeEvent(); 
    } 
 
    /*--------------------------------[ ListenerSupport ]--------------------------------*/ 
 
    private Vector listeners = new Vector(); 
    private ChangeEvent ce = new ChangeEvent(this); 
 
    public void addChangeListener(ChangeListener listener){ 
        listeners.add(listener); 
    } 
 
    public void removeChangeListener(ChangeListener listener){ 
        listeners.remove(listener); 
    } 
 
    private void fireChangeEvent(){ 
        Iterator iter = listeners.iterator(); 
        while(iter.hasNext()){ 
            ((ChangeListener)iter.next()).stateChanged(ce); 
        } 
    } 
}
