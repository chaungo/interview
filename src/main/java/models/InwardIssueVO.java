package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class InwardIssueVO {

    private String id;
    private String key;
    private String self;
    private FieldsVO fields;
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
    public FieldsVO getFields() {
        return fields;
    }
    public void setFields(FieldsVO fields) {
        this.fields = fields;
    }
    
    
    
    
    
}
