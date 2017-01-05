package models.gadget;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import models.main.Release;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CycleVsTestExecution extends GadgetAPI {
    private Type type = Type.TEST_CYCLE_TEST_EXECUTION;
    @JsonProperty(required = true)
    private String projectName;
    private Set<String> metrics;
    private Set<String> cycles;
    private boolean selectAllCycle;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getUser() {
        return user;
    }

    public Set<String> getProducts() {
        return products;
    }

    public void setProducts(Set<String> products) {
        this.products = products;
    }

    public boolean isSelectAllCycle() {
        return selectAllCycle;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setSelectAllCycle(boolean selectAll) {
        this.selectAllCycle = selectAll;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Set<String> getMetrics() {
        return metrics;
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
