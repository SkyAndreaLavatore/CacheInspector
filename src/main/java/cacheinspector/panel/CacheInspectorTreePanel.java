package cacheinspector.panel;

import cacheinspector.core.Inspector;
import cacheinspector.frame.Salesforce;
import cacheinspector.swing.*;
import cacheinspector.utility.RowFilterUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Serial;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;


public class CacheInspectorTreePanel extends JPanel
{
 @Serial
 private static final long serialVersionUID = -4949484525552546995L;
 private static final String CLASS_NAME = "CacheInspectorTreePanel";
 private static final int maxdelete = 200;
 private JFrame frame;
 private JTextField filterField;
 private Logger logger;
 private final Map<String, Set<String>> entityMap;
 public CacheInspectorTreePanel(JFrame frame, Map<String, Set<String>> entityMap, Logger logger)
 {
  super(new BorderLayout());
  this.entityMap = entityMap;
  this.frame = frame;
  this.logger = logger;
  add(viewTree());
 }

 public JPanel viewTree() {
  String METHOD_NAME = "viewTree";
  setEnabled(false);
  JPanel panel = new JPanel(new BorderLayout());
  try{
   CheckBoxNode[] accessibilityOptions = new CheckBoxNode[entityMap.size()];
   int i=0;
   for (String key : entityMap.keySet()) {
    accessibilityOptions[i] = new CheckBoxNode(key + " [" + entityMap.get(key).size() + "]", key, false);
    i++;
   }
   TableModel tableModel = createTableModel(Inspector.getCckMap());
   JTable table = new JTable(tableModel);
   filterField = RowFilterUtil.createRowFilter(table);
   CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer(filterField);
   Vector rootVector = new NamedVector("Root", accessibilityOptions);
   JTree tree = new JTree(rootVector);
   tree.setCellRenderer(renderer);
   tree.setCellEditor(new CheckBoxNodeEditor(tree));
   tree.setEditable(true);
   tree.getExpandsSelectedPaths();
   JPanel panelTop = new JPanel(new BorderLayout());
   panelTop.add(filterField, BorderLayout.SOUTH);
   panel.add(panelTop, BorderLayout.NORTH);
   JScrollPane treeView = new JScrollPane(tree);
   JPanel panelCenter = new JPanel(new BorderLayout());
   JPanel panelLeft = new JPanel(new BorderLayout());
   panelLeft.add(treeView, BorderLayout.LINE_START);
   JPanel panelBottomLeft = new JPanel(new BorderLayout());
   JButton extractFileChooser = new JButton("Extract Ccks");
   extractFileChooser.addActionListener(new ExtractCcks());
   panelBottomLeft.add(extractFileChooser, BorderLayout.PAGE_START);
   JButton deleteChooser = new JButton("Delete Ccks");
   deleteChooser.addActionListener(new DeleteCcks());
   panelBottomLeft.add(deleteChooser, BorderLayout.PAGE_END);
   panelLeft.add(panelBottomLeft, BorderLayout.PAGE_END);
   panelCenter.add(panelLeft, BorderLayout.LINE_START);
   JScrollPane tableView = new JScrollPane(table);
   panelCenter.add(tableView, BorderLayout.CENTER);
   panel.add(panelCenter, BorderLayout.CENTER);
  } catch (Exception e) {
   logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Exception " + e.getMessage());
  }
  return panel;
 }

 private class ExtractCcks implements ActionListener {
  String METHOD_NAME = "ExtractCcks";
  public ExtractCcks(){
  }

  public void actionPerformed(ActionEvent e) {
   try {
    String value = filterField.getText();
    Set<String> allCcks = new HashSet<>();

    for(String path:value.split(", "))
      allCcks.addAll(entityMap.get(path));
    logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** extract: " + allCcks.size() + " ccks");
    JFileChooser fileChooser = new JFileChooser();
    int r = fileChooser.showSaveDialog(null);
    if(r==0) {
     BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile(), true));
     for(String cck:allCcks) {
      writer.append(cck);
      writer.newLine();
     }
     writer.close();
    }
   } catch (Exception ex) {logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Exception: " + ex.getMessage());}
  }
 }

 private class DeleteCcks implements ActionListener,Runnable {
  private String CLASS_NAME = "DeleteCcks";
  List<Set<String>> listCck;
  int count;
  public DeleteCcks(){
  }

  public void actionPerformed(ActionEvent e) {
    String METHOD_NAME = "actionPerformed";
    String value = filterField.getText();
    Set<String> allCcks = new HashSet<>();

    for (String path : value.split(", "))
     allCcks.addAll(entityMap.get(path));

    listCck = new ArrayList<>();
    int i = 0;
    Set<String> setCck = null;
    count = allCcks.size();
   logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** " + count + " will be deleted");
   int result = JOptionPane.showConfirmDialog(frame,"Do you want to delete " + count + " ccks from Salesforce cache? \n (the related records in the CacheInspector will also be deleted)", "Delete Ccks(" + count + ")",
           JOptionPane.YES_NO_OPTION,
           JOptionPane.QUESTION_MESSAGE);
   if(result == JOptionPane.YES_OPTION) {
    for (String ids : allCcks) {
     if (i == 0) setCck = new HashSet<>();
     setCck.add(ids);
     if (i == maxdelete - 1) {
      i = 0;
      listCck.add(setCck);
     } else
      i++;
    }
    if (i < maxdelete && setCck != null && !setCck.isEmpty()) listCck.add(setCck);
    logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Deletion started");
    Thread thread1 = new Thread(this);
    thread1.start();
   }
  }

  @Override
  public void run() {
   String METHOD_NAME = "run";
    SwingProgressBar swingProgressBar = new SwingProgressBar(frame.getLocationOnScreen().x+frame.getSize().width/3 , frame.getLocationOnScreen().y+frame.getSize().height/3, count);
    logger.info(CLASS_NAME + '.' + METHOD_NAME + " Start");
    Map<String, Set<String>> mapReturn = Salesforce.queryAndDeleteCachedAPIResponse(listCck, swingProgressBar);
    JFrame fDialog= new JFrame();
    fDialog.setLocation(frame.getLocationOnScreen().x+frame.getSize().width/3, frame.getLocationOnScreen().y+frame.getSize().height/3);
    fDialog.setSize(200,30);
    fDialog.setUndecorated(true);
    fDialog.setAlwaysOnTop(true);
    JDialog d = new JDialog(fDialog , "Result", true);
    d.setLayout( new FlowLayout() );
    JButton b = new JButton ("OK");
    b.addActionListener ( new ActionListener()
    {
     public void actionPerformed( ActionEvent e )
     {
      d.setVisible(false);
     }
    });
    if(!mapReturn.isEmpty()) {
     int success=0,fail=0;
     if(mapReturn.get("success")!=null && !mapReturn.get("success").isEmpty())
      success= mapReturn.get("success").size();
     if(mapReturn.get("fail")!=null && !mapReturn.get("fail").isEmpty())
      fail= mapReturn.get("fail").size();
     d.add(new JLabel("Cck deleted: " + success));
     d.add(new JLabel("Cck failed: " + fail));
     d.add(new JLabel("Reload the page with the top button \"Get\""));
    }
    d.add(b);
    d.setSize(300,300);
    d.setVisible(true);
   logger.info(CLASS_NAME + '.' + METHOD_NAME + " Exit");
  }
 }

 private static TableModel createTableModel(Map<String,Map<String, List<String>>> cckMap) {
  Vector<String> columns = new Vector<>(Arrays.asList("Products", "Promotions", "Catalogs", "CCK"));
  Vector<Vector<Object>> rows = new Vector<>();
  for (String cck: cckMap.keySet()) {
   Vector<Object> v = new Vector<>();
   Map<String,List<String>> mapElement = cckMap.get(cck);
   if(mapElement.containsKey(Inspector.TYPE_PRODUCTS))
    v.add(cckMap.get(cck).get(Inspector.TYPE_PRODUCTS).toString());
   else
    v.add("");
   if(mapElement.containsKey(Inspector.TYPE_PROMOTIONS))
    v.add(cckMap.get(cck).get(Inspector.TYPE_PROMOTIONS).toString());
   else
    v.add("");
   v.add(cckMap.get(cck).get(Inspector.TYPE_CATALOGS).toString());
   v.add(cck);
   rows.add(v);
  }

  return new DefaultTableModel(rows, columns);
 }
}