package cacheinspector.frame;

import cacheinspector.entity.CacheInspectorEntity;
import cacheinspector.swing.SwingProgressBar;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;


public class Salesforce {
    private static String REST_ENDPOINT = "/services/data" ;
    private static String API_VERSION = "/v56.0" ;
    private static Header prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
    private static final String CLASS_NAME = "Salesforce";
    private static final int maxdelete = 200;
    private static String PASSWORD;
    private static String USERNAME;
    static String LOGINURL;
    static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    private static String CLIENTID;
    private static String CLIENTSECRET;
    private static final String DIGITAL_REST_ENDPOINT = "/services/apexrest/vlocity_cmt" ;
    private static final String DIGITAL_API_VERSION = "/v3" ;
    public static Header oauthHeader;
    public static String loginInstanceUrl;
    private static final String urlBasket = "/catalogs/Residential/basket";
    private static final Logger logger = Logger.getLogger("Salesforce.log");
    private static final String START = "Start";
    private static final String EXIT = "Exit";

    public static void getClientCredentials(Properties property) throws Exception {
        String METHOD_NAME = "getClientCredentials";
        FileHandler fh;
        try {
            fh = new FileHandler("Salesforce.log");
            logger.addHandler(fh);
            logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Create Frame " + START);
            PASSWORD = property.getProperty("password");
            USERNAME = property.getProperty("username");
            CLIENTID = property.getProperty("clientId");
            CLIENTSECRET = property.getProperty("clientSecret");
            LOGINURL = property.getProperty("loginUrl");
            getClientCredentials();
        }catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            throw new Exception(e);
        }
    }

    private static void getClientCredentials() throws Exception {
        String METHOD_NAME = "getClientCredentials";

        // Assemble the login request URL
        String loginURL = LOGINURL +
                GRANTSERVICE +
                "&client_id=" + CLIENTID +
                "&client_secret=" + CLIENTSECRET +
                "&username=" + USERNAME +
                "&password=" + PASSWORD;
        System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** loginURL " + loginURL);
        HttpPost httpPost = new HttpPost(loginURL);

        try {
            // Login requests must be POSTs
            HttpResponse response;
            HttpClient httpclient = HttpClientBuilder.create().build();
            // Execute the login POST request
            response = httpclient.execute(httpPost);

            // verify response is HTTP OK
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** Error authenticating to Force.com: "+statusCode);
                // Error is in EntityUtils.toString(response.getEntity())
                return;
            }

            String getResult = EntityUtils.toString(response.getEntity());

            JSONObject jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
            String loginAccessToken = jsonObject.getString("access_token");
            loginInstanceUrl = jsonObject.getString("instance_url");
            oauthHeader = new BasicHeader("Authorization", "OAuth " + loginAccessToken);
            logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Successful login");
            System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** Successful login");
            logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** instance URL: "+loginInstanceUrl);
            System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** instance URL: "+loginInstanceUrl);
            logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** access token/session ID: "+loginAccessToken);
            System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** access token/session ID: "+loginAccessToken);
        } catch (JSONException | IOException e) {
            logger.severe(CLASS_NAME + '.' + METHOD_NAME + " **** Exception " + e.getMessage());
            throw new Exception(e);
        }finally {
            httpPost.releaseConnection();
        }
    }

    public static String getBasket(String cartContextKey) throws Exception {
        String METHOD_NAME = "getBasket";
        try {

            //Set up the HTTP objects needed to make the request.
            HttpClient httpClient = HttpClientBuilder.create().build();
            String baseUri = loginInstanceUrl + DIGITAL_REST_ENDPOINT + DIGITAL_API_VERSION ;
            String uri = baseUri + "/catalogs/Residential/basket/" + cartContextKey;
            System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** Query URL: " + uri);
            HttpGet httpGet = new HttpGet(uri);
            System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** oauthHeader2: " + oauthHeader);
            httpGet.addHeader(oauthHeader);
            httpGet.addHeader(prettyPrintHeader);

            // Make the request.
            HttpResponse response = httpClient.execute(httpGet);

            // Process the result
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String response_string = EntityUtils.toString(response.getEntity());
                System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** Reponse GetBasket:\n" + response_string);
                logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Reponse GetBasket:\n" + response_string);
                return response_string;
            } else {
                System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** Query was unsuccessful. Status code returned is " + statusCode);
                logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Query was unsuccessful. Status code returned is " + statusCode);
                System.out.println(CLASS_NAME + '.' + METHOD_NAME + " **** An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                System.out.println(CLASS_NAME + '.' + METHOD_NAME + " " + response.getEntity().getContent());
                logger.info(CLASS_NAME + '.' + METHOD_NAME + " " + response.getEntity().getContent());
                throw new Exception(response.getEntity().getContent().toString());
            }
        } catch (IOException | NullPointerException ioe) {
            logger.severe(CLASS_NAME + '.' + METHOD_NAME + " Exception " + ioe.getMessage());
            throw new Exception(ioe);
        }
    }

    public static List<CacheInspectorEntity> queryCacheInspector(SwingProgressBar swingProgressBar) {
        return queryCacheInspector(null, swingProgressBar);
    }

    public static List<CacheInspectorEntity> queryCacheInspector(String uri, SwingProgressBar swingProgressBar) {
        String METHOD_NAME = "queryCacheInspector";
        System.out.println("\n_______________ Lead QUERY _______________");
        List<CacheInspectorEntity> cacheInspectorEntityList = new ArrayList<>();
        try {
            if(uri==null) {
                String baseUri = Salesforce.loginInstanceUrl + REST_ENDPOINT + API_VERSION;
                uri = baseUri + "/query?q=SELECT+CacheKey__c,+CatalogCode__c,+Id,+JsonObject__c+FROM+CacheInspector__c&offset=0&pagesize=200";
            }
            //Set up the HTTP objects needed to make the request.
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(uri);
            httpGet.addHeader(Salesforce.oauthHeader);
            httpGet.addHeader(prettyPrintHeader);
            System.out.println("Query URL: " + uri);
            logger.info(CLASS_NAME + '.' + METHOD_NAME + "Query URL: " + uri);
            // Make the request.
            HttpResponse response = httpClient.execute(httpGet);
            // Process the result
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String response_string = EntityUtils.toString(response.getEntity());
                try {
                    JSONObject json = new JSONObject(response_string);
                    JSONArray j = json.getJSONArray("records");
                    int i = 0;
                    while (i < j.length()) {
                        String catalogCode = json.getJSONArray("records").getJSONObject(i).getString("CatalogCode__c");
                        String jsonObject = json.getJSONArray("records").getJSONObject(i).getString("JsonObject__c");
                        String cacheKey = json.getJSONArray("records").getJSONObject(i).getString("CacheKey__c");
                        cacheInspectorEntityList.add(new CacheInspectorEntity(jsonObject,catalogCode,cacheKey));
                        i++;
                    }
                    swingProgressBar.updateBar(i);
                    if(json.has("nextRecordsUrl"))
                        cacheInspectorEntityList.addAll(queryCacheInspector(Salesforce.loginInstanceUrl + json.get("nextRecordsUrl"), swingProgressBar));
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            } else {
                logger.info(CLASS_NAME + '.' + METHOD_NAME + "Query was unsuccessful. Status code returned is " + statusCode);
                System.out.println("Query was unsuccessful. Status code returned is " + statusCode);
                logger.info(CLASS_NAME + '.' + METHOD_NAME + "An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                System.out.println("An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                logger.info(CLASS_NAME + '.' + METHOD_NAME + " body: " + getBody(response.getEntity().getContent()));
                System.out.println(getBody(response.getEntity().getContent()));
            }
        } catch (IOException | NullPointerException ioe) {
            logger.severe(CLASS_NAME + '.' + METHOD_NAME + " Exception: " + ioe.getMessage());
            ioe.printStackTrace();
        }
        return cacheInspectorEntityList;
    }

    public static Map<String, Set<String>> queryAndDeleteCachedAPIResponse(List<Set<String>> listCck, SwingProgressBar swingProgressBar) {
        String METHOD_NAME = "queryAndDeleteCachedAPIResponse";
        System.out.println("\n_______________ Lead QUERY _______________");
        Map<String, Set<String>> mapReturn = new HashMap<>();
        Set<String> setFail = new HashSet<>();
        Set<String> setSuccess = new HashSet<>();
        try {
            for (Set<String> ccks : listCck) {
                StringBuilder strIds = new StringBuilder();
                for (String ids : ccks) {
                    if (strIds.toString().equals("")) strIds = new StringBuilder("'" + ids + "'");
                    else strIds.append(",'").append(ids).append("'");
                }
                String baseUri = Salesforce.loginInstanceUrl + REST_ENDPOINT + API_VERSION;
                String uri = baseUri + "/query?q=SELECT+id,vlocity_cmt__CacheKey__c+FROM+vlocity_cmt__CachedAPIResponse__c+WHERE+vlocity_cmt__CacheKey__c+IN+(" + strIds + ")";
                //Set up the HTTP objects needed to make the request.
                HttpClient httpClient = HttpClientBuilder.create().build();

                HttpGet httpGet = new HttpGet(uri);
                httpGet.addHeader(Salesforce.oauthHeader);
                httpGet.addHeader(prettyPrintHeader);
                System.out.println("Query URL: " + uri);
                logger.info(CLASS_NAME + '.' + METHOD_NAME + "Query URL: " + uri);
                // Make the request.
                HttpResponse response = httpClient.execute(httpGet);
                // Process the result
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String response_string = EntityUtils.toString(response.getEntity());
                    JSONObject json = new JSONObject(response_string);
                    JSONArray j = json.getJSONArray("records");
                    int i = 0;
                    Set<String> listId = new HashSet<>();
                    List<Set<String>> listSetId = new ArrayList<>();
                    Map<String, Set<String>> mapCck = new HashMap<>();
                    while (i < j.length()) {
                        String id = json.getJSONArray("records").getJSONObject(i).getString("Id");
                        listId.add(id);
                        String cck = json.getJSONArray("records").getJSONObject(i).getString("vlocity_cmt__CacheKey__c");
                        Set<String> setId;
                        if(mapCck.containsKey(cck)){
                            setId = mapCck.get(cck);
                        }else{
                            setId = new HashSet<>();
                        }
                        setId.add(id);
                        mapCck.put(cck,setId);
                        i++;
                    }
                    if(!listId.isEmpty()) {
                        i = 0;
                        Set<String> setCck = null;
                        for (String ids : listId) {
                            if (i == 0) setCck = new HashSet<>();
                            setCck.add(ids);
                            if (i == maxdelete - 1) {
                                i = 0;
                                listSetId.add(setCck);
                            } else
                                i++;
                        }
                        if (i < maxdelete) listSetId.add(setCck);

                        Map<String, Set<String>> mapDelete = deleteCachedAPIResponse(listSetId);
                        Set<String> setDeleteFail = mapDelete.get("fail");
                        setCck = new HashSet<>();
                        boolean found;
                        for(String cck:mapCck.keySet()){
                            Set<String> setId = mapCck.get(cck);
                            found=false;
                            for(String id:setId){
                                if(setDeleteFail.contains(id)) {
                                    found = true;
                                    break;
                                }
                            }
                            if(!found) {
                                setSuccess.add(cck);
                                setCck.add(cck);
                            }
                            else setFail.add(cck);
                        }
                        queryAndDeleteCacheInspector(setCck);
                    }
                } else {
                    System.out.println("Query was unsuccessful. Status code returned is " + statusCode);
                    logger.info(CLASS_NAME + '.' + METHOD_NAME + "Query was unsuccessful. Status code returned is " + statusCode);
                    System.out.println("An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                    logger.info(CLASS_NAME + '.' + METHOD_NAME + "An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                    System.out.println(getBody(response.getEntity().getContent()));
                    logger.info(CLASS_NAME + '.' + METHOD_NAME + " body: " + getBody(response.getEntity().getContent()));
                }
                swingProgressBar.updateBar(ccks.size());
            }
        } catch (IOException | NullPointerException ioe) {
            logger.severe(CLASS_NAME + '.' + METHOD_NAME + " Exception: " + ioe.getMessage());
            ioe.printStackTrace();
        }
        mapReturn.put("success",setSuccess);
        mapReturn.put("fail",setFail);
        return mapReturn;
    }

    public static Map<String,Set<String>> deleteCachedAPIResponse(List<Set<String>> listCck) {
        String METHOD_NAME = "deleteCachedAPIResponse";
        System.out.println("\n_______________ delete QUERY _______________");
        Map<String,Set<String>> mapReturn= new HashMap<>();
        for (Set<String> listId : listCck) {
            try {
                StringBuilder strIds = new StringBuilder();
                for (String ids : listId) {
                    if (strIds.toString().equals("")) {
                        strIds = new StringBuilder(ids);
                    } else {
                        strIds.append(",").append(ids);
                    }
                }
                HttpClient httpClient = HttpClientBuilder.create().build();
                String baseUri = Salesforce.loginInstanceUrl + REST_ENDPOINT + API_VERSION;
                String uri = baseUri + "/composite/sobjects?ids=" + strIds;

                HttpDelete httpDelete = new HttpDelete(uri);
                httpDelete.addHeader(Salesforce.oauthHeader);
                httpDelete.addHeader(prettyPrintHeader);
                System.out.println("Query URL: " + uri);
                logger.info(CLASS_NAME + '.' + METHOD_NAME + "Query URL: " + uri);
                // Make the request.
                HttpResponse response = httpClient.execute(httpDelete);
                // Process the result
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String response_string = EntityUtils.toString(response.getEntity());
                    JSONArray j = new JSONArray(response_string);
                    int i = 0;
                    Set<String> setSuccess = new HashSet<>();
                    Set<String> setFail = new HashSet<>();
                    while (i < j.length()) {
                        boolean success = j.getJSONObject(i).getBoolean("success");
                        if (success) {
                            setSuccess.add(j.getJSONObject(i).getString("id"));
                        } else {
                            setFail.add(j.getJSONObject(i).getString("id"));
                        }
                        i++;
                    }
                    mapReturn.put("success", setSuccess);
                    mapReturn.put("fail", setFail);
                } else {
                    Set<String> setSuccess = new HashSet<>();
                    mapReturn.put("success", setSuccess);
                    mapReturn.put("fail", listId);
                }
            } catch (JSONException | IOException je) {
                Set<String> setSuccess = new HashSet<>();
                mapReturn.put("success",setSuccess);
                mapReturn.put("fail",listId);
            }
        }
        return mapReturn;
    }

    public static void queryAndDeleteCacheInspector(Set<String> listCck) {
        String METHOD_NAME = "queryAndDeleteCacheInspector";
        System.out.println("\n_______________ Lead QUERY _______________");
        try {
            StringBuilder strIds = new StringBuilder();
            for (String ids : listCck) {
                if (strIds.toString().equals("")) strIds = new StringBuilder("'" + ids + "'");
                else strIds.append(",'").append(ids).append("'");
            }
            String baseUri = Salesforce.loginInstanceUrl + REST_ENDPOINT + API_VERSION;
            String uri = baseUri + "/query?q=SELECT+id+FROM+CacheInspector__c+WHERE+CacheKey__c+IN+(" + strIds + ")";
            //Set up the HTTP objects needed to make the request.
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(uri);
            httpGet.addHeader(Salesforce.oauthHeader);
            httpGet.addHeader(prettyPrintHeader);
            System.out.println("Query URL: " + uri);
            logger.info(CLASS_NAME + '.' + METHOD_NAME + "Query URL: " + uri);
            // Make the request.
            HttpResponse response = httpClient.execute(httpGet);
            // Process the result
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String response_string = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(response_string);
                JSONArray j = json.getJSONArray("records");
                int i = 0;
                Set<String> listId = new HashSet<>();
                List<Set<String>> listSetId = new ArrayList<>();
                while (i < j.length()) {
                    listId.add(json.getJSONArray("records").getJSONObject(i).getString("Id"));
                    i++;
                }
                i = 0;
                Set<String> setCck = null;
                for (String ids : listId) {
                    if (i == 0) setCck = new HashSet<>();
                    setCck.add(ids);
                    if (i == maxdelete - 1) {
                        i = 0;
                        listSetId.add(setCck);
                    } else
                        i++;
                }
                if (i < maxdelete) listSetId.add(setCck);
                deleteCacheInspector(listSetId);
            } else {
                logger.info(CLASS_NAME + '.' + METHOD_NAME + "Query was unsuccessful. Status code returned is " + statusCode);
                System.out.println("Query was unsuccessful. Status code returned is " + statusCode);
                logger.info(CLASS_NAME + '.' + METHOD_NAME + "An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                System.out.println("An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                logger.info(CLASS_NAME + '.' + METHOD_NAME + " body:" + getBody(response.getEntity().getContent()));
                System.out.println(getBody(response.getEntity().getContent()));
            }
        } catch (IOException | NullPointerException ioe) {
            logger.severe(CLASS_NAME + '.' + METHOD_NAME + " Exception:" + ioe.getMessage());
            ioe.printStackTrace();
        }
    }

    public static void deleteCacheInspector(List<Set<String>> listCck) {
        String METHOD_NAME = "deleteCacheInspector";
        System.out.println("\n_______________ delete QUERY _______________");
        for (Set<String> listId : listCck) {
            try {
                StringBuilder strIds = new StringBuilder();
                for (String ids : listId) {
                    if (strIds.toString().equals("")) {
                        strIds = new StringBuilder(ids);
                    } else {
                        strIds.append(",").append(ids);
                    }
                }
                HttpClient httpClient = HttpClientBuilder.create().build();
                String baseUri = Salesforce.loginInstanceUrl + REST_ENDPOINT + API_VERSION;
                String uri = baseUri + "/composite/sobjects?ids=" + strIds;

                HttpDelete httpDelete = new HttpDelete(uri);
                httpDelete.addHeader(Salesforce.oauthHeader);
                httpDelete.addHeader(prettyPrintHeader);
                System.out.println("Query URL: " + uri);
                logger.info(CLASS_NAME + '.' + METHOD_NAME + "Query URL: " + uri);
                // Make the request.
                HttpResponse response = httpClient.execute(httpDelete);
                // Process the result
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String response_string = EntityUtils.toString(response.getEntity());
                    JSONArray j = new JSONArray(response_string);
                    int i = 0;
                    Set<String> setSuccess = new HashSet<>();
                    Set<String> setFail = new HashSet<>();
                    while (i < j.length()) {
                        boolean success = j.getJSONObject(i).getBoolean("success");
                        if (success) {
                            setSuccess.add(j.getJSONObject(i).getString("id"));
                        } else {
                            setFail.add(j.getJSONObject(i).getString("id"));
                        }
                        i++;
                    }
                }
            } catch (JSONException | IOException je) {
                logger.severe(CLASS_NAME + '.' + METHOD_NAME + "Exception: " + je.getMessage());
                je.printStackTrace();
            }
        }
    }

    public static Integer queryCachedApiResponse() {
        String METHOD_NAME = "queryCachedApiResponse";
        System.out.println("\n_______________ Lead QUERY _______________");
        try {
            String baseUri = Salesforce.loginInstanceUrl + REST_ENDPOINT + API_VERSION;
            String uri = baseUri + "/query?q=SELECT+count(id)+FROM+CacheInspector__c";
            //Set up the HTTP objects needed to make the request.
            HttpClient httpClient = HttpClientBuilder.create().build();

            HttpGet httpGet = new HttpGet(uri);
            httpGet.addHeader(Salesforce.oauthHeader);
            httpGet.addHeader(prettyPrintHeader);
            System.out.println("Query URL: " + uri);
            logger.info(CLASS_NAME + '.' + METHOD_NAME + "Query URL: " + uri);
            // Make the request.
            HttpResponse response = httpClient.execute(httpGet);
            // Process the result
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String response_string = EntityUtils.toString(response.getEntity());
                try {
                    JSONObject json = new JSONObject(response_string);
                    JSONArray j = json.getJSONArray("records");
                    int i = 0;
                    while (i < j.length()) {
                        return json.getJSONArray("records").getJSONObject(i).getInt("expr0");
                    }
                } catch (JSONException je) {
                    logger.severe(CLASS_NAME + '.' + METHOD_NAME + "Exception " + je.getMessage());
                    je.printStackTrace();
                }
            } else {
                logger.info(CLASS_NAME + '.' + METHOD_NAME + "Query was unsuccessful. Status code returned is " + statusCode);
                System.out.println("Query was unsuccessful. Status code returned is " + statusCode);
                logger.info(CLASS_NAME + '.' + METHOD_NAME + "An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                System.out.println("An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                logger.info(CLASS_NAME + '.' + METHOD_NAME + " body " + getBody(response.getEntity().getContent()));
                System.out.println(getBody(response.getEntity().getContent()));
            }
        } catch (IOException | NullPointerException ioe) {
            logger.severe(CLASS_NAME + '.' + METHOD_NAME + " Exception " + ioe.getMessage());
            ioe.printStackTrace();
        }
        return null;
    }

    private static String getBody(InputStream inputStream) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStream)
            );
            String inputLine;
            while ( (inputLine = in.readLine() ) != null ) {
                result.append(inputLine);
                result.append("\n");
            }
            in.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result.toString();
    }

}