package com.hydrologis.remarkable;

public interface PreKeys {
    String HOST = "REMARKABLE_HOST";
    String USER = "REMARKABLE_USER";
    String PWD = "REMARKABLE_PWD";
    String LOCAL_BASEPATH = "REMARKABLE_LOCAL_BASEPATH";

    String REMOTE_TEMPLATES_PATH = "/usr/share/remarkable/templates";
    String REMOTE_GRAPHICS_PATH = "/usr/share/remarkable/";
    String REMOTE_BACKUP_PATH = "/home/root/.local/share/remarkable/xochitl/";

    String REMOTE_HOME = "/home/root/";
    String REMOTE_SYSTEMD_PATH = "/etc/systemd/system/";
    String BUTTON_NAME = "button_toggler";
    String BUTTON_SERVICE_NAME = BUTTON_NAME + ".service";
    String TOUCH_NAME = "touchToggler";
    String TOUCH_SERVICE_NAME = TOUCH_NAME + ".service";

    String CLOUD_TOKEN = "REMARKABLE_CLOUD_TOKEN";

}
