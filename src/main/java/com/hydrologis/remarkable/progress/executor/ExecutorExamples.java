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
package com.hydrologis.remarkable.progress.executor;

import javax.swing.SwingUtilities;

/**
 * Usage examples for the executor.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ExecutorExamples {

    public static void main( String args[] ) {
        runWithProgress();
    }

    public static void runIndeterminate() {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                new ExecutorIndeterminateGui(){
                    @Override
                    public void backGroundWork() throws Exception {
                        for( int i = 0; i < 15; i++ ) {
                            publish(new ProgressUpdate("Working...", (i + 1)));
                            Thread.sleep(300);
                        }
                    }
                }.execute();
            }
        });

    }

    public static void runWithProgress() {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                int max = 15;
                new ExecutorProgressGui(max){
                    @Override
                    public void backGroundWork() throws Exception {
                        for( int i = 0; i < max; i++ ) {
                            int workDone = i + 1;
                            publish(new ProgressUpdate("Working " + workDone + "...", workDone));
                            Thread.sleep(300);
                        }
                    }
                }.execute();
            }
        });

    }

    public static void runWithProgressException() {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                int max = 15;
                new ExecutorProgressGui(max){
                    @Override
                    public void backGroundWork() throws Exception {
                        for( int i = 0; i < max; i++ ) {
                            int workDone = i + 1;
                            publish(new ProgressUpdate("Working " + workDone + "...", workDone));
                            Thread.sleep(300);
                            if (i == 3) {
                                throw new RuntimeException("Exiting due to error");
                            }
                        }
                    }
                }.execute();
            }
        });
        
    }

    public static void runWithProgressSysout() {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                int max = 15;
                new ExecutorProgressPrintStream(System.out, max){
                    @Override
                    public void backGroundWork() throws Exception {
                        for( int i = 0; i < max; i++ ) {
                            int workDone = i + 1;
                            publish(new ProgressUpdate("Working " + workDone + "...", workDone));
                            Thread.sleep(300);
                        }
                    }
                }.execute();
            }
        });

    }

}
