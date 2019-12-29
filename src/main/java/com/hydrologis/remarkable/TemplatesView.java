package com.hydrologis.remarkable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class TemplatesView extends JPanel
{
   JLabel _hostLabel = new JLabel();
   JTextField _hostField = new JTextField();
   JLabel _passwordLabel = new JLabel();
   JPasswordField _passwordField = new JPasswordField();
   JLabel _userLabel = new JLabel();
   JTextField _userField = new JTextField();
   JButton _uploadButton = new JButton();
   JLabel _basefolderLabel = new JLabel();
   JTextField _basefolderField = new JTextField();
   JButton _basefolderButton = new JButton();
   JButton _aboutButton = new JButton();
   JButton _restartRemarkableButton = new JButton();
   JButton _touchButtonToolsButton = new JButton();
   JTable _localTable = new JTable();
   JTextField _remotePathField = new JTextField();
   JTable _remoteTable = new JTable();
   JButton _refreshRemoteTemplatesButton = new JButton();
   JRadioButton _templatesModeButton = new JRadioButton();
   ButtonGroup _buttongroup1 = new ButtonGroup();
   JRadioButton _graphicsModeButton = new JRadioButton();
   JRadioButton _backupModeButton = new JRadioButton();

   /**
    * Default constructor
    */
   public TemplatesView()
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
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:8DLU:GROW(1.0),FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE","CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,FILL:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:GROW(1.0),CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      _hostLabel.setName("hostLabel");
      _hostLabel.setText("Host");
      jpanel1.add(_hostLabel,cc.xy(2,2));

      _hostField.setName("hostField");
      jpanel1.add(_hostField,cc.xywh(4,2,16,1));

      _passwordLabel.setName("passwordLabel");
      _passwordLabel.setText("Password");
      jpanel1.add(_passwordLabel,cc.xy(2,6));

      _passwordField.setName("passwordField");
      jpanel1.add(_passwordField,cc.xywh(4,6,16,1));

      _userLabel.setName("userLabel");
      _userLabel.setText("User");
      jpanel1.add(_userLabel,cc.xy(2,4));

      _userField.setName("userField");
      jpanel1.add(_userField,cc.xywh(4,4,16,1));

      _uploadButton.setActionCommand("Upload local templates");
      _uploadButton.setName("uploadButton");
      _uploadButton.setText("upload");
      jpanel1.add(_uploadButton,cc.xy(2,26));

      _basefolderLabel.setName("basefolderLabel");
      _basefolderLabel.setText("Basefolder");
      jpanel1.add(_basefolderLabel,cc.xy(2,8));

      _basefolderField.setName("basefolderField");
      jpanel1.add(_basefolderField,cc.xywh(4,8,14,1));

      _basefolderButton.setActionCommand("...");
      _basefolderButton.setName("basefolderButton");
      _basefolderButton.setText("...");
      jpanel1.add(_basefolderButton,cc.xy(19,8));

      _aboutButton.setActionCommand("About");
      _aboutButton.setName("aboutButton");
      _aboutButton.setText("About");
      jpanel1.add(_aboutButton,cc.xy(19,26));

      _restartRemarkableButton.setActionCommand("Restart Remarkable");
      _restartRemarkableButton.setName("restartRemarkableButton");
      _restartRemarkableButton.setText("Restart Remarkable");
      jpanel1.add(_restartRemarkableButton,cc.xy(17,26));

      _touchButtonToolsButton.setActionCommand("Touch/Button Tools");
      _touchButtonToolsButton.setName("touchButtonToolsButton");
      _touchButtonToolsButton.setText("Touch/Button Tools");
      jpanel1.add(_touchButtonToolsButton,cc.xy(15,26));

      jpanel1.add(createPanel1(),cc.xywh(2,12,18,13));
      jpanel1.add(createPanel2(),cc.xywh(2,10,18,1));
      addFillComponents(jpanel1,new int[]{ 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20 },new int[]{ 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27 });
      return jpanel1;
   }

   public JPanel createPanel1()
   {
      JPanel jpanel1 = new JPanel();
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE","CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,FILL:DEFAULT:GROW(1.0),CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),CENTER:DEFAULT:NONE");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      JLabel jlabel1 = new JLabel();
      jlabel1.setText("local");
      jpanel1.add(jlabel1,cc.xy(1,1));

      _localTable.setName("localTable");
      EtchedBorder etchedborder1 = new EtchedBorder(EtchedBorder.LOWERED,null,null);
      _localTable.setBorder(etchedborder1);
      JScrollPane jscrollpane1 = new JScrollPane();
      jscrollpane1.setViewportView(_localTable);
      jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jpanel1.add(jscrollpane1,new CellConstraints(1,3,3,1,CellConstraints.FILL,CellConstraints.FILL));

      JLabel jlabel2 = new JLabel();
      jlabel2.setText("remote");
      jpanel1.add(jlabel2,cc.xy(1,5));

      _remotePathField.setName("remotePathField");
      jpanel1.add(_remotePathField,cc.xy(1,6));

      _remoteTable.setName("remoteTable");
      EtchedBorder etchedborder2 = new EtchedBorder(EtchedBorder.LOWERED,null,null);
      _remoteTable.setBorder(etchedborder2);
      JScrollPane jscrollpane2 = new JScrollPane();
      jscrollpane2.setViewportView(_remoteTable);
      jscrollpane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      jscrollpane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jpanel1.add(jscrollpane2,cc.xywh(1,7,3,1));

      _refreshRemoteTemplatesButton.setActionCommand("Refresh");
      _refreshRemoteTemplatesButton.setName("refreshRemoteTemplatesButton");
      _refreshRemoteTemplatesButton.setText("Refresh");
      jpanel1.add(_refreshRemoteTemplatesButton,cc.xy(3,6));

      addFillComponents(jpanel1,new int[]{ 2,3 },new int[]{ 2,4,8 });
      return jpanel1;
   }

   public JPanel createPanel2()
   {
      JPanel jpanel1 = new JPanel();
      TitledBorder titledborder1 = new TitledBorder(null,"Working mode",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,null,new Color(33,33,33));
      jpanel1.setBorder(titledborder1);
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE","CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      _templatesModeButton.setActionCommand("templates");
      _templatesModeButton.setName("templatesModeButton");
      _templatesModeButton.setText("templates");
      _buttongroup1.add(_templatesModeButton);
      jpanel1.add(_templatesModeButton,cc.xy(2,2));

      _graphicsModeButton.setActionCommand("graphics");
      _graphicsModeButton.setName("graphicsModeButton");
      _graphicsModeButton.setText("graphics");
      _buttongroup1.add(_graphicsModeButton);
      jpanel1.add(_graphicsModeButton,cc.xy(5,2));

      _backupModeButton.setActionCommand("backup");
      _backupModeButton.setName("backupModeButton");
      _backupModeButton.setText("backup");
      _buttongroup1.add(_backupModeButton);
      jpanel1.add(_backupModeButton,cc.xy(8,2));

      addFillComponents(jpanel1,new int[]{ 1,2,3,4,5,6,7,8 },new int[]{ 1,2,3 });
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
