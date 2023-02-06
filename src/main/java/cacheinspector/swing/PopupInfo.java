package cacheinspector.swing;

import cacheinspector.entity.TreeNodes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

@SuppressWarnings("serial")
public
class PopupInfo extends JFrame
{
 private int cordX,cordY;
 private TreeNodes nodo;
 public PopupInfo(int cordX, int cordY, TreeNodes nodo)
 {
  this.setCordX(cordX);
  this.setCordY(cordY);
  this.setSize(500, 150);
  this.setResizable(false); 
  this.setUndecorated(true);
  this.setLocation(cordX, cordY);
  this.setNodo(nodo);
  addWindowFocusListener(new WindowAdapter ()
  {
   public void windowLostFocus (WindowEvent we)
   {
	dispose();	
   }
  });
  PopupInfoPanel puntPanel = new PopupInfoPanel(nodo);
  Container puntContainer = getContentPane();
  puntContainer.add(puntPanel);
 }
public int getCordX() {
	return cordX;
}
public void setCordX(int cordX) {
	this.cordX = cordX;
}
public int getCordY() {
	return cordY;
}
public void setCordY(int cordY) {
	this.cordY = cordY;
}
public TreeNodes getNodo() {
	return nodo;
}
public void setNodo(TreeNodes nodo) {
	this.nodo = nodo;
}
}

@SuppressWarnings("serial")
class PopupInfoPanel extends JPanel
{
 private Rectangle2D.Double Rettangolo;
 private Color coloreRettangolo,coloreStringa;
 private ArrayList<PopupString> arrayPopupString;
 private TreeNodes node;
 private int indice;
 public PopupInfoPanel(TreeNodes node)
 {
  arrayPopupString = new ArrayList<>();
  this.Rettangolo = new Rectangle2D.Double(0,0,499,149);
  this.node = node;
  
  coloreRettangolo = new Color(255,255,255);
  coloreStringa = new Color(0,0,0);
  int y=0;
  arrayPopupString.add(new PopupString(0, y, node.getName(), 8, y+12, coloreRettangolo, coloreStringa));
  y+=15;
  if(node.getPromotions()!=null && !node.getPromotions().isEmpty()){
   arrayPopupString.add(new PopupString(0, y, "Promotions:", 8, y+12, coloreRettangolo, coloreStringa));
   y+=15;
   for(String value:node.getPromotions()) {
    arrayPopupString.add(new PopupString(0, y, " -" + value, 8, y+12, coloreRettangolo, coloreStringa));
    y+=15;
   }
  }
  if(node.getAttributes()!=null && !node.getAttributes().isEmpty()) {
   arrayPopupString.add(new PopupString(0, y, "Attributes:", 8, y+12, coloreRettangolo, coloreStringa));
   y+=15;
   for (String value : node.getAttributes()) {
    arrayPopupString.add(new PopupString(0, y, " -" + value, 8, y + 12, coloreRettangolo, coloreStringa));
    y += 15;
   }
  }
 }
 
 public void paintComponent(Graphics g)
 {
  super.paintComponent(g);
  g.drawRect((int)Rettangolo.getX(),(int)Rettangolo.getY(),(int)Rettangolo.getMaxX(),(int)Rettangolo.getMaxY());
  for(int i = 0; i< arrayPopupString.size(); i++)
   arrayPopupString.get(i).draw(g);
 }

public TreeNodes getNode() {
	return node;
}

public void setNode(TreeNodes node) {
	this.node = node;
}
}