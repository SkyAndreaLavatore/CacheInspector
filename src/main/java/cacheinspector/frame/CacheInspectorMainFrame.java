package cacheinspector.frame;

import javax.swing.*;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CacheInspectorMainFrame extends JFrame
{
 private static final String CLASS_NAME = "CacheInspectorMainFrame";
 private static final String START = "Start";
 private static final String EXIT = "Exit";
 private CacheInspectorPanelFrame panel;
 private static final Logger logger = Logger.getLogger("CacheInspector.log");
 public CacheInspectorMainFrame()
 {
  String METHOD_NAME = "CacheInspectorMainFrame";
  FileHandler fh;
  try {
   fh = new FileHandler("CacheInspector.log");
   logger.addHandler(fh);
   logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Create Frame " + START);
   setSize(1250, 800);
   setLocation(0, 0);
   setTitle("Cache Inspector v2.0");
   addMenu();
  } catch (IOException e) {
   System.out.println("Exception: " + e.getMessage());
  }
  logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Create Frame " + EXIT);
 }
 public void addMenu()
 {
  String METHOD_NAME = "addMenu";
  logger.info(CLASS_NAME + '.' + METHOD_NAME + START);
  JMenuBar menuBar = new JMenuBar();
  JMenu menu = new JMenu("File");
  JMenuItem menuItemScan = new JMenuItem("Select File...");
  menuItemScan.addActionListener(arg0 -> {
   JFileChooser chooser = new JFileChooser();
   int returnVal = chooser.showOpenDialog(null);
   if (returnVal == JFileChooser.APPROVE_OPTION) {
    logger.info(CLASS_NAME + '.' + METHOD_NAME + " Select file: " + chooser.getSelectedFile().getPath());
    Container cont = getContentPane();
    cont.removeAll();
    panel = new CacheInspectorPanelFrame(this, chooser.getSelectedFile().getPath(), logger);
    cont.add(panel);
    panel.revalidate();
   }
  });
  menu.add(menuItemScan);
  menu.addSeparator();
  JMenuItem poweredby = new JMenuItem("Support alavatore@salesforce.com");
  menu.add(poweredby);
  menu.addSeparator();
  JMenuItem menuItemEsci = new JMenuItem("Esci    Alt+F4");
  menuItemEsci.addActionListener(arg0 -> System.exit(0));
  menu.add(menuItemEsci);
  menuBar.add(menu);

  List<String> listPropertyFiles = listFilesUsingJavaIO("config/");
  Vector<String> vectors = new Vector<>();
  for(String value:listPropertyFiles) {
   if(value.endsWith(".properties")) {
    System.out.println("Read file:" + value);
    String text = value.substring(0, value.indexOf("."));
    vectors.add(text);
   }
  }
  logger.info(CLASS_NAME + '.' + METHOD_NAME + " Read config files: " + vectors);

  JCheckBox[] jCheckBoxList = new JCheckBox[vectors.size()];
  int i;
  for(i=0;i<vectors.size();i++) {
   jCheckBoxList[i] = new JCheckBox(vectors.get(i));
  }
  for(i=0;i<jCheckBoxList.length;i++){
   int finalI = i;
   jCheckBoxList[i].addItemListener(e -> {
    if(e.getStateChange()== ItemEvent.SELECTED) {
     for (int c = 0; c < jCheckBoxList.length; c++) {
      if (c != finalI) {
       if (jCheckBoxList[c].isSelected())
        jCheckBoxList[c].setSelected(false);
      }
     }
    }
   });
  }
  for (JCheckBox jCheckBox : jCheckBoxList) {
   menuBar.add(jCheckBox);
  }
  JButton getButton = new JButton("Get....");
  getButton.addActionListener(e -> {
    Properties property;
    String env = null;
    for (JCheckBox jCheckBox : jCheckBoxList) {
     if (jCheckBox.isSelected()) {
      env = jCheckBox.getText();
      break;
     }
    }
    logger.info(CLASS_NAME + '.' + METHOD_NAME + " Get from: " + env);
    if (env != null && !env.isEmpty()) try (InputStream input = new FileInputStream("config/" + env + ".properties")) {
     property = new Properties();
     //load a properties file from class path, inside static method
     property.load(input);
     //get the property value and print it out
     if (property.getProperty("username") != null
             && property.getProperty("password") != null
             && property.getProperty("clientId") != null
             && property.getProperty("clientSecret") != null
             && property.getProperty("loginUrl") != null) {
      logger.info(CLASS_NAME + '.' + METHOD_NAME + " Get Credentials: username(" + property.getProperty("username") + ") clientId(" + property.getProperty("clientId") + ") url(" + property.getProperty("loginUrl") + ")");
      Salesforce.getClientCredentials(property);
      panel = new CacheInspectorPanelFrame(this, logger);
      Container cont = getContentPane();
      cont.removeAll();
      cont.add(panel);
      panel.revalidate();
     }
    } catch (Exception ex) {
     System.out.println("Unable to read property file" + ex.getMessage());
     logger.info(CLASS_NAME + '.' + METHOD_NAME + "Unable to read property file" + ex.getMessage());
    }
  });
  menuBar.add(getButton);
  setJMenuBar(menuBar);
  logger.info(CLASS_NAME + '.' + METHOD_NAME + EXIT);
 }

  public List<String> listFilesUsingJavaIO(String dir) {
  return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
          .filter(file -> !file.isDirectory())
          .map(File::getName)
          .collect(Collectors.toList());
 }
}