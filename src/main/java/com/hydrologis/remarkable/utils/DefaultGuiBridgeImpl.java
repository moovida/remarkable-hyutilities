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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

/**
 * JGT implementation of the {@link GuiBridgeHandler}.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class DefaultGuiBridgeImpl implements GuiBridgeHandler {

    private Component rootComponent;
    private File libsFolder;

    public DefaultGuiBridgeImpl() {
        this.rootComponent = null;
    }

    public DefaultGuiBridgeImpl( Component rootComponent ) {
        this.rootComponent = rootComponent;
    }

    protected Component getRootComponent() {
        return this.rootComponent;
    }

    private String translate( String message ) {
        return message;
        // I18nManager i18nManager = ToolsLocator.getI18nManager();
        // return i18nManager.getTranslation(message);
    }

    private String translate( String message, String[] args ) {
        return message;
        // I18nManager i18nManager = ToolsLocator.getI18nManager();
        // return i18nManager.getTranslation(message, args);
    }

    public int confirmDialog( final String message, final String title, final int optionType, final int messageType ) {
        RunnableWithParameters runnable = new RunnableWithParameters(){
            public void run() {
                this.returnValue = JOptionPane.showConfirmDialog(getRootComponent(), message, title, optionType, messageType);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (Integer) runnable.getReturnValue();
    }

    public String inputDialog( final String message, final String title, final int messageType, final String initialValue ) {
        // inputDialog dlg = new inputDialog();
        // return dlg.show(translate(message), translate(title), messageType,
        // initialValue);
        //
        RunnableWithParameters runnable = new RunnableWithParameters(){
            public void run() {
                this.returnValue = JOptionPane.showInputDialog(getRootComponent(), translate(message), translate(title),
                        messageType, null, null, initialValue);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (String) runnable.getReturnValue();
    }

    public String inputDialog( final String message, final String title ) {
        RunnableWithParameters runnable = new RunnableWithParameters(){
            public void run() {
                this.returnValue = JOptionPane.showInputDialog(getRootComponent(), (String) translate(message), translate(title),
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (String) runnable.getReturnValue();
    }

    public void messageDialog( String message, String title, int messageType ) {
        messageDialog(message, null, title, messageType);
    }

    public void messageDialog( final String message, final String messageArgs[], final String title, final int messageType ) {
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(new Runnable(){
                    public void run() {
                        messageDialog(message, messageArgs, title, messageType);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (message == null) {
            return;
        }
        JOptionPane.showMessageDialog(getRootComponent(), translate(message, messageArgs), translate(title), messageType);
    }

    public File[] showChooserDialog( final String title, final int type, // SAVE_DIALOG /
                                                                         // OPEN_DIALOG
            final int selectionMode, // JFileChooser.FILES_ONLY, JFileChooser.DIRECTORIES_ONLY,
                                     // JFileChooser.FILES_AND_DIRECTORIES
            final boolean multiselection, final File initialPath, final FileFilter filter, final boolean fileHidingEnabled ) {
        RunnableWithParameters runnable = new RunnableWithParameters(){
            public void run() {

                // FileDialog fd = new FileDialog((Frame) null, title, type);
                // fd.setDirectory(initialPath.getAbsolutePath());
                // fd.setMultipleMode(multiselection);
                // fd.setFilenameFilter(new FilenameFilter(){
                // @Override
                // public boolean accept( File dir, String name ) {
                // File file = new File(dir, name);
                // if (selectionMode == JFileChooser.DIRECTORIES_ONLY && !file.isDirectory()) {
                // return false;
                // }
                //
                // if (fileHidingEnabled && !file.isDirectory()) {
                // return false;
                // }
                // if (filter != null) {
                // return filter.accept(file);
                // }
                // return true;
                // }
                // });
                // fd.setVisible(true);
                // this.returnValue = fd.getFiles();

                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle(title);
                fc.setDialogType(type);
                fc.setFileSelectionMode(selectionMode);
                fc.setMultiSelectionEnabled(multiselection);
                fc.setCurrentDirectory(initialPath);
                fc.setFileFilter(filter);
                fc.setFileHidingEnabled(fileHidingEnabled);
                boolean isSave = false;
                int r = JFileChooser.CANCEL_OPTION;
                switch( type ) {
                case JFileChooser.SAVE_DIALOG:
                    r = fc.showSaveDialog(getRootComponent());
                    isSave = true;
                    break;
                case JFileChooser.OPEN_DIALOG:
                default:
                    r = fc.showOpenDialog(getRootComponent());
                    break;
                }
                if (r != JFileChooser.APPROVE_OPTION) {
                    this.returnValue = null;
                    return;
                }

                if (filter != null) {
                    List<File> allowedFiles = new ArrayList<>();
                    if (fc.isMultiSelectionEnabled()) {
                        File[] selectedFiles = fc.getSelectedFiles();
                        for( File selectedFile : selectedFiles ) {
                            if (filter.accept(selectedFile)) {
                                allowedFiles.add(selectedFile);
                            }
                        }
                    } else {
                        File selectedFile = fc.getSelectedFile();
                        if (isSave) {
                            if (!filter.accept(selectedFile)) {
                                String[] allowedExtensions = null;
//                                if (filter instanceof HMFileFilter) {
//                                    HMFileFilter hmfilter = (HMFileFilter) filter;
//                                    allowedExtensions = hmfilter.getAllowedExtensions();
//                                }
                                String msg = "The used extension is not supported.";
                                if (allowedExtensions != null)
                                    msg += " Supported extensions are: " + Arrays.toString(allowedExtensions);
                                messageDialog(msg, "WARNING", JOptionPane.WARNING_MESSAGE);
                                allowedFiles = null;
                                this.returnValue = null;
                            }
                        }
                        if (filter.accept(selectedFile) && allowedFiles != null) {
                            allowedFiles.add(selectedFile);
                        }
                    }
                    if (allowedFiles != null && allowedFiles.size() > 0) {
                        this.returnValue = allowedFiles.toArray(new File[0]);
                    }
                } else {
                    if (fc.isMultiSelectionEnabled()) {
                        File[] selectedFiles = fc.getSelectedFiles();
                        this.returnValue = selectedFiles;
                    } else {
                        File selectedFile = fc.getSelectedFile();
                        this.returnValue = new File[]{selectedFile};
                    }
                }

            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (File[]) runnable.getReturnValue();
    }

    public File[] showOpenDirectoryDialog( String title, File initialPath ) {
        return showChooserDialog(title, JFileChooser.OPEN_DIALOG, JFileChooser.DIRECTORIES_ONLY, false, initialPath, null, true);
    }

    public File[] showOpenFileDialog( String title, File initialPath, FileFilter filter ) {
        return showChooserDialog(title, JFileChooser.OPEN_DIALOG, JFileChooser.FILES_ONLY, false, initialPath, filter, true);
    }

    public File[] showSaveFileDialog( String title, File initialPath, FileFilter filter ) {
        return showChooserDialog(title, JFileChooser.SAVE_DIALOG, JFileChooser.FILES_ONLY, false, initialPath, filter, true);
    }

    @Override
    public Point2D getWorldPoint( int x, int y ) {
        return null;
    }

    @Override
    public String promptForCrs() {
        return null;
    }

    @Override
    public boolean supportsMapContext() {
        return false;
    }

    @Override
    public HashMap<String, String> getSpatialToolboxPreferencesMap() {
        Preferences preferences = Preferences.userRoot().node(PREFS_NODE_NAME);
        String debug = preferences.get(DEBUG_KEY, "false");
        String heap = preferences.get(HEAP_KEY, "64");

        HashMap<String, String> prefsMap = new HashMap<>();
        prefsMap.put(DEBUG_KEY, debug);
        prefsMap.put(HEAP_KEY, heap);

        return prefsMap;
    }

    @Override
    public void setSpatialToolboxPreferencesMap( HashMap<String, String> prefsMap ) {
        Preferences preferences = Preferences.userRoot().node(GuiBridgeHandler.PREFS_NODE_NAME);

        String debug = prefsMap.get(DEBUG_KEY);
        String heap = prefsMap.get(HEAP_KEY);

        preferences.put(DEBUG_KEY, debug);
        preferences.put(HEAP_KEY, heap);
    }

    @Override
    public HashMap<String, String> getGeopaparazziProjectViewerPreferencesMap() {
        Preferences preferences = Preferences.userRoot().node(PREFS_NODE_NAME);
        String lastPath = preferences.get(LAST_GP_PROJECTS_PATH, "");

        HashMap<String, String> prefsMap = new HashMap<>();
        prefsMap.put(LAST_GP_PROJECTS_PATH, lastPath);

        return prefsMap;
    }

    @Override
    public void setGeopaparazziProjectViewerPreferencesMap( HashMap<String, String> prefsMap ) {
        Preferences preferences = Preferences.userRoot().node(GuiBridgeHandler.PREFS_NODE_NAME);
        String lastPath = prefsMap.get(LAST_GP_PROJECTS_PATH);
        preferences.put(LAST_GP_PROJECTS_PATH, lastPath);
    }

    public void setLibsFolder( File libsFolder ) {
        this.libsFolder = libsFolder;
    }

    @Override
    public File getLibsFolder() {
        return libsFolder;
    }

    @Override
    public JFrame showWindow( JComponent component, String windowTitle ) {
        JFrame frame = new JFrame(windowTitle);
        frame.getContentPane().add(component, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

}
