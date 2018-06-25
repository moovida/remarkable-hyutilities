package com.hydrologis.remarkable.utils;

import javax.swing.JOptionPane;

import com.hydrologis.remarkable.PreKeys;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class MyUserInfo implements UserInfo, UIKeyboardInteractive {

    public String getPassword() {
        String thePwd = GuiUtilities.getPreference(PreKeys.PDW, "");
        return thePwd;
    }
    public boolean promptYesNo( String str ) {
        return true;
    }

    public String getPassphrase() {
        return null;
    }
    public boolean promptPassphrase( String message ) {
        return true;
    }
    public boolean promptPassword( String message ) {
        return true;
    }
    public void showMessage( String message ) {
        JOptionPane.showMessageDialog(null, message);
    }
    public String[] promptKeyboardInteractive( String destination, String name, String instruction, String[] prompt,
            boolean[] echo ) {
        return null;
    }
}