package com.hydrologis.remarkable.utils;

import java.awt.Image;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

import com.hydrologis.remarkable.TemplatesController;

public enum IconsHandler {
    INSTANCE;

    public static final String CUSTOM_ICON = "\\ue9fe";

    private HashMap<String, String> iconsMap = new HashMap<>();

    private Image frameIconImage;
    private IconsHandler() {
        iconsMap.put("\ue9fe", "\\ue9fe");
        iconsMap.put("\ue9fd", "\\ue9fd");
        iconsMap.put("\ue9aa", "\\ue9aa");
        iconsMap.put("\ue9ab", "\\ue9ab");
        iconsMap.put("\ue9ac", "\\ue9ac");
        iconsMap.put("\ue9ad", "\\ue9ad");
        iconsMap.put("\ue9b4", "\\ue9b4");
        iconsMap.put("\ue9b6", "\\ue9b6");
        iconsMap.put("\ue9bc", "\\ue9bc");
        iconsMap.put("\ue9c9", "\\ue9c9");
        iconsMap.put("\ue9b7", "\\ue9b7");
        iconsMap.put("\ue9ba", "\\ue9ba");
        iconsMap.put("\ue9b8", "\\ue9b8");
        iconsMap.put("\ue9b9", "\\ue9b9");
        iconsMap.put("\ue9bb", "\\ue9bb");
        iconsMap.put("\ue9c8", "\\ue9c8");
        iconsMap.put("\ue9ca", "\\ue9ca");
        iconsMap.put("\ue9cc", "\\ue9cc");
        iconsMap.put("\ue9cb", "\\ue9cb");
        iconsMap.put("\ue9d4", "\\ue9d4");
        iconsMap.put("\ue9d7", "\\ue9d7");
        iconsMap.put("\ue9cd", "\\ue9cd");
        iconsMap.put("\ue9b5", "\\ue9b5");
        iconsMap.put("\ue997", "\\ue997");
        iconsMap.put("\ue9ce", "\\ue9ce");
        iconsMap.put("\ue9cf", "\\ue9cf");
        iconsMap.put("\ue9d3", "\\ue9d3");
        iconsMap.put("\ue98f", "\\ue98f");
        iconsMap.put("\ue9ff", "\\ue9ff");
        iconsMap.put("\ue991", "\\ue991");
        iconsMap.put("\ue993", "\\ue993");
        iconsMap.put("\ue996", "\\ue996");
        iconsMap.put("\ue995", "\\ue995");
        iconsMap.put("\ue994", "\\ue994");
        iconsMap.put("\ue9f9", "\\ue9f9");
        iconsMap.put("\ue9f8", "\\ue9f8");
        iconsMap.put("\ue999", "\\ue999");
        iconsMap.put("\ue99a", "\\ue99a");
        iconsMap.put("\ue99d", "\\ue99d");
        iconsMap.put("\ue99e", "\\ue99e");
        iconsMap.put("\ue9fc", "\\ue9fc");
        iconsMap.put("\ue9fb", "\\ue9fb");
        iconsMap.put("\ue9fa", "\\ue9fa");
        iconsMap.put("\ue99b", "\\ue99b");
        iconsMap.put("\ue99c", "\\ue99c");
        iconsMap.put("\ue99f", "\\ue99f");
        iconsMap.put("\ue9a5", "\\ue9a5");
        iconsMap.put("\ue9a0", "\\ue9a0");
        iconsMap.put("\ue9a9", "\\ue9a9");
        iconsMap.put("\ue9a6", "\\ue9a6");
        iconsMap.put("\ue9a7", "\\ue9a7");
        iconsMap.put("\ue9a8", "\\ue9a8");
        iconsMap.put("\ue9d0", "\\ue9d0");
        iconsMap.put("\ue9d1", "\\ue9d1");
        iconsMap.put("\ue9d2", "\\ue9d2");
        iconsMap.put("\ue9d8", "\\ue9d8");
        iconsMap.put("\ue9d9", "\\ue9d9");
        iconsMap.put("\ue9dc", "\\ue9dc");
        iconsMap.put("\ue9da", "\\ue9da");
        iconsMap.put("\ue9db", "\\ue9db");
        iconsMap.put("\uea00", "\\uea00");
        iconsMap.put("\ue9d5", "\\ue9d5");
        iconsMap.put("\ue9d6", "\\ue9d6");
        iconsMap.put("\ue9fe", "\\ue9fe");
        iconsMap.put("\ue9fe", "\\ue9fe");
        iconsMap.put("\ue9fe", "\\ue9fe");
        iconsMap.put("\ue9fe", "\\ue9fe");
    }

    public String getIconString( String code ) {
        return iconsMap.get(code);
    }

    public Image getFrameIcon() {
        if (frameIconImage == null) {
            Class<TemplatesController> class1 = TemplatesController.class;
            URL resource = class1.getResource("/com/hydrologis/remarkable/hm150.png");
            ImageIcon icon = new ImageIcon(resource);
            frameIconImage = icon.getImage();
        }
        return frameIconImage;
    }

}
