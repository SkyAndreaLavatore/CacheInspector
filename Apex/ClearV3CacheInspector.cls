//ClearV3CacheInspector b = new ClearV3CacheInspector ();
//database.executebatch(b,100000);
global class ClearV3CacheInspector implements Database.Batchable<sObject>{

    global final String cacheRecordListQuery;
    private Boolean runBatch = true;


    global ClearV3CacheInspector(){
        this.cacheRecordListQuery = 'SELECT Id FROM CacheInspector__c';
    }
    global Database.QueryLocator start(Database.BatchableContext BC){
        return Database.getQueryLocator(cacheRecordListQuery);
    }

    global void execute(Database.BatchableContext BC, List<CacheInspector__c> cacheRecordList){
        runBatch = !cacheRecordList.isEmpty();
        vlocity_cmt.VOIInvoker voi = vlocity_cmt.VOIInvoker.getInstance();

        delete cacheRecordList;

    }
    global void finish(Database.BatchableContext BC){

    }
}
