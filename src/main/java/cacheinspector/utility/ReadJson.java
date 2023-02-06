package cacheinspector.utility;

import cacheinspector.entity.TreeNodes;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadJson {
    private static final String CLASS_NAME = "cacheinspector.utility.ReadJson";

    public static TreeNodes readFile(String fileName) throws Exception
    {
        Path file = Path.of(fileName);
        String actual = Files.readString(file);
        return readJson(actual);
    }

    public static TreeNodes readResponse(String response) throws Exception
    {
        return readJson(response);
    }

    private static TreeNodes readJson(String json) throws Exception {
        String METHOD_NAME = "readJson";
        TreeNodes treeNode = new TreeNodes("Basket","root");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> root = mapper.readValue(json, Map.class);
        System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** result: " + root);
        treeNode.setChild(readJson(root));
        readTotalPrice(root, treeNode);
        return treeNode;
    }

    private static void readTotalPrice(Map<String, Object> root,TreeNodes treeNode){
        String METHOD_NAME = "readTotalPrice";
        Map<String, Object> resultResponse = (Map<String,Object>)root.get("result");
        System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** resultResponse :" + resultResponse);
        Map<String, Double> totals = (Map<String, Double>) resultResponse.get("totals");
        System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** totals :" + totals);
        treeNode.setRecurring(String.valueOf(totals.get("EffectiveRecurringTotal__c")));
        treeNode.setTotal(String.valueOf(totals.get("EffectiveOneTimeTotal__c")));
    }

    private static List<TreeNodes> readJson(Map<String, Object> root){
        String METHOD_NAME = "readJson";
        Map<String, Object> resultResponse = (Map<String,Object>)root.get("result");
        System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** resultResponse :" + resultResponse);
        List<Object> records = (List<Object>)resultResponse.get("records");
        System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** records :" + records);
        List<TreeNodes> treeNodes = new ArrayList<>();
        for(Object record:records){
            Map<String, Object> r = (Map<String,Object>)record;
            TreeNodes treeProduct = new TreeNodes((String) r.get("ProductCode"),"product");
            System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** lineItems :" + r);
            treeProduct.setChild(readChild(r));
            treeProduct.setAttributes(readAttributes(r));
            treeProduct.setPromotions(readPromotions(r));
            setPrice(r,treeProduct);
            treeNodes.add(treeProduct);
        }
        return treeNodes;
    }

    private static void setPrice(Map<String, Object> r, TreeNodes treeProduct){
        String METHOD_NAME = "setPrice";
        Map<String, Object> recurringTotal = (Map<String, Object>) r.get("vlocity_cmt__RecurringTotal__c");
        System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** recurringTotal :" + recurringTotal);
        treeProduct.setRecurring(String.valueOf(recurringTotal.get("value")));
        Map<String, Object> oneTimeTotal = (Map<String, Object>) r.get("vlocity_cmt__OneTimeTotal__c");
        System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** oneTimeTotal :" + oneTimeTotal);
        treeProduct.setTotal(String.valueOf(oneTimeTotal.get("value")));
    }

    private static List<String> readPromotions(Map<String, Object> r){
        String METHOD_NAME = "readPromotions";
        List<String> promotionList = new ArrayList<>();
        Map<String, Object> promotions = (Map<String, Object>) r.get("promotions");
        if (promotions != null) {
            System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** promotions :" + promotions);
            List<Object> recordsPromotions = (List<Object>) promotions.get("records");
            System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** recordsPromotions :" + recordsPromotions);
            for (Object recordsPromotion : recordsPromotions) {
                Map<String, Object> rPromotion = (Map<String,Object>)recordsPromotion;
                promotionList.add((String) rPromotion.get("Name"));
            }
        }
        return promotionList;
    }

    private static List<TreeNodes> readChild(Map<String, Object> r){
        String METHOD_NAME = "readChild";
        List<TreeNodes> treeNodes = new ArrayList<>();
        Map<String, Object> childProducts = (Map<String, Object>) r.get("lineItems");
        if (childProducts != null) {
            System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** lineItems :" + childProducts);
            List<Object> recordsChilds = (List<Object>) childProducts.get("records");
            System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** recordsChilds :" + recordsChilds);
            for (Object recordsChild : recordsChilds) {
                Map<String, Object> rChild = (Map<String,Object>)recordsChild;
                TreeNodes treeProduct = new TreeNodes((String) rChild.get("ProductCode"),"product");
                treeProduct.setAttributes(readAttributes(rChild));
                treeProduct.setChild(readChild(rChild));
                treeProduct.setPromotions(readPromotions(rChild));
                setPrice(rChild,treeProduct);
                treeNodes.add(treeProduct);
            }
        }
        return treeNodes;
    }

    private static List<String> readAttributes(Map<String, Object> r){
        String METHOD_NAME = "readAttributes";
        List<String> attributes = new ArrayList<>();
        Map<String, Object> recordsAttributeCategories = (Map<String, Object>) r;
        Map<String, Object> attributeCategories = (Map<String, Object>) recordsAttributeCategories.get("attributeCategories");
        //System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** attributeCategories :" + attributeCategories);
        if (attributeCategories != null) {
            //System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** attributeCategories :" + attributeCategories);
            List<Object> recordsAttribute = (List<Object>) attributeCategories.get("records");
            //System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** recordsAttribute :" + recordsAttribute);
            for (Object recordAttribute : recordsAttribute) {
                Map<String, Object> r1 = (Map<String, Object>) recordAttribute;
                //System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** code__c :" + r1.get("Code__c"));
                Map<String, Object> productAttributes = (Map<String, Object>) r1.get("productAttributes");
                List<Object> recordsProductAttributes = (List<Object>) productAttributes.get("records");
                for (Object recordProductAttribute : recordsProductAttributes) {
                    Map<String, Object> r2 = (Map<String, Object>) recordProductAttribute;
                    //System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** code :" + r2.get("code"));
                    if(r2.get("userValues")!=null && !r2.get("userValues").equals("null"))
                        attributes.add(r2.get("code") + ":" + r2.get("userValues"));
                }
            }
        }
        return attributes;
    }
}
