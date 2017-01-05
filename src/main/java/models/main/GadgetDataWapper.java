package models.main;

import java.util.List;

public class GadgetDataWapper {
    private List<GadgetData> issueData;
    private String summary;

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
