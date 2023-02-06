package cacheinspector.frame;

import cacheinspector.swing.CustomCellRenderer;
import cacheinspector.swing.PopupInfo;
import cacheinspector.utility.ReadJson;
import cacheinspector.entity.TreeNodes;
import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;


public class BasketUIPanelFrame extends JPanel implements TreeExpansionListener,TreeSelectionListener
{
 private static final long serialVersionUID = -4949484525552546995L;
 private JTree tree;
 private Vector<TreeNodes> leaf;
 private TreeNodes node;
 private JScrollPane treeView;
 public BasketUIPanelFrame()
 {

  add(createNorthPanel());
 }

 private JPanel createNorthPanel() {
  JPanel panel = new JPanel(new BorderLayout());

  JTextField cck = new JTextField(50);
  panel.add(cck, BorderLayout.NORTH);

  JButton getButton = new JButton("Get....");
  getButton.addActionListener(e -> {
   if(treeView!=null)
    remove(treeView);
   try {
    ScanSalesforce(cck.getText());
    add(treeView, BorderLayout.SOUTH);
    revalidate();
    repaint();
   } catch (Exception ex) {
    ex.printStackTrace();
   }
  });
  panel.add(getButton, BorderLayout.SOUTH);
  return panel;
 }


 public void ScanSalesforce(String cck) throws Exception {
  setEnabled(false);
  TreeNodes top;
  top = ReadJson.readResponse(Salesforce.getBasket(cck));
  tree = new JTree(top);
  tree.setEnabled(false);
  tree.setEditable(false);
  tree.getExpandsSelectedPaths();
  CustomCellRenderer renderer = new CustomCellRenderer();
  tree.setCellRenderer(renderer);
  tree.addTreeSelectionListener(this);
  tree.addTreeExpansionListener(this);
  treeView = new JScrollPane(tree);
  tree.setSelectionRow(0);
  tree.addMouseListener(new MouseListener()
  {
   public void mouseClicked(MouseEvent e)
   {
    if (e.getButton() == 3)
    {
     PopupInfo nuovoPopUP = new PopupInfo(e.getXOnScreen(),e.getYOnScreen(),(TreeNodes)tree.getLastSelectedPathComponent());
     nuovoPopUP.setVisible(true);
    }
   }
   @Override
   public void mouseEntered(MouseEvent arg0) {}
   @Override
   public void mouseExited(MouseEvent arg0) {}
   @Override
   public void mousePressed(MouseEvent arg0) {}
   @Override
   public void mouseReleased(MouseEvent arg0) {}
  });
  addNode();
 }

 public void valueChanged(TreeSelectionEvent arg0) 
 {
  addChild();
 }
 private void addChild()
 {
  node = (TreeNodes)tree.getLastSelectedPathComponent();
  if (node == null) return;
  if (node.isLeaf())
  {
   node.addChild();
  }
 }

 public void treeCollapsed(TreeExpansionEvent arg0)
 {
 }
 public void treeExpanded(TreeExpansionEvent arg0)
 {
 }
 private void addNode()
 {
  node = (TreeNodes)tree.getLastSelectedPathComponent();
  if (node == null) return;
  if (node.isLeaf())
  {
   leaf = new Vector<>();
    for(TreeNodes treeNode:node.getChild())
    {
     leaf.add(treeNode);
     node.add(treeNode);
    }
    tree.expandPath(tree.getSelectionPath());
  }
  tree.repaint();
  tree.setEnabled(true);
 }

 public void expandAllNodes(){
  if(tree!=null) {
   for (int i = 0; i < tree.getRowCount(); ++i) {
    tree.expandRow(i);
   }
  }
 }

 public void collapseAllNodes(){
  if(tree!=null) {
   for (int i = tree.getRowCount(); i > 0; --i) {
    tree.collapseRow(i);
   }
  }
 }
}