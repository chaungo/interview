package models;

public class ProjectVO {
    private String expand;
    private String self;
    private String id;
    private String key;
    private String name;
    private AvatarUrlsVO avatarUrls;
    private ProjectCategoryVO projectCategory;
    public String getExpand() {
        return expand;
    }
    public void setExpand(String expand) {
        this.expand = expand;
    }
    public String getSelf() {
        return self;
    }
    public void setSelf(String self) {
        this.self = self;
    }
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public AvatarUrlsVO getAvatarUrls() {
        return avatarUrls;
    }
    public void setAvatarUrls(AvatarUrlsVO avatarUrls) {
        this.avatarUrls = avatarUrls;
    }
    public ProjectCategoryVO getProjectCategory() {
        return projectCategory;
    }
    public void setProjectCategory(ProjectCategoryVO projectCategory) {
        this.projectCategory = projectCategory;
    }
    
}
