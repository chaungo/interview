package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JQLIssueVO {
	private String expand;
	private String id;
	private String self;
	private String key;
	private JQLIssuefields fields;
	
    public String getExpand() {
		return expand;
	}

	public void setExpand(String expand) {
		this.expand = expand;
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public JQLIssuefields getFields() {
		return fields;
	}

	public void setFields(JQLIssuefields fields) {
		this.fields = fields;
	}

}
