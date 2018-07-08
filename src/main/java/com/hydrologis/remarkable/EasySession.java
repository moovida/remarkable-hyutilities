package com.hydrologis.remarkable;

import com.hydrologis.remarkable.utils.GuiUtilities;
import com.hydrologis.remarkable.utils.MyUserInfo;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class EasySession implements AutoCloseable {

    private static JSch jsch = new JSch();
    private Session session;

    public EasySession() throws Exception {
        String theHost = GuiUtilities.getPreference(PreKeys.HOST, "");
        String theUser = GuiUtilities.getPreference(PreKeys.USER, "");
        session = jsch.getSession(theUser, theHost, 22);
        UserInfo ui = new MyUserInfo();
        session.setUserInfo(ui);
        session.setPassword(ui.getPassword().getBytes());
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
    }

    public Session getSession() {
        return session;
    }

    @Override
    public void close() throws Exception {
        session.disconnect();
    }

}
