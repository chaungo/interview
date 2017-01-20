package models.main;

import java.util.List;

public class GadgetDataWrapper {
    private List<GadgetData> issueData;
    private String summary;
    private long lastUpdate = 0;
    
    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastTimeUpdate) {
        this.lastUpdate = lastTimeUpdate;
    }

    public List<GadgetData> getIssueData() {
        return issueData;
    }

    public void setIssueData(List<GadgetData> gadgetData) {
        this.issueData = gadgetData;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

}
