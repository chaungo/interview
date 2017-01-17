package models.gadget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import util.MessageConstant;
import util.PropertiesUtil;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"author", "name", "pictureUrl", "description"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpicVsTestExecution extends GadgetAPI {
    public static final String author = PropertiesUtil.getString(MessageConstant.EPIC_AUTHOR, "");
    public static final String name = PropertiesUtil.getString(MessageConstant.EPIC_NAME, "Epic Report");
    public static final String pictureUrl = PropertiesUtil.getString(MessageConstant.EPIC_PICTUREURL, "");
    public static final String addnewUIurl = "assets/html/EpicSettings.html";
    public static final String description = PropertiesUtil.getString(MessageConstant.EPIC_DESCRIPTION, "Epic Report");

    private boolean selectAll;
    private Gadget.Type type = Gadget.Type.EPIC_US_TEST_EXECUTION;
    private Set<String> epic;

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

    public Set<String> getEpic() {
        return epic;
    }

    public void setEpic(Set<String> epic) {
        this.epic = epic;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Gadget.Type getType() {
        return type;
    }

    public void setType(Gadget.Type type) {
        this.type = type;
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    public void setDashboardId(String id) {
        dashboardId = id;
    }
}
