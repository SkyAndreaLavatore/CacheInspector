package cacheinspector.core;

import java.util.*;

public class Inspector {
    private static Map<String,Set<String>> productMap = new HashMap<>();
    private static Map<String,Set<String>> promoMap = new HashMap<>();
    private static Map<String,Set<String>> catalogMap = new HashMap<>();
    private static Map<String,Map<String,List<String>>> cckMap = new HashMap<>();
    public static String TYPE_PRODUCTS= "products";
    public static String TYPE_PROMOTIONS= "promotions";
    public static String TYPE_CATALOGS= "catalogs";

    public static void clearMap() {
        productMap = new HashMap<>();
        promoMap = new HashMap<>();
        catalogMap = new HashMap<>();
        cckMap = new HashMap<>();
    }

    public static Map<String, Set<String>> getProductMap() {
        return productMap;
    }

    public static Map<String, Set<String>> getPromoMap() {
        return promoMap;
    }

    public static Map<String, Set<String>> getCatalogMap() {
        return catalogMap;
    }

    public static Map<String,Map<String,List<String>>> getCckMap() {
        return cckMap;
    }

    public static void readJson(String cck, String catalogName, Map<String, Object> root){
        if(root.containsKey(TYPE_PRODUCTS)) {
            List<Map<String, Object>> records = (List<Map<String, Object>>) root.get(TYPE_PRODUCTS);
            for (Map<String, Object> record : records) {
                Map<String, Object> r = record;
                for (String key : r.keySet()) {

                    if(r.containsKey("attributes")){
                        List<String> attributes = new ArrayList<>((Collection<? extends String>) r.get("attributes"));
                    }
                    setProductMap(key, cck);
                    setCckMap(cck, key, TYPE_PRODUCTS);
                }
            }
        }
        if(root.containsKey(TYPE_PROMOTIONS)){
            List<String> records = (List<String>) root.get(TYPE_PROMOTIONS);
            for (String name : records) {
                setPromoMap(name, cck);
                setCckMap(cck, name, TYPE_PROMOTIONS);
            }
        }
        setCckMap(cck, catalogName, TYPE_CATALOGS);
        setCatalogMap(catalogName, cck);
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

    private static void setCatalogMap(String catalogName, String cck){
        Set<String> ccks;
        if(!catalogMap.containsKey(catalogName)) {
            ccks = new HashSet<>();
        }else{
            ccks = catalogMap.get(catalogName);
        }
        ccks.add(cck);
        catalogMap.put(catalogName, ccks);
    }
}
