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
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

/**
 * An swingworker executor helper to work with a progress printer.
 *
 * @author Andrea Antonello (www.hydrologis.com)
 */
public abstract class HMExecutor extends SwingWorker<Void, ProgressUpdate> implements IProgressPrinter {
    protected int w = 600;
    protected int h = 120;

    protected IProgressPrinter progress;

    public void setProgressPrinter( IProgressPrinter progress ) {
        this.progress = progress;
    }

    @Override
    protected void process( List<ProgressUpdate> chunks ) {
        ProgressUpdate update = chunks.get(chunks.size() - 1);
        this.progress.publish(update);
    }

    @Override
    protected Void doInBackground() {
        try {
            backGroundWork();
        } catch (Exception e) {
            publish(new ProgressUpdate(e.getMessage()));
        }
        return null;
    }

    public abstract void backGroundWork() throws Exception;

    public void publish( ProgressUpdate update ) {
        super.publish(update);
    }

    @Override
    public void done() {
        try {
            get();
            progress.done();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}