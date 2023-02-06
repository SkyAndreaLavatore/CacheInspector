package cacheinspector.swing;

import java.awt.*;

class PopupString
{
 int x,y,etichettaX,etichettaY;
 String etichetta;
 Font fontStringa;
 Color coloreRettangolo,coloreStringa;
 public PopupString(int x, int y, String etichetta, int etichettaX, int etichettaY, Color coloreRettangolo, Color coloreStringa)
 {
  this.x = x;
  this.y = y;
  this.etichettaX = etichettaX;
  this.etichettaY = etichettaY;
  this.etichetta = etichetta;
  this.coloreRettangolo = coloreRettangolo;
  this.coloreStringa = coloreStringa;
  fontStringa = new Font("Lucida Sans Unicode", 0 , 12);
 }
 public void draw(Graphics g)
 {
  g.setColor(coloreRettangolo);
  g.setColor(coloreStringa);
  g.setFont(fontStringa);
  g.drawString(etichetta, etichettaX, etichettaY);
 }
}