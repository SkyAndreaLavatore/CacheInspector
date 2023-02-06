package cacheinspector.swing;

import cacheinspector.entity.TreeNodes;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class CustomCellRenderer extends		JLabel		implements	TreeCellRenderer
{
 private static final long serialVersionUID = 5913806457265642690L; 
 private boolean	bSelected;
 private ImageIcon IconTree[];
 public CustomCellRenderer()
 {
  setPreferredSize(new Dimension(1000, 48));
 }
 public Component getTreeCellRendererComponent( JTree tree,Object value, boolean bSelected, boolean bExpanded,boolean bLeaf, int iRow, boolean bHasFocus )
 {
  // Find out which node we are rendering and get its text
  this.removeAll();
  TreeNodes node = (TreeNodes) value;
  String name = (String) node.getUserObject();
  this.bSelected = bSelected;
  if( !bSelected )
	setForeground( Color.black );
  else
	setForeground( Color.white );
  setText( name
          + " [REC " + node.getRecurring() + "] [OT " + node.getTotal() +"]"
          + (!node.getPromotions().isEmpty()?" [promotions select and right-click]":"" )
          + (!node.getAttributeValues().isEmpty()?" [attributes select and right-click]":"" ));
  return this;
 }
 public void paint( Graphics g )
 {
  Color		bColor;
  bColor = bSelected ? SystemColor.textHighlight : Color.white;
  g.setColor( bColor );
  g.fillRect( 0, 0, getWidth() - 1, getHeight() - 1 );
  super.paint( g );
 }
}