package com.hydrologis.remarkable.progress.executor;
/**
  * Class to help printing to console or gui.
  */
public interface IProgressPrinter {
    /**
     * Publish a new progress update to the monitor.
     * 
     * @param update the update object.
     */
    void publish( ProgressUpdate update );

    /**
     * Finishes the progress.
     */
    void done();
}