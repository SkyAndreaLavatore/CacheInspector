@isTest(seeAllData=false)
global without sharing class ClearV3CacheInspector_Test {

    static private void TestDataSetup(){
            CacheInspector__c cacheInsp = new CacheInspector__c(
                CacheKey__c = 'e558eacc66592dfdfe346fae81ea5e3d',
                CatalogCode__c = 'llama',
                JsonObject__c = 'ccdsdgfdfgdfgdfgdfsfe');
            insert cacheInsp;

    }

	public static testMethod void testClearV3CacheInspector() {
        TestDataSetup();
        Test.startTest();
        ClearV3CacheInspector b = new ClearV3CacheInspector ();
        database.executebatch(b,100000);
        Test.stopTest();
    }
}