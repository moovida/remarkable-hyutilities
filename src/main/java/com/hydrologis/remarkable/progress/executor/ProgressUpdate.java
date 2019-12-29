package com.hydrologis.remarkable.progress.executor;
/**
 * Class to hold update messages and work done.
 */
public class ProgressUpdate {
    public String errorMessage;
    public String updateString;
    public int workDone;

    /**
     * Progress update with message and work done.
     *  
     * @param updateString message for teh progress monitor.
     * @param workDone the work done up to that point.
     */
    public ProgressUpdate( String updateString, int workDone ) {
        this.updateString = updateString;
        this.workDone = workDone;
    }

    /**
     * Progress update with error message.
     * 
     * @param errorMessage the error message.
     */
    public ProgressUpdate( String errorMessage ) {
        this.errorMessage = errorMessage;
    }
}