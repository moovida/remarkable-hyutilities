package com.hydrologis.remarkable;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.hydrologis.remarkable.utils.DefaultGuiBridgeImpl;
import com.hydrologis.remarkable.utils.GuiBridgeHandler;
import com.hydrologis.remarkable.utils.GuiUtilities;
import com.hydrologis.remarkable.utils.GuiUtilities.IOnCloseListener;
import com.hydrologis.remarkable.utils.HMProgressMonitorDialog;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class TemplatesController extends TemplatesView implements IOnCloseListener {

    private static final String LS_TEMPLATES = "ls " + PreKeys.REMOTE_TEMPLATES_PATH + "/*.png";
    private GuiBridgeHandler guiBridge;
    private String localPath;
    private String[] remoteTemplates;
    private List<String> localTemplatesList = new ArrayList<>();

    public TemplatesController( GuiBridgeHandler guiBridge ) {
        this.guiBridge = guiBridge;
        setPreferredSize(new Dimension(900, 600));

        String host = GuiUtilities.getPreference(PreKeys.HOST, "");
        String user = GuiUtilities.getPreference(PreKeys.USER, "");
        String pwd = GuiUtilities.getPreference(PreKeys.PDW, "");
        localPath = GuiUtilities.getPreference(PreKeys.LOCAL_TEMPLATES_PATH, "");

        _localTemplatesTable.setTableHeader(null);
        _remoteTemplatesTable.setTableHeader(null);

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

        _refreshRemoteTemplatesButton.addActionListener(e -> {
            try (EasySession session = new EasySession()) {
                getRemoteTemplates(session.getSession());
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this, e1.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }

        });

        _uploadButton.addActionListener(e -> {
            uploadTemplates();
        });

        refreshLocal();
    }

    private void uploadTemplates() {
        int size = localTemplatesList.size();
        if (size == 0) {
            JOptionPane.showMessageDialog(this, "No templates available to donload!", "WARNING", JOptionPane.WARNING_MESSAGE);
            return;
        }

        HMProgressMonitorDialog monitor = new HMProgressMonitorDialog(this, "Progress", size + 2){

            @Override
            public void processInBackground() throws Exception {
                int prog = 0;
                progressMonitor.setProgress(prog++);
                // get the json and add the pngs to upload
                setProgressText("Download templates configuration file...");

                downloadTemplatesJson();

                progressMonitor.setProgress(prog++);

            }

            @Override
            public void postDoneInUi() {
                JOptionPane.showMessageDialog(TemplatesController.this, "DONE!", "INFO", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        monitor.run();
    }

    private void downloadTemplatesJson() {
        // TODO Auto-generated method stub

    }

    private void getRemoteTemplates( Session session ) throws JSchException, IOException {
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(LS_TEMPLATES);
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
        String remoteTemplatesStr = sb.toString();
        String[] split = remoteTemplatesStr.split("\n");
        remoteTemplates = new String[split.length];
        for( int i = 0; i < split.length; i++ ) {
            String path = split[i];
            int lastSlash = path.lastIndexOf("/");
            String name = path.substring(lastSlash + 1);
            remoteTemplates[i] = name;
        }

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
            localTemplatesList = Arrays.asList(templateFiles).stream().map(f -> f.getName()).collect(Collectors.toList());
            Collections.sort(localTemplatesList);

            String[][] templatesArray = new String[localTemplatesList.size()][1];
            for( int i = 0; i < templatesArray.length; i++ ) {
                templatesArray[i][0] = localTemplatesList.get(i);
            }
            _localTemplatesTable.setModel(new DefaultTableModel(templatesArray, new String[]{"Template"}));
        }
    }
    private void refreshRemote() {
        if (remoteTemplates != null) {

            String[][] templatesArray = new String[remoteTemplates.length][1];
            for( int i = 0; i < templatesArray.length; i++ ) {
                templatesArray[i][0] = remoteTemplates[i];
            }
            _remoteTemplatesTable.setModel(new DefaultTableModel(templatesArray, new String[]{"Template"}));
        }
    }

    public JComponent asJComponent() {
        return this;
    }

    public void onClose() {
    }

    public static void main( String[] args ) {

        GuiUtilities.setDefaultLookAndFeel();

        DefaultGuiBridgeImpl gBridge = new DefaultGuiBridgeImpl();
        final TemplatesController controller = new TemplatesController(gBridge);
        final JFrame frame = gBridge.showWindow(controller.asJComponent(), "Remarkable Utilities");

        Class<TemplatesController> class1 = TemplatesController.class;
        URL resource = class1.getResource("/com/hydrologis/remarkable/hm150.png");
        ImageIcon icon = new ImageIcon(resource);
        frame.setIconImage(icon.getImage());

        GuiUtilities.addClosingListener(frame, controller);

    }
}
