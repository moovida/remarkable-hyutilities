package com.hydrologis.remarkable;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;


public class TemplatesView extends JPanel
{
   JLabel _hodstLabel = new JLabel();
   JTextField _hostField = new JTextField();
   JPasswordField _passwordField = new JPasswordField();
   JTextField _localPathField = new JTextField();
   JButton _localPathButton = new JButton();
   JTextField _remotePathField = new JTextField();
   JTable _localTemplatesTable = new JTable();
   JTable _remoteTemplatesTable = new JTable();
   JButton _connectButton = new JButton();
   JLabel _userLabel = new JLabel();
   JTextField _userField = new JTextField();
   JButton _uploadButton = new JButton();
   JButton _downloadButton = new JButton();

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
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:8DLU:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE","CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:GROW(1.0),CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      _hodstLabel.setName("hodstLabel");
      _hodstLabel.setText("Host");
      jpanel1.add(_hodstLabel,cc.xy(2,2));

      _hostField.setName("hostField");
      jpanel1.add(_hostField,cc.xywh(4,2,14,1));

      JLabel jlabel1 = new JLabel();
      jlabel1.setText("Password");
      jpanel1.add(jlabel1,cc.xy(2,6));

      _passwordField.setName("passwordField");
      jpanel1.add(_passwordField,cc.xywh(4,6,14,1));

      jpanel1.add(createPanel1(),cc.xywh(2,9,18,13));
      _connectButton.setActionCommand("Connect");
      _connectButton.setName("connectButton");
      _connectButton.setText("Connect");
      jpanel1.add(_connectButton,cc.xywh(19,2,1,5));

      _userLabel.setName("userLabel");
      _userLabel.setText("User");
      jpanel1.add(_userLabel,cc.xy(2,4));

      _userField.setName("userField");
      jpanel1.add(_userField,cc.xywh(4,4,14,1));

      _uploadButton.setActionCommand("Upload local templates");
      _uploadButton.setName("uploadButton");
      _uploadButton.setText("Upload local templates");
      jpanel1.add(_uploadButton,cc.xy(2,22));

      _downloadButton.setActionCommand("Download remote templates");
      _downloadButton.setName("downloadButton");
      _downloadButton.setText("Download remote templates");
      jpanel1.add(_downloadButton,cc.xy(19,22));

      addFillComponents(jpanel1,new int[]{ 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20 },new int[]{ 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22 });
      return jpanel1;
   }

   public JPanel createPanel1()
   {
      JPanel jpanel1 = new JPanel();
      TitledBorder titledborder1 = new TitledBorder(null,"Templates",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,null,new Color(33,33,33));
      jpanel1.setBorder(titledborder1);
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE","CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,FILL:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      JLabel jlabel1 = new JLabel();
      jlabel1.setText("local");
      jpanel1.add(jlabel1,cc.xy(1,1));

      JLabel jlabel2 = new JLabel();
      jlabel2.setText("remote");
      jpanel1.add(jlabel2,cc.xy(5,1));

      _localPathField.setName("localPathField");
      jpanel1.add(_localPathField,cc.xy(1,3));

      _localPathButton.setActionCommand("...");
      _localPathButton.setName("localPathButton");
      _localPathButton.setText("...");
      jpanel1.add(_localPathButton,cc.xy(3,3));

      _remotePathField.setName("remotePathField");
      jpanel1.add(_remotePathField,cc.xywh(5,3,3,1));

      _localTemplatesTable.setName("localTemplatesTable");
      JScrollPane jscrollpane1 = new JScrollPane();
      jscrollpane1.setViewportView(_localTemplatesTable);
      jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jpanel1.add(jscrollpane1,new CellConstraints(1,5,3,1,CellConstraints.FILL,CellConstraints.FILL));

      _remoteTemplatesTable.setName("remoteTemplatesTable");
      JScrollPane jscrollpane2 = new JScrollPane();
      jscrollpane2.setViewportView(_remoteTemplatesTable);
      jscrollpane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      jscrollpane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jpanel1.add(jscrollpane2,new CellConstraints(5,5,3,1,CellConstraints.FILL,CellConstraints.FILL));

      addFillComponents(jpanel1,new int[]{ 2,3,4,6,7 },new int[]{ 2,4 });
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
