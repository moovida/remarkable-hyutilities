package com.hydrologis.remarkable.utils;

import java.io.File;

import com.hydrologis.remarkable.EasySession;
import com.hydrologis.remarkable.PreKeys;
import com.hydrologis.remarkable.SshHelper;
import com.hydrologis.remarkable.progress.executor.ExecutorProgressGui;
import com.hydrologis.remarkable.progress.executor.ProgressUpdate;

public enum TouchButtonToolsHandler {
    INSTANCE;

    private String MAKE_BUTTON_EXE_COMMAND = "chmod 755 " + PreKeys.REMOTE_HOME + PreKeys.BUTTON_NAME;
    private String DELETE_BUTTON_EXE_COMMAND = "rm " + PreKeys.REMOTE_HOME + PreKeys.BUTTON_NAME;
    private String DELETE_BUTTON_SERVICE_COMMAND = "rm " + PreKeys.REMOTE_SYSTEMD_PATH + PreKeys.BUTTON_SERVICE_NAME;

    private String START_SERVICE_BUTTON_COMMAND = "systemctl enable " + PreKeys.BUTTON_NAME;
    private String STOP_SERVICE_BUTTON_COMMAND = "systemctl disable " + PreKeys.BUTTON_NAME;

    private String MAKE_TOUCH_EXE_COMMAND = "chmod 755 " + PreKeys.REMOTE_HOME + PreKeys.TOUCH_NAME;
    private String DELETE_TOUCH_EXE_COMMAND = "rm " + PreKeys.REMOTE_HOME + PreKeys.TOUCH_NAME;
    private String DELETE_TOUCH_SERVICE_COMMAND = "rm " + PreKeys.REMOTE_SYSTEMD_PATH + PreKeys.TOUCH_SERVICE_NAME;

    private String START_SERVICE_TOUCH_COMMAND = "systemctl enable " + PreKeys.TOUCH_NAME;
    private String STOP_SERVICE_TOUCH_COMMAND = "systemctl disable " + PreKeys.TOUCH_NAME;

    private File buttonExec;
    private File buttonService;
    private File touchExec;
    private File touchService;

    public boolean checkToolsPaths() {
        File here = new File(".");
        System.out.println(here.getAbsolutePath());
        buttonExec = new File(here, "deploy/extras/" + PreKeys.BUTTON_NAME);
        buttonService = new File(here, "deploy/extras/" + PreKeys.BUTTON_SERVICE_NAME);
        touchExec = new File(here, "deploy/extras/" + PreKeys.TOUCH_NAME);
        touchService = new File(here, "deploy/extras/" + PreKeys.TOUCH_SERVICE_NAME);
        if (!buttonExec.exists()) {
            buttonExec = new File(here, "extras/" + PreKeys.BUTTON_NAME);
            buttonService = new File(here, "extras/" + PreKeys.BUTTON_SERVICE_NAME);
            touchExec = new File(here, "extras/" + PreKeys.TOUCH_NAME);
            touchService = new File(here, "extras/" + PreKeys.TOUCH_SERVICE_NAME);
        }
        return buttonExec.exists();
    }

    public void installButtonTool() {
        new ExecutorProgressGui(6){
            @Override
            public void backGroundWork() throws Exception {
                int prog = 1;
                publish(new ProgressUpdate("Connect to device...", prog++));
                try (EasySession session = new EasySession()) {
                    publish(new ProgressUpdate("Upload exec...", prog++));
                    SshHelper.uploadFile(session.getSession(), buttonExec.getAbsolutePath(),
                            PreKeys.REMOTE_HOME + buttonExec.getName());

                    publish(new ProgressUpdate("Make executable...", prog++));
                    SshHelper.launchSshCommand(session.getSession(), MAKE_BUTTON_EXE_COMMAND);

                    publish(new ProgressUpdate("Upload service...", prog++));
                    SshHelper.uploadFile(session.getSession(), buttonService.getAbsolutePath(),
                            PreKeys.REMOTE_SYSTEMD_PATH + buttonService.getName());

                    publish(new ProgressUpdate("Enable service...", prog++));
                    SshHelper.launchSshCommand(session.getSession(), START_SERVICE_BUTTON_COMMAND);

                    publish(new ProgressUpdate("Done.", prog++));
                }
            }
        }.execute();
    }

    public void uninstallButtonTool() {
        new ExecutorProgressGui(5){
            @Override
            public void backGroundWork() throws Exception {
                int prog = 1;
                publish(new ProgressUpdate("Connect to device...", prog++));
                try (EasySession session = new EasySession()) {
                    publish(new ProgressUpdate("Disable service...", prog++));
                    SshHelper.launchSshCommand(session.getSession(), STOP_SERVICE_BUTTON_COMMAND);

                    publish(new ProgressUpdate("Remove exec file...", prog++));
                    SshHelper.launchSshCommand(session.getSession(), DELETE_BUTTON_EXE_COMMAND);

                    publish(new ProgressUpdate("Remove service file...", prog++));
                    SshHelper.launchSshCommand(session.getSession(), DELETE_BUTTON_SERVICE_COMMAND);

                    publish(new ProgressUpdate("Done.", prog++));
                }
            }
        }.execute();
    }

    public void installTouchTool() {
        new ExecutorProgressGui(6){
            @Override
            public void backGroundWork() throws Exception {
                int prog = 1;
                publish(new ProgressUpdate("Connect to device...", prog++));
                try (EasySession session = new EasySession()) {
                    publish(new ProgressUpdate("Upload exec...", prog++));
                    SshHelper.uploadFile(session.getSession(), touchExec.getAbsolutePath(),
                            PreKeys.REMOTE_HOME + touchExec.getName());

                    publish(new ProgressUpdate("Make executable...", prog++));
                    SshHelper.launchSshCommand(session.getSession(), MAKE_TOUCH_EXE_COMMAND);

                    publish(new ProgressUpdate("Upload service...", prog++));
                    SshHelper.uploadFile(session.getSession(), touchService.getAbsolutePath(),
                            PreKeys.REMOTE_SYSTEMD_PATH + touchService.getName());

                    publish(new ProgressUpdate("Enable service...", prog++));
                    SshHelper.launchSshCommand(session.getSession(), START_SERVICE_TOUCH_COMMAND);

                    publish(new ProgressUpdate("Done.", prog++));
                }
            }
        }.execute();
    }

    public void uninstallTouchTool() {
        new ExecutorProgressGui(5){
            @Override
            public void backGroundWork() throws Exception {
                int prog = 1;
                publish(new ProgressUpdate("Connect to device...", prog++));
                try (EasySession session = new EasySession()) {
                    publish(new ProgressUpdate("Disable service...", prog++));
                    SshHelper.launchSshCommand(session.getSession(), STOP_SERVICE_TOUCH_COMMAND);

                    publish(new ProgressUpdate("Remove exec file...", prog++));
                    SshHelper.launchSshCommand(session.getSession(), DELETE_TOUCH_EXE_COMMAND);

                    publish(new ProgressUpdate("Remove service file...", prog++));
                    SshHelper.launchSshCommand(session.getSession(), DELETE_TOUCH_SERVICE_COMMAND);

                    publish(new ProgressUpdate("Done.", prog++));
                }
            }
        }.execute();
    }

}
