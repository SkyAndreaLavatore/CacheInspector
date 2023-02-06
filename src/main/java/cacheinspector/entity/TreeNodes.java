package cacheinspector.entity;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

public class TreeNodes extends DefaultMutableTreeNode
{
 private static final long serialVersionUID = -702831379558720105L;
 private String name;
 private String type;
 private String recurring="0";
 private String total="0";
 private List<String> attributes;
 private List<String> promotions;
 private List<TreeNodes> child;
 public TreeNodes(String name, String type)
 {
  super(name);
  this.name = name;
  this.type = type;
 }
 public void addChild()
 {
  if(child!=null && !child.isEmpty()) {
   for (TreeNodes treeNodes : child)
    add(treeNodes);
  }
 }

 public String getName() 
 {
  return name;
 }
 public String getType()
 {
  return type;
 }
 public void setAttributes(List<String> attributes)
 {
  this.attributes = attributes;
 }
 public List<String> getAttributes()
 {
  if(attributes==null || attributes.isEmpty())
   return new ArrayList<>();
  else
   return attributes;
 }

 public String getAttributeValues()
 {
  String values="";
  if(attributes!=null && !attributes.isEmpty()){
    for(String value:attributes){
     values += value + ",";
    }
    if(values.endsWith(",")) values = values.substring(0,values.length()-1);
   }
   return values;
 }


 public void setPromotions(List<String> promotions)
 {
  this.promotions = promotions;
 }
 public List<String> getPromotions()
 {
  if(promotions==null || promotions.isEmpty())
   return new ArrayList<>();
  else
   return promotions;
 }

 public String getPromotionsValues()
 {
  String values="";
  if(promotions!=null && !promotions.isEmpty()){
   for(String value:promotions){
    values += value + ",";
   }
   if(values.endsWith(",")) values = values.substring(0,values.length()-1);
  }
  return values;
 }

 public void setChild(List<TreeNodes> child)
 {
  this.child = child;
  for(TreeNodes node:child)
   add(node);
 }
 public List<TreeNodes> getChild()
 {
  return this.child;
 }
 public TreeNodes getChild(int index)
 {
  return this.child.get(index);
 }

 public String getRecurring() {
  return recurring;
 }

 public void setRecurring(String recurring) {
  this.recurring = recurring;
 }

 public String getTotal() {
  return total;
 }

 public void setTotal(String total) {
  this.total = total;
 }
}