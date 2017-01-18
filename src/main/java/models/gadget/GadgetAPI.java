package models.gadget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class GadgetAPI implements Gadget {

    protected String id;
    protected Type type;
    protected String user;
    protected String dashboardId;
    // fixVersion
    protected String release;
    protected Set<String> products;
    protected List<String> metrics;
    protected String projectName;

    public static Iterator<GadgetAPI> getIterator() {
        List<GadgetAPI> gadgets = new ArrayList<>();
        gadgets.add(new AssigneeVsTestExecution());
        gadgets.add(new CycleVsTestExecution());
        gadgets.add(new EpicVsTestExecution());
        gadgets.add(new OverdueReviewsGadget());
        gadgets.add(new SonarStatisticsGadget());
        gadgets.add(new StoryVsTestExecution());
        return gadgets.iterator();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
    }

    @Override
    public Set<String> getProducts() {
        return products;
    }

    public void setProducts(Set<String> products) {
        this.products = products;
    }

    @Override
    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    @Override
    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Type getType() {
        return type;
    }

    public abstract String getAuthor();

    public abstract String getName();

    public abstract String getPictureUrl();

    public abstract String getAddnewUIurl();

    public abstract String getDescription();
}
