/*
 * This file is part of HortonMachine (http://www.hortonmachine.org)
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * The HortonMachine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hydrologis.remarkable.utils;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

/**
 * A progress dialog for long background jobs.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public abstract class HMProgressMonitorDialog implements PropertyChangeListener {

    protected ProgressMonitor progressMonitor;
    protected BackgroundTask task;
    private Component parent;

    protected StringBuilder finalInfoSb = new StringBuilder();
    protected StringBuilder finalErrorSb = new StringBuilder();

    class BackgroundTask extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
            try {
                processInBackground();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void done() {
            progressMonitor.close();

            if (finalErrorSb.length() > 0) {
                JOptionPane.showMessageDialog(parent, finalErrorSb.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
            } else if (finalInfoSb.length() > 0) {
                JOptionPane.showMessageDialog(parent, finalInfoSb.toString(), "INFO", JOptionPane.INFORMATION_MESSAGE);
            }

            postDoneInUi();
        }
    }

    /**
     * Create the monitor dialog.
     * 
     * @param parent the parent component.
     * @param title the title of the monitor dialog.
     * @param workLoad the maximum work to do.
     */
    public HMProgressMonitorDialog( Component parent, String title, int workLoad ) {
        this.parent = parent;
        progressMonitor = new ProgressMonitor(parent, title, "", 0, workLoad);
        progressMonitor.setProgress(0);
    }

    /**
     * Start the job with monitor dialog.
     */
    public void run() {
        task = new BackgroundTask();
        task.addPropertyChangeListener(this);
        task.execute();
    }

    /**
     * Method inside which the job has to be done in background.
     * 
     * <ul>
     * <li>update progress bar with <code>progressMonitor.setProgress(0);</code></li>
     * <li>update progress text with <code>setProgressText("My message");</code></li>
     * </ul>
     * 
     * @throws Exception
     */
    public abstract void processInBackground() throws Exception;

    /**
     * Execute something after the progress has finished in the UI thread.
     */
    public abstract void postDoneInUi();

    protected boolean isCancelled() {
        return task.isCancelled();
    }

    public void setProgressText( String text ) {
        task.firePropertyChange("progressText", "", text);
    }

    public void setProgress( int progress ) {
        progressMonitor.setProgress(progress);
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange( PropertyChangeEvent evt ) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressMonitor.setProgress(progress);
            // String message = String.format("Completed %d%%.\n", progress);
            // progressMonitor.setNote(message);
            if (progressMonitor.isCanceled() || task.isDone()) {
                if (progressMonitor.isCanceled()) {
                    task.cancel(true);
                }
            }
        }
        if ("progressText" == evt.getPropertyName()) {
            String progressText = (String) evt.getNewValue();
            progressMonitor.setNote(progressText);
            if (progressMonitor.isCanceled() || task.isDone()) {
                if (progressMonitor.isCanceled()) {
                    task.cancel(true);
                }
            }
        }

    }

    public static void main( String[] args ) {
        JFrame jframe = new JFrame("Dummy frame");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);

        HMProgressMonitorDialog monitor = new HMProgressMonitorDialog(jframe, "Progress", 100){

            @Override
            public void processInBackground() throws Exception {
                Random random = new Random();
                int progress = 0;
                progressMonitor.setProgress(0);
                try {
                    while( progress < 100 && !isCancelled() ) {
                        // Sleep for up to one second.
                        Thread.sleep(random.nextInt(1000));
                        // Make random progress.
                        progress += random.nextInt(10);
                        progressMonitor.setProgress(Math.min(progress, 100));
                        setProgressText("Blah: " + progress);
                    }
                } catch (InterruptedException ignore) {
                }

            }

            @Override
            public void postDoneInUi() {
                JOptionPane.showMessageDialog(jframe, "DONE!", "INFO", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        monitor.run();
    }
}