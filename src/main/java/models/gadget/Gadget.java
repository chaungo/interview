package models.gadget;

import java.util.Set;

import models.main.Release;

public interface Gadget {
    public String getId();

    public Type getType();

    public String getUser();
    
    public String getDashboardId();
    
    public Set<String> getProducts();
    
    public Release getRelease();
    
    public String getProjectName();
    
    public enum Type {
        ASSIGNEE_TEST_EXECUTION, TEST_CYCLE_TEST_EXECUTION, EPIC_US_TEST_EXECUTION, STORY_TEST_EXECUTION;
    }
}
