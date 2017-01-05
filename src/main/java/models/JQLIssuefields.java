package models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JQLIssuefields {
    private JQLIssuetypeVO issuetype;
    private List<JQLIssueLinkVO> issuelinks;
    private PriorityVO priority;
    // pland
    private int customfield_14809;

    private String summary;
    // Product
    @JsonProperty("customfield_12718")
    private ProductVO product;

    // Epic Parrent
    @JsonProperty("customfield_11209")
    private String epicLink;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public PriorityVO getPriority() {
        return priority;
    }

    public void setPriority(PriorityVO priority) {
        this.priority = priority;
    }

    public ProductVO getProduct() {
        return product;
    }

    public void setProduct(ProductVO product) {
        this.product = product;
    }

    public JQLIssuetypeVO getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(JQLIssuetypeVO issuetype) {
        this.issuetype = issuetype;
    }

    public List<JQLIssueLinkVO> getIssuelinks() {
        return issuelinks;
    }

    public void setIssuelinks(List<JQLIssueLinkVO> issuelinks) {
        this.issuelinks = issuelinks;
    }

    public int getCustomfield_14809() {
        return customfield_14809;
    }

    public void setCustomfield_14809(int customfield_14809) {
        this.customfield_14809 = customfield_14809;
    }

    public String getEpicLink() {
        return epicLink;
    }

    public void setEpicLink(String epicLink) {
        this.epicLink = epicLink;
    }

}
