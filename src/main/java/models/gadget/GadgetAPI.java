package models.gadget;

import java.util.List;
import java.util.Set;

import models.main.Release;

public abstract class GadgetAPI implements Gadget {
    protected String id;
    protected Type type;
    protected String user;
    protected String dashboardId;
 // fixVersion
    protected Release release;
    protected Set<String> products;
    protected List<String> metrics;
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
    
}
