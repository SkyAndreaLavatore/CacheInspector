package cacheinspector.swing;

import javax.swing.*;
import java.awt.*;

public class ProgressBar
{
 private FrameProgressBar f;
 private int base=200;
 private int altezza=50;
 private int positionX=0;
 private int positionY=0;
 private Color colorBackground=Color.WHITE;
 private Color colorScroll=Color.RED;
 private Color colorScrollGradient=Color.WHITE;
 private Color colorBorder=Color.gray;
 private float opacity=0.70f;
 public ProgressBar()
 {
 }
 public ProgressBar(int base,int altezza)
 {
  this.base=base;
  this.altezza=altezza;
 }
 public ProgressBar(int base,int altezza,int positionX,int positionY)
 {
  this.base=base;
  this.altezza=altezza;
  this.positionX=positionX;
  this.positionY=positionY;
 }
 public ProgressBar(int base,int altezza,int positionX,int positionY,float opacity)
 {
  this.base=base;
  this.altezza=altezza;
  this.positionX=positionX;
  this.positionY=positionY;
  this.opacity=opacity;
 }
 public ProgressBar(int base,int altezza,int positionX,int positionY,Color colorBackground,Color colorScroll)
 {
  this.base=base;
  this.altezza=altezza;
  this.positionX=positionX;
  this.positionY=positionY;
  this.colorBackground=colorBackground;
  this.colorScroll=colorScroll;
 }
 public ProgressBar(int base,int altezza,int positionX,int positionY,Color colorBackground,Color colorScroll,Color colorScrollGradient,float opacity)
 {
  this.base=base;
  this.altezza=altezza;
  this.positionX=positionX;
  this.positionY=positionY;
  this.colorBackground=colorBackground;
  this.colorScroll=colorScroll;
  this.colorScrollGradient=colorScrollGradient;
  this.opacity=opacity;
 }
 public ProgressBar(int base,int altezza,int positionX,int positionY,Color colorBackground,Color colorScroll,float opacity)
 {
  this.base=base;
  this.altezza=altezza;
  this.positionX=positionX;
  this.positionY=positionY;
  this.colorBackground=colorBackground;
  this.colorScroll=colorScroll;
  this.opacity=opacity;
 }
 public ProgressBar(int base,int altezza,int positionX,int positionY,Color colorBackground,Color colorScroll,Color colorScrollGradient,Color colorBorder,float opacity)
 {
  this.base=base;
  this.altezza=altezza;
  this.positionX=positionX;
  this.positionY=positionY;
  this.colorBackground=colorBackground;
  this.colorScroll=colorScroll;
  this.colorScrollGradient=colorScrollGradient;
  this.colorBorder=colorBorder;
  this.opacity=opacity;
 } 
 public void setVisible(boolean state)
 {
  if(state)
  {
   f = new FrameProgressBar(base,altezza,colorBackground,colorScroll,colorScrollGradient,colorBorder,opacity);
   f.setSize(base,altezza);
   f.setLocation(positionX, positionY);
   f.setUndecorated(true);
   f.setAlwaysOnTop(true);
   f.setVisible(true);
  }
  else
  {
   f.setVisible(false);
  }
 }
 public int getBase() 
 {
  return base;
 }
 public void setBase(int base) 
 {
  this.base = base;
 }
 public int getAltezza() 
 {
  return altezza;
 }
 public void setAltezza(int altezza) 
 {
  this.altezza = altezza;
 }
 public int getPositionX() 
 {
  return positionX;
 }
 public void setPositionX(int positionX) 
 {
  this.positionX = positionX;
 }
 public int getPositionY() 
 {
  return positionY;
 }
 public void setPositionY(int positionY) 
 {
  this.positionY = positionY;
 }
 public Color getColorBackground() 
 {
  return colorBackground;
 }
 public void setColorBackground(Color colorBackground) 
 {
  this.colorBackground = colorBackground;
 }
 public Color getColorScroll() 
 {
  return colorScroll;
 }
 public void setColorScroll(Color colorScroll) 
 {
  this.colorScroll = colorScroll;
 }
 public Color getColorScrollGradient() 
 {
  return colorScrollGradient;
 }
 public void setColorScrollGradient(Color colorScrollGradient) 
 {
  this.colorScrollGradient = colorScrollGradient;
 }
 public Color getColorBorder() 
 {
  return colorBorder;
 }
 public void setColorBorder(Color colorBorder) 
 {
  this.colorBorder = colorBorder;
 }
 public float getOpacity() 
 {
  return opacity;
 }
 public void setOpacity(float opacity) 
 {
  this.opacity = opacity;
 }
}

class FrameProgressBar extends JFrame
{
 private static final long serialVersionUID = 9034898167863673949L;
 public FrameProgressBar(int base,int altezza,Color colorBackground,Color colorScroll,Color colorScrollGradient,Color colorBorder,float opacity)
 {
  //AWTUtilities.setWindowOpacity(this, opacity);
  PanelProgressBar panel = new PanelProgressBar(base,altezza,colorBackground,colorScroll,colorScrollGradient,colorBorder);
  Container cont = getContentPane();
  cont.removeAll();
  cont.add(panel);
 }
}

class PanelProgressBar extends JPanel implements Runnable
{
 private static final long serialVersionUID = 3903928974751864707L;
 private int base;
 private int altezza;
 private Color colorBackground;
 private Color colorScroll;
 private Color colorScrollGradient;
 private Color colorBorder;
 private int x;
 public PanelProgressBar(int base,int altezza,Color colorBackground,Color colorScroll,Color colorScrollGradient,Color colorBorder)
 {
  this.base = base;
  this.altezza = altezza;
  this.colorBackground = colorBackground;
  this.colorScroll = colorScroll;
  this.colorScrollGradient=colorScrollGradient;
  this.colorBorder=colorBorder;
  Thread thread1 = new Thread(this);
  thread1.start();
 }
 public void paintComponent(Graphics g)
 {
  super.paintComponent(g);
  Graphics2D g2 = (Graphics2D)g;
  g2.setColor(colorBackground);
  g2.fillRect(0, 0, base, altezza);
  GradientPaint gradient = new GradientPaint(10,10,colorScrollGradient,3,3,colorScroll,true);
  g2.setPaint(gradient);
  g2.fillRect(x, 0, base/8, altezza);
  g2.setColor(colorBorder);
  g2.drawRect(0, 0, base-1, altezza-1);
 }
 public void run() 
 {
  x=0;
  int inc=1;
  while(true)
  {
   if(x+(base/8) >= base)
   {
	inc=-1;
   }
   else if(x==0)
   {
	inc=1;
   }
   x=x+inc;
   try{Thread.sleep(5);}
   catch (InterruptedException e){e.printStackTrace();}
   repaint();
  }
 }
}