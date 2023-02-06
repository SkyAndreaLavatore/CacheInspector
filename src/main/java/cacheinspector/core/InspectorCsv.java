package cacheinspector.core;

import cacheinspector.entity.EntityCsv;

import java.util.*;

public class InspectorCsv {
    private static Map<String,Set<String>> productMap = new HashMap<>();;
    private static Map<String,Set<String>> promoMap = new HashMap<>();;
    private static Map<String,Map<String,List<String>>> cckMap = new HashMap<>();
    public static String TYPE_PRODUCTS= "products";
    public static String TYPE_PROMOTIONS= "promotions";
    public static Map<String, Set<String>> getProductMap() {
        return productMap;
    }

    public static Map<String, Set<String>> getPromoMap() {
        return promoMap;
    }

    public static Map<String,Map<String,List<String>>> getCckMap() {
        return cckMap;
    }

    public static String getBasket(List<EntityCsv> entityList){
        StringBuilder cacheApiResponse = null;
        int previousSeq = 0;
        for (EntityCsv entity:entityList)
        {
            if(previousSeq == 1 && entity.getSequence() == 1)
                break;
            if(entity.getSequence() == 1)
                cacheApiResponse = new StringBuilder(entity.getResponse());
            else
                cacheApiResponse.append(entity.getResponse());

            previousSeq = entity.getSequence();
        }

        return cacheApiResponse.toString();
    }

    public static void readJson(String cck, Map<String, Object> root){
        List<Object> records = (List<Object>)root.get("records");
        for(Object record:records){
            Map<String, Object> r = (Map<String,Object>)record;
            String key = (String) r.get("ProductCode");
            setProductMap(key, cck);
            setCckMap(cck, key, TYPE_PRODUCTS);
            readChild(cck, r);
            //readAttributes(r);
            readPromotions(cck, r);
        }
    }

    private static void setPromoMap(String objectCode, String cck){
        Set<String> ccks;
        if(!promoMap.containsKey(objectCode)) {
            ccks = new HashSet<>();
        }else{
            ccks = promoMap.get(objectCode);
        }
        ccks.add(cck);
        promoMap.put(objectCode, ccks);
    }

    private static void setProductMap(String objectCode, String cck){
        Set<String> ccks;
        if(!productMap.containsKey(objectCode)) {
            ccks = new HashSet<>();
        }else{
            ccks = productMap.get(objectCode);
        }
        ccks.add(cck);
        productMap.put(objectCode, ccks);
    }

    private static void setCckMap(String cck, String objectCode, String type){
        Map<String,List<String>> objects;
        List<String> productList;
        if(!cckMap.containsKey(cck)) {
            objects = new HashMap<>();
            productList = new ArrayList<>();
        }else{
            objects = cckMap.get(cck);
            if(objects.containsKey(type)){
                productList = objects.get(type);
            }else{
                productList = new ArrayList<>();
            }
        }
        if(!productList.contains(objectCode)) {
            productList.add(objectCode);
            Collections.sort(productList);
            objects.put(type, productList);
            cckMap.put(cck, objects);
        }
    }

    private static void readPromotions(String cck, Map<String, Object> r){
        Map<String, Object> promotions = (Map<String, Object>) r.get("promotions");
        if (promotions != null) {
            List<Object> recordsPromotions = (List<Object>) promotions.get("records");
            for (Object recordsPromotion : recordsPromotions) {
                Map<String, Object> rPromotion = (Map<String,Object>)recordsPromotion;
                String name = (String) rPromotion.get("vlocity_cmt__Code__c");
                setPromoMap(name, cck);
                setCckMap(cck, name, TYPE_PROMOTIONS);
            }
        }
    }

    private static void readChild(String cck, Map<String, Object> r){
        Map<String, Object> childProducts = (Map<String, Object>) r.get("lineItems");
        if (childProducts != null) {
            List<Object> recordsChilds = (List<Object>) childProducts.get("records");
            for (Object recordsChild : recordsChilds) {
                Map<String, Object> rChild = (Map<String,Object>)recordsChild;
                String key = (String) rChild.get("ProductCode");
                setProductMap(key , cck);
                setCckMap(cck, key, TYPE_PRODUCTS);
            }
        }
    }

    private static List<String> readAttributes(Map<String, Object> r){
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
