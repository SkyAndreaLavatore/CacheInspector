//SELECT CacheKey__c, CatalogCode__c, Id, JsonObject__c FROM CacheInspector__c
//Batch_CacheInspector b = new Batch_CacheInspector ();
//database.executebatch(b,100);
//CacheInspector.engine('dbeac3b871cd2721b680e64e251450cd');
global without sharing class CacheInspector implements Queueable
{
    private static String CLASS_NAME = 'CacheInspector';

    private static String BODY_OFFER = 'offer';
    private static String BODY_ATTRIBUTECATEGORIES = 'attributeCategories';

    private static String RESPONSE_JSONOBJECT = 'jsonObject';
    private static String RESPONSE_CATALOG = 'catalogCode';
    private static String RESPONSE_CARTCONTEXTKEY = 'cartContextKey';

    private static String RESPONSE_RESULT = 'result';
    private static String RESPONSE_RECORDS = 'records';
    private static String RESPONSE_LINEITEMS = 'lineItems';
    private static String RESPONSE_PRODUCTCODE = 'ProductCode';
    private static String RESPONSE_PRODUCTATTRIBUTES = 'productAttributes';
    private static String RESPONSE_USERVALUES = 'userValues';
    private static String RESPONSE_ATTRIBUTECODE = 'code';

    private static String INVOKE200 = 'INVOKE-200';
    private static String BASKETACTION = 'basketAction';

    Set<vlocity_cmt__CachedAPIResponse__c> cachedAPIResponses;
    public CacheInspector(Set<vlocity_cmt__CachedAPIResponse__c> cachedAPIResponses) {
        this.cachedAPIResponses = cachedAPIResponses;
    }

    public void execute(QueueableContext context) {
        String METHOD_NAME = 'execute';
        Set<String> ccks = new Set<String>();
        System.debug(CLASS_NAME + '.' + METHOD_NAME + ' records returned: ' + cachedAPIResponses.size());
        for(vlocity_cmt__CachedAPIResponse__c cachedAPIResponse:cachedAPIResponses){
            ccks.add(cachedAPIResponse.vlocity_cmt__CacheKey__c);
        }
        System.debug(CLASS_NAME + '.' + METHOD_NAME + ' records filtered: ' + ccks.size());
        List<CacheInspector__c> listCacheInspectors = [SELECT CacheKey__c, CatalogCode__c, JsonObject__c FROM CacheInspector__c where CacheKey__c in :ccks];
        for(CacheInspector__c cacheinsp:listCacheInspectors){
            if(ccks.contains(cacheinsp.CacheKey__c))
                ccks.remove(cacheinsp.CacheKey__c);
        }
        System.debug(CLASS_NAME + '.' + METHOD_NAME + ' records remained: ' + ccks.size());
        try
        {
            List<CacheInspector__c> insertCacheInspectors = new List<CacheInspector__c>();
            for(String cck:ccks){
                insertCacheInspectors.add(createCacheInspector(cck));
            }
            if(!insertCacheInspectors.isEmpty())
                Database.insert(insertCacheInspectors, false);
        }
        catch (Exception e)
        {
            System.debug(CLASS_NAME + '.' + METHOD_NAME + ' ' + e.getMessage());
        }
    }

    private static CacheInspector__c createCacheInspector(String cartContextKey){
        String METHOD_NAME = 'createCacheInspector';
        Map<String,Object> mapObject = new Map<String,Object>();
        Datetime currentTime = Datetime.now();
        Boolean corrupted = false;
        String cache_type = 'orderConfiguration';
        String catalogCode;
        vlocity_cmt__CachedAPIResponse__c cacheApiResponse;
        Decimal previousSeq = 0;
        try{
            for (vlocity_cmt__CachedAPIResponse__c resApiResponse : [SELECT vlocity_cmt__CacheKey__c, vlocity_cmt__ApiURL__c, vlocity_cmt__ApiResponse__c, vlocity_cmt__OverflowSequence__c, vlocity_cmt__ExpirationTime__c
                                                    FROM vlocity_cmt__CachedAPIResponse__c
                                                    WHERE  vlocity_cmt__CacheKey__c = :cartContextKey AND vlocity_cmt__Type__c = :cache_type
                                                    AND ((vlocity_cmt__EffectiveStartTime__c = NULL OR vlocity_cmt__EffectiveStartTime__c <= :currentTime)
                                                    AND (vlocity_cmt__ExpirationTime__c > :currentTime OR vlocity_cmt__ExpirationTime__c = NULL))
                                                    ORDER BY vlocity_cmt__CacheKey__c, CreatedDate DESC, vlocity_cmt__OverflowSequence__c ASC])
            {
                System.debug(CLASS_NAME + '.' + METHOD_NAME + ' cartContextKey: ' + cartContextKey);
                if(previousSeq == 1 && resApiResponse.vlocity_cmt__OverflowSequence__c == 1){
                    System.debug(CLASS_NAME + '.' + METHOD_NAME + ' cartContextKey: ' + cartContextKey + ' duplicate');
                    corrupted=true;
                    break;
                }
                if(resApiResponse.vlocity_cmt__OverflowSequence__c == 1 || resApiResponse.vlocity_cmt__OverflowSequence__c == null)
                    cacheApiResponse = resApiResponse;
                else
                    cacheApiResponse.vlocity_cmt__ApiResponse__c += resApiResponse.vlocity_cmt__ApiResponse__c;

                previousSeq = resApiResponse.vlocity_cmt__OverflowSequence__c;

                if(catalogCode==null){
                    try{
                        Map<String, Object> mapApiUrl = (Map<String, Object>) JSON.deserializeUntyped(resApiResponse.vlocity_cmt__ApiURL__c);
                        if (mapApiUrl != null && mapApiUrl.containsKey(RESPONSE_CATALOG))
                            catalogCode = (String)mapApiUrl.get(RESPONSE_CATALOG);
                        }
                    catch (Exception e) {
                        String url = resApiResponse.vlocity_cmt__ApiURL__c;
                        url = url.replaceAll('/v3/catalogs/', '');
                        catalogCode = url.substring(0,url.indexOf('/'));
                    }
                }
            }
            if (cacheApiResponse != null){
                if(!corrupted)
                    createMapEngine((Map<String, Object>)JSON.deserializeUntyped(cacheApiResponse.vlocity_cmt__ApiResponse__c), mapObject);
                else{
                    mapObject.put('corrupted', 'check this cck: ' + cartContextKey);
                    catalogCode ='corrupted';
                }
                if(mapObject!=null && catalogCode!=null){
                    try{
                        CacheInspector__c cacheInspector = new CacheInspector__c(
                            CacheKey__c = cartContextKey,
                            CatalogCode__c = catalogCode,
                            JsonObject__c = JSON.serialize(mapObject));
                            return cacheInspector;
                    }catch (Exception e) {
                        System.debug(CLASS_NAME + '.' + METHOD_NAME + ' error CacheInspector: '+e.getMessage());
                    }
                }
            }
        }catch (Exception e)
        {
            System.debug(CLASS_NAME + '.' + METHOD_NAME + ' ' + e.getMessage());
        }
        return null;
    }

    private static void createMapEngine(Map<String, Object> result, Map<String,Object> mapObject){
        String METHOD_NAME = 'createMapEngine';
        Map<String, Map<String, Object>> mapProducts = new Map<String,Map<String, Object>>();
        Map<String, Object> mapAttribute = new Map<String, Object>();
        List<Object> records = (List<Object>)result.get(RESPONSE_RECORDS);
        for(Object record:records){
            Map<String, Object> r = (Map<String,Object>)record;
            String productCode = (String)r.get(RESPONSE_PRODUCTCODE);
            if(r.containsKey(BODY_ATTRIBUTECATEGORIES) && r.get(BODY_ATTRIBUTECATEGORIES)!=null){
                Map<String, Object> attrCategories = (Map<String,Object>)r.get(BODY_ATTRIBUTECATEGORIES);
                List<String> attributes = getAttribute(attrCategories);
                if(attributes!=null && !attributes.isEmpty()){
                    attributes.sort();
                    mapAttribute.put('attributes',attributes);
                }
            }
            if(r.containsKey('promotions') && r.get('promotions')!=null){
                Map<String, Object> promotions = (Map<String,Object>)r.get('promotions');
                getPromotions(promotions,mapObject);
            }
            if(r.containsKey(RESPONSE_LINEITEMS) && r.get(RESPONSE_LINEITEMS)!=null){
                Map<String, Object> lineItems = (Map<String,Object>)r.get(RESPONSE_LINEITEMS);
                createMapEngine(lineItems,mapObject);
            }
            List<Map<String, Object>> listProducts;
            if(mapObject.containsKey('products')){
                listProducts = (List<Map<String, Object>>)mapObject.get('products');
            }else{
                listProducts = new List<Map<String, Object>>();
            }
            Map<String, Object> mapProduct = new Map<String, Object>();
            mapProduct.put(productCode,mapAttribute);
            listProducts.add(mapProduct);
            mapObject.put('products',listProducts);
        }
    }

    private static void getPromotions(Map<String, Object> result, Map<String,Object> mapObject){
        String METHOD_NAME = 'getPromotions';
        List<Object> records = (List<Object>)result.get(RESPONSE_RECORDS);
        for(Object record:records){
            Map<String, Object> r = (Map<String,Object>)record;
            String code = (String)r.get('vlocity_cmt__Code__c');
            List<String> listPromo;
            if(mapObject.containsKey('promotions'))
                listPromo = (List<String>)mapObject.get('promotions');
            else
                listPromo = new List<String>();
            if(!listPromo.contains(code))
                listPromo.add(code);
            mapObject.put('promotions',listPromo);
        }
    }

    private static List<String> getAttribute(Map<String, Object> attributeCategories){
        String METHOD_NAME = 'getAttribute';
        List<String> attributeList = new List<String>();
        List<Object> recordsAttribute = (List<Object>)attributeCategories.get(RESPONSE_RECORDS);
        for(Object recordAttribute:recordsAttribute){
            Map<String, Object> r1 = (Map<String,Object>)recordAttribute;
            Map<String, Object> productAttributes = (Map<String,Object>)r1.get(RESPONSE_PRODUCTATTRIBUTES);
            List<Object> recordsProductAttributes = (List<Object>)productAttributes.get(RESPONSE_RECORDS);
            for(Object recordProductAttribute: recordsProductAttributes){
                Map<String, Object> r2 = (Map<String,Object>)recordProductAttribute;
                if(r2.get(RESPONSE_ATTRIBUTECODE)!=null && r2.get(RESPONSE_USERVALUES)!=null && String.isNotBlank(String.valueOf(r2.get(RESPONSE_USERVALUES)))){
                    attributeList.add(String.valueOf(r2.get(RESPONSE_USERVALUES)));
                }
            }
        }
        return attributeList;
    }

    global class CacheEngineException extends Exception {}
}
