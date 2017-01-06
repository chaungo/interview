package models.gadget;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import models.main.Release;

public interface Gadget {
    public String getId();

    public Type getType();

    public String getUser();
    
    public String getDashboardId();
    
    public Set<String> getProducts();
    
    public Release getRelease();
    
    public String getProjectName();
    
    public enum Type {
        ASSIGNEE_TEST_EXECUTION, TEST_CYCLE_TEST_EXECUTION, EPIC_US_TEST_EXECUTION, STORY_TEST_EXECUTION, AMS_SONAR_STATISTICS_GADGET, AMS_OVERDUE_REVIEWS;
        @JsonCreator
        public static Type fromString(String str){
            for(Type type : values()){
                if(type.toString().equalsIgnoreCase(str)){
                    return type;
                }
            }
            return null;
        }
        @JsonValue
        public String toString(){
            return super.toString();
        }
    }
}
