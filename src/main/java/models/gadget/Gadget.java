package models.gadget;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Set;

public interface Gadget {
    String getId();

    Type getType();

    String getUser();

    String getDashboardId();

    Set<String> getProducts();

    String getRelease();

    String getProjectName();

    List<String> getMetrics();

    enum Type {
        ASSIGNEE_TEST_EXECUTION, TEST_CYCLE_TEST_EXECUTION, EPIC_US_TEST_EXECUTION, STORY_TEST_EXECUTION, AMS_SONAR_STATISTICS_GADGET, AMS_OVERDUE_REVIEWS;

        @JsonCreator
        public static Type fromString(String str) {
            for (Type type : values()) {
                if (type.toString().equalsIgnoreCase(str)) {
                    return type;
                }
            }
            return null;
        }

        @JsonValue
        public String toString() {
            return super.toString();
        }
    }
}
