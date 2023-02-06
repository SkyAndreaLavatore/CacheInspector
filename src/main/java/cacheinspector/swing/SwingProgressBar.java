package cacheinspector.swing;

import javax.swing.*;
import java.awt.*;

public class SwingProgressBar extends JPanel implements Runnable{

  private JProgressBar pbar;

  private static final int MY_MINIMUM = 0;
  private int MY_MAXIMUM;
  private int count=0;
  private JFrame frame;

  public SwingProgressBar(int posX, int posY, int MY_MAXIMUM) {
    frame = new JFrame();
    frame.setLocation(posX, posY);
    frame.setSize(200,30);
    frame.setUndecorated(true);
    frame.setAlwaysOnTop(true);
    pbar = new JProgressBar();
    pbar.setMinimum(MY_MINIMUM);
    this.MY_MAXIMUM = MY_MAXIMUM;
    pbar.setMaximum(MY_MAXIMUM);
    pbar.setStringPainted(true);
    add(pbar);
    Container cont = frame.getContentPane();
    cont.removeAll();
    cont.add(this);
    setVisible(true);
    Thread thread1 = new Thread(this);
    thread1.start();
  }

  public void setVisible(boolean flag){
    frame.setVisible(flag);
    if(!flag){
      count = MY_MAXIMUM;
      frame.dispose();
    }
  }

  public void updateBar(int newValue) {
    count += newValue;
  }

  public void run()
  {
    while ( count < MY_MAXIMUM) {
      try {
         SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            pbar.setValue(count);
          }
        });
        java.lang.Thread.sleep(1000);
      } catch (InterruptedException e) {
        ;
      }
    }
    setVisible(false);
  }
}