package models.gadget;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import util.MessageConstant;
import util.PropertiesUtil;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"author", "name", "pictureUrl", "description"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssigneeVsTestExecution extends GadgetAPI {
    public static final String author = PropertiesUtil.getString(MessageConstant.ASSIGNEE_AUTHOR, "");
    public static final String name = PropertiesUtil.getString(MessageConstant.ASSIGNEE_NAME, "Assignee");
    public static final String addnewUIurl = "assets/html/AssigneeSettings.html";
    public static final String pictureUrl = PropertiesUtil.getString(MessageConstant.ASSIGNEE_PICTUREURL, "");
    public static final String description = PropertiesUtil.getString(MessageConstant.ASSIGNEE_DESCRIPTION, "Assignee Report");

    private final Gadget.Type type = Gadget.Type.ASSIGNEE_TEST_EXECUTION;
    private boolean selectAllTestCycle;
    private Set<String> cycles;

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

    public boolean isSelectAllTestCycle() {
        return true;
    }

    public void setSelectAllTestCycle(boolean selectAllTestCycle) {
        this.selectAllTestCycle = selectAllTestCycle;
    }

    public Set<String> getCycles() {
        return cycles;
    }

    public void setCycles(Set<String> cycles) {
        this.cycles = cycles;
    }

}
