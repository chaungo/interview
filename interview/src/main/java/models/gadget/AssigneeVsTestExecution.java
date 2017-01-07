package models.gadget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import models.main.Release;

import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssigneeVsTestExecution extends GadgetAPI {
    private final Gadget.Type type = Gadget.Type.ASSIGNEE_TEST_EXECUTION;
    private String projectName;
    private boolean selectAllTestCycle;
    private Set<String> cycles;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Type getType() {
        return type;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean isSelectAllTestCycle() {
        return selectAllTestCycle;
    }

    public void setSelectAllTestCycle(boolean selectAllTestCycle) {
        this.selectAllTestCycle = selectAllTestCycle;
    }

    public Set<String> getProducts() {
        return products;
    }

    public void setProducts(Set<String> products) {
        this.products = products;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Set<String> getCycles() {
        return cycles;
    }

    public void setCycles(Set<String> cycles) {
        this.cycles = cycles;
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    @Override
    public String getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(String id) {
        dashboardId = id;
    }

}