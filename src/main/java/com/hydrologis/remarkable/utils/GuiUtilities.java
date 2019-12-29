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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.hydrologis.remarkable.utils.OsCheck.OSType;

/**
 * Utilities class.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GuiUtilities {

    public static final String LAST_PATH = "KEY_LAST_PATH";
    public static final String PREF_STRING_SEPARATORS = "@@@@";

    public static interface IOnCloseListener {
        public void onClose();
    }

    /**
     * Set the location of a component to center it on the screen.
     * 
     * @param component
     *            the component to center.
     */
    public static void centerOnScreen( Component component ) {
        Dimension prefSize = component.getPreferredSize();
        Dimension parentSize;
        java.awt.Point parentLocation = new java.awt.Point(0, 0);
        parentSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
        int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
        component.setLocation(x, y);
    }

    /**
     * Handle the last set path preference.
     * 
     * @return the last set path or the user home.
     */
    public static File getLastFile() {
        Preferences preferences = Preferences.userRoot().node(GuiBridgeHandler.PREFS_NODE_NAME);

        String userHome = System.getProperty("user.home");
        String lastPath = preferences.get(LAST_PATH, userHome);
        File file = new File(lastPath);
        if (!file.exists()) {
            return new File(userHome);
        }
        return file;
    }

    /**
     * Save the passed path as last path available.
     * 
     * @param lastPath
     *            the last path to save.
     */
    public static void setLastPath( String lastPath ) {
        File file = new File(lastPath);
        if (!file.isDirectory()) {
            lastPath = file.getParentFile().getAbsolutePath();
        }
        Preferences preferences = Preferences.userRoot().node(GuiBridgeHandler.PREFS_NODE_NAME);
        preferences.put(LAST_PATH, lastPath);
    }

    /**
     * Get from preference.
     * 
     * @param preferenceKey
     *            the preference key.
     * @param defaultValue
     *            the default value in case of <code>null</code>.
     * @return the string preference asked.
     */
    public static String getPreference( String preferenceKey, String defaultValue ) {
        Preferences preferences = Preferences.userRoot().node(GuiBridgeHandler.PREFS_NODE_NAME);
        String preference = preferences.get(preferenceKey, defaultValue);
        return preference;
    }

    public static String[] getPreference( String preferenceKey, String[] defaultValue ) {
        Preferences preferences = Preferences.userRoot().node(GuiBridgeHandler.PREFS_NODE_NAME);
        String preference = preferences.get(preferenceKey, "");
        String[] split = preference.split(PREF_STRING_SEPARATORS);
        return split;
    }

    public static byte[] getPreference( String preferenceKey, byte[] defaultValue ) {
        Preferences preferences = Preferences.userRoot().node(GuiBridgeHandler.PREFS_NODE_NAME);
        byte[] preference = preferences.getByteArray(preferenceKey, defaultValue);
        return preference;
    }

    /**
     * Set a preference.
     * 
     * @param preferenceKey
     *            the preference key.
     * @param value
     *            the value to set.
     */
    public static void setPreference( String preferenceKey, String value ) {
        Preferences preferences = Preferences.userRoot().node(GuiBridgeHandler.PREFS_NODE_NAME);
        if (value != null) {
            preferences.put(preferenceKey, value);
        } else {
            preferences.remove(preferenceKey);
        }
    }

    public static void setPreference( String preferenceKey, byte[] value ) {
        Preferences preferences = Preferences.userRoot().node(GuiBridgeHandler.PREFS_NODE_NAME);
        if (value != null) {
            preferences.putByteArray(preferenceKey, value);
        } else {
            preferences.remove(preferenceKey);
        }
    }

    public static void setPreference( String preferenceKey, String[] valuesArray ) {
        Preferences preferences = Preferences.userRoot().node(GuiBridgeHandler.PREFS_NODE_NAME);
        if (valuesArray != null) {
            int maxLength = Preferences.MAX_VALUE_LENGTH;
            String arrayToString = Stream.of(valuesArray).collect(Collectors.joining(PREF_STRING_SEPARATORS));

            // remove from last if it is too large
            int remIndex = valuesArray.length - 1;
            while( arrayToString.length() > maxLength ) {
                valuesArray[remIndex--] = "";
                arrayToString = Stream.of(valuesArray).collect(Collectors.joining(PREF_STRING_SEPARATORS));
            }

            preferences.put(preferenceKey, arrayToString);
        } else {
            preferences.remove(preferenceKey);
        }
    }

    public static void copyToClipboard( String text ) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    public static String getFromClipboard() throws Exception {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String string = (String) clipboard.getData(DataFlavor.stringFlavor);
        return string;
    }

    public static void openFile( File file ) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
        }
    }

    public static boolean openWebpage( URI uri ) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage( URL url ) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static JDialog openDialogWithPanel( JPanel panel, String title, Dimension dimension ) {
        JDialog f = new JDialog();
        f.add(panel, BorderLayout.CENTER);
        f.setTitle(title);
        f.pack();
        if (dimension != null)
            f.setSize(dimension);
        f.setLocationRelativeTo(null); // Center on screen
        f.setVisible(true);
        f.setIconImage(IconsHandler.INSTANCE.getFrameIcon());
        f.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        return f;
    }

    public static String showInputDialog( Component parentComponent, String message, String defaultInput ) {
        String answer = JOptionPane.showInputDialog(parentComponent, message, defaultInput);
        return answer;
    }

    /**
     * Create a simple multi input pane, that returns what the use inserts.
     * 
     * @param parentComponent
     * @param title
     *            the dialog title.
     * @param labels
     *            the labels to set.
     * @param defaultValues
     *            a set of default values.
     * @param fields2ValuesMap a map that allows to set combos for the various options.
     * @return the result inserted by the user.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static String[] showMultiInputDialog( Component parentComponent, String title, String[] labels, String[] defaultValues,
            HashMap<String, String[]> fields2ValuesMap ) {
        Component[] valuesFields = new Component[labels.length];
        JPanel panel = new JPanel();
        // panel.setPreferredSize(new Dimension(400, 300));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String input[] = new String[labels.length];
        panel.setLayout(new GridLayout(labels.length, 2, 5, 5));
        for( int i = 0; i < labels.length; i++ ) {
            panel.add(new JLabel(labels[i]));

            boolean doneCombo = false;
            if (fields2ValuesMap != null) {
                String[] values = fields2ValuesMap.get(labels[i]);
                if (values != null) {
                    JComboBox<String> valuesCombo = new JComboBox<>(values);
                    valuesFields[i] = valuesCombo;
                    panel.add(valuesCombo);
                    if (defaultValues != null) {
                        valuesCombo.setSelectedItem(defaultValues[i]);
                    }
                    doneCombo = true;
                }
            }
            if (!doneCombo) {
                valuesFields[i] = new JTextField();
                panel.add(valuesFields[i]);
                if (defaultValues != null) {
                    ((JTextField) valuesFields[i]).setText(defaultValues[i]);
                }
            }
        }
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(550, 300));
        int result = JOptionPane.showConfirmDialog(parentComponent, scrollPane, title, JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        for( int i = 0; i < labels.length; i++ ) {
            if (valuesFields[i] instanceof JTextField) {
                JTextField textField = (JTextField) valuesFields[i];
                input[i] = textField.getText();
            }
            if (valuesFields[i] instanceof JComboBox) {
                JComboBox<String> combo = (JComboBox) valuesFields[i];
                input[i] = combo.getSelectedItem().toString();
            }
        }
        return input;
    }

    public static String showComboDialog( Component parentComponent, String title, String message, String[] values ) {
        String result = (String) JOptionPane.showInputDialog(parentComponent, message, title, JOptionPane.QUESTION_MESSAGE, null,
                values, values[0]);
        return result;
    }

    public static void setDefaultLookAndFeel() {
        try {
            OSType osType = OsCheck.getOperatingSystemType();
            switch( osType ) {
            case Windows:
                for( UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() ) {
                    String name = info.getName();
                    if ("Windows".equalsIgnoreCase(name)) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        return;
                    }
                }
            case Linux:
                for( UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() ) {
                    String name = info.getName();
                    if ("GTK".equalsIgnoreCase(name) || "GTK+".equalsIgnoreCase(name)) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        return;
                    }
                }

                // for( UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() ) {
                // String name = info.getName();
                // if ("Nimbus".equalsIgnoreCase(name)) {
                // javax.swing.UIManager.setLookAndFeel(info.getClassName());
                // break;
                // }
                // }
            default:
                for( UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() ) {
                    String name = info.getName();
                    if ("Nimbus".equalsIgnoreCase(name)) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addClosingListener( JFrame frame, IOnCloseListener freeResourcesComponent ) {
        WindowListener exitListener = new WindowAdapter(){
            @Override
            public void windowClosing( WindowEvent e ) {
                int confirm = JOptionPane.showOptionDialog(frame, "Are you sure you want to exit?", "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == JOptionPane.YES_OPTION) {
                    freeResourcesComponent.onClose();
                    System.exit(0);
                }
            }
        };
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(exitListener);
    }

    public static void showInfoMessage( Component parentComponent, String message ) {
        showInfoMessage(parentComponent, null, message);
    }

    public static void showInfoMessage( Component parentComponent, String title, String message ) {
        if (title == null) {
            title = "INFO";
        }
        JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarningMessage( Component parentComponent, String message ) {
        showWarningMessage(parentComponent, null, message);
    }

    public static void showWarningMessage( Component parentComponent, String title, String message ) {
        if (title == null) {
            title = "WARNING";
        }
        JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void showErrorMessage( Component parentComponent, String message ) {
        showErrorMessage(parentComponent, null, message);
    }

    public static void showErrorMessage( Component parentComponent, String title, String message ) {
        if (title == null) {
            title = "ERROR";
        }
        JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.ERROR_MESSAGE);
    }

}
