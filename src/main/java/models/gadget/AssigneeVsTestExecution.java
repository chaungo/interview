package models.gadget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"author", "name", "pictureUrl", "description"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssigneeVsTestExecution extends GadgetAPI {
    public static final String author = "";
    public static final String name = "Assignee Test Execution Report";
    public static final String pictureUrl = "";
    public static final String addnewUIurl = "assets/html/AssigneeSettings.html";
    public static final String description = "Assignee Test Execution Report";

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
        return selectAllTestCycle;
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
