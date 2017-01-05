package models;

public class JQLIssuetypeVO {
	private String self;
	private String id;
	private String description;
	private String iconUrl;
	private String name;
	boolean subtask;
	int avatarId;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSubtask() {
		return subtask;
	}

	public void setSubtask(boolean subtask) {
		this.subtask = subtask;
	}

	public int getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(int avatarId) {
		this.avatarId = avatarId;
	}

	public enum Type {
	    TEST, BUG, EPIC, STORY, IMPROVEMENT;
	    
	    public static Type fromString(String str){
	        for(Type t : values()){
	            if(t.toString().equalsIgnoreCase(str)){
	                return t;
	            }
	        }
	        return null;
	    }
	}
}
