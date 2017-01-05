package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JQLIssueLinkVO {
    private String id;
    private String self;
    private InwardIssue inwardIssue;
    private IssueLinkType type;
    
    public IssueLinkType getType() {
        return type;
    }
    public void setType(IssueLinkType type) {
        this.type = type;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSelf() {
        return self;
    }
    public void setSelf(String self) {
        this.self = self;
    }
    public InwardIssue getInwardIssue() {
        return inwardIssue;
    }
    public void setInwardIssue(InwardIssue inwardIssue) {
        this.inwardIssue = inwardIssue;
    }
    
}
