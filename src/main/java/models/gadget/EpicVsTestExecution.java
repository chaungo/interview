package models.gadget;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import models.main.Release;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpicVsTestExecution extends GadgetAPI {
    private boolean selectAll;
    private Gadget.Type type = Gadget.Type.EPIC_US_TEST_EXECUTION;
    private String projectName;
    private Set<String> epic;
    
    public Set<String> getEpic() {
        return epic;
    }

    public void setProducts(Set<String> products) {
        this.products = products;
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

    public void setUser(String user) {
        this.user = user;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }
    
    public void setEpic(Set<String> epic) {
        this.epic = epic;
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
