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
}
