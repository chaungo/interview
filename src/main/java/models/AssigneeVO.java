package models;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssigneeVO {
    @JsonProperty("assignee")
    private String name;
    @JsonProperty("assigneeUserName")
    private String userName;
    @JsonProperty("assigneeDisplay")
    private String display;

    public AssigneeVO(String name, String userName, String display) {
        this.name = name;
        this.userName = userName;
        this.display = display;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(this == obj)
            return true;
        if(obj instanceof AssigneeVO){
            if(userName != null && userName.equals(((AssigneeVO) obj).getUserName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }
}
