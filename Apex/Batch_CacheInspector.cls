//SELECT CacheKey__c, CatalogCode__c, Id, JsonObject__c FROM CacheInspector__c
//Batch_CacheInspector b = new Batch_CacheInspector ();
//database.executebatch(b,100);
global class Batch_CacheInspector implements Database.Batchable<sObject>{

    global String queryValue;
    private static String CLASS_NAME = 'Batch_CacheInspector';

    global Batch_CacheInspector(){
        Datetime dt = System.now();
        String myDtString = dt.format('yyyy-MM-dd\'T\'HH:mm:ss.SSSZ');
        System.debug( ' **** myDtString:' + myDtString);
        this.queryValue = 'SELECT  vlocity_cmt__CacheKey__c FROM vlocity_cmt__CachedAPIResponse__c WHERE  vlocity_cmt__Type__c = \'orderConfiguration\' AND ((vlocity_cmt__EffectiveStartTime__c = NULL OR vlocity_cmt__EffectiveStartTime__c <= ' + myDtString + ') AND (vlocity_cmt__ExpirationTime__c > ' + myDtString + ' OR vlocity_cmt__ExpirationTime__c = NULL))';
        System.debug( ' **** queryValue:' + queryValue);
    }

    global Database.QueryLocator start(Database.BatchableContext BC){
        return Database.getQueryLocator(queryValue);
    }

    global void finish(Database.BatchableContext BC){}

    global void execute(Database.BatchableContext BC, vlocity_cmt__CachedAPIResponse__c[] records){
        String METHOD_NAME = 'execute';
        System.debug(CLASS_NAME + '.' + METHOD_NAME + ' **** CachedAPIResponse:' + records);
        System.enqueueJob(new CacheInspector(new Set<vlocity_cmt__CachedAPIResponse__c>(records)));
    }
}