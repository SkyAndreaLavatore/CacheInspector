package cacheinspector.panel;

import cacheinspector.core.InspectorCsv;
import cacheinspector.swing.CheckBoxNode;
import cacheinspector.swing.CheckBoxNodeEditor;
import cacheinspector.swing.CheckBoxNodeRenderer;
import cacheinspector.swing.NamedVector;
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
import java.util.List;
import java.util.*;


public class CacheInspectorCsvTreePanel extends JPanel
{
 @Serial
 private static final long serialVersionUID = -4949484525552546995L;
 JTextField filterField;
 private final Map<String, Set<String>> entityMap;
 public CacheInspectorCsvTreePanel(Map<String, Set<String>> entityMap)
 {
  super(new BorderLayout());
  this.entityMap = entityMap;
  add(viewTree());
 }

 public JPanel viewTree() {
  setEnabled(false);
  JPanel panel = new JPanel(new BorderLayout());
  try{
   CheckBoxNode[] accessibilityOptions = new CheckBoxNode[entityMap.size()];
   int i=0;
   for (String key : entityMap.keySet()) {
    accessibilityOptions[i] = new CheckBoxNode(key + " [" + entityMap.get(key).size() + "]", key, false);
    i++;
   }
   TableModel tableModel = createTableModel(InspectorCsv.getCckMap());
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
   JButton extractFileChooser = new JButton("Extract Ccks");
   extractFileChooser.addActionListener(new OpenFileChooser());
   panelLeft.add(extractFileChooser, BorderLayout.PAGE_END);
   panelCenter.add(panelLeft, BorderLayout.LINE_START);
   JScrollPane tableView = new JScrollPane(table);
   panelCenter.add(tableView, BorderLayout.CENTER);
   panel.add(panelCenter, BorderLayout.CENTER);
  } catch (Exception e) {
   e.printStackTrace();
  }
  return panel;
 }

 private class OpenFileChooser implements ActionListener {
  public OpenFileChooser(){
  }

  public void actionPerformed(ActionEvent e) {
   try {
    String value = filterField.getText();
    Set<String> allCcks = new HashSet<>();

    for(String path:value.split(", "))
      allCcks.addAll(entityMap.get(path));

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
   } catch (Exception ex) {ex.getMessage();}
  }
 }

 private static TableModel createTableModel(Map<String,Map<String, List<String>>> cckMap) {
  Vector<String> columns = new Vector<>(Arrays.asList("Products", "Promotions", "CCK"));
  Vector<Vector<Object>> rows = new Vector<>();
  for (String cck: cckMap.keySet()) {
   Vector<Object> v = new Vector<>();
   Map<String,List<String>> mapElement = cckMap.get(cck);
   if(mapElement.containsKey(InspectorCsv.TYPE_PRODUCTS))
    v.add(cckMap.get(cck).get(InspectorCsv.TYPE_PRODUCTS).toString());
   else
    v.add("");
   if(mapElement.containsKey(InspectorCsv.TYPE_PROMOTIONS))
    v.add(cckMap.get(cck).get(InspectorCsv.TYPE_PROMOTIONS).toString());
   else
    v.add("");
   v.add(cck);
   rows.add(v);
  }

  return new DefaultTableModel(rows, columns);
 }
}