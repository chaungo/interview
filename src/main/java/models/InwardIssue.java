package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InwardIssue {
    private String id;
    private String key;
    private String self;
    private JQLIssuetypeVO issuetype;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public JQLIssuetypeVO getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(JQLIssuetypeVO issuetype) {
        this.issuetype = issuetype;
    }

}
