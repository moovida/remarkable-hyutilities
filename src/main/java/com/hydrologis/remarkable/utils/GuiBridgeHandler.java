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

import java.awt.geom.Point2D;
import java.io.File;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

/**
 * A class to help to bridge with extenral softwares.
 * 
 * <p>Implementing apps need to create the wrapper for this bridge</p>.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public interface GuiBridgeHandler {

    String SPATIAL_TOOLBOX_PREFERENCES_KEY = "SPATIAL_TOOLBOX_PREFERENCES";
    String GEOPAPARAZZI_PREFERENCES_KEY = "GEOPAPARAZZI_PREFERENCES_KEY";
    String HEAP_KEY = "jgt_prefs_heap";
    String DEBUG_KEY = "jgt_prefs_debug";
    String LAST_GP_PROJECTS_PATH = "jgt_last_projects_path";

    String PREFS_NODE_NAME = "/org/hortonmachine/gui";

    /**
     * Open a directory selection dialog.
     * 
     * @param title
     * @param initialPath
     * @return
     */
    public File[] showOpenDirectoryDialog( String title, File initialPath );

    /**
     * Open an open file dialog.
     * 
     * @param title
     * @param initialPath
     * @return
     */
    public File[] showOpenFileDialog( String title, File initialPath, FileFilter filter );

    /**
     * Open a save file dialog.
     * 
     * @param title
     * @param initialPath
     * @return
     */
    public File[] showSaveFileDialog( String title, File initialPath, FileFilter filter );

    public void messageDialog( String message, String title, int messageType );

    public void messageDialog( final String message, final String messageArgs[], final String title, final int messageType );

    /**
     * A check to see if this handler supports a map context.
     * 
     *  <p>This also defines if conversion between screen and world coordinates
     *  and prompting for a crs are supported.</p>
     * 
     * @return <code>true</code> if a mapcontext is supported.
     */
    public boolean supportsMapContext();

    /**
     * Get the world {@link Point2D} from and screen pixel x/y (ex. coming from a mouse event).
     * 
     * @param x the screen X.
     * @param y the screen Y.
     * @return the world position or <code>null</code>.
     */
    public Point2D getWorldPoint( int x, int y );

    /**
     * Open a dialog to prompt for a CRS.
     * 
     * @return the selected epsg code or <code>null</code>.
     */
    public String promptForCrs();

    /**
     * Get the map of user preferences.
     * 
     * @return the {@link HashMap} of preferences.
     */
    public HashMap<String, String> getSpatialToolboxPreferencesMap();

    /**
     * Save SpatialToolbox preferences map.
     * 
     * @param prefsMap
     */
    public void setSpatialToolboxPreferencesMap( HashMap<String, String> prefsMap );

    /**
     * Get the map of user preferences.
     * 
     * @return the {@link HashMap} of preferences.
     */
    public HashMap<String, String> getGeopaparazziProjectViewerPreferencesMap();

    /**
     * Save SpatialToolbox preferences map.
     * 
     * @param prefsMap
     */
    public void setGeopaparazziProjectViewerPreferencesMap( HashMap<String, String> prefsMap );

    /**
     * Get the folder inside which the libraries to browse are contained. 
     * 
     * @return the file to the libraries folder.
     */
    public File getLibsFolder();

    /**
     * @param libsFolder
     */
    public void setLibsFolder( File libsFolder );

    /**
     * Show a {@link JComponent} inside a window.
     * 
     * @param component the component to show.
     * @param windowTitle
     */
    public JFrame showWindow( JComponent component, String windowTitle );

}
