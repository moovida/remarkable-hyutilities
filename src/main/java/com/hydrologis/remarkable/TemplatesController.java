package com.hydrologis.remarkable;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;

import com.hydrologis.remarkable.utils.GuiBridgeHandler;
import com.hydrologis.remarkable.utils.GuiUtilities;
import com.hydrologis.remarkable.utils.GuiUtilities.IOnCloseListener;
import com.hydrologis.remarkable.utils.MyUserInfo;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class TemplatesController extends TemplatesView implements IOnCloseListener {

    private GuiBridgeHandler guiBridge;
    private String localPath;
    private JSch jsch;
    private String[] templates;
    private Session session;

    public TemplatesController( GuiBridgeHandler guiBridge ) {
        this.guiBridge = guiBridge;
        setPreferredSize(new Dimension(900, 600));

        String host = GuiUtilities.getPreference(PreKeys.HOST, "");
        String user = GuiUtilities.getPreference(PreKeys.USER, "");
        String pwd = GuiUtilities.getPreference(PreKeys.PDW, "");
        localPath = GuiUtilities.getPreference(PreKeys.LOCAL_TEMPLATES_PATH, "");

        _hostField.setText(host);
        _hostField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased( KeyEvent e ) {
                String hostText = _hostField.getText();
                GuiUtilities.setPreference(PreKeys.HOST, hostText);
            }
        });
        _userField.setText(user);
        _userField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased( KeyEvent e ) {
                String userText = _userField.getText();
                GuiUtilities.setPreference(PreKeys.USER, userText);
            }
        });
        _passwordField.setText(pwd);
        _passwordField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased( KeyEvent e ) {
                String pwdText = _passwordField.getText();
                GuiUtilities.setPreference(PreKeys.PDW, pwdText);
            }
        });
        _localPathField.setText(localPath);
        _localPathField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased( KeyEvent e ) {
                String pathText = _localPathField.getText();
                GuiUtilities.setPreference(PreKeys.LOCAL_TEMPLATES_PATH, pathText);
                refreshLocal();
            }
        });
        
        _remotePathField.setText(PreKeys.REMOTE_TEMPLATES_PATH);
        _remotePathField.setEditable(false);
        

        jsch = new JSch();

        _connectButton.addActionListener(e -> {
            String theHost = GuiUtilities.getPreference(PreKeys.HOST, "");
            String theUser = GuiUtilities.getPreference(PreKeys.USER, "");
            try {
                session = jsch.getSession(theUser, theHost, 22);
                UserInfo ui = new MyUserInfo();
                session.setUserInfo(ui);
                session.connect();
                getRemoteTemplates();

            } catch (Exception e1) {
                e1.printStackTrace();
            }

        });
        
        _downloadButton.addActionListener(e->{
            
        });

        refreshLocal();
    }

    private void getRemoteTemplates() throws JSchException, IOException {
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand("ls " + PreKeys.REMOTE_TEMPLATES_PATH);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.connect();
        StringBuilder sb = new StringBuilder();
        byte[] tmp = new byte[1024];
        while( true ) {
            while( in.available() > 0 ) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0)
                    break;
                sb.append(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                if (in.available() > 0)
                    continue;
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }
        channel.disconnect();
        String remoteTemplates = sb.toString();
        templates = remoteTemplates.split("\n");
        refreshRemote();
    }

    private void refreshLocal() {
        File localFile = new File(localPath);
        if (localFile.exists() && localFile.isDirectory()) {
            File[] templateFiles = localFile.listFiles(new FilenameFilter(){
                @Override
                public boolean accept( File dir, String name ) {
                    return name.endsWith(".png");
                }
            });
            List<String> templatesList = Arrays.asList(templateFiles).stream().map(f -> f.getName()).collect(Collectors.toList());
            Collections.sort(templatesList);

            String[][] templatesArray = new String[templatesList.size()][1];
            for( int i = 0; i < templatesArray.length; i++ ) {
                templatesArray[i][0] = templatesList.get(i);
            }
            _localTemplatesTable.setModel(new DefaultTableModel(templatesArray, new String[]{"Template"}));
        }
    }
    private void refreshRemote() {
        if (templates != null) {

            String[][] templatesArray = new String[templates.length][1];
            for( int i = 0; i < templatesArray.length; i++ ) {
                templatesArray[i][0] = templates[i];
            }
            _remoteTemplatesTable.setModel(new DefaultTableModel(templatesArray, new String[]{"Template"}));
        }
    }

    public JComponent asJComponent() {
        return this;
    }

    public void onClose() {
        if (session != null)
            session.disconnect();
    }
}
