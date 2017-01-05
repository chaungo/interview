package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AvatarUrlsVO {
    @JsonProperty("48x48")
    private String size48;
    @JsonProperty("24x24")
    private String size24;
    @JsonProperty("16x16")
    private String size16;
    @JsonProperty("32x32")
    private String size32;

    public String getSize48() {
        return size48;
    }

    public void setSize48(String size48) {
        this.size48 = size48;
    }

    public String getSize24() {
        return size24;
    }

    public void setSize24(String size24) {
        this.size24 = size24;
    }

    public String getSize16() {
        return size16;
    }

    public void setSize16(String size16) {
        this.size16 = size16;
    }

    public String getSize32() {
        return size32;
    }

    public void setSize32(String size32) {
        this.size32 = size32;
    }

}
