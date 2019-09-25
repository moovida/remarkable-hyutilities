package com.hydrologis.remarkable;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hydrologis.remarkable.progress.ActionWithProgress;
import com.hydrologis.remarkable.progress.ProgressMonitor;
import com.hydrologis.remarkable.utils.DefaultGuiBridgeImpl;
import com.hydrologis.remarkable.utils.FileUtilities;
import com.hydrologis.remarkable.utils.GuiBridgeHandler;
import com.hydrologis.remarkable.utils.GuiUtilities;
import com.hydrologis.remarkable.utils.GuiUtilities.IOnCloseListener;
import com.hydrologis.remarkable.utils.IconsHandler;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@SuppressWarnings("serial")
public class TemplatesController extends TemplatesView implements IOnCloseListener {

    private static final String VERSION = "v1.2";

    private static final String BACKUP_FOLDERNAME = "backup";

    private static final String GRAPHICS_FOLDERNAME = "graphics";

    private static final String TEMPLATES_FOLDERNAME = "templates";

    private static final String TEMPLATES_KEY_IN_JSON = "templates";
    private static final String FILENAME_KEY_IN_JSON = "filename";
    private static final String ICONCODE_KEY_IN_JSON = "iconCode";
    private static final String TEMPLATES_JSON_NAME = "templates.json";
    private static final String LS_TEMPLATES = "ls " + PreKeys.REMOTE_TEMPLATES_PATH + "/*.png";
    private static final String LS_GRAPHICS = "ls " + PreKeys.REMOTE_GRAPHICS_PATH + "/*.{png,bmp}";
    private static final String LS_BACKUP = "ls " + PreKeys.REMOTE_BACKUP_PATH;
    private static final String REMOTE_TEMPLATES_JSON_PATH = PreKeys.REMOTE_TEMPLATES_PATH + "/" + TEMPLATES_JSON_NAME;

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");

    private enum MODE {
        TEMPLATES, GRAPHICS, BACKUP
    };
    private MODE currentMode = MODE.TEMPLATES;

    private String[] remoteDataArray;
    private List<String> localTemplatesNamesList = new ArrayList<>();
    private List<String> localGraphicsNamesList = new ArrayList<>();
    private List<String> localBackupNamesList = new ArrayList<>();

    private Action backupAction;

    public TemplatesController( GuiBridgeHandler guiBridge ) {
        setPreferredSize(new Dimension(900, 800));

        String host = GuiUtilities.getPreference(PreKeys.HOST, "");
        String user = GuiUtilities.getPreference(PreKeys.USER, "");
        String pwd = GuiUtilities.getPreference(PreKeys.PWD, "");
        String localPath = GuiUtilities.getPreference(PreKeys.LOCAL_BASEPATH, "");

        _localTable.setTableHeader(null);
        _remoteTable.setTableHeader(null);

        _hostField.setText(host);
        _hostField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased( KeyEvent e ) {
                String hostText = _hostField.getText();
                GuiUtilities.setPreference(PreKeys.HOST, hostText);
                checkHostUserPwdFolder();
            }
        });
        _userField.setText(user);
        _userField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased( KeyEvent e ) {
                String userText = _userField.getText();
                GuiUtilities.setPreference(PreKeys.USER, userText);
                checkHostUserPwdFolder();
            }
        });
        _passwordField.setText(pwd);
        _passwordField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased( KeyEvent e ) {
                @SuppressWarnings("deprecation")
                String pwdText = _passwordField.getText();
                GuiUtilities.setPreference(PreKeys.PWD, pwdText);
                checkHostUserPwdFolder();
            }
        });

        _basefolderField.setEditable(false);

        _basefolderButton.addActionListener(e -> {
            File lastFile = GuiUtilities.getLastFile();
            File[] selectedFile = guiBridge.showOpenDirectoryDialog("Open base folder", lastFile);
            if (selectedFile != null && selectedFile.length > 0) {
                File baseFolderFile = selectedFile[0];
                String absolutePath = baseFolderFile.getAbsolutePath();
                GuiUtilities.setLastPath(absolutePath);
                if (!checkLocalPath(absolutePath)) {
                    JOptionPane
                            .showMessageDialog(this,
                                    "The basefolder has to contain the following folders:\n-" + TEMPLATES_FOLDERNAME + "\n-"
                                            + GRAPHICS_FOLDERNAME + "\n-" + BACKUP_FOLDERNAME,
                                    "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    GuiUtilities.setPreference(PreKeys.LOCAL_BASEPATH, absolutePath);
                    refreshLocal();
                }
            }

        });

        _remotePathField.setEditable(false);

        ActionWithProgress getRemoteDataAction = new ActionWithProgress(this, "Get remote data...", 2, true){
            @Override
            public void onError( Exception e ) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, e.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            @Override
            public void backGroundWork( ProgressMonitor monitor ) throws Exception {
                try (EasySession session = new EasySession()) {
                    getRemoteData(session.getSession());
                }
            }
        };
        _refreshRemoteTemplatesButton.setAction(getRemoteDataAction);
        _refreshRemoteTemplatesButton.setText("refresh");

        _uploadButton.setEnabled(false);

        _backupButton.setVisible(false);
        backupAction = new ActionWithProgress(this, "Backup", 4, false){
            @Override
            public void onError( Exception e ) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, e.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            @Override
            public void backGroundWork( ProgressMonitor monitor ) throws Exception {
                backup(monitor);
            }

            @Override
            public void postWork() throws Exception {
                refreshLocal();
            }
        };
        _backupButton.setAction(backupAction);
        _backupButton.setText("Backup");

        _templatesModeButton.setSelected(true);
        _templatesModeButton.addActionListener(e -> {
            if (_templatesModeButton.isSelected()) {
                currentMode = MODE.TEMPLATES;
                refreshLocal();
                resetRemoteFields();
                _backupButton.setVisible(false);
            }
        });
        _graphicsModeButton.addActionListener(e -> {
            if (_graphicsModeButton.isSelected()) {
                currentMode = MODE.GRAPHICS;
                refreshLocal();
                resetRemoteFields();
                _backupButton.setVisible(false);
            }
        });
        _backupModeButton.addActionListener(e -> {
            if (_backupModeButton.isSelected()) {
                currentMode = MODE.BACKUP;
                refreshLocal();
                resetRemoteFields();

                if (checkLocalPath(_basefolderField.getText())) {
                    _backupButton.setVisible(true);
                }
            }
        });

        ActionWithProgress restartAction = new ActionWithProgress(this, "Restart", 4, true){
            @Override
            public void onError( Exception e ) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, e.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            @Override
            public void backGroundWork( ProgressMonitor monitor ) throws Exception {
                try (EasySession s1 = new EasySession()) {
                    SshHelper.launchSshCommand(s1.getSession(), "systemctl restart xochitl");
                }
            }
        };
        _restartRemarkableButton.setAction(restartAction);
        _restartRemarkableButton.setText("Restart Device");

        _aboutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "This software is developed by HydroloGIS S.r.l. and released under GPL v3.0 license.\n\n"
                            + "We carry no responability if the software burned your reMarkable to death\n"
                            + "or destroyed your data (but yes, we would be very sorry about that).\n\n"
                            + "If you find the application useful and feel the urge to thank us, send us a box of good beer from your country.",
                    "ABOUT Remarkable-HyUtilities " + VERSION, JOptionPane.INFORMATION_MESSAGE);
        });

        if (checkLocalPath(localPath)) {
            _basefolderField.setText(localPath);
        }
        refreshLocal();
    }

    private void resetRemoteFields() {
        _remoteTable.setModel(new DefaultTableModel(new String[0][0], new String[0]));
        _remotePathField.setText("");
    }

    private void checkHostUserPwdFolder() {
        String host = GuiUtilities.getPreference(PreKeys.HOST, "");
        String user = GuiUtilities.getPreference(PreKeys.USER, "");
        String pwd = GuiUtilities.getPreference(PreKeys.PWD, "");

        boolean okToGo = true;
        if (!user.equals("root")) {
            okToGo = false;
        }
        if (pwd.length() == 0) {
            okToGo = false;
        }
        if (!ip(host)) {
            okToGo = false;
        }
        if (!checkLocalPath(null)) {
            okToGo = false;
        }

        _uploadButton.setEnabled(okToGo);
        _backupButton.setEnabled(okToGo);
    }

    public static boolean ip( String text ) {
        Pattern p = Pattern
                .compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
        Matcher m = p.matcher(text);
        return m.find();
    }

    private boolean checkLocalPath( String basePath ) {
        if (basePath != null && basePath.trim().length() == 0) {
            return false;
        }
        File templatesFile = getTemplatesFolder(basePath);
        File graphicsFile = getGraphicsFolder(basePath);
        File backupFile = getBackupFolder(basePath);
        if (templatesFile.exists() && graphicsFile.exists() && backupFile.exists()) {
            return true;
        }
        return false;
    }

    private File getBackupFolder( String basePath ) {
        if (basePath == null) {
            basePath = GuiUtilities.getPreference(PreKeys.LOCAL_BASEPATH, "");
        }
        File baseFolderFile = new File(basePath);
        File backupFile = new File(baseFolderFile, BACKUP_FOLDERNAME);
        return backupFile;
    }

    private File getGraphicsFolder( String basePath ) {
        if (basePath == null) {
            basePath = GuiUtilities.getPreference(PreKeys.LOCAL_BASEPATH, "");
        }
        File baseFolderFile = new File(basePath);
        File graphicsFile = new File(baseFolderFile, GRAPHICS_FOLDERNAME);
        return graphicsFile;
    }

    private File getTemplatesFolder( String basePath ) {
        if (basePath == null) {
            basePath = GuiUtilities.getPreference(PreKeys.LOCAL_BASEPATH, "");
        }
        File baseFolderFile = new File(basePath);
        File templatesFile = new File(baseFolderFile, TEMPLATES_FOLDERNAME);
        return templatesFile;
    }

    private void uploadTemplates( ProgressMonitor monitor ) throws Exception {

        int prog = 0;
        monitor.setCurrent("Download templates configuration file...", prog);
        // get the json and add the pngs to upload
        try (EasySession s1 = new EasySession()) {
            downloadTemplatesJson(s1.getSession());
        }
        monitor.setCurrent("Done.", prog++);

        File templatesFolder = getTemplatesFolder(null);
        String localJsonPath = templatesFolder.getAbsolutePath() + File.separator + TEMPLATES_JSON_NAME;
        try (EasySession s1 = new EasySession()) {
            monitor.setCurrent("Upload new templates configuration file....", prog);
            SshHelper.uploadFile(s1.getSession(), localJsonPath, REMOTE_TEMPLATES_JSON_PATH);
            monitor.setCurrent(null, prog++);

            for( String localTemplateName : localTemplatesNamesList ) {
                File templateToUpload = new File(templatesFolder, localTemplateName);

                monitor.setCurrent("Upload template: " + localTemplateName, prog);
                SshHelper.uploadFile(s1.getSession(), templateToUpload.getAbsolutePath(),
                        PreKeys.REMOTE_TEMPLATES_PATH + "/" + localTemplateName);
                monitor.setCurrent(null, prog++);
            }
        }

    }

    private void uploadGraphics( ProgressMonitor monitor ) throws Exception {
        int prog = 0;
        File graphicsFolder = getGraphicsFolder(null);
        try (EasySession s1 = new EasySession()) {
            for( String localGraphicName : localGraphicsNamesList ) {
                File graphicToUpload = new File(graphicsFolder, localGraphicName);
                String remoteFile = PreKeys.REMOTE_GRAPHICS_PATH + "/" + localGraphicName;
                monitor.setCurrent("Upload graphic: " + localGraphicName, prog);
                SshHelper.uploadFile(s1.getSession(), graphicToUpload.getAbsolutePath(), remoteFile);
                monitor.setCurrent(null, prog++);
            }
        }
    }

    private void backup( ProgressMonitor monitor ) throws Exception {
        int prog = 1;
        File backupFolder = getBackupFolder(null);
        try (EasySession s1 = new EasySession()) {
            monitor.setCurrent("Compressing remote data for backup (this might take a while...)", prog++);
            compress(s1.getSession());

            monitor.setCurrent("Downloading backup data (this might take a while depending on the size...)", prog++);
            File newBackup = new File(backupFolder, dateFormatter.format(new Date()) + "_backup.tar.gz");
            SshHelper.downloadFile(s1.getSession(), "~/backup.tar.gz", newBackup.getAbsolutePath());

            monitor.setCurrent("Cleanup on device...", prog++);
            cleanup(s1.getSession());
            monitor.setCurrent("Backup done!", prog++);
        }
    }

    private void compress( Session session ) throws Exception {
        String command = "tar -zcvf backup.tar.gz /home/root/.local/share/remarkable/xochitl";
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.connect();
//        StringBuilder sb = new StringBuilder();
        byte[] tmp = new byte[1024];
        while( true ) {
            while( in.available() > 0 ) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0)
                    break;
//                sb.append(new String(tmp, 0, i));
                System.out.println(new String(tmp, 0, i));
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

    }

    private void cleanup( Session session ) throws Exception {
        String command = "rm backup.tar.gz";
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.connect();
//        StringBuilder sb = new StringBuilder();
        byte[] tmp = new byte[1024];
        while( true ) {
            while( in.available() > 0 ) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0)
                    break;
//                sb.append(new String(tmp, 0, i));
                System.out.println(new String(tmp, 0, i));
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

    }

    private void downloadTemplatesJson( Session session ) {
        File templatesFolderFile = getTemplatesFolder(null);
        if (!templatesFolderFile.exists() || !templatesFolderFile.isDirectory()) {
            JOptionPane.showMessageDialog(this, "The local folder is not properly set!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String localJsonPath = templatesFolderFile.getAbsolutePath() + File.separator + TEMPLATES_JSON_NAME;
            SshHelper.downloadFile(session, REMOTE_TEMPLATES_JSON_PATH, localJsonPath);

            String json = FileUtilities.readFile(localJsonPath);
            JSONObject root = new JSONObject(json);
            JSONArray templatesArray = root.getJSONArray(TEMPLATES_KEY_IN_JSON);
            List<String> remoteTemplateFileNames = new ArrayList<>();
            for( int i = 0; i < templatesArray.length(); i++ ) {
                JSONObject templateObject = templatesArray.getJSONObject(i);
                String fileName = templateObject.getString(FILENAME_KEY_IN_JSON);
                String iconCode = templateObject.getString(ICONCODE_KEY_IN_JSON);
                String iconString = IconsHandler.INSTANCE.getIconString(iconCode);
                if (iconString != null) {
                    templateObject.put(ICONCODE_KEY_IN_JSON, iconString);
                }
                remoteTemplateFileNames.add(fileName);
            }

            for( String localName : localTemplatesNamesList ) {
                if (!remoteTemplateFileNames.contains(localName)) {
                    localName = localName.replace(".png", "");
                    JSONObject newJson = getNewJson(localName.replace('_', ' '), localName);
                    templatesArray.put(newJson);
                }
            }

            FileUtilities.copyFile(localJsonPath, localJsonPath + "_" + dateFormatter.format(new Date()));

            String newTemplates = root.toString(2);
            newTemplates = newTemplates.replaceAll("\\\\\\\\", "\\\\");
            FileUtilities.writeFile(newTemplates, new File(localJsonPath));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    private JSONObject getNewJson( String name, String fileName ) {
        String json = "{" + //
                "       \"name\": \"" + name + "\"," + //
                "       \"filename\": \"" + fileName + "\"," + //
                "       \"iconCode\": \"" + IconsHandler.CUSTOM_ICON + "\"," + //
                "       \"categories\": [" + //
                "              \"Custom\"" + //
                "       ]" + //
                "}";
        return new JSONObject(json);
    }

    private void getRemoteData( Session session ) throws JSchException, IOException {
        String command = LS_TEMPLATES;
        if (currentMode == MODE.TEMPLATES) {
            command = LS_TEMPLATES;
        } else if (currentMode == MODE.GRAPHICS) {
            command = LS_GRAPHICS;
        } else if (currentMode == MODE.BACKUP) {
            command = LS_BACKUP;
        }

        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
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
        remoteDataArray = new String[split.length];
        for( int i = 0; i < split.length; i++ ) {
            String path = split[i];
            int lastSlash = path.lastIndexOf("/");
            String name = path.substring(lastSlash + 1);
            remoteDataArray[i] = name;
        }

        refreshRemote();
    }

    private void refreshLocal() {
        if (currentMode == MODE.TEMPLATES) {
            File templatesFolderFile = getTemplatesFolder(null);
            if (templatesFolderFile.exists() && templatesFolderFile.isDirectory()) {
                File[] templateFiles = templatesFolderFile.listFiles(new FilenameFilter(){
                    @Override
                    public boolean accept( File dir, String name ) {
                        return name.endsWith(".png");
                    }
                });
                localTemplatesNamesList = Arrays.asList(templateFiles).stream().map(f -> f.getName())
                        .collect(Collectors.toList());
                Collections.sort(localTemplatesNamesList);

                String[][] templatesArray = new String[localTemplatesNamesList.size()][1];
                for( int i = 0; i < templatesArray.length; i++ ) {
                    templatesArray[i][0] = localTemplatesNamesList.get(i);
                }
                _localTable.setModel(new DefaultTableModel(templatesArray, new String[]{"Template"}));
            }
        } else if (currentMode == MODE.GRAPHICS) {
            File graphicsFolderFile = getGraphicsFolder(null);
            if (graphicsFolderFile.exists() && graphicsFolderFile.isDirectory()) {
                File[] graphicsFiles = graphicsFolderFile.listFiles(new FilenameFilter(){
                    @Override
                    public boolean accept( File dir, String name ) {
                        return name.endsWith(".png") || name.endsWith(".bmp");
                    }
                });
                localGraphicsNamesList = Arrays.asList(graphicsFiles).stream().map(f -> f.getName()).collect(Collectors.toList());
                Collections.sort(localGraphicsNamesList);

                String[][] graphicsArray = new String[localGraphicsNamesList.size()][1];
                for( int i = 0; i < graphicsArray.length; i++ ) {
                    graphicsArray[i][0] = localGraphicsNamesList.get(i);
                }
                _localTable.setModel(new DefaultTableModel(graphicsArray, new String[]{"Graphic"}));
            }
        } else if (currentMode == MODE.BACKUP) {
            File backupFolderFile = getBackupFolder(null);
            if (backupFolderFile.exists() && backupFolderFile.isDirectory()) {
                File[] backupFiles = backupFolderFile.listFiles(new FilenameFilter(){
                    @Override
                    public boolean accept( File dir, String name ) {
                        return name.endsWith(".tar.gz");
                    }
                });
                localBackupNamesList = Arrays.asList(backupFiles).stream().map(f -> f.getName()).collect(Collectors.toList());
                Collections.sort(localBackupNamesList);

                String[][] backupsArray = new String[localBackupNamesList.size()][1];
                for( int i = 0; i < backupsArray.length; i++ ) {
                    backupsArray[i][0] = localBackupNamesList.get(i);
                }
                _localTable.setModel(new DefaultTableModel(backupsArray, new String[]{"Backup"}));
            }
        }

        int size = 0;
        if (currentMode == MODE.TEMPLATES) {
            size = localTemplatesNamesList.size();
        } else if (currentMode == MODE.GRAPHICS) {
            size = localGraphicsNamesList.size();
        } else if (currentMode == MODE.BACKUP) {
            size = 0; // no upload here
        }
        if (size != 0) {
            _uploadButton.setEnabled(true);
            ActionWithProgress uploadAction = new ActionWithProgress(this, "Upload", size, false){
                @Override
                public void onError( Exception e ) {
                    JOptionPane.showMessageDialog(parent, "ERROR: " + e.getLocalizedMessage(), "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                }

                @Override
                public void backGroundWork( ProgressMonitor monitor ) throws Exception {
                    if (currentMode == MODE.TEMPLATES) {
                        uploadTemplates(monitor);
                    } else if (currentMode == MODE.GRAPHICS) {
                        uploadGraphics(monitor);
                    }

                }
            };
            _uploadButton.setAction(uploadAction);
            _uploadButton.setText("Upload local (selected mode)");
        } else {
            _uploadButton.setEnabled(false);
        }
    }

    private void refreshRemote() {
        if (currentMode == MODE.TEMPLATES) {
            _remotePathField.setText(PreKeys.REMOTE_TEMPLATES_PATH);
            if (remoteDataArray != null) {
                String[][] dataArray = new String[remoteDataArray.length][1];
                for( int i = 0; i < dataArray.length; i++ ) {
                    dataArray[i][0] = remoteDataArray[i];
                }
                _remoteTable.setModel(new DefaultTableModel(dataArray, new String[]{"Template"}));
            }
        } else if (currentMode == MODE.GRAPHICS) {
            _remotePathField.setText(PreKeys.REMOTE_TEMPLATES_PATH);
            if (remoteDataArray != null) {
                String[][] dataArray = new String[remoteDataArray.length][1];
                for( int i = 0; i < dataArray.length; i++ ) {
                    dataArray[i][0] = remoteDataArray[i];
                }
                _remoteTable.setModel(new DefaultTableModel(dataArray, new String[]{"Graphics"}));
            }
        } else if (currentMode == MODE.BACKUP) {
            _remotePathField.setText(PreKeys.REMOTE_BACKUP_PATH);
            if (remoteDataArray != null) {
                String[][] dataArray = new String[remoteDataArray.length][1];
                for( int i = 0; i < dataArray.length; i++ ) {
                    dataArray[i][0] = remoteDataArray[i];
                }
                _remoteTable.setModel(new DefaultTableModel(dataArray, new String[]{"Backup"}));
            }
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
        final JFrame frame = gBridge.showWindow(controller.asJComponent(), "Remarkable HyUtilities " + VERSION);

        Class<TemplatesController> class1 = TemplatesController.class;
        URL resource = class1.getResource("/com/hydrologis/remarkable/hm150.png");
        ImageIcon icon = new ImageIcon(resource);
        frame.setIconImage(icon.getImage());

        GuiUtilities.addClosingListener(frame, controller);

        Dimension prefSize = frame.getPreferredSize();
        Dimension parentSize;
        java.awt.Point parentLocation = new java.awt.Point(0, 0);
        parentSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
        int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
        frame.setLocation(x, y);

    }
}
