package com.hydrologis.remarkable;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.hydrologis.remarkable.utils.DefaultGuiBridgeImpl;
import com.hydrologis.remarkable.utils.FileUtilities;
import com.hydrologis.remarkable.utils.GuiBridgeHandler;
import com.hydrologis.remarkable.utils.GuiUtilities;
import com.hydrologis.remarkable.utils.GuiUtilities.IOnCloseListener;
import com.hydrologis.remarkable.utils.HMProgressMonitorDialog;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class TemplatesController extends TemplatesView implements IOnCloseListener {

    private static final String BACKUP_FOLDERNAME = "backup";

    private static final String GRAPHICS_FOLDERNAME = "graphics";

    private static final String TEMPLATES_FOLDERNAME = "templates";

    private static final String TEMPLATES_KEY_IN_JSON = "templates";
    private static final String FILENAME_KEY_IN_JSON = "filename";
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

    private GuiBridgeHandler guiBridge;
    private String[] remoteDataArray;
    private List<String> localTemplatesNamesList = new ArrayList<>();
    private List<String> localGraphicsNamesList = new ArrayList<>();
    private List<String> localBackupNamesList = new ArrayList<>();

    public TemplatesController( GuiBridgeHandler guiBridge ) {
        this.guiBridge = guiBridge;
        setPreferredSize(new Dimension(900, 600));

        String host = GuiUtilities.getPreference(PreKeys.HOST, "");
        String user = GuiUtilities.getPreference(PreKeys.USER, "");
        String pwd = GuiUtilities.getPreference(PreKeys.PDW, "");
        String localPath = GuiUtilities.getPreference(PreKeys.LOCAL_BASEPATH, "");

        _localTable.setTableHeader(null);
        _remoteTable.setTableHeader(null);

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

        _refreshRemoteTemplatesButton.addActionListener(e -> {
            try (EasySession session = new EasySession()) {

                getRemoteData(session.getSession());
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this, e1.getLocalizedMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }

        });

        _uploadButton.setEnabled(false);
        _uploadButton.addActionListener(e -> {
            if (currentMode == MODE.TEMPLATES) {
                uploadTemplates();
            } else if (currentMode == MODE.GRAPHICS) {
                uploadGraphics();
            } else if (currentMode == MODE.BACKUP) {
                JOptionPane.showMessageDialog(this, "Not implemented yet", "WARNING", JOptionPane.WARNING_MESSAGE);
            }
        });
        _backupButton.setVisible(false);
        _backupButton.addActionListener(e -> {
            try {
                _backupButton.setEnabled(false);
                backup();
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                _backupButton.setEnabled(true);
            }
        });

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
                _backupButton.setVisible(true);
            }
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

    private boolean checkLocalPath( String basePath ) {
        if (basePath.trim().length() == 0) {
            return false;
        }
        File templatesFile = getTemplatesFolder(basePath);
        File graphicsFile = getGraphicsFolder(basePath);
        File backupFile = getBackupFolder(basePath);
        if (templatesFile.exists() && graphicsFile.exists() && backupFile.exists()) {
            _uploadButton.setEnabled(true);
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

    private void uploadTemplates() {
        int size = localTemplatesNamesList.size();
        if (size == 0) {
            JOptionPane.showMessageDialog(this, "No templates available to upload!", "WARNING", JOptionPane.WARNING_MESSAGE);
            return;
        }

        HMProgressMonitorDialog monitor = new HMProgressMonitorDialog(this, "Progress", size + 2){
            @Override
            public void processInBackground() throws Exception {
                int prog = 0;
                progressMonitor.setProgress(prog++);
                // get the json and add the pngs to upload
                setProgressText("Download templates configuration file...");
                try (EasySession s1 = new EasySession()) {
                    downloadTemplatesJson(s1.getSession());
                }
                progressMonitor.setProgress(prog++);

                File templatesFolder = getTemplatesFolder(null);
                String localJsonPath = templatesFolder.getAbsolutePath() + File.separator + TEMPLATES_JSON_NAME;
                try (EasySession s1 = new EasySession()) {
                    setProgressText("Upload new templates configuration file...");
                    SshHelper.uploadFile(s1.getSession(), localJsonPath, REMOTE_TEMPLATES_JSON_PATH);
                    progressMonitor.setProgress(prog++);

                    for( String localTemplateName : localTemplatesNamesList ) {
                        File templateToUpload = new File(templatesFolder, localTemplateName);
                        setProgressText("Upload template: " + localTemplateName);
                        SshHelper.uploadFile(s1.getSession(), templateToUpload.getAbsolutePath(),
                                PreKeys.REMOTE_TEMPLATES_PATH + "/" + localTemplateName);
                        progressMonitor.setProgress(prog++);
                    }
                }

            }

            @Override
            public void postDoneInUi() {
                JOptionPane.showMessageDialog(TemplatesController.this, "DONE!", "INFO", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        monitor.run();
    }

    private void uploadGraphics() {
        int size = localGraphicsNamesList.size();
        if (size == 0) {
            JOptionPane.showMessageDialog(this, "No graphics available to upload!", "WARNING", JOptionPane.WARNING_MESSAGE);
            return;
        }

        HMProgressMonitorDialog monitor = new HMProgressMonitorDialog(this, "Progress", size){
            @Override
            public void processInBackground() throws Exception {
                int prog = 0;
                progressMonitor.setProgress(prog++);

                File graphicsFolder = getGraphicsFolder(null);
                try (EasySession s1 = new EasySession()) {
                    for( String localGraphicName : localGraphicsNamesList ) {
                        File graphicToUpload = new File(graphicsFolder, localGraphicName);
                        String remoteFile = PreKeys.REMOTE_GRAPHICS_PATH + "/" + localGraphicName;
                        setProgressText("Upload graphic: " + localGraphicName);
                        SshHelper.uploadFile(s1.getSession(), graphicToUpload.getAbsolutePath(), remoteFile);
                        progressMonitor.setProgress(prog++);
                    }
                }

            }

            @Override
            public void postDoneInUi() {
                JOptionPane.showMessageDialog(TemplatesController.this, "DONE!", "INFO", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        monitor.run();
    }

    private void backup() {
        HMProgressMonitorDialog monitor = new HMProgressMonitorDialog(this, "Progress", 3){
            @Override
            public void processInBackground() throws Exception {
                int prog = 1;

                File backupFolder = getBackupFolder(null);
                try (EasySession s1 = new EasySession()) {
                    setProgressText("Compressing remote data for backup (this might take a while...)");
                    compress(s1.getSession());
                    progressMonitor.setProgress(prog++);

                    setProgressText("Downloading backup data (this might take a while depending on the size...)");
                    File newBackup = new File(backupFolder, dateFormatter.format(new Date()) + "_backup.tar.gz");
                    SshHelper.downloadFile(s1.getSession(), "~/backup.tar.gz", newBackup.getAbsolutePath());
                    progressMonitor.setProgress(prog++);

                    cleanup(s1.getSession());
                    progressMonitor.setProgress(prog++);
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
//                StringBuilder sb = new StringBuilder();
                byte[] tmp = new byte[1024];
                while( true ) {
                    while( in.available() > 0 ) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0)
                            break;
//                        sb.append(new String(tmp, 0, i));
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
//                StringBuilder sb = new StringBuilder();
                byte[] tmp = new byte[1024];
                while( true ) {
                    while( in.available() > 0 ) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0)
                            break;
//                        sb.append(new String(tmp, 0, i));
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

            @Override
            public void postDoneInUi() {
                JOptionPane.showMessageDialog(TemplatesController.this, "DONE!", "INFO", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        monitor.run();
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
                "       \"iconCode\": \"\ue9fe\"," + //
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
        final JFrame frame = gBridge.showWindow(controller.asJComponent(), "Remarkable Utilities");

        Class<TemplatesController> class1 = TemplatesController.class;
        URL resource = class1.getResource("/com/hydrologis/remarkable/hm150.png");
        ImageIcon icon = new ImageIcon(resource);
        frame.setIconImage(icon.getImage());

        GuiUtilities.addClosingListener(frame, controller);

    }
}
