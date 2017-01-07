package models;

/**
 * Created by nnmchau on 1/7/2017.
 */
public class Dashboard {
    private String id;
    private String owner;
    private String name;
    private String privacyStatus;
    private String privacyShare;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrivacyStatus() {
        return privacyStatus;
    }

    public void setPrivacyStatus(String privacyStatus) {
        this.privacyStatus = privacyStatus;
    }

    public String getPrivacyShare() {
        return privacyShare;
    }

    public void setPrivacyShare(String privacyShare) {
        this.privacyShare = privacyShare;
    }
}
