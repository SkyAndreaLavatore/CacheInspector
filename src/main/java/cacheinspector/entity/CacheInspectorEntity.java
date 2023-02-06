package cacheinspector.entity;

public class CacheInspectorEntity {
    private final String jsonObject;
    private final String catalogCode;
    private final String cacheKey;

    public CacheInspectorEntity(String jsonObject, String catalogCode, String cacheKey) {
        this.jsonObject = jsonObject;
        this.catalogCode = catalogCode;
        this.cacheKey = cacheKey;
    }

    public String getJsonObject() {
        return jsonObject;
    }


    public String getCatalogCode() {
        return catalogCode;
    }


    public String getCacheKey() {
        return cacheKey;
    }

}
