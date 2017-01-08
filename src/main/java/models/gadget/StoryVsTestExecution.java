package models.gadget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import models.main.Release;

import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true, value={"author","name","pictureUrl","addnewUIurl","description"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoryVsTestExecution extends GadgetAPI {
    public static final String author = "";
    public static final String name = "Assignee Test Execution Report";
    public static final String pictureUrl = "";
    public static final String addnewUIurl = "assets/html/xxx.html";
    public static final String description = "Assignee Test Execution Report";
    
    private Type type = Type.STORY_TEST_EXECUTION;
    private String projectName;
    private Set<String> epic;
    private Set<String> stories;
    private boolean selectAllStory;
    private boolean selectAllEpic;

    @Override
    public String getAuthor() {
        return author;
    }
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getPictureUrl() {
        return pictureUrl;
    }

    @Override
    public String getAddnewUIurl() {
        return addnewUIurl;
    }
    @Override
    public String getDescription() {
        return description;
    }
    
    public boolean isSelectAllEpic() {
        return selectAllEpic;
    }

    public void setSelectAllEpic(boolean selectAllEpic) {
        this.selectAllEpic = selectAllEpic;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Set<String> getProducts() {
        return products;
    }

    public void setProducts(Set<String> products) {
        this.products = products;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    public Set<String> getEpic() {
        return epic;
    }

    public void setEpic(Set<String> epic) {
        this.epic = epic;
    }

    public Set<String> getStories() {
        return stories;
    }

    public void setStories(Set<String> stories) {
        this.stories = stories;
    }

    public boolean isSelectAllStory() {
        return selectAllStory;
    }

    public void setSelectAllStory(boolean selectAll) {
        this.selectAllStory = selectAll;
    }

    @Override
    public String getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(String id) {
        dashboardId = id;
    }
}
