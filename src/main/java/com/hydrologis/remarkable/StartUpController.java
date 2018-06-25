package com.hydrologis.remarkable;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.hydrologis.remarkable.utils.DefaultGuiBridgeImpl;
import com.hydrologis.remarkable.utils.GuiBridgeHandler;
import com.hydrologis.remarkable.utils.GuiUtilities;
import com.hydrologis.remarkable.utils.GuiUtilities.IOnCloseListener;

public class StartUpController extends StartUpView implements IOnCloseListener {

    private GuiBridgeHandler guiBridge;

    public StartUpController( GuiBridgeHandler guiBridge ) {
        this.guiBridge = guiBridge;
        setPreferredSize(new Dimension(900, 600));
        init();
    }

    private void init() {
        _templatesButton.addActionListener(e->{
            DefaultGuiBridgeImpl gBridge = new DefaultGuiBridgeImpl();
            final TemplatesController controller = new TemplatesController(gBridge);
            final JFrame frame = gBridge.showWindow(controller.asJComponent(), "Remarkable Templates Handler");

//            Class<StartUpController> class1 = StartUpController.class;
//            ImageIcon icon = new ImageIcon(class1.getResource("/org/hortonmachine/images/hm150.png"));
//            frame.setIconImage(icon.getImage());

            GuiUtilities.addClosingListener(frame, controller);
        });
        _graphicsButton.addActionListener(e->{
            
        });

    }

    public JComponent asJComponent() {
        return this;
    }

    public void onClose() {
    }

    public static void main( String[] args ) {

        // GuiUtilities.setDefaultLookAndFeel();

        DefaultGuiBridgeImpl gBridge = new DefaultGuiBridgeImpl();
        final StartUpController controller = new StartUpController(gBridge);
        final JFrame frame = gBridge.showWindow(controller.asJComponent(), "Remarkable Utilities");

//        Class<StartUpController> class1 = StartUpController.class;
//        ImageIcon icon = new ImageIcon(class1.getResource("/org/hortonmachine/images/hm150.png"));
//        frame.setIconImage(icon.getImage());

        GuiUtilities.addClosingListener(frame, controller);

    }
}
