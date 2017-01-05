package models;

public class ExecutionDefectVO {
    private int defectId;
    private String defectKey;
    private String defectSummary;
    private String defectStatus;
    private String defectResolutionId;
    public int getDefectId() {
        return defectId;
    }
    public void setDefectId(int defectId) {
        this.defectId = defectId;
    }
    public String getDefectKey() {
        return defectKey;
    }
    public void setDefectKey(String defectKey) {
        this.defectKey = defectKey;
    }
    public String getDefectSummary() {
        return defectSummary;
    }
    public void setDefectSummary(String defectSummary) {
        this.defectSummary = defectSummary;
    }
    public String getDefectStatus() {
        return defectStatus;
    }
    public void setDefectStatus(String defectStatus) {
        this.defectStatus = defectStatus;
    }
    public String getDefectResolutionId() {
        return defectResolutionId;
    }
    public void setDefectResolutionId(String defectResolutionId) {
        this.defectResolutionId = defectResolutionId;
    }
    
}
