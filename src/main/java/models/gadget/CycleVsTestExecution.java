package models.gadget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.main.Release;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true, value={"author","name","pictureUrl","addnewUIurl","description"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CycleVsTestExecution extends GadgetAPI {
    public static final String author = "";
    public static final String name = "Cycle Test Execution Report";
    public static final String pictureUrl = "";
    public static final String addnewUIurl = "assets/html/xxx.html";
    public static final String description = "Cycle Test Execution Report";
    
    private Type type = Type.TEST_CYCLE_TEST_EXECUTION;
    @JsonProperty(required = true)
    private String projectName;
    private Set<String> metrics;
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

    @Override
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setProducts(Set<String> products) {
        this.products = products;
    }

    public boolean isSelectAllCycle() {
        return selectAllCycle;
    }

    public void setSelectAllCycle(boolean selectAll) {
        this.selectAllCycle = selectAll;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setMetrics(Set<String> metrics) {
        this.metrics = metrics;
    }

    public Set<String> getCycles() {
        return cycles;
    }

    public void setCycles(Set<String> cycles) {
        this.cycles = cycles;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public void setDashboardId(String id) {
        dashboardId = id;
    }

}
