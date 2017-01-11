package models.main;

import java.util.Map;

public class GadgetDataCacheVO {
    private Map<String, GadgetDataWapper> gadgetsData;
    private State state = State.LOADING;
    public long lastAccessed = System.currentTimeMillis();
    
    public long getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public Map<String, GadgetDataWapper> getGadgetsData() {
        return gadgetsData;
    }

    public void setGadgetsData(Map<String, GadgetDataWapper> gadgetsData) {
        this.gadgetsData = gadgetsData;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        LOADING, SUCCESS, FAIL
    }
}
