package models.main;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import models.JQLIssueVO;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JQLSearchResult {
    private String expand;
    private int startAt;
    private int maxResults;
    private int total;
    private List<JQLIssueVO> issues;

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    public int getStartAt() {
        return startAt;
    }

    public void setStartAt(int startAt) {
        this.startAt = startAt;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<JQLIssueVO> getIssues() {
        return issues;
    }

    public void setIssues(List<JQLIssueVO> issues) {
        this.issues = issues;
    }

}
