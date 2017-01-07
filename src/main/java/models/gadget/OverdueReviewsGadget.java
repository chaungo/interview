package models.gadget;

public class OverdueReviewsGadget extends GadgetAPI {
    private Type type = Type.AMS_OVERDUE_REVIEWS;
    private String data;
    private String cache;

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String getProjectName() {
        return "";
    }

    @Override
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

}
