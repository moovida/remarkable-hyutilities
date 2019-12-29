package com.hydrologis.remarkable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class TouchButtonToolsView extends JPanel
{
   JButton _buttonToggleInstallButton = new JButton();
   JButton _buttonToggleUninstallButton = new JButton();
   JButton _buttonToggleReadmeButton = new JButton();
   JButton _touchToggleReadmeButton = new JButton();
   JButton _touchToggleInstallButton = new JButton();
   JButton _touchToggleUninstallButton = new JButton();

   /**
    * Default constructor
    */
   public TouchButtonToolsView()
   {
      initializePanel();
   }

   /**
    * Adds fill components to empty cells in the first row and first column of the grid.
    * This ensures that the grid spacing will be the same as shown in the designer.
    * @param cols an array of column indices in the first row where fill components should be added.
    * @param rows an array of row indices in the first column where fill components should be added.
    */
   void addFillComponents( Container panel, int[] cols, int[] rows )
   {
      Dimension filler = new Dimension(10,10);

      boolean filled_cell_11 = false;
      CellConstraints cc = new CellConstraints();
      if ( cols.length > 0 && rows.length > 0 )
      {
         if ( cols[0] == 1 && rows[0] == 1 )
         {
            /** add a rigid area  */
            panel.add( Box.createRigidArea( filler ), cc.xy(1,1) );
            filled_cell_11 = true;
         }
      }

      for( int index = 0; index < cols.length; index++ )
      {
         if ( cols[index] == 1 && filled_cell_11 )
         {
            continue;
         }
         panel.add( Box.createRigidArea( filler ), cc.xy(cols[index],1) );
      }

      for( int index = 0; index < rows.length; index++ )
      {
         if ( rows[index] == 1 && filled_cell_11 )
         {
            continue;
         }
         panel.add( Box.createRigidArea( filler ), cc.xy(1,rows[index]) );
      }

   }

   /**
    * Helper method to load an image file from the CLASSPATH
    * @param imageName the package and name of the file to load relative to the CLASSPATH
    * @return an ImageIcon instance with the specified image file
    * @throws IllegalArgumentException if the image resource cannot be loaded.
    */
   public ImageIcon loadImage( String imageName )
   {
      try
      {
         ClassLoader classloader = getClass().getClassLoader();
         java.net.URL url = classloader.getResource( imageName );
         if ( url != null )
         {
            ImageIcon icon = new ImageIcon( url );
            return icon;
         }
      }
      catch( Exception e )
      {
         e.printStackTrace();
      }
      throw new IllegalArgumentException( "Unable to load image: " + imageName );
   }

   /**
    * Method for recalculating the component orientation for 
    * right-to-left Locales.
    * @param orientation the component orientation to be applied
    */
   public void applyComponentOrientation( ComponentOrientation orientation )
   {
      // Not yet implemented...
      // I18NUtils.applyComponentOrientation(this, orientation);
      super.applyComponentOrientation(orientation);
   }

   public JPanel createPanel()
   {
      JPanel jpanel1 = new JPanel();
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE","CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:GROW(1.0),CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      JLabel jlabel1 = new JLabel();
      jlabel1.setFont(new Font("Noto Sans",Font.PLAIN,28));
      jlabel1.setText("Button Toggler");
      jlabel1.setHorizontalAlignment(JLabel.CENTER);
      jpanel1.add(jlabel1,cc.xywh(2,2,18,1));

      JLabel jlabel2 = new JLabel();
      jlabel2.setFont(new Font("Noto Sans",Font.PLAIN,28));
      jlabel2.setText("Touch Toggler");
      jlabel2.setHorizontalAlignment(JLabel.CENTER);
      jpanel1.add(jlabel2,cc.xywh(2,20,18,1));

      _buttonToggleInstallButton.setActionCommand("install");
      _buttonToggleInstallButton.setName("buttonToggleInstallButton");
      _buttonToggleInstallButton.setText("install");
      jpanel1.add(_buttonToggleInstallButton,cc.xywh(5,10,12,1));

      _buttonToggleUninstallButton.setActionCommand("install");
      _buttonToggleUninstallButton.setName("buttonToggleUninstallButton");
      _buttonToggleUninstallButton.setText("uninstall");
      jpanel1.add(_buttonToggleUninstallButton,cc.xywh(5,12,12,1));

      _buttonToggleReadmeButton.setActionCommand("install");
      _buttonToggleReadmeButton.setName("buttonToggleReadmeButton");
      _buttonToggleReadmeButton.setText("AUTHOR'S WEBSITE WITH INFO");
      jpanel1.add(_buttonToggleReadmeButton,cc.xywh(5,8,12,1));

      _touchToggleReadmeButton.setActionCommand("install");
      _touchToggleReadmeButton.setName("touchToggleReadmeButton");
      _touchToggleReadmeButton.setText("AUTHOR'S WEBSITE WITH INFO");
      jpanel1.add(_touchToggleReadmeButton,cc.xywh(5,26,12,1));

      _touchToggleInstallButton.setActionCommand("install");
      _touchToggleInstallButton.setName("touchToggleInstallButton");
      _touchToggleInstallButton.setText("install");
      jpanel1.add(_touchToggleInstallButton,cc.xywh(5,28,12,1));

      _touchToggleUninstallButton.setActionCommand("install");
      _touchToggleUninstallButton.setName("touchToggleUninstallButton");
      _touchToggleUninstallButton.setText("uninstall");
      jpanel1.add(_touchToggleUninstallButton,cc.xywh(5,30,12,1));

      JLabel jlabel3 = new JLabel();
      jlabel3.setFont(new Font("Noto Sans",Font.PLAIN,16));
      jlabel3.setText("This tool allows to toggle physical buttons enablement");
      jlabel3.setHorizontalAlignment(JLabel.CENTER);
      jpanel1.add(jlabel3,cc.xywh(4,4,14,1));

      JLabel jlabel4 = new JLabel();
      jlabel4.setFont(new Font("Noto Sans",Font.PLAIN,16));
      jlabel4.setText("This tool allows to toggle finger touch enablement");
      jlabel4.setHorizontalAlignment(JLabel.CENTER);
      jpanel1.add(jlabel4,cc.xywh(4,22,14,1));

      JLabel jlabel5 = new JLabel();
      jlabel5.setFont(new Font("Noto Sans",Font.PLAIN,16));
      jlabel5.setText("by pressing the left and right buttons together.");
      jlabel5.setHorizontalAlignment(JLabel.CENTER);
      jpanel1.add(jlabel5,cc.xywh(4,6,14,1));

      JLabel jlabel6 = new JLabel();
      jlabel6.setFont(new Font("Noto Sans",Font.PLAIN,16));
      jlabel6.setText("by pressing the left and right buttons together.");
      jlabel6.setHorizontalAlignment(JLabel.CENTER);
      jpanel1.add(jlabel6,cc.xywh(4,24,14,1));

      JLabel jlabel7 = new JLabel();
      jlabel7.setFont(new Font("Noto Sans",Font.PLAIN,16));
      jlabel7.setForeground(new Color(255,0,0));
      jlabel7.setText("This needs a manual restart of the remarkable to apply.");
      jlabel7.setHorizontalAlignment(JLabel.CENTER);
      jpanel1.add(jlabel7,cc.xywh(4,14,14,1));

      JLabel jlabel8 = new JLabel();
      jlabel8.setFont(new Font("Noto Sans",Font.PLAIN,16));
      jlabel8.setForeground(new Color(255,0,0));
      jlabel8.setText("This needs a manual restart of the remarkable to apply.");
      jlabel8.setHorizontalAlignment(JLabel.CENTER);
      jpanel1.add(jlabel8,cc.xywh(4,32,14,1));

      addFillComponents(jpanel1,new int[]{ 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20 },new int[]{ 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34 });
      return jpanel1;
   }

   /**
    * Initializer
    */
   protected void initializePanel()
   {
      setLayout(new BorderLayout());
      add(createPanel(), BorderLayout.CENTER);
   }


}
