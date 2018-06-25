package com.hydrologis.remarkable.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class RunnableWithParameters implements Runnable {
    protected List parameters = null;
    protected Object returnValue;

    public RunnableWithParameters() {
        this.parameters = null;
    }

    public RunnableWithParameters( Object[] parameters ) {
        this.parameters = Arrays.asList(parameters);
    }

    public void setParameters( Object[] parameters ) {
        this.parameters = Arrays.asList(parameters);
    }

    public List getParameters() {
        if (this.parameters == null) {
            this.parameters = new ArrayList();
        }
        return parameters;
    }

    public RunnableWithParameters add( Object paramter ) {
        this.getParameters().add(paramter);
        return this;
    }

    public Object get( int index ) {
        return this.parameters.get(index);
    }

    public Object getReturnValue() {
        return this.returnValue;
    }
}
