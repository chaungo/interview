package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import models.JQLIssuetypeVO.Type;

@JsonIgnoreProperties(ignoreUnknown = true)
public class APIIssueVO {
    private String key;
    private String self;
    private String summary;
    private PriorityVO priority;
    private Type type;
    
    public APIIssueVO() {
    }

    public APIIssueVO(String key, String self) {
        this.key = key;
        this.self = self;
    }

    public APIIssueVO(String key, String self, String summary) {
        this.key = key;
        this.self = self;
        this.summary = summary;
    }

    public PriorityVO getPriority() {
        return priority;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setPriority(PriorityVO priority) {
        this.priority = priority;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

}
