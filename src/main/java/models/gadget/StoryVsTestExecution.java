package models.gadget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import util.MessageConstant;
import util.PropertiesUtil;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"author", "name", "pictureUrl", "description"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoryVsTestExecution extends GadgetAPI {
    public static final String author = PropertiesUtil.getString(MessageConstant.STORY_AUTHOR, "");
    public static final String name = PropertiesUtil.getString(MessageConstant.STORY_NAME, "User Story Test Execution Report");
    public static final String pictureUrl = PropertiesUtil.getString(MessageConstant.STORY_PICTUREURL, "");
    public static final String addnewUIurl = "assets/html/UserStorySettings.html";
    public static final String description = PropertiesUtil.getString(MessageConstant.STORY_DESCRIPTION, "User Story Execution Report");

    private Type type = Type.STORY_TEST_EXECUTION;
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
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

}
