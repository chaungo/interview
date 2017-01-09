package models.gadget;

import models.main.Release;

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
    protected Release release;
    protected Set<String> products;
    protected List<String> metrics;

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

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getDashboardId() {
        return dashboardId;
    }

    @Override
    public Set<String> getProducts() {
        return products;
    }

    @Override
    public Release getRelease() {
        return release;
    }

    public abstract String getAuthor();

    public abstract String getName();

    public abstract String getPictureUrl();

    public abstract String getAddnewUIurl();

    public abstract String getDescription();
}
