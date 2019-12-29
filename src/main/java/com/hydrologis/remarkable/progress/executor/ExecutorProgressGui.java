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

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.hydrologis.remarkable.TemplatesController;
import com.hydrologis.remarkable.utils.GuiUtilities;
import com.hydrologis.remarkable.utils.IconsHandler;

/**
 * Executor swingworker with an progress monitor that allows for messages and progress updates.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public abstract class ExecutorProgressGui extends HMExecutor {

    private JFrame frame;

    public ExecutorProgressGui( int max ) {
        frame = new JFrame();
        JLabel label = new JLabel("Loading...");
        JProgressBar jpb = new JProgressBar(0, max);
        jpb.setIndeterminate(false);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(label, BorderLayout.NORTH);
        panel.add(jpb, BorderLayout.CENTER);
        frame.add(panel);
        frame.pack();
        frame.setSize(w, h);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setIconImage(IconsHandler.INSTANCE.getFrameIcon());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        progress = new IProgressPrinter(){
            @Override
            public void publish( ProgressUpdate update ) {
                if (update.errorMessage != null) {
                    frame.dispose();
                    GuiUtilities.showErrorMessage(panel, update.errorMessage);
                } else {
                    label.setText(update.updateString);
                    jpb.setValue(update.workDone);
                }
            }

            @Override
            public void done() {
                if (frame != null && frame.isVisible())
                    frame.dispose();
            }
        };
    }

}
