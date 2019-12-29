package com.hydrologis.remarkable;

import java.net.MalformedURLException;
import java.net.URL;

import com.hydrologis.remarkable.utils.GuiUtilities;
import com.hydrologis.remarkable.utils.TouchButtonToolsHandler;

public class TouchButtonToolsController extends TouchButtonToolsView {
    private static final long serialVersionUID = 1L;

    public TouchButtonToolsController() {
        init();
    }

    private void init() {

        _buttonToggleReadmeButton.addActionListener(e -> {
            try {
                GuiUtilities.openWebpage(new URL("https://github.com/LinusCDE/rmButtonToggler#important"));
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        });

        _buttonToggleInstallButton.addActionListener(e -> {
            TouchButtonToolsHandler.INSTANCE.installButtonTool();
        });
        _buttonToggleUninstallButton.addActionListener(e -> {
            TouchButtonToolsHandler.INSTANCE.uninstallButtonTool();
        });

        _touchToggleReadmeButton.addActionListener(e -> {
            try {
                GuiUtilities.openWebpage(new URL("https://github.com/LinusCDE/rmTouchToggler#rmtouchtoggler"));
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        });
        
        _touchToggleInstallButton.addActionListener(e -> {
            TouchButtonToolsHandler.INSTANCE.installTouchTool();
        });
        _touchToggleUninstallButton.addActionListener(e -> {
            TouchButtonToolsHandler.INSTANCE.uninstallTouchTool();
        });

    }

}
