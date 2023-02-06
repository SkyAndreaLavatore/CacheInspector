package cacheinspector;

import cacheinspector.frame.CacheInspectorMainFrame;

import javax.swing.*;
import java.awt.*;

/*SELECT  ID,VLOCITY_CMT__CACHEKEY__C,VLOCITY_CMT__APIRESPONSE__C,VLOCITY_CMT__OVERFLOWSEQUENCE__C FROM vlocity_cmt__CachedAPIResponse__c WHERE  vlocity_cmt__Type__c = 'orderConfiguration'
        AND ((vlocity_cmt__EffectiveStartTime__c = NULL OR vlocity_cmt__EffectiveStartTime__c <= TODAY)
        AND (vlocity_cmt__ExpirationTime__c > TODAY OR vlocity_cmt__ExpirationTime__c = NULL))
        ORDER BY VLOCITY_CMT__CACHEKEY__C ASC, vlocity_cmt__OverflowSequence__c ASC*/

//Batch_CacheInspector b = new Batch_CacheInspector ();
//database.executebatch(b,100);

//ClearV3CacheInspector b = new ClearV3CacheInspector ();
//database.executebatch(b,100000);

class CacheInspectorMain
{
 public static void main(String[] args)
 {
  CacheInspectorMainFrame f = new CacheInspectorMainFrame();
  f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  f.setLayout(new BorderLayout());
  f.setVisible(true);
 }
}