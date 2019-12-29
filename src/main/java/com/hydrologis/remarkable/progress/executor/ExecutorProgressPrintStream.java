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

import java.io.PrintStream;

/**
 * Executor swingworker with an {@link PrintStream} that allows for messages.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public abstract class ExecutorProgressPrintStream extends HMExecutor {

    private PrintStream _ps;

    public ExecutorProgressPrintStream( PrintStream ps, int max ) {
        _ps = ps;
        progress = new IProgressPrinter(){
            @Override
            public void publish( ProgressUpdate update ) {
                if (update.errorMessage != null) {
                    System.err.println(update.errorMessage);
                    _ps = null;
                } else {
                    String message = update.updateString + " (" + update.workDone + "/" + max + ")";
                    ps.println(message);
                }
            }

            @Override
            public void done() {
                if (_ps != null)
                    _ps.println("Done.");
            }
        };
    }

}
