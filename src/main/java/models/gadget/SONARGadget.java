package models.gadget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SONARGadget extends GadgetAPI {
    private Type type = Type.AMS_SONAR_STATISTICS_GADGET;
    private String data;
    private String cache;
    private int date;
    
    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getProjectName() {
        return "";
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    public void setId(String id){
        this.id = id;
    }
}
