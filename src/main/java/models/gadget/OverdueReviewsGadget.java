package models.gadget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true, value={"author","name","pictureUrl","addnewUIurl","description"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverdueReviewsGadget extends GadgetAPI {
    public static final String author = "By Alcatel-Lucent AMS R&D";
    public static final String name = "AMS Overdue Reviews Report Gadget";
    public static final String pictureUrl = "";
    public static final String addnewUIurl = "assets/html/addNewOverdueReviewReportGadget.html";
    public static final String description = "AMS Overdue Reviews Report Gadget";
    
    private Type type = Type.AMS_OVERDUE_REVIEWS;
    private String data;
    private String cache;

    @Override
    public String getAuthor() {
        return author;
    }
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getPictureUrl() {
        return pictureUrl;
    }

    @Override
    public String getAddnewUIurl() {
        return addnewUIurl;
    }
    @Override
    public String getDescription() {
        return description;
    }
    
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
