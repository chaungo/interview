package models.gadget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"author", "name", "pictureUrl", "addnewUIurl", "description"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SonarStatisticsGadget extends GadgetAPI {
    public static final String author = "By Alcatel-Lucent AMS R&D";
    public static final String name = "AMS SONAR Statistics Gadget";
    public static final String pictureUrl = "";
    public static final String addnewUIurl = "assets/html/addNewSonarGadget.html";
    public static final String description = "Gadget to display statistics from AMS SONAR Setup";

    private Type type = Type.AMS_SONAR_STATISTICS_GADGET;
    private String data;
    private String cache;
    private int date;

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

    @Override
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    public void setId(String id) {
        this.id = id;
    }
}
