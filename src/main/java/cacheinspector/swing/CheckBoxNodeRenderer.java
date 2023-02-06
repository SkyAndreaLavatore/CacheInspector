package cacheinspector.swing;

import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.List;

public class CheckBoxNodeRenderer implements TreeCellRenderer {
  JCheckBox leafRenderer = new JCheckBox();
  DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();
  Color selectionBorderColor, selectionForeground, selectionBackground,
      textForeground, textBackground;
  JTextField filterField;

  protected JCheckBox getLeafRenderer() {
    return leafRenderer;
  }

  public CheckBoxNodeRenderer(JTextField filterField) {
    this.filterField = filterField;
    Font fontValue;
    fontValue = UIManager.getFont("Tree.font");
    if (fontValue != null) {
      leafRenderer.setFont(fontValue);
    }
    Boolean booleanValue = (Boolean) UIManager
        .get("Tree.drawsFocusBorderAroundIcon");
    leafRenderer.setFocusPainted((booleanValue != null)
        && (booleanValue.booleanValue()));

    selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
    selectionForeground = UIManager.getColor("Tree.selectionForeground");
    selectionBackground = UIManager.getColor("Tree.selectionBackground");
    textForeground = UIManager.getColor("Tree.textForeground");
    textBackground = UIManager.getColor("Tree.textBackground");
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean selected, boolean expanded, boolean leaf, int row,
      boolean hasFocus) {

    Component returnValue;
    if (leaf) {

      CheckBoxNode checkBoxNode = (CheckBoxNode) ((JTree.DynamicUtilTreeNode) value).getUserObject();
      leafRenderer.setText(checkBoxNode.getText());
      leafRenderer.setSelected(false);

      leafRenderer.setEnabled(tree.isEnabled());

      if (selected) {
        leafRenderer.setForeground(selectionForeground);
        leafRenderer.setBackground(selectionBackground);
      } else {
        leafRenderer.setForeground(textForeground);
        leafRenderer.setBackground(textBackground);
      }

      Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
      if (userObject instanceof CheckBoxNode) {
        CheckBoxNode node = (CheckBoxNode) userObject;
        if(filterField!=null) {
          StringBuilder textFilter = new StringBuilder();
          List<String> listText = new ArrayList<>();
          if (!filterField.getText().isEmpty()) {
            String[] textList = filterField.getText().split(", ");
            Collections.addAll(listText, textList);
          }
          if (node.isSelected()) {
            if (!listText.contains(node.getKey()))
              listText.add(node.getKey());
          } else {
            listText.remove(node.getKey());
          }
          Collections.sort(listText);
          for (String text : listText) {
            textFilter.append(text).append(", ");
          }
          if (!textFilter.isEmpty())
            textFilter = new StringBuilder(textFilter.substring(0, textFilter.lastIndexOf(",")));
          if(!filterField.getText().equals(textFilter.toString()))
            filterField.setText(textFilter.toString());
        }
        leafRenderer.setText(node.getText());
        leafRenderer.setSelected(node.isSelected());
      }
      returnValue = leafRenderer;
    } else {
      returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree, value,
          selected, expanded, leaf, row, hasFocus);
    }
    return returnValue;
  }
}

