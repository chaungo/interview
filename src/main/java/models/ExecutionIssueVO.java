package models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionIssueVO {
    private int id;
    private int orderId;
    private int cycleId;
    private String cycleName;
    private String issueId;
    private String issueKey;
    private String issueSummary;
    private String issueDescription;
    private String projectKey;
    private int projectId;
    private String project;
    private int projectAvatarId;
    private String priority;
    private List<ComponentVO> components;
    private int versionId;
    private String versionName;
    private StatusVO status;
    private String executedOn;
    private String creationDate;
    private String comment;
    private String htmlComment;
    private String executedBy;
    private String executedByUserName;
    private List<ExecutionDefectVO> executionDefects;
    private List<String> stepDefects;
    private int executionDefectCount;
    private int stepDefectCount;
    private int totalDefectCount;
    private String executedByDisplay;
    private String assignee;
    private String assigneeUserName;
    private String assigneeDisplay;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public int getCycleId() {
        return cycleId;
    }
    public void setCycleId(int cycleId) {
        this.cycleId = cycleId;
    }
    public String getCycleName() {
        return cycleName;
    }
    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }
    public String getIssueId() {
        return issueId;
    }
    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }
    public String getIssueKey() {
        return issueKey;
    }
    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }
    public String getIssueSummary() {
        return issueSummary;
    }
    public void setIssueSummary(String issueSummary) {
        this.issueSummary = issueSummary;
    }
    public String getIssueDescription() {
        return issueDescription;
    }
    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }
    public String getProjectKey() {
        return projectKey;
    }
    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
    public int getProjectId() {
        return projectId;
    }
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
    public String getProject() {
        return project;
    }
    public void setProject(String project) {
        this.project = project;
    }
    public int getProjectAvatarId() {
        return projectAvatarId;
    }
    public void setProjectAvatarId(int projectAvatarId) {
        this.projectAvatarId = projectAvatarId;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public List<ComponentVO> getComponents() {
        return components;
    }
    public void setComponents(List<ComponentVO> components) {
        this.components = components;
    }
    public int getVersionId() {
        return versionId;
    }
    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }
    public String getVersionName() {
        return versionName;
    }
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    public StatusVO getStatus() {
        return status;
    }
    public void setStatus(StatusVO status) {
        this.status = status;
    }
    public String getExecutedOn() {
        return executedOn;
    }
    public void setExecutedOn(String executedOn) {
        this.executedOn = executedOn;
    }
    public String getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getHtmlComment() {
        return htmlComment;
    }
    public void setHtmlComment(String htmlComment) {
        this.htmlComment = htmlComment;
    }
    public String getExecutedBy() {
        return executedBy;
    }
    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }
    public String getExecutedByUserName() {
        return executedByUserName;
    }
    public void setExecutedByUserName(String executedByUserName) {
        this.executedByUserName = executedByUserName;
    }

    public List<ExecutionDefectVO> getExecutionDefects() {
        return executionDefects;
    }
    public void setExecutionDefects(List<ExecutionDefectVO> executionDefects) {
        this.executionDefects = executionDefects;
    }
    public List<String> getStepDefects() {
        return stepDefects;
    }
    public void setStepDefects(List<String> stepDefects) {
        this.stepDefects = stepDefects;
    }
    public int getExecutionDefectCount() {
        return executionDefectCount;
    }
    public void setExecutionDefectCount(int executionDefectCount) {
        this.executionDefectCount = executionDefectCount;
    }
    public int getStepDefectCount() {
        return stepDefectCount;
    }
    public void setStepDefectCount(int stepDefectCount) {
        this.stepDefectCount = stepDefectCount;
    }
    public int getTotalDefectCount() {
        return totalDefectCount;
    }
    public void setTotalDefectCount(int totalDefectCount) {
        this.totalDefectCount = totalDefectCount;
    }
    public String getExecutedByDisplay() {
        return executedByDisplay;
    }
    public void setExecutedByDisplay(String executedByDisplay) {
        this.executedByDisplay = executedByDisplay;
    }
    public String getAssignee() {
        return assignee;
    }
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
    public String getAssigneeUserName() {
        return assigneeUserName;
    }
    public void setAssigneeUserName(String assigneeUserName) {
        this.assigneeUserName = assigneeUserName;
    }
    public String getAssigneeDisplay() {
        return assigneeDisplay;
    }
    public void setAssigneeDisplay(String assigneeDisplay) {
        this.assigneeDisplay = assigneeDisplay;
    }
    
}
