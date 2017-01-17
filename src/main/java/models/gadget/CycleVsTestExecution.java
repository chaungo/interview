package models.gadget;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import util.MessageConstant;
import util.PropertiesUtil;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"author", "name", "pictureUrl", "description"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CycleVsTestExecution extends GadgetAPI {
    public static final String author = PropertiesUtil.getString(MessageConstant.CYCLE_AUTHOR, "");
    public static final String name = PropertiesUtil.getString(MessageConstant.CYCLE_NAME, "Cycle");
    public static final String pictureUrl = PropertiesUtil.getString(MessageConstant.CYCLE_PICTUREURL, "");
    public static final String addnewUIurl = "assets/html/CycleSettings.html";
    public static final String description = PropertiesUtil.getString(MessageConstant.CYCLE_DESCRIPTION, "Cycle Report");

    private Type type = Type.TEST_CYCLE_TEST_EXECUTION;
    @JsonProperty(required = true)
    private Set<String> cycles;
    private boolean selectAllCycle;

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

    public boolean isSelectAllCycle() {
        return true;
    }

    public void setSelectAllCycle(boolean selectAll) {
        this.selectAllCycle = selectAll;
    }

    public Set<String> getCycles() {
        return cycles;
    }

    public void setCycles(Set<String> cycles) {
        this.cycles = cycles;
    }

}
